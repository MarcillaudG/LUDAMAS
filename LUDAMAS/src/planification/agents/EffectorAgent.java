package planification.agents;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class EffectorAgent {

	private String name;
	
	private String dataInfluenced;

	// inputs
	private Set<String> dataUsed;
	
	// Data gathered by sensors
	private Set<String> dataPerceived;
	
	// Data gathered by communication
	private Set<String> dataCommunicated;
	
	// Objectives during situation
	private Map<Integer,Float> objectives;
	
	// The function used to plan the step
	private PlanificationFunction planFun;
	
	private int myObjectiveState;
	
	public EffectorAgent(String name,PlanificationFunction pf, int objState) {
		this.planFun = pf;
		this.name = name;
		this.myObjectiveState = objState;
		init();
	}
	
	
	private void init() {
		this.dataCommunicated = new TreeSet<String>();
		this.dataPerceived = new TreeSet<String>();
		this.dataUsed = new TreeSet<String>();
		this.objectives = new TreeMap<Integer,Float>();
	}


	public void perceive() {
		// Recupration des donnes percues
		// Recuperation des donnees communiquees
		System.out.println("Perceive");
	}
	
	public void decide() {
		System.out.println("Decide");
		// Creation of the matrix DataUsed minus dataPerceived / dataCommunicated
		// Choix des Dcom
		// Decision des objectifs en fonction des donnees choisies
		// Creation du plan, Intensite de l action pour chaque temps t
	}
	
	public void act() {
		System.out.println("Act");
		// Tant qu il reste une action a faire
			// Recuperation des donnees percues
			// Si je peux la faire je la fais
			// Sinon modification
			// Feedback difference Dp et dcom used
	}


	public String getName() {
		return name;
	}


	public String getDataInfluenced() {
		return dataInfluenced;
	}


	public Set<String> getDataUsed() {
		return dataUsed;
	}


	public Set<String> getDataPerceived() {
		return dataPerceived;
	}


	public Set<String> getDataCommunicated() {
		return dataCommunicated;
	}


	public Map<Integer, Float> getObjectives() {
		return objectives;
	}


	public PlanificationFunction getPlanFun() {
		return planFun;
	}


	public int getMyObjectiveState() {
		return myObjectiveState;
	}


	public void start() {
		this.perceive();
		this.decide();
		this.act();
	}
	
	
}
