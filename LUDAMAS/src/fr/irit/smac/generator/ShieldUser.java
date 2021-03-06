package fr.irit.smac.generator;

import java.util.List;
import java.util.Set;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.shield.c2av.GeneratorOfComposedFunction;
import fr.irit.smac.shield.c2av.GeneratorOfFunction;
import fr.irit.smac.shield.c2av.GeneratorOfTypedVariable;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.model.Generator;
import fr.irit.smac.shield.model.Variable;

public class ShieldUser {

	private GeneratorOfTypedVariable generator;
	
	private GeneratorOfFunction funGen;
	
	private GeneratorOfComposedFunction compFunGen;
	
	public ShieldUser() {
		this.generator = new GeneratorOfTypedVariable();
	}
	
	
	public void initSetOfVariableWithRange(int nbVar,double min, double max) {
		this.generator.initSetOfVariableWithRange(nbVar, min, max);
		this.generator.generateAllValues();
	}
	
	public void initSetOfTypedVariableWithRange(int nbVar, double min, double max, String type) {
		this.generator.initSetOfTypedVariable(nbVar, min, max, type);
	}
	
	/**
	 * Generate nbSimilar data with the same value as dataName
	 * 
	 * @param dataName
	 * 		The name of the variable to copy
	 * @param nbSimilar
	 * 		The number of copy needed
	 */
	public void generateSimilarData(String dataName, int nbSimilar) {
		for(int i =0; i < nbSimilar; i++) {
			this.generator.generateSimilarData(dataName,i);
		}
	}
	
	/**
	 * Generate nbSimilar data with a different value than dataName
	 * 
	 * @param dataName
	 * 		The name of the variable to copy
	 * @param nbSimilar
	 * 		The number of copy needed
	 */
	public void generateSimilarDataDifferent(String dataName, int nbSimilar) {
		for(int i =0; i < nbSimilar; i++) {
			this.generator.generateSimilarDataDifferent(dataName,i);
		}
	}
	
	public void generateAllFunctionsOfVariable() {
		this.generator.generateAllFunctions();
	}
	
	/**
	 * Init the generator of function
	 */
	public void initGeneratorOfFunction() {
		this.funGen = new GeneratorOfFunction(this.generator);
	}
	

	/**
	 * Init the generator of ComposedFunction
	 */
	public void initGeneratorOfComposedFunction() {
		this.compFunGen = new GeneratorOfComposedFunction(this.generator);
	}
	
	
	/**
	 * Return the synthetic function with the matching name
	 * 
	 * @param name
	 * 			The name of the function
	 * @return a synthetic function
	 */
	public SyntheticFunction getSyntheticFunctionWithName(String name) {
		return this.funGen.getSyntheticFunctionWithName(name);
	}
	
	/**
	 * Generate a new synthetic function
	 * 
	 * @param name
	 * 			The name of the function
	 * @param nbVarMax
	 * 			The max number of variable for the compute of the function
	 */
	public void generateSyntheticFunction(String name, int nbVarMax) {
		this.funGen.generateFunction(name,nbVarMax);
	}

	/**
	 * Generate a new synthetic function
	 * 
	 * @param name
	 * 			The name of the function
	 * @param nbVarMax
	 * 			The max number of variable for the compute of the function
	 */
	public void generateComposedFunction(String name, List<String> input, List<String> outputs, int complexity, int seuilFunction) {
		this.compFunGen.generateFunction(name, input, outputs, complexity, seuilFunction);
	}
	
	/**
	 * Return the synthetic function with the matching name
	 * 
	 * @param name
	 * 			The name of the function
	 * @return a synthetic function
	 */
	public ComposedFunction getComposedFunctionWithName(String name) {
		return this.compFunGen.getComposedFunctionWithName(name);
	}
	
	/**
	 * Return the value of a variable
	 * 
	 * @param name
	 * 
	 * @return the value
	 */
	public double getValueOfVariable(String name) {
		return this.generator.getValueOfVariable(name);
	}
	
	public void nextCycle() {
		this.generator.generateAllValues();
	}
	
	public Set<String> getAllVariables(){
		return this.generator.getAllVariables();
	}


	public String getCopyOfVar(String s) {
		return this.generator.getCopyOfVar(s);
	}


	public Variable getVariableWithName(String s) {
		return this.generator.getVariableWithName(s);
	}


}
