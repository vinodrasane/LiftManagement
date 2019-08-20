package com.example.liftmgt.model;

import java.util.List;

import lombok.Data;

@Data
public class Building {
	private Integer floors;
	private List<Lift> lifts;
}
