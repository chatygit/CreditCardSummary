package com.chaty.credit.repository;

public class Test {

	public static void main(String[] args) {

		String name = "mounika"; // m-0 a-6 ( index)

		System.out.println(name.length());
		
		for (int i = name.length() - 1; i >= 0; i--) {
			System.out.print(name.charAt(i));
		}

	}

}
