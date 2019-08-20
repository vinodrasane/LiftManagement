package com.example.liftmgt.service;

import lombok.Getter;

import com.example.liftmgt.model.Lift;
import com.example.liftmgt.model.LiftCallRequest;


public class LiftThread implements Runnable{
	
	private LiftManagementServiceImpl liftService;
	
	@Getter
	private Lift lift;
	
	public LiftThread(Lift lift, LiftManagementServiceImpl liftService) {
		this.lift = lift;
		this.liftService = liftService;
	}
	
	@Override
	public void run() {
		while (true) {
			while (lift.getStopages().size() > 0) {
				liftService.start(lift);
				liftService.move(lift);
				LiftCallRequest stop = liftService.stop(lift);
				if (lift.isTravellingEmpty()) {
					liftService.loadPassengers(lift, stop.getDirection());
				} else {
					liftService.loadUnloadPassengers(lift, stop.getDirection());
				}
			}
			liftService.idle(lift);
		}
	}
	
}
