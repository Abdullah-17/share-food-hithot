package com.hobom.mobile.model;

import java.util.Comparator;

public class ConsumeDataNewestDateComparator implements Comparator<Consume> {

	@Override
	public int compare(Consume lhs, Consume rhs) {
		// TODO Auto-generated method stub

		if (lhs.getDate() > rhs.getDate())
			return -1;
		else if (lhs.getDate() == rhs.getDate())
			return 0;
		else
			return 1;

	}

}
