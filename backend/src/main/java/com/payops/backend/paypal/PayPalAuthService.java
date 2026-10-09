package com.payops.backend.paypal;

import com.payops.backend.paypal.dto.PayPalAccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PayPalAuthService {

    private final PayPalProperties payPalProperties;
    private final RestTemplate restTemplate;

    public PayPalAuthService(
            PayPalProperties payPalProperties,
            RestTemplate restTemplate
    ) {
        this.payPalProperties = payPalProperties;
        this.restTemplate = restTemplate;
    }

    public PayPalAccessTokenResponse getAccessToken() {

        String url =
                payPalProperties.getBaseUrl()
                        + "/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();

        String credentials =
                payPalProperties.getClientId()
                        + ":"
                        + payPalProperties.getClientSecret();

        String encodedCredentials =
                Base64.getEncoder().encodeToString(
                        credentials.getBytes(StandardCharsets.UTF_8)
                );

        headers.set(
                HttpHeaders.AUTHORIZATION,
                "Basic " + encodedCredentials
        );

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<PayPalAccessTokenResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        PayPalAccessTokenResponse.class
                );

        return response.getBody();
    }
}