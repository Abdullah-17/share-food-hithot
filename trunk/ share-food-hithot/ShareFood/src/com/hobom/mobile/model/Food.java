package com.hobom.mobile.model;

import java.io.Serializable;

public class Food implements Serializable{

	/**
	 * 美食名称
	 */
	private String name;
	/**
	 * 美食类别
	 */
	private FoodType type;
	/**
	 * 价格
	 */
	private float price;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 纬度
	 */
	private int latitude;
	/**
	 * 经度
	 */
	private int longitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FoodType getType() {
		return type;
	}
	public void setType(FoodType type) {
		this.type = type;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getLatitude() {
		return latitude;
	}
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}
	public int getLongitude() {
		return longitude;
	}
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	
	
}
