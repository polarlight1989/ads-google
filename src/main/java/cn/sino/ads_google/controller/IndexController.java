package cn.sino.ads_google.controller;

import cn.sino.ads_google.entity.GoogleAccountEntity;
import cn.sino.ads_google.mapper.GoogleAccountMapper;
import cn.sino.ads_google.service.ChannelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@Slf4j
public class IndexController {
    @Autowired
    ChannelService channelService;
    @Resource
    GoogleAccountMapper googleAccountMapper;
    @RequestMapping("/adList/{id}")
    public void index(@PathVariable(name = "id") String id) throws IOException {
        System.out.println("id:"+id);
        QueryWrapper<GoogleAccountEntity> qw = new QueryWrapper<>();
        qw.eq("account_id",id);
        GoogleAccountEntity googleAccountEntity = googleAccountMapper.selectOne(qw);
        channelService.getAdList(googleAccountEntity);
    }
}
