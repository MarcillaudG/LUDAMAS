package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class PlanificationFunction {

	
	private int nbStep;
	
	private List<PLInput> plinputs;
	
	private List<List<String>> inputsByStep;
	
	public PlanificationFunction(int nbStep,List<String> inputs) {
		this.nbStep = nbStep;
		this.inputsByStep = new ArrayList<List<String>>();
		
		Random rand = new Random();
		this.plinputs = new ArrayList<PLInput>();
		for(int i =0; i < inputs.size();i++) {
			String operator = "PLUS";
			if(rand.nextInt(2) ==0) {
				operator = "MOINS";
			}
			this.plinputs.add(new PLInput(i,inputs.get(i),operator ));
		}
		
	}
	
	/**
	 * Compute the value
	 * @param affectation
	 * @return
	 */
	public float computeStep(int step, Map<String,Float> affectation) {
		float res = 0.0f;
		for(int i =0; i < this.plinputs.size();i++) {
			res += this.plinputs.get(i).compute(affectation.get(this.plinputs.get(i).input));
		}
		return res;
	}
	
	private class PLInput{
		
		private int id;
		
		private String input;
		
		private String operator;
		
		public PLInput(int id, String input, String operator) {
			this.id = id;
			this.input = input;
			this.operator = operator;
		}
		
		public float compute(float value) {
			if(this.operator.equals("PLUS")) {
				return value;
			}
			else {
				return -value;
			}
		}
	}
}
