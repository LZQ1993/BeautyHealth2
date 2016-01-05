package com.LocationEntity;

import java.util.ArrayList;
import java.util.List;

public class BusRouteStepInfo extends RounteStepInfo{

	public String BustLineName;
	public String EntranceDoorName;
	public String ExitDoorName;
	public List<RounteStepInfo> walkSteps;
	public BusRouteStepInfo(){
		routeType=1;
		BustLineName="";
		EntranceDoorName="";
		ExitDoorName="";
		walkSteps=new ArrayList<RounteStepInfo>();
	}
}
