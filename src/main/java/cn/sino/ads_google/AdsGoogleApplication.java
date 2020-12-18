package cn.sino.ads_google;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.sino.ads_google.mapper.facebook")
public class AdsGoogleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdsGoogleApplication.class, args);
	}

}
