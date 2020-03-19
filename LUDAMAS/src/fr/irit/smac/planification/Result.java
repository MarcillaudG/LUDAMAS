package fr.irit.smac.planification;

import java.util.Map;

public class Result {

	
	private float value;
	
	private int step;
	
	public Result(int step, float value) {
		this.value = value;
		this.step = step;
	}
	
	public float getValue() {
		return this.value;
	}
	
	
	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Result [value=" + value + ", step=" + step + "]";
	}


	
	
}
