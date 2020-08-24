package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.planification.system.Environment;
import scala.util.Random;

public class Situation {


	private int id;

	private Objective myobjective;

	private List<Objective> subObjective;

	private ComposedFunction cf;

	private int nbDecoupage;
	
	private int nbStep;
	
	private int nbGoodDataNotPerceived;

	private float[] internalState;
	private float[] internalEffect;
	
	private float valueToAchieve;

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
	

	public Situation(int id, int nbStep, List<String> informationAvailable, int nbDecoupage) {
		this.id = id;
		this.informationAvailable = new TreeMap<>();
		this.nbStep = nbStep;

		for(String inf : informationAvailable) {
			this.informationAvailable.put(inf, 0);
		}
		this.nbDecoupage = nbDecoupage;

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
		this.valueToAchieve = (float)cf.getOutput(1).getValue();
		for(int i =0; i < this.internalState.length;i++) {
			this.internalState[i] = (float)cf.getOutput(i+1).getValue();
		}
		int slice = (int) (valueToAchieve / nbDecoupage);
		//System.out.println("SLICE:"+slice);
		List<String> informationTmp = new ArrayList<String>(this.informationAvailable.keySet());
		Collections.shuffle(informationTmp);
		for(int i = 0; i < nbDecoupage;i++) {
			for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
				this.informationAvailable.put(informationTmp.get(rand.nextInt(informationTmp.size())), i*slice);
			}
		}
		//System.out.println("SITU :"+this.informationAvailable);
	}
	
	/**
	 * Initialise les slice dans lesquelles les inforamtions sont disponibles
	 */
	public void startSituation2() {
		// Decoupage des informations disponibles
		Random rand = new Random();
		int slice = (int) (this.nbStep / nbDecoupage);
		//System.out.println("SLICE:"+slice);
		List<String> informationTmp = new ArrayList<String>(this.informationAvailable.keySet());
		Collections.shuffle(informationTmp);
		List<String> goodData = new ArrayList<String>();
		for(String s : this.informationAvailable.keySet()) {
			if(!s.contains("copy")) {
				goodData.add(s);
			}
		}
		informationTmp.removeAll(goodData);
		for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
			this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), 0*slice);
		}
		informationTmp.addAll(goodData);
		for(int i = 1; i < nbDecoupage;i++) {
			for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
				this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), i*slice);
			}
		}
		//System.out.println("SITU :"+this.informationAvailable);
	}
	
	/**
	 * Initialise les slice dans lesquelles les inforamtions sont disponibles
	 */
	public void startSituationOneCopyMinimum() {
		// Decoupage des informations disponibles
		Random rand = new Random();
		int slice = (int) (this.nbStep / nbDecoupage);
		//System.out.println("SLICE:"+slice);
		List<String> informationTmp = new ArrayList<String>(this.informationAvailable.keySet());
		Collections.shuffle(informationTmp);
		List<String> goodData = new ArrayList<String>();
		for(String s : this.informationAvailable.keySet()) {
			if(!s.contains("copy")) {
				goodData.add(s);
			}
		}
		informationTmp.removeAll(goodData);
		List<String> alreadyDone = new ArrayList<>();
		List<String> toRemove = new ArrayList<>();
		for(String s : informationTmp) {
			String subStr = s.substring(0,s.indexOf(':'));
			if(!alreadyDone.contains(subStr)) {
				this.informationAvailable.put(s, 0*slice);
				alreadyDone.add(subStr);
				toRemove.add(s);
			}
		}
		for(String s : toRemove) {
			informationTmp.remove(s);
		}
		
		/*for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
			this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), 0*slice);
		}*/
		informationTmp.addAll(goodData);
		for(int i = 1; i <= nbDecoupage;i++) {
			for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
				this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), i*slice);
			}
		}
		System.out.println(this.informationAvailable);
		//System.out.println("SITU :"+this.informationAvailable);
	}
	
	/**
	 * Initialise les slice dans lesquelles les inforamtions sont disponibles
	 * 
	 * @param nbNotPerceivedInit
	 * 		The number of internalData that the CAV is not able to perceived at step 0
	 */
	public void startSituation2(int nbNotPerceivedInit) {
		// Decoupage des informations disponibles
		Random rand = new Random();
		int slice = (int) (this.nbStep / nbDecoupage);
		//System.out.println("SLICE:"+slice);
		List<String> informationTmp = new ArrayList<String>(this.informationAvailable.keySet());
		Collections.shuffle(informationTmp);
		List<String> goodData = new ArrayList<String>();
		for(String s : this.informationAvailable.keySet()) {
			if(!s.contains("copy")) {
				goodData.add(s);
			}
		}
		Collections.shuffle(goodData);
		for(int i =0; i < nbNotPerceivedInit; i++) {
			informationTmp.remove(goodData.get(i));
		}
		
		goodData.removeAll(informationTmp);
		
		for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
			this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), 0*slice);
		}
		informationTmp.addAll(goodData);
		for(int i = 1; i < nbDecoupage;i++) {
			for(int j = 0; j < informationAvailable.size()/nbDecoupage +1 && informationTmp.size()>0; j++) {
				this.informationAvailable.put(informationTmp.remove(rand.nextInt(informationTmp.size())), i*slice);
			}
		}
		//System.out.println("SITU :"+this.informationAvailable);
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
	
	public List<String> getInformationAvailable(int value){
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
	
	public float getValueToAchieve() {
		return this.valueToAchieve;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Situation other = (Situation) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Integer getTime() {
		return this.nbStep+1;
	}


}
