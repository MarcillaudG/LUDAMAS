package fr.irit.smac.planification;

public class Objective {

	private int nbState;
	
	private float[] objectives;
	
	public Objective(int nbState) {
		this.nbState = nbState;
		objectives = new float[nbState];
	}
	
	public void setObjective(int obj,float value) {
		if(this.nbState > obj) {
			this.objectives[obj] = value;
		}
		else {
			System.err.println("Not enough state");
		}
	}
	
	public Float getObjectiveValue(int obj) {
		if(this.nbState > obj) {
			return this.objectives[obj];
		}
		else {
			return null;
		}
	}
}
