package com.LocationEntity;

import java.io.Serializable;

public class RounteStepInfo implements Serializable{

	public String StepName;
	public float Distance;
	public int routeType;
	public RounteStepInfo(){
		StepName="";
		Distance=0;
		routeType=0;
	}


}
