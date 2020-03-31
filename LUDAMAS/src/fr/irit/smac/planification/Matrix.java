package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.planification.agents.EffectorAgent;
import fr.irit.smac.planification.agents.MorphingAgent;

public class Matrix {

	private Map<Input,Map<String,Float>> matrix;

	private Matrix subMatrix;

	private int nbInput;
	
	private EffectorAgent effectorAgent;
	
	public Matrix (List<String> dataExteroceptive) {
		this.matrix = new HashMap<Input,Map<String,Float>>();
		this.nbInput =0;
		for(int i =0; i < dataExteroceptive.size();i++) {
			String s = dataExteroceptive.get(i);
			this.matrix.put(new Input(s,i),new TreeMap<String,Float>());
			this.matrix.get(new Input(s,i)).put(s, 1.0f);
			nbInput++;
		}
	}

	public Matrix (List<String> dataExteroceptive, EffectorAgent eff) {
		this.matrix = new HashMap<Input,Map<String,Float>>();
		this.nbInput =0;
		this.effectorAgent = eff;
		for(int i =0; i < dataExteroceptive.size();i++) {
			String s = dataExteroceptive.get(i);
			this.matrix.put(new Input(s,i),new TreeMap<String,Float>());
			this.matrix.get(new Input(s,i)).put(s, 1.0f);
			nbInput++;
		}
	}

	private Matrix (List<String> exteroData, Matrix mat) {
		this.matrix = new HashMap<Input,Map<String,Float>>();
		for(Input in: mat.matrix.keySet()) {
			String s = in.getData();
			this.matrix.put(in, new TreeMap<String,Float>());
			for(String var: exteroData) {
				if(mat.matrix.get(in).get(var) != null) {
					this.matrix.get(in).put(var, mat.matrix.get(in).get(var));
				}
				else {
					this.matrix.get(in).put(var, 0.5f);
				}
			}
		}
	}


	public Matrix(List<String> exteroData, List<String> exteroInSituation, Matrix mat) {
		this.matrix = new HashMap<Input,Map<String,Float>>();
		for(Input in: mat.matrix.keySet()) {
			if(exteroInSituation.contains(in.getData())) {
				String s = in.getData();
				this.matrix.put(in, new TreeMap<String,Float>());
				for(String var: exteroData) {
					if(mat.matrix.get(in).get(var) != null) {
						this.matrix.get(in).put(var, mat.matrix.get(in).get(var));
					}
					else {
						this.matrix.get(in).put(var, 0.5f);
					}
				}
			}
		}
	}

	public Matrix constructSubmatrix(List<String> exteroData) {
		this.subMatrix = null;

		this.subMatrix = new Matrix(exteroData, this);


		return this.subMatrix;
	}

	public Matrix constructSubmatrix(List<String> exteroData, List<String> exteroInSituation) {
		this.subMatrix = null;

		this.subMatrix = new Matrix(exteroData, exteroInSituation, this);


		return this.subMatrix;
	}

	public void updateMatrixFromSub(Matrix mat) {
		for(Input in : mat.matrix.keySet()) {
			for(String s : mat.matrix.get(in).keySet()) {
				if(!this.matrix.get(in).containsKey(s)) {
					this.matrix.get(in).put(s, 0.5f);
					//this.effectorAgent.addMorphingAgent(new MorphingAgent(s, in.getData(), this.effectorAgent, mat));
				}
			}
		}
	}


	public void setWeight(String in, String data,float weight) {
		this.matrix.get(new Input(in, 0)).put(data, weight);
	}
	
	/**
	 * Add a copy of a row
	 * @param dataName
	 * @param row
	 */
	private void addRow(String dataName, Map<String,Float> row) {
		Map<String,Float> rowTmp = new TreeMap<String,Float>();
		for(String s : row.keySet()) {
			rowTmp.put(s, row.get(s));
		}
		this.matrix.put(new Input(dataName, nbInput), rowTmp);
		nbInput++;
	}

	public String toString() {
		String res = "MATRIX\n";
		for(Input in: this.matrix.keySet()) {
			res += "| "+in.getData()+" |";
			for(String s : this.matrix.get(in).keySet()) {
				res += s+"="+this.matrix.get(in).get(s)+" |";
			}
			res += "\n--------------------------------------------------------------\n";
		}
		return res;
	}


	public static void main(String args[]) {
		List<String> list = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		for(int i =0; i < 5;i++) {
			list.add("data"+i);
		}
		for(int i =0; i < 5;i++) {
			list2.add("data'"+i);
		}
		Matrix mat = new Matrix(list);
		System.out.println(mat);
		Matrix sub = mat.constructSubmatrix(list2);
		mat.updateMatrixFromSub(sub);
		System.out.println(mat);
		mat.setWeight("data2", "data'0", 5.0f);
		System.out.println(mat);

	}

	public Map<Input, Map<String, Float>> getMatrix() {
		return matrix;
	}

	public int getNbInput() {
		return nbInput;
	}

	public void addNewData(String data) {
		for(Input in: this.matrix.keySet()) {
			this.matrix.get(in).put(data, 0.5f);
		}
		
	}



}
