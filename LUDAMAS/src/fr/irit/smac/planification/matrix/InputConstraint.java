package fr.irit.smac.planification.matrix;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.agents.DataMorphAgent;
import fr.irit.smac.planification.agents.EffectorAgent;
import fr.irit.smac.planification.agents.MorphingAgent;
import fr.irit.smac.planification.generic.CompetitiveAgent;

public class InputConstraint {


	//private EffectorAgent eff;

	private String input;

	private List<Offer> offers;
	
	private boolean offerChanged;

	/*public InputConstraint(EffectorAgent eff, String input) {
		this.eff = eff;
		this.input = input;
		this.offers = new ArrayList<>();
	}*/

	public InputConstraint(String input) {
		this.input = input;
		this.offers = new ArrayList<>();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		this.offerChanged = true;
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
		return better;
	}

	public boolean hasMyOffer(CompetitiveAgent agent) {
		boolean found = false;
		for(Offer off : this.offers) {
			if(off.getAgent().equals(agent)) {
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
		return "InputConstraint [ input=" + input + "]";
	}

	public List<Offer> getOffers() {
		return this.offers;
	}

	public void restart() {
		this.offers.clear();
		this.offerChanged = false;
	}

	public void setTrueValue(Float valueForFeedback) {
		this.offers.clear();
		this.offers.add(new Offer(null, this, 0, 0, valueForFeedback));
	}

	public void newCycleOffer() {
		this.offerChanged = false;
	}
	
	public boolean hasChanged() {
		return this.offerChanged;
	}

	public void removeOffer(CompetitiveAgent agent) {
		Offer toRemove = null;
		for(Offer offer : this.offers) {
			if(offer.getAgent().equals(agent)) {
				toRemove = offer;
			}
		}
		this.offers.remove(toRemove);
	}



}
