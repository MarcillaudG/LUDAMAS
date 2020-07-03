package fr.irit.smac.planification.generic;

import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Offer;

public interface CompetitiveAgent {

	public String getData();
	
	public void sendOffer(Offer offer);

	public boolean isAvailable();
	
	public void cycleOffer();
	
	public float getUsefulness();

	public String getInput();

	public float getValue();
	
	public String getCompetitiveName();

	public void wonCompet(String string);

}
