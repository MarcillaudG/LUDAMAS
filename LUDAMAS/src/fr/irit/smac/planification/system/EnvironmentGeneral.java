package fr.irit.smac.planification.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.model.Variable;

public abstract class EnvironmentGeneral {

	
	protected Map<Integer,Map<String,Double>> historic;
	protected Map<String, Float> data;
	protected ShieldUser shieldUser;
	
	private Scanner reader;
	
	
	
	
	public abstract void newCycle();
	
	public SyntheticFunction generateFunction(String name, int nbVar) {
		this.shieldUser.generateSyntheticFunction(name,nbVar);
		
		return this.shieldUser.getSyntheticFunctionWithName(name);
	}
	
	public float getValueOfVariableWithName(String name) {
		return this.data.get(name);
	}

	public Set<String> getAllVariable() {
		return this.data.keySet();
	}

	public abstract void generateNewValues(int cycle);

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
		for(String s : this.data.keySet()) {
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
		List<String> tmp = new ArrayList<String>(this.data.keySet());
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
		String res = null;
		for(String dat : this.data.keySet()) {
			if(!dat.equals(var) && dat.contains(var)) {
				return dat;
			}
		}
		return res;
	}
	
	public Set<String> getAllCopyOfVar(String var){
		Set<String> res = new TreeSet<>();
		for(String dat : this.data.keySet()) {
			if(!dat.equals(var) && dat.contains(var+":copy")) {
				res.add(dat);
			}
		}
		return res;		
	}

	public void generateSimilarDataDifferent(String var, int i) {
		this.shieldUser.generateSimilarDataDifferent(var, i);
		
	}

	public Variable getVariableWithName(String s) {
		return this.shieldUser.getVariableWithName(s);
	}
	
	
}
