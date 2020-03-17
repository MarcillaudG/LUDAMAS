package fr.irit.smac.planification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Matrix {

	private Map<String,Map<String,Float>> matrix;

	private Matrix subMatrix;

	public Matrix (Collection<String> dataExteroceptive) {
		this.matrix = new TreeMap<String,Map<String,Float>>();
		for(String s : dataExteroceptive) {
			this.matrix.put(s,new TreeMap<String,Float>());
			this.matrix.get(s).put(s, 1.0f);
		}
	}

	private Matrix (Collection<String> exteroData, Matrix mat) {
		System.out.println(exteroData);
		this.matrix = new TreeMap<String,Map<String,Float>>();
		for(String s : mat.matrix.keySet()) {
			this.matrix.put(s, new TreeMap<String,Float>());
			for(String var: exteroData) {
				if(mat.matrix.get(s).get(var) != null) {
					this.matrix.get(s).put(var, mat.matrix.get(s).get(var));
				}
				else {
					this.matrix.get(s).put(var, 0.5f);
				}
			}
		}
	}


	public Matrix constructSubmatrix(Collection<String> exteroData) {
		this.subMatrix = null;

		this.subMatrix = new Matrix(exteroData, this);


		return this.subMatrix;
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
		this.matrix.put(dataName, rowTmp);
	}

}
