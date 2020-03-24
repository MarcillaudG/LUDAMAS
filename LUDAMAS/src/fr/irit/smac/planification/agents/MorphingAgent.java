package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.planification.Matrix;

public class MorphingAgent {


	private String dataName;

	private String inputName;

	private float morphValue;

	private EffectorAgent superiorAgent;

	private Matrix matrix;

	private Float value;

	private Map<Integer, Pair<Float,Float>> historic;

	private Map<Float,Float> distribution;

	public MorphingAgent(String dataName, String inputName, EffectorAgent eff, Matrix mat) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.superiorAgent = eff;
		this.matrix = mat;
		this.morphValue = 1.0f;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
	}

	public MorphingAgent(String dataName, String inputName) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.morphValue = 1.0f;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
	}

	public void perceive() {
		this.value = null;
		// voit sa valeur
		this.value = this.superiorAgent.askValue(this.dataName);
		// voit les inputs disponibles
		this.superiorAgent.getInputsInScenario();

	}

	public void decide() {
		// si valeur != null
		// chercher à se lier

	}

	public void act() {
		// si lie
		// alors envoyer valeur transformer
		this.morphValue = this.dico();
		float valueToSend = this.value * this.morphValue;
		System.out.println(valueToSend);
	}
	
	/**
	 * Recherche dans l'historique la valeur la plus proche
	 * et renvoi la valeur de transformation
	 * @return morphedValue
	 * 		la valeur de transformation
	 */
	private float dico() {
		float morphedValue = 1.0f;
		List<Float> toDico = new ArrayList<>(this.distribution.keySet());
		Collections.sort(toDico);
		System.out.println(toDico);
		boolean found = false;
		int ind =toDico.size() /2;
		int borneSup = toDico.size()-1;
		int borneInf = 0;
		while (!found && borneSup > borneInf && toDico.size() > 0) {
			ind = (borneSup+borneInf)/2;
			if(toDico.get(ind) == this.value) {
				found = true;
				morphedValue = this.distribution.get(toDico.get(ind));
			}
			else {
				if(borneSup - borneInf   == 1) {
					found = true;
					if(this.value -toDico.get(borneInf) < toDico.get(borneSup) - this.value) {
						ind = borneInf;
						morphedValue = this.distribution.get(toDico.get(ind));
					}
					else {
						ind = borneSup;
						morphedValue = this.distribution.get(toDico.get(ind));
					}
				}
				else {
					if(this.value > toDico.get(ind)) {
						borneInf = ind;
					}
					else {
						borneSup = ind;
					}
				}
			}
		}
		if(toDico.size() == 1) {
			morphedValue = this.distribution.get(toDico.get(0));
		}
		return morphedValue;
	}

	/**
	 * A REVOIR
	 * @param myValue
	 * @param otherValue
	 */
	@Deprecated
	public void computeMorph(Float myValue, Float otherValue) {
		Float inf = null;
		Float sup = null;
		float ratio = myValue/otherValue;
		for(Float borne: this.distribution.keySet()) {
			if(inf == null && borne < myValue) {
				inf = borne;
			}
			if(inf != null && borne > inf && borne < myValue ) {
				inf = borne;
			}
			if(sup == null && borne > myValue) {
				sup = borne;
			}
		}
		if(inf != null && sup != null) {
			if(this.distribution.get(inf) != ratio ||this.distribution.get(sup) != ratio  ) {
				this.distribution.put(myValue, ratio);
			}
		}
		else {
			if ((inf == null && sup != null)) {
				this.distribution.put(myValue, ratio);
				if(this.distribution.keySet().size()>2 && this.distribution.get(sup)== ratio) {
					this.distribution.remove(sup);
				}
			}
			if ((inf != null && sup == null)) {
				this.distribution.put(myValue, ratio);
				if(this.distribution.keySet().size()>2 && this.distribution.get(inf)== ratio) {
					this.distribution.remove(inf);
				}
			}
		}
		if(this.distribution.keySet().size()==0) {
			this.distribution.put(myValue, ratio);
		}
	}


	public void addMorph(Float myValue, Float otherValue) {
		if(otherValue !=0)
			this.distribution.put(myValue, myValue/otherValue);
		else
			this.distribution.put(myValue, 0.f);
	}

	public static void main(String args[]) {
		MorphingAgent morphling = new MorphingAgent("Data", "Input");
		for(int i =0; i < 1; i++) {
			morphling.addMorph(10.f*(i+1), 15.f*(i+1)+i*10);
		}
		System.out.println(morphling.distribution);
		morphling.value = 35.f;
		morphling.act();
	}

	public String getData() {
		return this.dataName;
	}
	
	public String getInput() {
		return this.inputName;
	}
}
