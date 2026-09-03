package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.InvoiceDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;

/**
 * Sends the "Show the invoice" summary email shown in the reference design.
 *
 * NOTE: the sender identity comes from app.mail.from-address, which is
 * currently pointed at a personal email for testing rather than the
 * invoice's own companyEmail field. Swap the property value (not this
 * class) once a company mailbox is ready — no code change needed.
 *
 * Works off InvoiceDTO since that's what InvoiceService exposes to
 * controllers — adjust the getters below if your DTO's field names differ.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvoiceEmail(InvoiceDTO invoice, String recipientEmail, String personalMessage, MultipartFile attachment) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        boolean hasAttachment = attachment != null && !attachment.isEmpty();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, hasAttachment, "UTF-8");

        helper.setFrom(fromAddress, fromName);
        helper.setTo(recipientEmail);
        helper.setSubject("Invoice no.: " + invoice.getInvoiceNumber());
        helper.setText(buildInvoiceEmailHtml(invoice, personalMessage), true);

        if (hasAttachment) {
            helper.addAttachment(attachment.getOriginalFilename(), attachment);
        }

        mailSender.send(mimeMessage);
    }

    private String buildInvoiceEmailHtml(InvoiceDTO invoice, String personalMessage) {
        String invoiceLink = frontendBaseUrl + "/invoices/" + invoice.getId();

        String messageBlock = "";
        if (personalMessage != null && !personalMessage.isBlank()) {
            messageBlock = """
                <table role="presentation" width="100%%" style="margin:24px 0;border:1px solid #e6e8ec;border-radius:6px;">
                  <tr>
                    <td style="padding:16px 20px;text-align:center;font-family:Arial,sans-serif;">
                      <div style="font-weight:700;font-size:13px;color:#222;margin-bottom:6px;">Message from: %s</div>
                      <div style="font-size:13.5px;color:#333;">%s</div>
                    </td>
                  </tr>
                </table>
                """.formatted(escape(fromName), escape(personalMessage));
        }

        String paymentTerms = invoice.getPaymentTerms() == null ? "Net 30" : invoice.getPaymentTerms();

        return """
            <div style="background:#f4f6f8;padding:32px 16px;font-family:Arial,sans-serif;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:8px;padding:36px 32px;text-align:center;">
                <h1 style="font-size:22px;color:#1f2933;margin:0 0 12px;">Summary of invoice</h1>
                <p style="font-size:14px;color:#444;margin:0 0 4px;">You have received an invoice from %s.</p>
                <p style="font-size:14px;color:#444;margin:0 0 24px;">Click the link below in order to see the invoice.</p>

                <a href="%s" style="display:inline-block;background:#2fa9a3;color:#ffffff;text-decoration:none;
                   font-weight:700;font-size:14px;padding:12px 28px;border-radius:4px;margin-bottom:8px;">
                  Show the invoice
                </a>

                %s

                <table role="presentation" width="100%%" style="margin-top:24px;border-collapse:collapse;text-align:left;">
                  <tr style="background:#f6f7f9;">
                    <td style="padding:10px 14px;font-size:13px;color:#444;">Invoice no.</td>
                    <td style="padding:10px 14px;font-size:13px;color:#222;text-align:right;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:10px 14px;font-size:13px;color:#444;">Payment terms</td>
                    <td style="padding:10px 14px;font-size:13px;color:#222;text-align:right;">%s</td>
                  </tr>
                  <tr style="background:#f6f7f9;">
                    <td style="padding:10px 14px;font-size:13px;color:#444;">Payment due</td>
                    <td style="padding:10px 14px;font-size:13px;color:#222;text-align:right;">%s</td>
                  </tr>
                </table>

                <div style="margin-top:32px;">
                  <div style="font-weight:800;font-size:15px;color:#37474f;">I-Ray</div>
                  <div style="font-size:12px;color:#8a8f96;margin-top:4px;">Simple and easy invoicing directly from the web.</div>
                  <div style="font-size:11px;color:#b0b4ba;margin-top:2px;">Copyright &copy; I-Ray, %d.</div>
                </div>
              </div>
            </div>
            """.formatted(
                escape(fromName),
                invoiceLink,
                messageBlock,
                escape(invoice.getInvoiceNumber()),
                escape(paymentTerms),
                escape(String.valueOf(invoice.getDueDate())),
                Year.now().getValue()
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}