package com.example.liftmgt.service;

import java.util.Random;

import lombok.Getter;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.liftmgt.model.Lift;
import com.example.liftmgt.model.LiftCallRequest;
import com.example.liftmgt.model.LiftConfig;
import com.example.liftmgt.model.LiftStatus;

/*
 * Class to manage all lift related operations.
 */
@Service
public class LiftManagementServiceImpl implements LiftManagementService {

	@Autowired
	@Getter
	private LiftConfig liftConfig;
	private Random random = new Random();

	@Override
	public void start(Lift lift) {
		System.out.println(lift.getCode() + ":.. accelerating..");
		sleep(liftConfig.getAccelerationTime());
	}

	@Override
	public void move(Lift lift) {
		if (lift.hasStopages()) {
			Integer nextStop = getNextStop(lift);
			while (lift.getCurrentFloorNumber() != nextStop) {
				sleep(liftConfig.getTravelTimePerFloor());
				System.out.println(lift.getCode() + ": current Floor:" + lift.getCurrentFloorNumber() + " going towards:" + nextStop);
				moveByOneFloor(lift);
				// Master thread may add new stoppage in hence check for every iteration.
				nextStop = getNextStop(lift);
			}
		} else {
			System.out.println("Why should i move when I dont have any stoppages?? please check..");
		}
	}

	private void moveByOneFloor(Lift lift) {
		if(lift.getLiftState() == LiftStatus.GOING_UP)
			lift.setCurrentFloorNumber(lift.getCurrentFloorNumber()+1);
		else 
			lift.setCurrentFloorNumber(lift.getCurrentFloorNumber()-1);
	}

	private void sleep(Integer seconds) {
		try {
			if (seconds != null) {
				Thread.sleep(seconds * 1000);
			}
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	// Responsible to get the next Stop in the journey of the lift.
	private Integer getNextStop(Lift lift) {
		LiftCallRequest closestFloor= lift.getStopages().ceiling(new LiftCallRequest(lift.getLiftState(), lift.getCurrentFloorNumber()));
		Integer next = null;
		// Added extra condition to check 0 - 4 (currentFl) - 7 condition, lift should first pick up from 0 and then come to 7.
		if (lift.isGoingDown()) {
			closestFloor= lift.getStopages().floor(new LiftCallRequest(lift.getLiftState(), lift.getCurrentFloorNumber()));
		}
		if(closestFloor != null){
			next = closestFloor.getFloorNumber();
		}
		// Might need to update the direction .. mostly when lift is idle.
		determineDirection(lift, next);
		return next;
	}
	
	@Override
	public LiftCallRequest stop(Lift lift) {
		System.out.print(lift.getCode() + ": stopping on Floor:" + lift.getCurrentFloorNumber());
		sleep(liftConfig.getStoppingTime());
		System.out.println(" .."+lift.getCode() + ": stopped");
		if(lift.isGoingDown()){
			return lift.getStopages().pollLast();	
		} else {
			return lift.getStopages().pollFirst();	
		}
		
	}

	@Override
	public void idle(Lift lift) {
		lift.setLiftState(LiftStatus.IDLE);
		System.out.println(lift.getCode() + ":idle now on:"+lift.getCurrentFloorNumber());
		sleep(5);
	}

	//Only Unload or Unload+Load
	@Override
	public void loadUnloadPassengers(Lift lift, LiftStatus direction) {
		if (random.nextBoolean() || random.nextBoolean() || random.nextBoolean()) {
			// Ensuring 75% chance of unloading always.
			unloadPassengers(lift);
		} else {
			// Ensuring 25% chance of UnLoading and then Loading
			unloadPassengers(lift);
			loadPassengers(lift, direction);
		}
		//System.out.println("Lift:" + lift.getCode() + " current used capacity:" + lift.getUsedCapacity());
	}

	
	@Override
	public void loadPassengers(Lift lift, LiftStatus liftStatus) {
		// TODO: LOADing can be based on Used Capacity if Lift is already full do not add new stoppage. 
		//Integer capacity = lift.getUsedCapacity() + random.nextInt(5);
		//lift.setUsedCapacity(capacity > liftConfig.getPassengerCapacity() ? 0 : liftConfig.getPassengerCapacity());
		//System.out.println(lift.getCode()+"Loading passengers: used capacity:"+lift.getUsedCapacity());
		System.out.println(lift.getCode() + ": loading passenger");
		Integer newStopage = addStopage(lift, liftStatus);
		if(newStopage >= 0){
			System.out.println(lift.getCode()+": added new stoppage:"+newStopage);
		}
		lift.setTravellingEmpty(false);
	}
	
	private void unloadPassengers(Lift lift) {
		//if (lift.getUsedCapacity() != null && lift.getUsedCapacity() > 0) {
			// unload
			System.out.println(lift.getCode() + ": unloading passengers");
		//	Integer capacity = lift.getUsedCapacity() - random.nextInt(5);
		//	lift.setUsedCapacity(capacity < 0 ? 0 : capacity);
		//}
	}

	private LiftStatus determineDirection(Lift lift, Integer floorNumber) {
		LiftStatus direction = null;
		if (lift.getCurrentFloorNumber() < floorNumber) {
			direction = LiftStatus.GOING_UP;
		} else if(lift.getCurrentFloorNumber() > floorNumber){
			direction = LiftStatus.GOING_DOWN;
		}
		if(direction != null){
			lift.setLiftState(direction);
		}
		return lift.getLiftState();
	}
	
	@Override
	public Integer addStopageAndDetermineDirection(Lift lift, Integer floorNumber) {
		lift.getStopages().add(new LiftCallRequest(determineDirection(lift, floorNumber), floorNumber));
		return floorNumber;
	}
	
	@Override
	public Integer addStopage(Lift lift, Integer floorNumber) {
		lift.getStopages().add(new LiftCallRequest(lift.getLiftState(), floorNumber));
		return floorNumber;
	}
	
	private Integer addStopage(Lift lift, LiftStatus direction) {
		Integer stopage = null;
		if (direction == LiftStatus.GOING_DOWN && lift.getCurrentFloorNumber() > 0) {
			stopage = RandomUtils.nextInt(0, lift.getCurrentFloorNumber() - 1);
			lift.getStopages().add(new LiftCallRequest(LiftStatus.GOING_DOWN, stopage));
		} else if (lift.getCurrentFloorNumber() < liftConfig.getFloors()) {
			//IDLE OR UP 
			stopage = RandomUtils.nextInt(lift.getCurrentFloorNumber() + 1, liftConfig.getFloors());
			lift.getStopages().add(new LiftCallRequest(LiftStatus.GOING_UP, stopage));
		}
		return stopage == null ? -1 : stopage;
	}
}
