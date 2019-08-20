package com.example.liftmgt.service;

import com.example.liftmgt.model.Lift;
import com.example.liftmgt.model.LiftCallRequest;
import com.example.liftmgt.model.LiftStatus;

public interface LiftManagementService {

	void start(Lift lift);

	void move(Lift lift);

	LiftCallRequest stop(Lift lift);

	void idle(Lift lift);

	void loadUnloadPassengers(Lift lift, LiftStatus direction);

	void loadPassengers(Lift lift, LiftStatus liftStatus);

	Integer addStopageAndDetermineDirection(Lift lift, Integer floorNumber);

	Integer addStopage(Lift lift, Integer floorNumber);


}
