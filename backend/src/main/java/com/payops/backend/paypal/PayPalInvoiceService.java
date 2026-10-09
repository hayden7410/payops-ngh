package com.payops.backend.paypal;

import com.payops.backend.invoice.InvoiceMapper;
import com.payops.backend.invoice.dto.InvoiceResponse;
import com.payops.backend.paypal.dto.PayPalAccessTokenResponse;
import com.payops.backend.paypal.dto.PayPalInvoiceListResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class PayPalInvoiceService {

    private final PayPalProperties payPalProperties;
    private final PayPalAuthService payPalAuthService;
    private final RestTemplate restTemplate;
    private final InvoiceMapper invoiceMapper;

    public PayPalInvoiceService(
            PayPalProperties payPalProperties,
            PayPalAuthService payPalAuthService,
            RestTemplate restTemplate,
            InvoiceMapper invoiceMapper
    ) {
        this.payPalProperties = payPalProperties;
        this.payPalAuthService = payPalAuthService;
        this.restTemplate = restTemplate;
        this.invoiceMapper = invoiceMapper;
    }

    public List<InvoiceResponse> getInvoices() {

        PayPalAccessTokenResponse tokenResponse =
                payPalAuthService.getAccessToken();

        String url =
                payPalProperties.getBaseUrl()
                        + "/v2/invoicing/invoices"
                        + "?page=1&page_size=20&total_required=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.accessToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        ResponseEntity<PayPalInvoiceListResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        PayPalInvoiceListResponse.class
                );

        PayPalInvoiceListResponse body = response.getBody();

        if (body == null || body.items() == null) {
            return Collections.emptyList();
        }

        return body.items()
                .stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    public String getInvoiceDetail(String invoiceId) {

    PayPalAccessTokenResponse tokenResponse =
            payPalAuthService.getAccessToken();

    String url =
            payPalProperties.getBaseUrl()
                    + "/v2/invoicing/invoices/"
                    + invoiceId;

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenResponse.accessToken());
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    HttpEntity<Void> request =
            new HttpEntity<>(headers);

    ResponseEntity<String> response =
            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

    return response.getBody();
}
}
