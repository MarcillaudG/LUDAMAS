package fr.irit.smac.planification.agents;

import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.planification.system.CAV;

public class Main {

	public static void main(String[] args) {
		
		
		Links links = new Links();
		/*Map<String,Number> params = new TreeMap<>();
		int nbCycleExp = 200;
		params.put("delta", 0.5f);
		
		int step = 5;
		for(int i =50; i < 100;i += step) {

			//CAV cav = new CAV("cavtest", 1, 1, 50, 0, "C:\\Users\\gmarcill\\Documents\\Dataset\\dataset_Custom_50_linear_Noise_Diff.csv");
			CAV cav = new CAV("cavtest", 1, 1, 16, 0, "C:\\Users\\gmarcill\\Documents\\Dataset\\dataset_mock_20_linear_Noise_Diff.csv");
			//params.put("delta",new Float(i/100.0f));
			float adapt = new Float(i/100.0f);
			params.put("delta",adapt);
			cav.setResult("C:\\Users\\gmarcill\\Documents\\Dataset\\Results\\Delta\\res_"+adapt);
			cav.setParams(params);
			for(int j = 1; j <= nbCycleExp;j++) {
				cav.generateNewValues(j);
				cav.manageSituation(j);
			}
		}*/
		
	}
}
