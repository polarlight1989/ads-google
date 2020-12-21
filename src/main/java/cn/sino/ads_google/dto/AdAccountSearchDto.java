package cn.sino.ads_google.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdAccountSearchDto {
    private String token = "NmMyYTMyNjY4NzkzODUzN2I0NWEwZWYxZDM3ZWVmYTdkZDkyMGVkZGI0ZTQ0OGUzNzYyN2FiMWZiNjVjYWM0Yg==";
    @JSONField(name="date_start")
    private String dateStart;
    @JSONField(name="date_stop")
    private String dateStop;
    private String account;
    private String classification = "media";
    private List<String> accountIds = new ArrayList<>();
    public void addAccount(String accountId){
        accountIds.add(accountId);
    }
    public String getAccount(){
        return JSON.toJSONString(accountIds);
    }
}
