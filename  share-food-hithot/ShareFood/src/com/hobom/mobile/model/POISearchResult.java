package com.hobom.mobile.model;

import com.amap.mapapi.poisearch.PoiPagedResult;

public class POISearchResult {

	private String keyword;
	private int totalCount;
	private int currentPage;
	private PoiPagedResult poiPagedResult;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public PoiPagedResult getPoiPagedResult() {
		return poiPagedResult;
	}
	public void setPoiPagedResult(PoiPagedResult poiPagedResult) {
		this.poiPagedResult = poiPagedResult;
	}
	
}
