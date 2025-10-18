package com.safechain.safechain;

import org.springframework.boot.SpringApplication;

public class TestSafechainApplication {

	public static void main(String[] args) {
		SpringApplication.from(SafechainApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
