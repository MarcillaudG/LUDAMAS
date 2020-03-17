package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Matrix {

	private Map<Input,Map<String,Float>> matrix;

	private Matrix subMatrix;
	
	private int nbInput;

	public Matrix (List<String> dataExteroceptive) {
		this.matrix = new TreeMap<Input,Map<String,Float>>();
		this.nbInput =0;
		for(int i =0; i < dataExteroceptive.size();i++) {
			String s = dataExteroceptive.get(i);
			this.matrix.put(new Input(s,i),new TreeMap<String,Float>());
			this.matrix.get(new Input(s,i)).put(s, 1.0f);
			nbInput++;
		}
	}

	private Matrix (List<String> exteroData, Matrix mat) {
		System.out.println(exteroData);
		this.matrix = new TreeMap<Input,Map<String,Float>>();
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


	public Matrix constructSubmatrix(List<String> exteroData) {
		this.subMatrix = null;

		this.subMatrix = new Matrix(exteroData, this);


		return this.subMatrix;
	}
	
	public void updateMatrixFromSub(Matrix mat) {
		for(Input in : this.matrix.keySet()) {
			for(String s : mat.matrix.get(in).keySet()) {
				if(!this.matrix.get(in).containsKey(s)) {
					this.matrix.get(in).put(s, 0.5f);
				}
			}
		}
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
		String res = "";
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
		
	}
	
}
