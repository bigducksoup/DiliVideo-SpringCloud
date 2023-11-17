package com.ducksoup.dilivideolive;

import cn.hutool.db.Db;
import com.ducksoup.dilivideolive.mainservices.LiveControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiliVideoLiveApplicationTests {


    @Autowired
    private LiveControlService controlService;

    @Test
    void contextLoads() {
        controlService.kickClient("1");
    }

}
