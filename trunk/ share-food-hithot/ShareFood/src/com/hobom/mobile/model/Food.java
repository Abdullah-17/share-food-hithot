package com.hobom.mobile.model;

public class Food {

	/**
	 * ��ʳ����
	 */
	private String name;
	/**
	 * ��ʳ���
	 */
	private FoodType type;
	/**
	 * �۸�
	 */
	private float price;
	/**
	 * ��ַ
	 */
	private String address;
	/**
	 * ͼƬ·��
	 */
	private String picPath;
	/**
	 * �ȼ�
	 */
	private int rating;
	/**
	 * ����
	 */
	private String remark;
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
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
