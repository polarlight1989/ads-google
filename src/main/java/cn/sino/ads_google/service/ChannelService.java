package cn.sino.ads_google.service;

import cn.sino.ads_google.entity.facebook.AdaccountsEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v6.services.GoogleAdsRow;
import com.google.ads.googleads.v6.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v6.services.SearchGoogleAdsRequest;
import io.swagger.models.properties.FileProperty;
import lombok.extern.slf4j.Slf4j;
import cn.sino.ads_google.mapper.facebook.AdaccountsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class ChannelService {
    @Resource
    AdaccountsMapper adaccountsMapper;



    public void getAdAccountList() throws IOException {
        GoogleAdsClient googleAdsClient = null;

        Properties properties = new Properties();
        properties.setProperty("api.googleads.clientId","134029198794-f0vaeiihmuquieo610ks41p7rh0r0fi7.apps.googleusercontent.com");
        properties.setProperty("api.googleads.clientSecret","P1ke_GLkiXq3KkIq4ay_HASr");
        properties.setProperty("api.googleads.refreshToken","1/HyG7aF3o7UbNPdqsw3LMQVbWQqMzt8FIV4bfgcfrIic");
        properties.setProperty("api.googleads.developerToken","Rw85yleL4r5CzsMccat8MQ");
        properties.setProperty("api.googleads.loginCustomerId","7802171637");
        googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(properties).build();

        String query = "SELECT metrics.cost_micros FROM customer" ;
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder().setCustomerId("7524435368").setQuery(query).build();
        GoogleAdsServiceClient googleAdsService = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
        GoogleAdsServiceClient.SearchPagedResponse response = googleAdsService.search(request);
        for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
            System.out.println(googleAdsRow);
        }
        log.info(String.valueOf(response));
//        log.info("getAdAccountList");
//        QueryWrapper<AdaccountsEntity> qw = new QueryWrapper<>();
//        qw.eq("channelid",3).last("limit 100");
//        List<AdaccountsEntity> list = adaccountsMapper.selectList(qw);
//        log.info("list count:" + list.size());
//
//        Properties properties = new Properties();
//        properties.setProperty("clientId","134029198794-f0vaeiihmuquieo610ks41p7rh0r0fi7.apps.googleusercontent.com");
//        properties.setProperty("clientSecret","P1ke_GLkiXq3KkIq4ay_HASr");
//        properties.setProperty("refreshToken","1/HyG7aF3o7UbNPdqsw3LMQVbWQqMzt8FIV4bfgcfrIic");
//        properties.setProperty("developerToken","Rw85yleL4r5CzsMccat8MQ");
//        properties.setProperty("loginCustomerId","7802171637");
//
//        log.info(String.valueOf(properties));
//        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(properties).build();
//
//        String query = "SELECT metrics.cost_micros FROM customer" ;
//
//        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder().setCustomerId("752-443-5368").setQuery(query).build();
//
//        GoogleAdsServiceClient googleAdsService = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
//        GoogleAdsServiceClient.SearchPagedResponse response = googleAdsService.search(request);
//        log.info("success");
//        log.info(String.valueOf(response));
        //googleAdsClient.getLatestVersion().createAccountBudgetServiceClient()
    }
}
