package com.example.liftmgt.model;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("lift.management.configuration.lift")
public class LiftConfig {

	//@Value("${lift.management.configuration.building.floors}")
	private Integer floors=10;
	private Integer passengerCapacity;
	private Integer stoppingTime;
	private Integer accelerationTime;
	// Time required to travel between two floors in full speed
	private Integer travelTimePerFloor;

}
