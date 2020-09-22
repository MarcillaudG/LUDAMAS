package fr.irit.smac.planification.tools;

import fr.irit.smac.core.Links;

public class TestStatic {

	private static int field1 = 10;
	
	
	public static void setField1(int a) {
		field1 = a;
	}
	
	public static int getField1() {
		return field1;
	}
	
	public static void main(String args[]) {
		//System.out.println(TestStatic.field1);
		//TestStatic.setField1(5);
		//System.out.println(TestStatic.field1);
		Links links = new Links();
	}
}
