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

	public boolean isBetterOrEquals(Offer offer) {
		return this.crit >= offer.crit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputConstraint == null) ? 0 : inputConstraint.hashCode());
		result = prime * result + ((morph == null) ? 0 : morph.hashCode());
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
		if (morph == null) {
			if (other.morph != null)
				return false;
		} else if (!morph.equals(other.morph))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Offer [morph=" + morph + ", inputConstraint=" + inputConstraint + ", step=" + step + ", crit=" + crit
				+ "]";
	}
	
	
	
}
