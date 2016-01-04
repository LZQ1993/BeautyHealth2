package com.beautyhealthapp.SafeGuardianship.Assistant;

import java.util.ArrayList;
import java.util.List;



public class SafeActionLine {
	public String LineTitle;  // 线的标题
	public String XUnit;      //时间字符
	public String YUnit;      //单位
	public String TimeSpan;   //时长
	public List<SafeActionPoint> SafeActionPoint;  //点集
	public SafeActionLine(){
		TimeSpan="";
		LineTitle="";
		XUnit=YUnit="";
		SafeActionPoint=new ArrayList<SafeActionPoint>();
	}

}
