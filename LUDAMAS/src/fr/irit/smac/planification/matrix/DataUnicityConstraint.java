package fr.irit.smac.planification.matrix;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.agents.EffectorAgent;
import fr.irit.smac.planification.agents.MorphingAgent;
import fr.irit.smac.planification.agents.Offer;

public class DataUnicityConstraint {

	private EffectorAgent eff;
	
	private String data;
	
	private List<Offer> offers;
	
	public DataUnicityConstraint(EffectorAgent eff, String data) {
		this.eff = eff;
		this.data = data;
		this.offers = new ArrayList<>();
	}

	public EffectorAgent getEff() {
		return eff;
	}

	public String getData() {
		return data;
	}
	
	public void addOffer(Offer offer) {
		this.offers.add(offer);
	}
	
	public boolean isSatisfied() {
		return this.offers.size() < 2;
	}
	
	public void startCycle() {
		this.offers.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((eff == null) ? 0 : eff.hashCode());
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
		DataUnicityConstraint other = (DataUnicityConstraint) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (eff == null) {
			if (other.eff != null)
				return false;
		} else if (!eff.equals(other.eff))
			return false;
		return true;
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
		System.out.println(this.offers);
		return this.offers.size();
	}

	@Override
	public String toString() {
		return "DataUnicityConstraint [eff=" + eff + ", data=" + data + "]";
	}
	

	public List<Offer> getOffers() {
		return this.offers;
	}

	public void restart() {
		this.offers.clear();
	}
	
}
