package com.example.liftmgt.model;

import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.util.CollectionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Lift {
	private String code;
	private Integer usedCapacity = 0;
	private LiftStatus liftState;
	private Integer currentFloorNumber;
	private ConcurrentSkipListSet<LiftCallRequest> stopages;
	// Used to determine that after reaching the stoppage lift will mandatorily load passengers.
	private boolean travellingEmpty;

	public Lift(String code, ConcurrentSkipListSet<LiftCallRequest> floorStopageSequence) {
		this.code = code;
		liftState = LiftStatus.IDLE;
		currentFloorNumber = 0;
		this.stopages = floorStopageSequence;
	}

	public boolean isGoingDown() {
		return getLiftState() == LiftStatus.GOING_DOWN;
	}
	public boolean isGoingUp() {
		return getLiftState() == LiftStatus.GOING_UP;
	}
	public boolean isIdle() {
		return getLiftState() == LiftStatus.IDLE;
	}
	public boolean hasStopages(){
		return !CollectionUtils.isEmpty(stopages);
	}

}
