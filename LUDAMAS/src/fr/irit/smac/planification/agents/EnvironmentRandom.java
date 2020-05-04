package fr.irit.smac.planification.agents;

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

public class EnvironmentRandom {

	
	private Map<Integer,Map<String,Double>> historic;
	private Map<String, Float> data;
	private ShieldUser shieldUser;
	
	private Scanner reader;
	
	public EnvironmentRandom() {
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
	public EnvironmentRandom(int nbVar, double min, double max, int nbtype) {
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
		

		this.data = new TreeMap<String,Float>();
		try {
			this.reader = new Scanner(new File("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\dataset_mock_enhanced.csv"));
			String line = this.reader.nextLine();
			String lineSplit[] = line.split(";");
			for(int i = 0; i < lineSplit.length; i++) {
				this.data.put(lineSplit[i], 0.f);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void newCycle() {
		String line = this.reader.nextLine();
		String lineSplit[] = line.split(";");
		int i = 0;
		for(String var : this.data.keySet()) {
			this.data.put(var, Float.parseFloat(lineSplit[i]));
			i++;
		}
		
	}
	
	public SyntheticFunction generateFunction(String name, int nbVar) {
		this.shieldUser.generateSyntheticFunction(name,nbVar);
		
		return this.shieldUser.getSyntheticFunctionWithName(name);
	}
	
	public double getValueOfVariableWithName(String name) {
		return this.data.get(name);
	}

	public Set<String> getAllVariable() {
		return this.data.keySet();
	}

	public void generateNewValues(int cycle) {
		this.historic.put(cycle-1, new TreeMap<String,Double>());
		for(String s : this.shieldUser.getAllVariables()) {
			this.historic.get(cycle-1).put(s, this.shieldUser.getValueOfVariable(s)-100);
		}
		/*System.out.println("ENV");
		for(String str : this.shieldUser.getAllVariables()){
			System.out.println(str+":"+this.shieldUser.getValueOfVariable(str));
		}*/
		this.shieldUser.nextCycle();
		this.newCycle();
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

	public void generateSimilarDataDifferent(String var, int i) {
		this.shieldUser.generateSimilarDataDifferent(var, i);
		
	}

	public Variable getVariableWithName(String s) {
		return this.shieldUser.getVariableWithName(s);
	}
	
	public static void main(String args[]) {
		EnvironmentRandom env = new EnvironmentRandom();
		env.newCycle();
	}
}
