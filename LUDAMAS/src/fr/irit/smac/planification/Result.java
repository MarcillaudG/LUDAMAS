package fr.irit.smac.planification;

import java.util.Map;

public class Result {

	private Map<String,Data> dataUsed;
	
	private float value;
	
	public Result(float value, Map<String,Data> dataUsed) {
		this.value = value;
		this.dataUsed = dataUsed;
	}
	
	public float getValue() {
		return this.value;
	}
	
	public Data getDataUsedForInput(String input) {
		return this.dataUsed.get(input);
	}
}
