package com.hobom.mobile.model;

import java.io.Serializable;

/**
 * ������
 * @author mingxunzh
 *
 */
public class Consume implements Serializable{

	private long date;//��������
	private Food food; //���ѵ���ʳ
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
	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public Food getFood() {
		return food;
	}
	public void setFood(Food food) {
		this.food = food;
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
