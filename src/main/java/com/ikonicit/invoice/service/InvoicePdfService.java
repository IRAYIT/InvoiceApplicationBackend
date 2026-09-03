package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.InvoiceDTO;
import com.ikonicit.invoice.dto.InvoiceExtraFieldDTO;
import com.ikonicit.invoice.dto.InvoiceItemDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Renders an invoice to PDF, matching the English ViewInvoice styling
 * (Bill to / Invoice no. / blue headers) rather than the Swedish
 * fakturan.nu reference.
 *
 * NOTE on fields that don't exist on InvoiceDTO yet:
 *  - No dedicated rounding amount — shown as 0.00 until one exists.
 *  - No client billing address on the DTO (only clientId/clientName) —
 *    the "Bill to" box only shows the name for now. Once InvoiceDTO (or
 *    a joined Client lookup) exposes an address, wire it into
 *    buildInvoiceHtml() below.
 *  - Seller identity (company address/email/F-tax) comes from
 *    app.company.* properties rather than per-invoice fields, since
 *    that's seller-side static info, not something that varies by
 *    invoice.
 */
@Service
public class InvoicePdfService {

    @Value("${app.company.name}")
    private String companyName;

    @Value("${app.company.address}")
    private String companyAddress;

    @Value("${app.company.email}")
    private String companyEmail;

    @Value("${app.company.approved-for-f-tax:false}")
    private boolean approvedForFTax;

    // Mirrors ViewInvoice.jsx's EXTRA_FIELD_LABELS so the PDF shows the
    // same readable labels instead of raw camelCase keys like "buyerVat".
    private static final Map<String, String> EXTRA_FIELD_LABELS = new HashMap<>();
    static {
        EXTRA_FIELD_LABELS.put("extraFieldsLong", "Extra information from the customer");
        EXTRA_FIELD_LABELS.put("buyerPersonalId", "Buyer personal id no.");
        EXTRA_FIELD_LABELS.put("buyerVat", "Buyer's VAT number");
        EXTRA_FIELD_LABELS.put("reverseCharge", "Reverse charge");
        EXTRA_FIELD_LABELS.put("threePartyTrade", "Three-party trade");
        EXTRA_FIELD_LABELS.put("brfOrgNo", "Housing association org. no.");
        EXTRA_FIELD_LABELS.put("apartmentDesignation", "Apartment designation");
        EXTRA_FIELD_LABELS.put("propertyDesignation", "Property designation");
    }

    public byte[] renderInvoicePdf(InvoiceDTO invoice) throws Exception {
        String html = buildInvoiceHtml(invoice);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(out);
        builder.run();
        return out.toByteArray();
    }

