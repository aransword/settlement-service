package dev.fisa.client;

import dev.fisa.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankClientService {

    private final RestClient restClient;

    public List<TransactionDto> getTransactionsByDate(LocalDate date) {
        return restClient.get()
                // Eureka 등록 명인 "bank-service" 사용
                .uri("http://bank-service/api/transactions?date={date}", date.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransactionDto>>() {});
    }
}