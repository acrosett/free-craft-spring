package com.freecraft.freecraftbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.fc.freecraftbackend.repo")
public class FreeCraftBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeCraftBackendApplication.class, args);
	}

}