package com.hobom.mobile.model;

public enum FoodType {

	CHINESE("�в�"),
	WEST("����");
	FoodType(String name){
		this.name = name;
	}
	private String name;
	public String getName(){
		return name;
	}
}
