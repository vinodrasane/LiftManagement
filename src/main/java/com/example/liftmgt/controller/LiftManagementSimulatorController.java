package com.example.liftmgt.controller;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.liftmgt.model.LiftCallRequest;
import com.example.liftmgt.model.LiftStatus;
import com.example.liftmgt.model.TrafficEvent;
import com.example.liftmgt.service.LiftManagementService;
import com.example.liftmgt.service.LiftMasterThread;

@RestController
@RequestMapping(path = "/simulator", produces = "application/json")
public class LiftManagementSimulatorController {

	@Autowired
	private LiftManagementService service;
	Random random = new Random();

	@Autowired
	private LiftMasterThread masterThread;

	@PostMapping("/{event}")
	public ResponseEntity simulateTrafficEvent(@PathVariable TrafficEvent event) throws InterruptedException {
		switch (event) {
		case LUNCH:
			// going to canteen Ground floor for lunch
			IntStream.range(0, 1).forEach(a -> {sleep(3);masterThread.getCallRequests().offer(new LiftCallRequest(LiftStatus.GOING_DOWN, RandomUtils.nextInt(1, 10)));});
			// Eating
			Thread.sleep(20000);
			// Lunch finished
			IntStream.range(0, 1).forEach(a -> {sleep(3);masterThread.getCallRequests().offer(new LiftCallRequest(LiftStatus.GOING_UP, 0));});
			break;
		case OFFICE_START:
			IntStream.range(0, 1).forEach(a -> {sleep(3); masterThread.getCallRequests().offer(new LiftCallRequest(LiftStatus.GOING_UP, 0));}); break;
		case OFFICE_END:
			IntStream.range(0, 1).forEach(a -> {sleep(3);masterThread.getCallRequests().offer(new LiftCallRequest(LiftStatus.GOING_DOWN, RandomUtils.nextInt(1, 10)));}); break;
		default:
			System.out.println("Unrecognized event");
		
		}
		return ResponseEntity.accepted().build();
	}
	public static void sleep(int sec){
		try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
