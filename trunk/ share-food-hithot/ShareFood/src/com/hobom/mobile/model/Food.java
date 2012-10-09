package com.hobom.mobile.model;

import java.io.Serializable;

public class Food implements Serializable{

	/**
	 * ��ʳ����
	 */
	private String name;
	/**
	 * ��ʳ���
	 */
	private String type;
	/**
	 * �۸�
	 */
	private float price;
	/**
	 * ��ַ
	 */
	private String address;
	/**
	 * γ��
	 */
	private int latitude;
	/**
	 * ����
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
