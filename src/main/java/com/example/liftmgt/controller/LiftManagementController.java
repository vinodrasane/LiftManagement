package com.example.liftmgt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.liftmgt.service.LiftMasterThread;


@RestController
@RequestMapping(path = "/lift-system", produces = "application/json")
public class LiftManagementController {

	@Autowired
	private LiftMasterThread masterThread;

	@PostMapping("/start")
	public void startSystem() {
		new Thread(masterThread).start();
	}
}
