package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.planification.system.CAV;

public class DataAgent {

	private CAV cav;

	private String dataName;

	private float myValue;
	
	private float min;
	
	private float max;

	private float morphedValue;

	private boolean actif;

	private Map<String,MorphingAgent> morphs;

	private List<MorphingAgent> morphActifs;

	private Map<String,DataAgent> neighbours;
	
	private boolean submissed;
	
	private CoalitionAgent coalition;
	
	private boolean wantToCoal;
	
	private static final float SEUILMORPH = 10.0f;
	
	private static final float SEUIL_COAL = 0.8f;
	
	private static final float SEUIL_LEAVE = 0.5f;

	public DataAgent(CAV cav, String dataName, List<String> allInputs) {
		this.cav = cav;
		this.dataName = dataName;
		this.morphs = new TreeMap<>();
		this.morphActifs = new ArrayList<>();
		this.submissed = false;
		this.neighbours = new TreeMap<>();

		for(String s : allInputs) {
			this.morphs.put(s,new MorphingAgent(dataName, s));
		}
	}

	public void perceive() {
		this.actif = this.cav.getCurrentSituation().getInformationAvailable(this.cav.getCurrentTime()).contains(this.dataName);
		this.wantToCoal = false;
		this.morphActifs.clear();
		this.submissed = this.coalition != null;
		//perceiveValue
		if(this.actif) {
			this.myValue = this.cav.getValueOfData(this.dataName);
			for(MorphingAgent morph : this.morphs.values()) {
				if(morph.isActif()) {
					this.morphActifs.add(morph);
				}
			}
		}
	}

	public void decide() {
		if(this.actif) {
			// decide which input
			// Decider de faire une coalition
			if(!this.submissed) {
				MorphingAgent best = null;
				float bestUseful = -1.0f;
				for(MorphingAgent morph : this.morphActifs) {
					if(morph.getUsefulness() > bestUseful) {
						bestUseful = morph.getUsefulness();
						best = morph;
					}
				}
				// morphvalue for the input
				this.morphedValue = best.morph(this.myValue);

				if(bestUseful > SEUIL_COAL) {
					this.wantToCoal = true;
					this.neighbours.get(best.getInput()).proposeCoalition(this.dataName, this.morphedValue);
				}

			}
			else {
				// evaluer sa coalition
				float sumUse = 0.0f;
				int nbOther = 0;
				for(DataAgent other : this.coalition.getOtherDataAgent(this)) {
					sumUse += this.morphs.get(other.dataName).getUsefulness();
					nbOther++;
				}
				if(sumUse / nbOther < SEUIL_LEAVE) {
					this.coalition.leave(this);
					this.coalition = null;
					this.submissed = false;
					System.gc();
				}
			}
		}
	}

	/**
	 * Receive a proposition to create a coalition btween the asker and itself
	 * 
	 * @param asker
	 * 			The other datagent initiating the coalition
	 * @param value
	 * 			The value of the asker the closest to this value
	 */
	private void proposeCoalition(String asker, float value) {
		if(!this.submissed) {
			if(this.valueClosedToMine(value) || this.morphs.get(asker).getUsefulness() > SEUIL_COAL) {
				this.cav.createCoalition(this.dataName, asker);
			}
		}
		else {
			if(this.valueClosedToMine(value)) {
				this.coalition.proposeNewAgent(asker);
			}
		}
		
	}

	/**
	 * Evaluate if the value is close to mine
	 * 
	 * @param value
	 * 	the value from another dataagent
	 * 
	 * @return true if close
	 */
	private boolean valueClosedToMine(float value) {
		return (Math.abs(value-this.myValue) < DataAgent.SEUILMORPH * (this.max- this.min)/100);
	}

	public void act() {
		if(this.actif) {
			if(submissed) {

			}
			else {

			}
		}
	}

	public boolean isActif() {
		return this.actif;
	}

	/**
	 * Return the usefulness calculated by a morph for a data
	 * 
	 * @param asker
	 *		the data 
	 *
	 * @return the usefulness for the data
	 */
	public float getUsefulnessForData(String asker) {
		return this.morphs.get(asker).getUsefulness();
	}

	public void mergeToCoalition(CoalitionAgent coalition2) {
		this.coalition = coalition2;
		this.submissed = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataName == null) ? 0 : dataName.hashCode());
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
		DataAgent other = (DataAgent) obj;
		if (dataName == null) {
			if (other.dataName != null)
				return false;
		} else if (!dataName.equals(other.dataName))
			return false;
		return true;
	}

	public void coalitionDestroyed() {
		this.coalition = null;
		this.submissed = false;
		
		this.cycle();
	}

	public void cycle() {
		this.perceive();
		this.decide();
		this.act();
		
	}
	
	

}
