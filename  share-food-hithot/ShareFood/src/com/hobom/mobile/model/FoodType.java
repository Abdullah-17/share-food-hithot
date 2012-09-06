package com.hobom.mobile.model;

public enum FoodType {

	CHINESE("ÖÐ²Í"),
	WEST("Î÷²Í");
	FoodType(String name){
		this.name = name;
	}
	private String name;
	public String getName(){
		return name;
	}
}
