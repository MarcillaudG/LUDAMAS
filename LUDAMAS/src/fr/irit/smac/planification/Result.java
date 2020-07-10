package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Result {

	
	private float value;
	
	private int step;
	
	private List<String> dataChosen;
	
	public Result(int step, float value) {
		this.value = value;
		this.step = step;
		this.dataChosen = new ArrayList<String>();
	}
	
	public Result(int step, float value, Collection<? extends String> datas) {
		this.value = value;
		this.step = step;
		this.dataChosen = new ArrayList<String>(datas);
	}
	
	public float getValue() {
		return this.value;
	}
	
	public void addData(String data) {
		this.dataChosen.add(data);
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

	public List<String> getDataChosen() {
		return dataChosen;
	}

	@Override
	public String toString() {
		return "Result [value=" + value + ", step=" + step + ", dataChosen=" + dataChosen + "]";
	}

	
	
}
