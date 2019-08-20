package com.example.liftmgt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import lombok.Getter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.example.liftmgt.model.Lift;
import com.example.liftmgt.model.LiftCallRequest;
import com.example.liftmgt.model.LiftStatus;

@Component
@ConfigurationProperties("lift.management.configuration.building")
public class LiftMasterThread implements Runnable, InitializingBean {
	@Autowired
	LiftManagementServiceImpl liftService;
	// @Value("${lift.management.configuration.building.no-of-lifts}")
	private Integer noOfLifts = 1;
	// @Value("${lift.management.configuration.building.floors}")
	private Integer floors = 10;
	// Data structure to store all lift call request.. basically button pressed
	// on any floor.
	@Getter
	private BlockingQueue<LiftCallRequest> callRequests;
	// Maintaining all the lifts and their stoppage sequence queue.
	// TODO: Reduce the visibility to Master as only LiftName, status and
	// stopageSequence
	private List<LiftThread> lifts = new ArrayList<>();
	private Executor executor;

	@Override
	public void afterPropertiesSet() throws Exception {
		callRequests = new ArrayBlockingQueue<>(floors);
		executor = Executors.newFixedThreadPool(noOfLifts);
	}

	@Override
	public void run() {
		IntStream.range(0, noOfLifts).forEach(p -> startLift(p));
		while (true) {
			LiftCallRequest callRequest = callRequests.poll();
			if (callRequest != null) {
				System.out.println("Master: Someone Pressed lift button for " + callRequest.getDirection() + " at floor: "
						+ callRequest.getFloorNumber());
				assignCallRequestToLift(callRequest);
			}
		}
	}

	private void assignCallRequestToLift(LiftCallRequest callRequest) {
		LiftThread closestLift = null;
		Integer currentClosestLiftDistance = Integer.MAX_VALUE;
		for (LiftThread lift : lifts) {
			// FIXME: Need to cover a case where lift is close to the end but
			// its currently moving in opposite direction.
			if (isClosestLift(callRequest, currentClosestLiftDistance, lift)) {
				currentClosestLiftDistance = Math.abs(lift.getLift().getCurrentFloorNumber() - callRequest.getFloorNumber());
				closestLift = lift;
			}
		}
		if (closestLift == null) {
			System.out.println("Master: cannot handle this requirst All Lifts and moving in opposite direction");
			return;
		}
		moveLiftToPassengerFloor(callRequest.getFloorNumber(), closestLift);
	}

	private boolean isClosestLift(LiftCallRequest callRequest, Integer distanceToClosestLift, LiftThread lift) {
		return liftToSameDirectionOrIdlePresent(callRequest, lift)
				&& Math.abs(lift.getLift().getCurrentFloorNumber() - callRequest.getFloorNumber()) < distanceToClosestLift;
	}

	private boolean liftToSameDirectionOrIdlePresent(LiftCallRequest callRequest, LiftThread lift) {
		return callRequest.getDirection() == lift.getLift().getLiftState() || lift.getLift().getLiftState() == LiftStatus.IDLE;
	}

	private void moveLiftToPassengerFloor(Integer floorNumber, LiftThread closestLiftThread) {
		Lift closestLift = closestLiftThread.getLift();
		if (closestLift.isIdle()) {
			closestLift.setTravellingEmpty(true);
			liftService.addStopageAndDetermineDirection(closestLift, floorNumber);
		} else {
			liftService.addStopage(closestLift, floorNumber);
		}
	}

	private void startLift(int p) {
		ConcurrentSkipListSet<LiftCallRequest> stopages = new ConcurrentSkipListSet<LiftCallRequest>();
		Lift lift = new Lift("LiftCode" + p, stopages);
		LiftThread liftThread = new LiftThread(lift, liftService);
		lifts.add(liftThread);
		executor.execute(liftThread);
	}

}
