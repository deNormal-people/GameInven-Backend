package com.blackcow.blackcowgameinven.dto.riot;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountDTO {
    String puuid;
    String gameName;
    String tagLine;
}
