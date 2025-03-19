package com.blackcow.blackcowgameinven.service.riot;

import com.blackcow.blackcowgameinven.dto.riot.AccountDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RiotApiService {

    private final WebClient webClient;

    public RiotApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://asia.api.riotgames.com/riot").build();
    }

    public String getRiotUUID(String username, String tag){
        AccountDTO params = webClient.get()
                .uri(String.format("/account/v1/accounts/by-riot-id/%s/%s",username, tag))
                .header("X-Riot-Token", "RGAPI-50f6071c-6c80-47c2-b2d1-212d50ace481")
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .block();
        if(params != null)
            return params.getPuuid();
        return null;
    }

}
