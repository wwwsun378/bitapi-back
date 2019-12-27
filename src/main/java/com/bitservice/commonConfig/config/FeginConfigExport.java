package com.bitservice.commonConfig.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeginConfigExport {
	@Bean
	public Retryer feginRetryer() {
		Retryer retryer = new Retryer.Default(100,
				TimeUnit.SECONDS.toMillis(100), 1);
		return retryer;
	}
//	@Bean
//	public ResponseEntityDecoder feignDecoder() {
//		HttpMessageConverter fastJsonConverter = WebAppConfigurer.createFastJsonConverter();
//		System.err.println(">>>>>>>>>>>>>"+fastJsonConverter.toString());
//		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(fastJsonConverter);
//		return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
//	}
//
//	@Bean
//	public SpringEncoder feignEncoder(){
//		HttpMessageConverter fastJsonConverter = WebAppConfigurer.createFastJsonConverter();
//		System.err.println(">>>>>11111111111>"+fastJsonConverter.toString());
//		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(fastJsonConverter);
//		return new SpringEncoder(objectFactory);
//	}

}
