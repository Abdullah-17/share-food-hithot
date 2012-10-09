package com.hobom.mobile.model;

import java.util.Comparator;

public class ConsumeDataNameComparator implements Comparator<Consume>{

	@Override
	public int compare(Consume lhs, Consume rhs) {
		// TODO Auto-generated method stub
		return lhs.getFood().getName().compareTo(rhs.getFood().getName());
	}

}
