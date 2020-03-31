package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.List;

public class InputConstraint {


	private EffectorAgent eff;

	private String input;

	private List<Offer> offers;

	public InputConstraint(EffectorAgent eff, String input) {
		this.eff = eff;
		this.input = input;
		this.offers = new ArrayList<>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eff == null) ? 0 : eff.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
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
		InputConstraint other = (InputConstraint) obj;
		if (eff == null) {
			if (other.eff != null)
				return false;
		} else if (!eff.equals(other.eff))
			return false;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		return true;
	}


	public boolean isSatisfied() {
		return this.offers.size() == 1;
	}

	public void addOffer(Offer offer) {
		this.offers.add(offer);
	}

	public void startCycle() {
		this.offers.clear();
	}

	public boolean isOfferBetter(Offer offer) {
		boolean better = true;
		for(int i =0; i < this.offers.size() && better;i++) {
			if(this.offers.get(i).isBetterOrEquals(offer)) {
				better = false;
			}
		}
		return false;
	}

	public boolean hasMyOffer(MorphingAgent morph) {
		boolean found = false;
		for(Offer off : this.offers) {
			if(off.getMorph().equals(morph)) {
				found = true;
			}
		}
		return found;
	}

	public void removeOffer(Offer myOffer) {
		System.out.print("");
		this.offers.remove(myOffer);
	}

	public int getNbOffer() {
		return this.offers.size();
	}

	@Override
	public String toString() {
		return "InputConstraint [eff=" + eff + ", input=" + input + "]";
	}

	public List<Offer> getOffers() {
		return this.offers;
	}



}
