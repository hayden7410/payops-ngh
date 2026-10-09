package com.payops.backend.paypal;

import com.payops.backend.invoice.dto.InvoiceResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paypal")
public class PayPalTestController {

    private final PayPalInvoiceService payPalInvoiceService;

    public PayPalTestController(
            PayPalInvoiceService payPalInvoiceService
    ) {
        this.payPalInvoiceService = payPalInvoiceService;
    }

    @GetMapping("/test-invoices")
    public List<InvoiceResponse> testInvoices() {
        return payPalInvoiceService.getInvoices();
    }

    @GetMapping("/test-invoice/{invoiceId}")
    public String testInvoiceDetail(
            @PathVariable String invoiceId
    ) {
        return payPalInvoiceService.getInvoiceDetail(invoiceId);
    }
}