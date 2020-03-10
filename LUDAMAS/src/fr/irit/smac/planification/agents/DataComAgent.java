package fr.irit.smac.planification.agents;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataComAgent {

	/*
	 * A map of trust between the data and the input
	 */
	private Map<InputAgent, Float> trust;
	
	private String name;
	
	private float value;
	
	private float transformationValue;
	
	private Set<String> inputToApplied;
	
	private EffectorAgent effector;
	
	public DataComAgent(String name, EffectorAgent effector) {
		this.name = name;
		this.transformationValue = 1.0f;
		this.trust = new TreeMap<InputAgent,Float>();
		this.inputToApplied = new TreeSet<String>();
		this.effector = effector;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public void perceive() {
		// remplir inputToApplied
	}
	
	public void decide() {
		
	}
	
	public void act() {
		
	}
	
}
