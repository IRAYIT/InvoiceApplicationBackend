package com.ikonicit.invoice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSettingsDTO {

    private String invoiceDeliveryMethod = "email";
    private Boolean emailAttachPdf = false;
}
