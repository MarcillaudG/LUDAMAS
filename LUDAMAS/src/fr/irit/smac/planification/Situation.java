package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.planification.agents.Environment;
import scala.util.Random;

public class Situation {


	private int id;

	private Objective myobjective;

	private List<Objective> subObjective;

	private ComposedFunction cf;

	private int nbDecoupage;

	private float[] internalState;
	private float[] internalEffect;

	private Map<String,Integer> informationAvailable;

	public Situation(int id, int nbState) {
		this.id = id;

		this.myobjective = new Objective(nbState);

		this.internalState = new float[nbState];
		this.internalEffect = new float[nbState];
		for(int i =0 ; i < nbState;i++) {
			this.myobjective.setObjective(i, 30.0f);
			this.internalState[i] = 0.0f;
			this.internalEffect[i] = 10.0f;
		}
	}

	public Situation(int id, int nbState, List<String> informationAvailable, ComposedFunction cf, int nbDecoupage) {
		this.id = id;
		this.informationAvailable = new TreeMap<>();
		this.myobjective = new Objective(nbState);

		this.internalState = new float[nbState];
		this.internalEffect = new float[nbState];
		for(int i =0 ; i < nbState;i++) {
			this.myobjective.setObjective(i, 30.0f);
			this.internalState[i] = 0.0f;
			this.internalEffect[i] = 10.0f;
		}
		for(String inf : informationAvailable) {
			this.informationAvailable.put(inf, 0);
		}
		this.cf = cf;
		this.nbDecoupage = nbDecoupage;

	}

	/**
	 * Initialise les slice dans lesquelles les inforamtions sont disponibles
	 */
	public void startSituation() {
		// Decoupage des informations disponibles
		Random rand = new Random();
		float valueToAchieve = (float)cf.getOutput(1).getValue();
		int slice = (int) (valueToAchieve / nbDecoupage);
		List<String> informationTmp = new ArrayList<String>(this.informationAvailable.keySet());
		for(int i = 0; i < nbDecoupage;i++) {
			for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
				this.informationAvailable.put(informationTmp.get(rand.nextInt(informationTmp.size())), i*slice);
			}
		}
	}

	public List<String> getInformationAvailable(float value){
		List<String> res = new ArrayList<>();
		for(String s: this.informationAvailable.keySet()) {
			if(this.informationAvailable.get(s)<= value) {
				res.add(s);
			}
		}
		return res;
	}

	public int getId() {
		return id;
	}

	public Objective getMyobjective() {
		return myobjective;
	}

	public List<Objective> getSubObjective() {
		return this.subObjective;
	}

	public float[] getInternalState() {
		return internalState;
	}

	public float[] getInternalEffect() {
		return internalEffect;
	}

	public void setInitInputCF(int i, float value) {
		this.cf.setInitInput(i, value);
	}
	
	public void setInitInputCF(int i, int value) {
		this.cf.setInitInput(i, value);
	}

	public void compute() {
		this.cf.compute();
	}

	public ComposedFunction getCf() {
		return this.cf;
	}


}
