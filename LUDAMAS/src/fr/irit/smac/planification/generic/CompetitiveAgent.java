package fr.irit.smac.planification.generic;

import fr.irit.smac.planification.agents.Offer;

public interface CompetitiveAgent {

	public String getData();
	
	public void sendOffer(Offer offer);

	public boolean isAvailable();
	
	public void cycleOffer();
	
	public float getUsefulness();

	public String getInput();

	public float getValue();

}
