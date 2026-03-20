package dev.fisa.domain.service;

import dev.fisa.domain.dto.AdjustRequest;
import dev.fisa.domain.dto.AdjustResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class BankAccountClientService {

    private final RestClient restClient;
    private static final String BANK_ACCOUNT_SERVICE_URL = "http://localhost:8085";

    public BankAccountClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<AdjustResponse> sendAdjustments(List<AdjustRequest> requests) {
        return restClient.post()
                .uri(BANK_ACCOUNT_SERVICE_URL + "/api/bok-accounts/update-balances")
                .body(requests)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}