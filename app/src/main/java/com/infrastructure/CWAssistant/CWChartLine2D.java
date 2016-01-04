package com.infrastructure.CWAssistant;

import java.util.ArrayList;
import java.util.List;

/*
 * 
 * 画折线的类
 * 
 */
public class CWChartLine2D {
	public String LineTitle;  // 线的标题
	public String XUnit;      //时间字符
	public String YUnit;      //单位
	public List<CWChartPoint2D> ChartPoints;  //点集
	public CWChartLine2D(){
		LineTitle="";
		XUnit=YUnit="";
		ChartPoints=new ArrayList<CWChartPoint2D>();
	}
}