    private String buildInvoiceHtml(InvoiceDTO invoice) {
        String itemsHtml = buildItemsRows(invoice.getItems());
        String extraFieldsHtml = buildExtraFieldsHtml(invoice);

        BigDecimal subtotal = nz(invoice.getSubtotal());
        BigDecimal tax = nz(invoice.getTaxAmount());
        BigDecimal total = nz(invoice.getTotalAmount());
        BigDecimal rounding = BigDecimal.ZERO; // see class-level note

        int vatRate = firstTaxPercent(invoice.getItems());
        String paymentTerms = invoice.getPaymentTerms() == null ? "Net 30" : invoice.getPaymentTerms();

        return """
            <html>
            <head>
            <meta charset="UTF-8" />
            <style>
              body { font-family: Helvetica, Arial, sans-serif; color: #222222; font-size: 11px; }
              table { border-collapse: collapse; width: 100%%; }
              .doc { padding: 24px 30px; }
              .title { text-align: right; font-size: 22px; font-weight: bold; color: #2f6fe4;
                       border-bottom: 2px solid #2f6fe4; padding-bottom: 4px; margin-bottom: 16px; }
              .meta-outer { width: 100%%; margin-bottom: 18px; }
              .meta-box { border: 1px solid #e6e8ec; padding: 12px 14px; vertical-align: top; }
              .meta-line td { padding: 2px 0; font-size: 11px; }
              .meta-line .label { color: #444444; font-weight: bold; width: 55%%; }
              .ref-label { font-weight: bold; margin-bottom: 2px; }
              .note { font-size: 9px; color: #6a6f76; margin-top: 10px; }
              .addr-box { border: 1px solid #e6e8ec; vertical-align: top; }
              .addr-header { background: #4c8ff0; color: #ffffff; font-weight: bold; padding: 6px 12px; font-size: 11px; }
              .addr-body { padding: 10px 12px; font-size: 11px; line-height: 1.5; }
              .items th { background: #4c8ff0; color: #ffffff; text-align: left; font-size: 10px; padding: 6px 10px; }
              .items th.num, .items td.num { text-align: right; }
              .items td { padding: 8px 10px; font-size: 11px; border-bottom: 1px solid #f0f1f3; }
              .totals { width: 45%%; margin-left: 55%%; margin-top: 10px; border-top: 1px dashed #d8dbe0; }
              .totals td { padding: 3px 0; font-size: 11px; }
              .totals td.val { text-align: right; }
              .grand td { border-top: 1px solid #222222; font-size: 15px; font-weight: bold; padding-top: 8px; }
              .extra-fields { margin-top: 14px; padding-top: 10px; border-top: 1px dashed #d8dbe0; }
              .extra-fields .field-label { font-weight: bold; margin-bottom: 2px; font-size: 11px; }
              .extra-fields .field-text { font-size: 11px; margin-bottom: 8px; white-space: pre-wrap; }
              .footer-box { border: 1px solid #e6e8ec; margin-top: 24px; }
              .footer-box td { padding: 12px 16px; font-size: 10px; vertical-align: top; }
              .footer-label { font-weight: bold; margin-bottom: 4px; }
            </style>
            </head>
            <body>
              <div class="doc">
                <div class="title">INVOICE</div>

                <table class="meta-outer"><tr>
                  <td style="width:60%%; padding-right:16px;">
                    <table class="meta-box"><tr>
                      <td style="width:60%%; vertical-align:top;">
                        <table class="meta-line">
                          <tr><td class="label">Invoice no.</td><td>%s</td></tr>
                          <tr><td class="label">Client no.</td><td>%s</td></tr>
                          <tr><td class="label">Invoice date</td><td>%s</td></tr>
                          <tr><td class="label">Payment terms</td><td>%s</td></tr>
                          <tr><td class="label">Payment due</td><td>%s</td></tr>
                        </table>
                        <div class="note">Interest will be charged on overdue payments</div>
                      </td>
                      <td style="width:40%%; vertical-align:top; padding-left:16px;">
                        <div class="ref-label">Your reference</div>
                        <div>%s</div>
                        <div class="ref-label" style="margin-top:10px;">Our reference</div>
                        <div>%s</div>
                      </td>
                    </tr></table>
                  </td>
                  <td style="width:40%%; vertical-align:top;">
                    <table class="addr-box"><tr><td>
                      <div class="addr-header">Bill to</div>
                      <div class="addr-body"><strong>%s</strong></div>
                    </td></tr></table>
                  </td>
                </tr></table>

                <table class="items">
                  <tr>
                    <th>Product / Service</th>
                    <th class="num">Quantity</th>
                    <th class="num">Price per unit</th>
                    <th class="num">Total</th>
                  </tr>
                  %s
                </table>

                <table class="totals">
                  <tr><td>Net:</td><td class="val">%s kr</td></tr>
                  <tr><td>VAT %d%% (calculated on %s kr):</td><td class="val">%s kr</td></tr>
                  <tr><td>Rounding:</td><td class="val">%s kr</td></tr>
                  <tr class="grand"><td>Total Due:</td><td class="val">%s kr</td></tr>
                </table>

                %s

                <table class="footer-box"><tr>
                  <td style="width:50%%;">
                    <div class="footer-label">Address</div>
                    <div>%s</div>
                  </td>
                  <td style="width:50%%;">
                    <div class="footer-label">Company Email</div>
                    <div>%s</div>
                    %s
                  </td>
                </tr></table>
              </div>
            </body>
            </html>
            """.formatted(
                escape(invoice.getInvoiceNumber()),
                escape(String.valueOf(invoice.getClientId())),
                escape(String.valueOf(invoice.getInvoiceDate())),
                escape(paymentTerms),
                escape(String.valueOf(invoice.getDueDate())),
                escape(invoice.getYourReference()),
                escape(invoice.getOurReference()),
                escape(invoice.getClientName()),
                itemsHtml,
                money(subtotal),
                vatRate,
                money(subtotal),
                money(tax),
                money(rounding),
                money(total),
                extraFieldsHtml,
                escape(companyAddress),
                escape(companyEmail),
                approvedForFTax ? "<div style=\"margin-top:4px;\">Approved for F-tax</div>" : ""
        );
    }

    private String buildItemsRows(List<InvoiceItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return "<tr><td></td><td class=\"num\">1.00</td><td class=\"num\">0.00</td><td class=\"num\">0.00</td></tr>";
        }
        StringBuilder sb = new StringBuilder();
        for (InvoiceItemDTO item : items) {
            sb.append("""
                <tr>
                  <td>%s</td>
                  <td class="num">%s</td>
                  <td class="num">%s</td>
                  <td class="num">%s</td>
                </tr>
                """.formatted(
                    escape(item.getDescription()),
                    money(nz(item.getQuantity())),
                    money(nz(item.getUnitPrice())),
                    money(nz(item.getLineTotal()))
            ));
        }
        return sb.toString();
    }

    // "More options" extra fields + tax deduction — mirrors what
    // ViewInvoice.jsx now renders, so the PDF matches the on-screen view
    // instead of silently omitting this data (it previously had nowhere
    // to come from, since InvoiceDTO didn't carry it at all).
    private String buildExtraFieldsHtml(InvoiceDTO invoice) {
        List<InvoiceExtraFieldDTO> extraFields = invoice.getExtraFields();
        boolean hasExtraFields = extraFields != null && !extraFields.isEmpty();
        boolean hasTaxDeduction = Boolean.TRUE.equals(invoice.getTaxDeductionApplied());

        if (!hasExtraFields && !hasTaxDeduction) {
            return "";
        }

        StringBuilder sb = new StringBuilder("<div class=\"extra-fields\">");
        if (hasExtraFields) {
            for (InvoiceExtraFieldDTO field : extraFields) {
                String label = EXTRA_FIELD_LABELS.getOrDefault(field.getKey(), field.getKey());
                sb.append("<div class=\"field-label\">%s</div><div class=\"field-text\">%s</div>"
                        .formatted(escape(label), escape(field.getText())));
            }
        }
        if (hasTaxDeduction) {
            sb.append("<div class=\"field-label\">Preliminary tax deduction</div><div class=\"field-text\">%d%%</div>"
                    .formatted(invoice.getTaxDeductionPercent() == null ? 0 : invoice.getTaxDeductionPercent()));
        }
        sb.append("</div>");
        return sb.toString();
    }

    private int firstTaxPercent(List<InvoiceItemDTO> items) {
        if (items == null || items.isEmpty() || items.get(0).getTaxPercent() == null) return 25;
        return items.get(0).getTaxPercent().intValue();
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String money(BigDecimal v) {
        return String.format(Locale.US, "%,.2f", v);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}