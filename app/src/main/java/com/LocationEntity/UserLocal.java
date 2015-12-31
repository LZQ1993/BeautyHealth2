package com.LocationEntity;

import com.infrastructure.CWDomain.EntityBase;

public class UserLocal extends EntityBase{

	public String CityName;
	public String Tel;

	public String getCityName() {
		return CityName;
	}
	public void setCityName(String cityName) {
		CityName = cityName;
	}
	public String getTel() {
		return Tel;
	}
	public void setTel(String tel) {
		Tel = tel;
	}
	
	
}
