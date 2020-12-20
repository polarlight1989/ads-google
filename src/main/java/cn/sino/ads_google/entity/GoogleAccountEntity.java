package cn.sino.ads_google.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("google_account")
public class GoogleAccountEntity {
    private Integer id;
    private String AccountNo;
    private Date createTime;
    private Double spend;
    private String type;
    private String spendLog;

    private String currency;
    private Long impressions;
    private Long clicks;
    @JSONField(name="landing_page_views")
    private Long landingPageViews;
    @JSONField(name="add_to_cart")
    private Long addToCart;
    private Long purchase;
    private Double conversions;
    private Double revenue;
    @JSONField(name="purchase_value")
    private Double purchaseValue;

    private Double syncTime;

}
