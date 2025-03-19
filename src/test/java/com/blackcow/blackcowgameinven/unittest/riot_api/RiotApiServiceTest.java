package com.blackcow.blackcowgameinven.unittest.riot_api;

import com.blackcow.blackcowgameinven.service.riot.RiotApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RiotApiServiceTest {

    @Autowired
    private RiotApiService riotApiService;

    @Test
    void testGetRiotUUID() {
        // 테스트용 소환사명과 태그를 입력 (실제 존재하는 사용자로 테스트해야 성공)
        String username = "champion of leag";
        String tag = "KR1";

        String uuid = riotApiService.getRiotUUID(username, tag);
        System.out.println("Riot UUID: " + uuid);

        Assertions.assertNotNull(uuid);
    }
}
