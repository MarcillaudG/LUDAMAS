package fr.irit.smac.planification.agents;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.InputConstraint;

public class Offer {

	
	private  CompetitiveAgent agent;
	
	private InputConstraint inputConstraint;
	
	private int step;
	
	private float crit;
	
	private float value;

	public Offer(CompetitiveAgent morph, InputConstraint inputConstraint, int step, float crit, float value) {
		this.agent = morph;
		this.inputConstraint = inputConstraint;
		this.step = step;
		this.crit = crit;
		this.value = value;
	}
	
	public Offer(CompetitiveAgent morph, InputConstraint inputConstraint, int step, float crit) {
		this.agent = morph;
		this.inputConstraint = inputConstraint;
		this.step = step;
		this.crit = crit;
	}

	public CompetitiveAgent getAgent() {
		return agent;
	}

	public void setAgent(CompetitiveAgent agent) {
		this.agent = agent;
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

	public boolean isBetterOrEquals(Offer offer) {
		return this.crit >= offer.crit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputConstraint == null) ? 0 : inputConstraint.hashCode());
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
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
		Offer other = (Offer) obj;
		if (inputConstraint == null) {
			if (other.inputConstraint != null)
				return false;
		} else if (!inputConstraint.equals(other.inputConstraint))
			return false;
		if (agent == null) {
			if (other.agent!= null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Offer [agent=" + agent + ", inputConstraint=" + inputConstraint + ", step=" + step + ", crit=" + crit
				+ "]";
	}
	
	public float getValue() {
		return this.value;
	}
	
}
