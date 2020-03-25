package fr.irit.smac.planification.agents;

public class Offer {

	
	private MorphingAgent morph;
	
	private InputConstraint inputConstraint;
	
	private int step;
	
	private float crit;

	public Offer(MorphingAgent morph, InputConstraint inputConstraint, int step, float crit) {
		this.morph = morph;
		this.inputConstraint = inputConstraint;
		this.step = step;
		this.crit = crit;
	}

	public MorphingAgent getMorph() {
		return morph;
	}

	public void setMorph(MorphingAgent morph) {
		this.morph = morph;
	}

	public InputConstraint getInputConstraint() {
		return inputConstraint;
	}

	public void setInputConstraint(InputConstraint inputConstraint) {
		this.inputConstraint = inputConstraint;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public float getCrit() {
		return crit;
	}

	public void setCrit(float crit) {
		this.crit = crit;
	}
	
	
}
