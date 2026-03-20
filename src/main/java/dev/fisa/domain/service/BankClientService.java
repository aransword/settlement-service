package dev.fisa.domain.service;

import dev.fisa.domain.dto.TransactionDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class BankClientService {

    private final RestClient restClient;
    private static final String BANK_SERVICE_URL = "http://localhost:8081";

    public BankClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TransactionDto> getTransactionsByDate(LocalDate date) {
        return restClient.get()
                .uri(BANK_SERVICE_URL + "/api/transactions?date={date}", date.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}