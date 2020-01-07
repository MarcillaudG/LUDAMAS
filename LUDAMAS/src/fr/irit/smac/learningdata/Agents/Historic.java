package fr.irit.smac.learningdata.Agents;

import java.util.Map;

/**
 * 
 * @author gmarcill
 *
 *	Class used to store the past
 */
public class Historic {

	private int cycle;
	
	private Double value;
	
	private Double feedback;
	
	private Configuration config;
	
	
	
	public Historic(int cycle, Double value, Double feedback, Configuration config) {
		this.cycle = cycle;
		this.value = value;
		this.feedback = feedback;
		this.config = config;
	}
	
	
	/**
	 * Return if the new value is better or weaker
	 * @param resConf
	 * 		The result for the new configuration
	 * @return true if it is better
	 */
	public boolean isConfigBetter(Double resConf) {
		if(this.feedback > 0.0 && resConf < this.value) {
			return true;
		}
		if(this.feedback < 0.0 && resConf > this.value) {
			return true;
		}
		return false;
	}

	/**
	 * Return if the new value is better or weaker
	 * @param resConf
	 * 		The result for the new configuration
	 * @return true if it is better
	 */
	public boolean isConfigBetterOrEquals(Double resConf) {
		if(this.feedback > 0.0 && resConf <= this.value) {
			return true;
		}
		if(this.feedback < 0.0 && resConf >= this.value) {
			return true;
		}
		return false;
	}

	public int getCycle() {
		return cycle;
	}


	public Double getValue() {
		return value;
	}


	public Double getFeedback() {
		return feedback;
	}


	public Configuration getConfig() {
		return config;
	}


	@Override
	public String toString() {
		return "Historic [cycle=" + cycle + ", value=" + value + ", feedback=" + feedback + "]";
	}



	
	
}
