package cn.sino.ads_google.service;

import cn.sino.ads_google.dto.AdAccountSearchDto;
import cn.sino.ads_google.entity.GoogleAccountEntity;
import cn.sino.ads_google.entity.facebook.AdaccountsEntity;
import cn.sino.ads_google.mapper.GoogleAccountMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public void updateAdAccountList(){
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        String endDate = sdf.format(calendar.getTime());

        calendar.add(calendar.DATE,-30);
        String startDate = sdf.format(calendar.getTime());
        log.info("startDate:"+startDate);
        log.info("endDate:"+endDate);
        QueryWrapper<GoogleAccountEntity> qw = new QueryWrapper<>();
        List<GoogleAccountEntity> googleAccountEntityList = googleAccountMapper.selectList(qw);
        log.info("googleAccountEntityList.size:"+String.valueOf(googleAccountEntityList.size()));
        Integer splitSize = 500;
        Map<String,GoogleAccountEntity> requestGoogleEntityMap = new HashMap<>();
        Integer ind = 0;
        AdAccountSearchDto adAccountSearchDto = new AdAccountSearchDto();
        for(GoogleAccountEntity googleAccountEntity : googleAccountEntityList){
            adAccountSearchDto.setDateStart(startDate);
            adAccountSearchDto.setDateStop(endDate);
            ind++;
            requestGoogleEntityMap.put(googleAccountEntity.getAccountId(),googleAccountEntity);
            adAccountSearchDto.addAccount(googleAccountEntity.getAccountId());
            if(requestGoogleEntityMap.size() >= splitSize || ind.equals(googleAccountEntityList.size())){
                log.info("JSON:"+JSON.toJSONString(adAccountSearchDto));
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://47.90.100.235/search";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(adAccountSearchDto), headers);
                ResponseEntity<JSONObject> response = restTemplate.postForEntity( url, request , JSONObject.class );
                log.info("response:"+response);
                JSONArray jsonArray = response.getBody().getJSONArray("data");
                for(Object o : jsonArray){
                    JSONObject object = JSONObject.parseObject(o.toString());
                    log.info(String.valueOf(object));
                    GoogleAccountEntity _googleAccountEntity = requestGoogleEntityMap.get(object.getString("adaccount_id"));
                    _googleAccountEntity.setCurrency(object.getString("currency"));
                    _googleAccountEntity.setImpressions(object.getLong("impressions"));
                    _googleAccountEntity.setClicks(object.getLong("clicks"));
                    _googleAccountEntity.setLandingPageViews(object.getLong("landing_page_views"));
                    _googleAccountEntity.setAddToCart(object.getLong("add_to_cart"));
                    _googleAccountEntity.setPurchase(object.getDouble("purchase"));
                    _googleAccountEntity.setConversions(object.getDouble("conversions"));
                    _googleAccountEntity.setSpend(object.getDouble("spend"));
                    _googleAccountEntity.setRevenue(object.getDouble("revenue"));
                    _googleAccountEntity.setPurchaseValue(object.getDouble("purchase_value"));
                    requestGoogleEntityMap.put(object.getString("adaccount_id"),_googleAccountEntity);
                    log.info("_googleAccountEntity:"+_googleAccountEntity.toString());
                }
                requestGoogleEntityMap.forEach((k,v)->{
                    v.setSyncTime(new Date());
                    googleAccountMapper.updateById(v);
                });
                adAccountSearchDto = new AdAccountSearchDto();
                requestGoogleEntityMap = new HashMap<>();
            }
        }
    }
//    public void post(){
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(adAccountSearchDto), headers);
//        ResponseEntity<JSONObject> response = restTemplate.postForEntity( url, request , JSONObject.class );
//    }
    public void getAdAccountDetail(String accountNo,String type){
        accountNo = accountNo.replace("-","");
        GoogleAdsVersion googleAdsClientVersion = getGoogleAdsClientVersion(type);
    }
    public void getAdList(GoogleAccountEntity googleAccountEntity){
        String accountNo = googleAccountEntity.getParseAccountId();

        GoogleAdsClient googleAdsClient = null;
        Properties properties = getProperties(googleAccountEntity.getType());
        System.out.println(properties);
        googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(properties).build();
        //String query = "SELECT customer.resource_name,customer.currency_code FROM customer  ";
        String query = "SELECT ad_group_ad.ad.id,ad_group_ad.ad.type,ad_group_ad.ad.final_urls FROM ad_group_ad   ";
        //String query = "SELECT metrics.cost_micros FROM customer WHERE segments.date >= '2020-11-19' AND segments.date <= '2020-12-19'";
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder().setCustomerId(accountNo).setQuery(query).build();
        GoogleAdsServiceClient googleAdsService = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();


        GoogleAdsServiceClient.SearchPagedResponse response = googleAdsService.search(request);
        for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
            log.info(String.valueOf(googleAdsRow));

        }
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
            String accountNo = googleAccountEntity.getAccountId().replace("-","");
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
