package com.hobom.mobile.model;

import java.util.Comparator;

public class ConsumeDataPriceComparator implements Comparator<Consume> {

	@Override
	public int compare(Consume lhs, Consume rhs) {
		// TODO Auto-generated method stub
		float diff = 0.5f;
		
		if(Math.abs(lhs.getFood().getPrice()- rhs.getFood().getPrice())<diff){
			return 0;
		}else if(lhs.getFood().getPrice()-rhs.getFood().getPrice()>diff){
			return 1;
		}else 
			return -1;
	}
}
