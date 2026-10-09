package com.payops.backend.invoice;

import com.payops.backend.invoice.dto.InvoiceResponse;
import com.payops.backend.paypal.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvoiceMapper {

    public InvoiceResponse toInvoiceResponse(
            PayPalInvoiceSummary invoice
    ) {

        BigDecimal amount = getAmount(invoice);
        BigDecimal paidAmount = getPaidAmount(invoice);
        BigDecimal remainingAmount = getRemainingAmount(
                invoice,
                amount,
                paidAmount
        );

        String customerEmail = getCustomerEmail(invoice);
        String customerName = getCustomerName(invoice);

        return new InvoiceResponse(
                invoice.id(),
                invoice.detail() != null
                        ? invoice.detail().invoiceNumber()
                        : null,

                customerName,
                customerEmail,

                amount,
                paidAmount,
                remainingAmount,

                getCurrency(invoice),

                invoice.detail() != null
                        ? invoice.detail().invoiceDate()
                        : null,

                getDueDate(invoice),

                invoice.status()
        );
    }

    private BigDecimal getAmount(
            PayPalInvoiceSummary invoice
    ) {
        if (invoice.amount() == null
                || invoice.amount().value() == null) {
            return BigDecimal.ZERO;
        }

        return invoice.amount().value();
    }

    private BigDecimal getPaidAmount(
            PayPalInvoiceSummary invoice
    ) {
        if (invoice.payments() == null
                || invoice.payments().paidAmount() == null
                || invoice.payments().paidAmount().value() == null) {
            return BigDecimal.ZERO;
        }

        return invoice.payments()
                .paidAmount()
                .value();
    }

    private BigDecimal getRemainingAmount(
            PayPalInvoiceSummary invoice,
            BigDecimal amount,
            BigDecimal paidAmount
    ) {

        if (invoice.dueAmount() != null
                && invoice.dueAmount().value() != null) {
            return invoice.dueAmount().value();
        }

        return amount.subtract(paidAmount);
    }

    private String getCurrency(
            PayPalInvoiceSummary invoice
    ) {
        if (invoice.amount() != null
                && invoice.amount().currencyCode() != null) {
            return invoice.amount().currencyCode();
        }

        if (invoice.detail() != null) {
            return invoice.detail().currencyCode();
        }

        return null;
    }

    private String getCustomerEmail(
            PayPalInvoiceSummary invoice
    ) {

        PayPalBillingInfo billingInfo =
                getPrimaryBillingInfo(invoice);

        if (billingInfo == null) {
            return null;
        }

        return billingInfo.emailAddress();
    }

    private String getCustomerName(
            PayPalInvoiceSummary invoice
    ) {

        PayPalBillingInfo billingInfo =
                getPrimaryBillingInfo(invoice);

        if (billingInfo == null
                || billingInfo.name() == null) {
            return null;
        }

        PayPalName name = billingInfo.name();

        String givenName = name.givenName();
        String surname = name.surname();

        if (givenName == null && surname == null) {
            return null;
        }

        if (givenName == null) {
            return surname;
        }

        if (surname == null) {
            return givenName;
        }

        return givenName + " " + surname;
    }

    private PayPalBillingInfo getPrimaryBillingInfo(
            PayPalInvoiceSummary invoice
    ) {

        if (invoice.primaryRecipients() == null
                || invoice.primaryRecipients().isEmpty()) {
            return null;
        }

        PayPalRecipient recipient =
                invoice.primaryRecipients().get(0);

        if (recipient == null) {
            return null;
        }

        return recipient.billingInfo();
    }

    private java.time.LocalDate getDueDate(
            PayPalInvoiceSummary invoice
    ) {

        if (invoice.detail() == null
                || invoice.detail().paymentTerm() == null) {
            return null;
        }

        return invoice.detail()
                .paymentTerm()
                .dueDate();
    }
}