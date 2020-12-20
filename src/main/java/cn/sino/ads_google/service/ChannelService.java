package cn.sino.ads_google.service;

import cn.sino.ads_google.entity.GoogleAccountEntity;
import cn.sino.ads_google.entity.facebook.AdaccountsEntity;
import cn.sino.ads_google.mapper.GoogleAccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v6.services.GoogleAdsRow;
import com.google.ads.googleads.v6.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v6.services.GoogleAdsVersion;
import com.google.ads.googleads.v6.services.SearchGoogleAdsRequest;
import io.swagger.models.properties.FileProperty;
import lombok.extern.slf4j.Slf4j;
import cn.sino.ads_google.mapper.facebook.AdaccountsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class ChannelService {
    @Resource
    GoogleAccountMapper googleAccountMapper;

    private Properties getProperties(String type){
        Properties properties = new Properties();
        if(type.equals("psp")){
            properties.setProperty("api.googleads.clientId","134029198794-f0vaeiihmuquieo610ks41p7rh0r0fi7.apps.googleusercontent.com");
            properties.setProperty("api.googleads.clientSecret","P1ke_GLkiXq3KkIq4ay_HASr");
            properties.setProperty("api.googleads.refreshToken","1/HyG7aF3o7UbNPdqsw3LMQVbWQqMzt8FIV4bfgcfrIic");
            properties.setProperty("api.googleads.developerToken","Rw85yleL4r5CzsMccat8MQ");
            properties.setProperty("api.googleads.loginCustomerId","7802171637");
        }
        if(type.equals("dvip")){
            properties.setProperty("api.googleads.clientId","418380844199-flmvbn3g7s9plmejr2mpnoaik7ftfbrq.apps.googleusercontent.com");
            properties.setProperty("api.googleads.clientSecret","kizyEbHS2OkR3X90if2ImOf8");
            properties.setProperty("api.googleads.refreshToken","1/TtdS3qwJ4LYkKSX2JdRpVi3VopUEdVrndRdUg_U3DXM");
            properties.setProperty("api.googleads.developerToken","OpcZApYOvxfUKpzJaPLjVQ");
            properties.setProperty("api.googleads.loginCustomerId","1122880238");
        }
        return properties;
    }
    public GoogleAdsVersion getGoogleAdsClientVersion(String type){
        return GoogleAdsClient.newBuilder().fromProperties(getProperties(type)).build().getLatestVersion();
    }
    public void getAdAccountDetail(String accountNo,String type){
        accountNo = accountNo.replace("-","");
        GoogleAdsVersion googleAdsClientVersion = getGoogleAdsClientVersion(type);

    }
    public void getAdAccountList() throws IOException {
        GoogleAdsClient googleAdsClient = null;

        QueryWrapper<GoogleAccountEntity> qw = new QueryWrapper<>();
        //qw.eq("account_no","752-443-5368");
        List<GoogleAccountEntity> googleAccountEntityList = googleAccountMapper.selectList(qw);

        for(GoogleAccountEntity googleAccountEntity : googleAccountEntityList){
            System.out.println(googleAccountEntity);
            Properties properties = getProperties(googleAccountEntity.getType());
            googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(properties).build();
            String query = "SELECT metrics.cost_micros FROM customer WHERE segments.date >= '2020-11-19' AND segments.date <= '2020-12-19'";
            String accountNo = googleAccountEntity.getAccountNo().replace("-","");
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder().setCustomerId(accountNo).setQuery(query).build();
            GoogleAdsServiceClient googleAdsService = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
            try{
                GoogleAdsServiceClient.SearchPagedResponse response = googleAdsService.search(request);
                for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                    Double micros = ((Long)googleAdsRow.getMetrics().getCostMicros()).doubleValue();
                    micros /= 1000000;
                    googleAccountEntity.setSpend(micros);
                }
            }catch(Exception e){
                googleAccountEntity.setSpendLog(getExceptionDetail(e));
            }
            googleAccountMapper.updateById(googleAccountEntity);

        }



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

    private String getExceptionDetail(Exception ex) {
        String ret = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            ex.printStackTrace(pout);
            ret = new String(out.toByteArray());
            pout.close();
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }
}
