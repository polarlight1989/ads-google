package cn.sino.ads_google.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChannelService.class)
public class ChannelServiceTest {
    @Autowired
    ChannelService channelService;
    @Test
    public void getAdAccountListTest() throws IOException {
        channelService.getAdAccountList();
    }
}
