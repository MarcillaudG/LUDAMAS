package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.system.EnvironmentGeneral;

public class DecisionProcess {


	private Situation situation;

	private Effector effector;

	private ComposedFunction function;

	//private Environment env;

	private EnvironmentGeneral env;
	
	private List<String> proprio;

	private List<String> extero;

	private Map<String,Integer> valuesExtero;

	public DecisionProcess(Situation situ, Effector eff, EnvironmentGeneral env) {
		this.situation = situ;
		this.effector = eff;
		this.env = env;

		this.proprio = new ArrayList<>();
		this.extero = new ArrayList<>();
		this.valuesExtero = new TreeMap<>();
	}

	public Situation getSituation() {
		return situation;
	}

	public Effector getEffector() {
		return effector;
	}

	public ComposedFunction getFunction() {
		return function;
	}

	public void initComposedFunction(List<String> proprioceptive, List<String> exteroceptive, List<String> effectors) {
		List<String> input = new ArrayList<String>();
		for(int i=0; i < proprioceptive.size()+exteroceptive.size()+effectors.size();i++) {
			input.add("float");
		}
		input.add("float");
		List<String> outputs = new ArrayList<String>();
		outputs.add("float");
		this.function = new ComposedFunction(this.effector.getName()+":"+this.situation.getId(), input,  outputs, 3, 3) ;
		//this.function = new ComposedFunction(this.effector.getName()+":"+this.situation.getId(), proprioceptive.size()+exteroceptive.size()+1, 1, 3, 3);
		this.proprio.addAll(proprioceptive);
		this.extero.addAll(exteroceptive);
		
		for(int i = this.proprio.size(); i< this.extero.size()+this.proprio.size();i++) {
			this.valuesExtero.put(this.extero.get(i-this.proprio.size()), i);
		}
	}

	public float compute(List<String> extero,List<String> effectors, int nbStep) {
		int i = 0;
		for(; i < this.proprio.size();i++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(this.proprio.get(i)));
		}
		for(int j=0; j < this.extero.size();j++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(extero.get(j)));
			i++;
		}
		for(int j =0; j< effectors.size();j++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(effectors.get(j)));
			i++;
		}
		this.function.setInitInput(i, nbStep);
		this.function.compute();
		return (float) this.function.getOutput(0).getValue();
	}

	public float computeAgent(List<Float> extero,List<String> effectors, int nbStep) {
		int i = 0;
		for(; i < this.proprio.size();i++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(this.proprio.get(i)));
		}
		for(int j =0; j< effectors.size();j++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(effectors.get(j)));
			i++;
		}
		this.function.setInitInput(i, nbStep);
		this.function.compute();
		return (float) this.function.getOutput(0).getValue();
	}

	public float computeMorph(List<String> effectors, int nbStep) {
		int i = 0;
		for(; i < this.proprio.size();i++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(this.proprio.get(i)));
		}
		i+=this.extero.size();
		for(int j =0; j< effectors.size();j++) {
			this.function.setInitInput(i, (float) this.env.getValueOfVariableWithName(effectors.get(j)));
			i++;
		}
		this.function.setInitInput(i, nbStep);
		this.function.compute();
		return (float) this.function.getOutput(0).getValue();
	}

	public void setValueOfInitInput(String input, Float value) {
		this.function.setInitInput(this.valuesExtero.get(input), value);
	}

	public float getResultFunction() {
		return (float) this.function.getOutput(0).getValue();
	}

	public List<String> getProprio() {
		return proprio;
	}

	public List<String> getExtero() {
		return extero;
	}


}
