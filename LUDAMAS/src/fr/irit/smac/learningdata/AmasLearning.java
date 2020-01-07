package fr.irit.smac.learningdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.learningdata.Agents.LearningFunction;
import fr.irit.smac.modelui.learning.DataLearningModel;
import fr.irit.smac.modelui.learning.InputLearningModel;
import fr.irit.smac.shield.c2av.Input;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.exceptions.TooMuchVariableToRemoveException;

public class AmasLearning extends Amas<EnvironmentLearning>{

	private Map<String, LearningFunction> allFunctions;
	private Map<String,SyntheticFunction> oracles;

	private FileWriter file;

	public AmasLearning(EnvironmentLearning environment, Scheduling scheduling, Object[] params) {
		super(environment, scheduling, params);

		init(params);
	}


	public AmasLearning(EnvironmentLearning environment,Object[] params) {
		super(environment, Scheduling.DEFAULT, params);

		init(params);
	}

	private void init(Object[] params) {
		this.allFunctions = new TreeMap<String,LearningFunction>();
		this.oracles = new TreeMap<String,SyntheticFunction>();

		// Writing the csv
		try {
			this.file = new FileWriter(new File("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\datas.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String name = "Function1";
		this.oracles.put(name, this.environment.generateFunction(name, 10));
		this.environment.printAllVariables();
		LearningFunction lfun = new LearningFunction(this, params,name,this.degradeFunction(name,7));
		for(String input : lfun.getInputsName()) {

		}
		this.allFunctions.put(lfun.getName(), lfun);


	}


	private SyntheticFunction degradeFunction(String name, int nbVar) {
		try {
			return this.oracles.get(name).degradeFunctionInput(nbVar);
		} catch (TooMuchVariableToRemoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public double getValueOfVariable(String variable) {
		return this.environment.getValueOfVariableWithName(variable);

	}

	public Set<String> getVariableInEnvironment() {
		System.out.println(this.environment.getAllVariable().size());
		return this.environment.getAllVariable();
	}

	public double getResultOracle(String name) {
		for(int i = 0; i < this.oracles.get(name).getNbInput();i++) {
			this.oracles.get(name).setValueOfOperand(i, this.getValueOfVariable(this.oracles.get(name).getInput(i).getOperand()));
		}
		return this.oracles.get(name).computeCustomOracle();
	}

	public double getResultOracle(String name,List<Integer> inputs) {
		return this.oracles.get(name).computeInput(inputs);
	}


	public Set<String> getInputsName(String name) {
		return this.allFunctions.get(name).getInputsName();
	}


	public void addListenerToInput(String function,String input, InputLearningModel model) {
		this.allFunctions.get(function).addListenerToInput(input,model);

	}


	public void addListenerToData(String function, String data, DataLearningModel model) {
		this.allFunctions.get(function).addListenerToData(data,model);		
	}


	public Set<String> getDatasNames(String function) {
		return this.allFunctions.get(function).getDatasNames();
	}


	public String getNameOfCorrectDataForInput(int idInput, String nameOfFunction) {
		return this.oracles.get(nameOfFunction).getInput(idInput).getOperand();

	}

	public String getNameOfInputFormula(int idInput,String nameOfFunction) {
		return this.oracles.get(nameOfFunction).getInput(idInput).getOperator().toString();
	}

	public void generateNewValues() {
		this.environment.generateNewValues(this.cycle);
	}

	public double getValueOfVariable(String var, int cycle) {
		return this.environment.getValueOfVariable(var, cycle);
	}

	@Override
	protected void onSystemCycleBegin() {
		super.onSystemCycleBegin();
		for(LearningFunction lf : this.allFunctions.values()) {
			this.setValueOfVariableNonDegraded(lf);
		}
	}


	public void setValueOfVariableNonDegraded(LearningFunction lf) {
		for(int i = 0; i < this.oracles.get(lf.getName()).getNbInput();i++) {
			if(!lf.getFunction().getInputIDRemoved().contains(i)) {
				lf.getFunction().setValueOfOperand(i,this.getValueOfVariable(lf.getFunction().getInput(i).getOperand()));
			}
		}
	}


	public void setupOraclePastValues(String name, int cycle) {
		for(Input inp : this.oracles.get(name).getAllInput()) {
			this.oracles.get(name).setValueOfOperand(inp.getId(), this.environment.getValueOfVariable(inp.getOperand(), cycle));
		}

	}

	/**
	 * Write the name of the inputs in the data file for the CSP
	 * 
	 * @param inputs
	 */
	public void writeInputsInFileDatas(Map<String,Integer> inputs, String nameOfFunction) {
		try {
			for(String input : inputs.keySet()) {
				this.file.write(input+"|"+this.getNameOfInputFormula(inputs.get(input), nameOfFunction)+";");
			}
			this.file.write("\n");
			this.file.flush();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeBorneAndFeedbackAndDatas(double borne, double feed) {

		if(this.cycle >= 2) {
			try {
				this.file.write(this.cycle+";");
				this.file.write(String.format(Locale.US,"%.4f", borne)+";");
				this.file.write(String.format(Locale.US,"%.1f", feed)+";");
				for(String var : this.environment.getAllVariable()) {
					String toWrite = var+":";
					String valueFormat = String.format(Locale.US,"%.4f", this.environment.getValueOfVariable(var, this.cycle));
					toWrite += valueFormat;
					this.file.write(toWrite+";");
				}
				this.file.write("\n");
				this.file.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
