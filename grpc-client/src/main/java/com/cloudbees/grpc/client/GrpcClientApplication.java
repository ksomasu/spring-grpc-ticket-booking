package com.cloudbees.grpc.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cloudbees.grpc.client.service.TicketBookingGrpcClientService;

@SpringBootApplication
public class GrpcClientApplication implements CommandLineRunner {

	private final TicketBookingGrpcClientService postGrpcClientService;

	public GrpcClientApplication(TicketBookingGrpcClientService postGrpcClientService) {
		this.postGrpcClientService = postGrpcClientService;
	}

	public static void main(String[] args) {
		SpringApplication.run(GrpcClientApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//create user object and pass as user parameter. Just for testing purpose. All methods are implemented in service layer
		
		postGrpcClientService.createBooking("clientTest@gmail.com", "London", "France", 20);

		postGrpcClientService.getBookingsBySection("A");
		postGrpcClientService.getBookingsBySection("B");
	}
}
