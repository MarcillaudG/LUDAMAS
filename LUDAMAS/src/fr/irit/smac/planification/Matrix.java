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
	
	public Matrix (Collection<String> dataPerceived) {
		this.matrix = new TreeMap<String,Map<String,Float>>();
		for(String s : dataPerceived) {
			this.matrix.put(s,new TreeMap<String,Float>());
		}
	}
	
	
	public Matrix constructSubmatrix(Collection<String> dataPerceivedToRemove,Collection<String> dataCommunicatedInSituation) {
		this.subMatrix = null;
		
		// Initialise the inputs
		Set<String> dataTmp = new TreeSet<String>(this.matrix.keySet());
		dataTmp.removeAll(dataPerceivedToRemove);
		this.subMatrix = new Matrix(dataTmp);
		
		// Initialise the matching row
		for(String s : dataTmp) {
			Map<String,Float> rowTmp = new TreeMap<String,Float>();
			for(String dcom : dataCommunicatedInSituation) {
				rowTmp.put(dcom, this.matrix.get(s).get(dcom));
			}
			this.subMatrix.addRow(s, rowTmp);
		}
		
		
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
