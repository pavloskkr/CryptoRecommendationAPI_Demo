package com.xm.cryptorec.cryptorec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CryptorecApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptorecApplication.class, args);
	}

}
