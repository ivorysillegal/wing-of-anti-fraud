package com.gduf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gduf.dao")
public class WingOfAntiFraudApplication {

	public static void main(String[] args) {
		SpringApplication.run(WingOfAntiFraudApplication.class, args);
	}

}
