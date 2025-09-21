package com.dgbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DgBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(DgBankApplication.class, args);
		System.out.println("DG Bank Backend Started!");
	}
}
