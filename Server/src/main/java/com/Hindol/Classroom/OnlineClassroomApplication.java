package com.Hindol.Classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnlineClassroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineClassroomApplication.class, args);
	}

}
