package cn.sino.ads_google.service;


import cn.sino.ads_google.entity.GoogleAccountEntity;
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
//    @Test
//    public void getAdAccountListTest() throws IOException {
//        channelService.getAdAccountList();
//    }
//
//    @Test
//    public void getAdAccountDetailTest() throws IOException {
//        channelService.getAdAccountDetail("981-482-9419","dvip");
//    }

//    @Test
//    public void updateAdAccountListTest(){
//        channelService.updateAdAccountList();
//    }

    @Test
    public void getAdListTest(){
        GoogleAccountEntity googleAccountEntity = new GoogleAccountEntity();
        googleAccountEntity.setAccountId("815-637-0405");
        googleAccountEntity.setType("psp");
        channelService.getAdList(googleAccountEntity);
    }
}
