package fr.irit.smac.planification;

import java.util.List;

public class Situation {

	
	private int id;
	
	private Objective myobjective;
	
	private PlanificationFunction function;
	
	private List<Objective> subObjective;
	
	private float[] internalState;
	private float[] internalEffect;
	
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

	
	
}
