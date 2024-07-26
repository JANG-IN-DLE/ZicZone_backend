package org.zerock.ziczone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZiczoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZiczoneApplication.class, args);
	}

}
