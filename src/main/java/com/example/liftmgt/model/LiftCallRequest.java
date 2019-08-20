package com.example.liftmgt.model;

import org.apache.commons.lang3.RandomUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiftCallRequest implements Comparable<LiftCallRequest>{
	public static final Integer topFloor = 10;
	private LiftStatus direction;
	private Integer floorNumber;
	
	public static LiftCallRequest buildRandomDownRequest(Integer fromFloor){
		if(fromFloor == null){
			// top most floor
			fromFloor = topFloor;
		}
		return new LiftCallRequest(LiftStatus.GOING_DOWN, RandomUtils.nextInt(0,fromFloor+1));
	}
	
	public static LiftCallRequest buildRandomUpRequest(Integer fromFloor){
		if(fromFloor == null){
			// top most floor
			fromFloor = 0;
		}
		return new LiftCallRequest(LiftStatus.GOING_UP, RandomUtils.nextInt(fromFloor+1, topFloor));
	}

	@Override
	public int compareTo(LiftCallRequest obj) {
		if(obj == null || obj.getFloorNumber() == null){
			return 1;
		} else if (this.getFloorNumber() == null){
			return -1;
		}
		return this.getFloorNumber().compareTo(obj.getFloorNumber());
	}
	
}
