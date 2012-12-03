package com.xixi.phonenumbermanager.entity;

public class Contact {
	private String name;
	private String phoneNum;
	
	private int smsCounter;
	private int callLogCounter;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append(name).append(",phoneNum=").append(phoneNum)
		.append(",smsCounter=").append(smsCounter+"").append(",callLogCounter=").append(callLogCounter);
		return sb.toString();
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCallLogCounter() {
		return callLogCounter;
	}
	public void setCallLogCounter(int callLogCounter) {
		this.callLogCounter = callLogCounter;
	}
	public int getSmsCounter() {
		return smsCounter;
	}
	public void setSmsCounter(int smsCounter) {
		this.smsCounter = smsCounter;
	}

}
