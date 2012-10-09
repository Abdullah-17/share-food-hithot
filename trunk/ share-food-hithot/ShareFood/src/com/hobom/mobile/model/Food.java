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
	private String type;
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
	private String telephone;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
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
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	
	
	
}
