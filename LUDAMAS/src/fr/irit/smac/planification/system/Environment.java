package fr.irit.smac.planification.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.model.Variable;

public class Environment extends EnvironmentGeneral{


	private ShieldUser shieldUser;
	
	private Map<Integer,Map<String,Double>> historic;
	
	
	public Environment() {
		init();
	}
	
	/**
	 * WARNING : NBTYPE IS NOT FUINCTIONAL
	 * TODO
	 * @param nbVar
	 * @param min
	 * @param max
	 * @param nbtype
	 */
	public Environment(int nbVar, double min, double max, int nbtype) {
		this.shieldUser = new ShieldUser();
		

		/*this.shieldUser.initSetOfTypedVariableWithRange(15, 0, 200, "Type 1");
		this.shieldUser.generateAllFunctionsOfVariable();
		
		this.shieldUser.initGeneratorOfFunction();
		
		this.shieldUser.initGeneratorOfComposedFunction();
		this.historic = new TreeMap<Integer,Map<String,Double>>();*/
		init();
	}
	
	
	private void init() {

		this.shieldUser = new ShieldUser();
		
		this.shieldUser.initSetOfTypedVariableWithRange(10, 0, 10, "Type 1");
		this.shieldUser.generateAllFunctionsOfVariable();
		
		this.shieldUser.initGeneratorOfFunction();
		this.historic = new TreeMap<Integer,Map<String,Double>>();
		

		this.shieldUser.initGeneratorOfComposedFunction();
	}
	
	public SyntheticFunction generateFunction(String name, int nbVar) {
		this.shieldUser.generateSyntheticFunction(name,nbVar);
		
		return this.shieldUser.getSyntheticFunctionWithName(name);
	}
	
	@Override
	public float getValueOfVariableWithName(String name) {
		return (float) this.shieldUser.getValueOfVariable(name);
	}

	@Override
	public Set<String> getAllVariable() {
		return this.shieldUser.getAllVariables();
	}

	@Override
	public void generateNewValues(int cycle) {
		this.historic.put(cycle-1, new TreeMap<String,Double>());
		for(String s : this.shieldUser.getAllVariables()) {
			this.historic.get(cycle-1).put(s, this.shieldUser.getValueOfVariable(s)-100);
		}
		/*System.out.println("ENV");
		for(String str : this.shieldUser.getAllVariables()){
			System.out.println(str+":"+this.shieldUser.getValueOfVariable(str));
		}*/
		//this.shieldUser.nextCycle();
		/*System.out.println("HISt");
		System.out.println(this.historic);*/
		
	}

	public double getValueOfVariable(String var, int cycle) {
		return this.historic.get(cycle-1).get(var);
	}
	
	public void printAllVariables() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Return the subset of all data not in dataToRemove
	 * @param dataToRemove
	 * @return a subset of String
	 */
	public Set<String> getOtherData(Set<String> dataToRemove) {
		Set<String> res = new TreeSet<String>();
		for(String s : this.shieldUser.getAllVariables()) {
			if(!dataToRemove.contains(s)) {
				res.add(s);
			}
		}
		return res;
	}

	/**
	 * Return a subset of data
	 * @param i
	 * @return
	 */
	public Set<String> getSubsetOfVariables(int i) {
		Set<String> res = new TreeSet<String>();
		List<String> tmp = new ArrayList<String>(this.shieldUser.getAllVariables());
		Collections.shuffle(tmp);
		res.addAll(tmp.subList(0, i));
		return res;
	}

	public void generateSimilarData(String dataName,int nbSimilar) {
		this.shieldUser.generateSimilarData(dataName, nbSimilar);
	}

	public void generateComposedFunction(String name, List<String> input, List<String> outputs, int i, int j) {
		this.shieldUser.generateComposedFunction(name, input, outputs, 3, 3);
		
	}

	public ComposedFunction getComposedFunctionWithName(String name) {
		return this.shieldUser.getComposedFunctionWithName(name);
	}

	public String getCopyOfVar(String var) {
		return this.shieldUser.getCopyOfVar(var);
	}

	public void generateSimilarDataDifferent(String var, int i) {
		this.shieldUser.generateSimilarDataDifferent(var, i);
		
	}

	public Variable getVariableWithName(String s) {
		return this.shieldUser.getVariableWithName(s);
	}

	@Override
	public void newCycle() {
		this.shieldUser.nextCycle();
		
	}
	
}
