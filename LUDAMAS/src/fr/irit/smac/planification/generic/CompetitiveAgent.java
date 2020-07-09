package fr.irit.smac.planification.generic;

import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.Offer;

public interface CompetitiveAgent {

	/**
	 * Return the name of the data
	 * 
	 * @return the data name
	 */
	public String getData();
	
	/**
	 * Send an offer to an input constraint
	 * 
	 * @param offer
	 */
	public void sendOffer(Offer offer);

	/**
	 * Return if the agent is available
	 * 
	 * @return available
	 */
	public boolean isAvailable();
	
	/**
	 * Start the cycle propose offer
	 */
	public void cycleOffer();
	
	/**
	 * Return the usefulness of the agent
	 * 
	 * @return usefulness
	 */
	public float getUsefulness();
	
	
	/**
	 * Return the input objective of the agent
	 * 
	 * @return the input
	 */
	public String getInput();
	
	/**
	 * Return the value chosen for the matching input
	 * 
	 * @return value
	 */
	public float getValue();
	
	/**
	 * Return the name of the agent
	 * 
	 * @return the name of the agent
	 */
	public String getCompetitiveName();

	public void prepareToNegociate();
	
	
	/**
	 * Choose the value for this cycle
	 */
	public void cycleValue(String input);


}
