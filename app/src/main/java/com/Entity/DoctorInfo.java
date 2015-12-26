package com.Entity;

import java.io.Serializable;

public class DoctorInfo implements Serializable{
    public String HospitalID;
	public String DoctorID;
	public String DoctorName;
	public String DoctorGender;//性别
	public String DoctorTel;
	public String DoctorDescription;
	public String DoctorImgUrl;
	public String ClassName;//科室
	public String MainCute; //主治，擅长
	public String Size;
	public String getDoctorID() {
		return DoctorID;
	}
	public void setDoctorID(String doctorID) {
		DoctorID = doctorID;
	}
	public String getDoctorName() {
		return DoctorName;
	}
	public void setDoctorName(String doctorName) {
		DoctorName = doctorName;
	}
	public String getDoctorGender() {
		return DoctorGender;
	}
	public void setDoctorGender(String doctorGender) {
		DoctorGender = doctorGender;
	}
	public String getDoctorTel() {
		return DoctorTel;
	}
	public void setDoctorTel(String doctorTel) {
		DoctorTel = doctorTel;
	}
	public String getDoctorDescription() {
		return DoctorDescription;
	}
	public void setDoctorDescription(String doctorDescription) {
		DoctorDescription = doctorDescription;
	}
	public String getDoctorImgUrl() {
		return DoctorImgUrl;
	}
	public void setDoctorImgUrl(String doctorImgUrl) {
		DoctorImgUrl = doctorImgUrl;
	}
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	
	

	
}
