package com.example.liftmgt.model;

import lombok.Data;

@Data
public class AddPassegerRequest {
  private String liftCode;
  private Integer noOfPassengers;
}