package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.system.CAV;

public class CoalitionAgent implements CompetitiveAgent{

	private String name;

	private int id;

	private CAV cav;

	private String input;

	private float proposition;

	private Map<String,DataAgent> datas;

	private List<DataAgent> datasActifs;

	private InputConstraint inputConstraint;

	private static final float SEUIL_REJECT = 0.2f;

	private static final float SEUIL_ACCEPT = 0.5f;

	private static final float ADVANTAGE = 0.5f;


	public CoalitionAgent(int id, CAV cav, DataAgent data1, DataAgent data2) {
		this.id = id;
		this.cav = cav;

		this.datas = new TreeMap<>();
		this.datas.put(data1.getDataName(),data1);
		this.datas.put(data2.getDataName(),data2);

		this.datasActifs = new ArrayList<>();
	}

	public void addData(DataAgent data) {
		this.datas.put(data.getDataName(),data);
	}

	

	public void perceive() {
		this.datasActifs.clear();
		for(DataAgent data : this.datas.values()) {
			if(data.isActif()) {
				this.datasActifs.add(data);
			}
		}
	}

	public void decide() {
		if(this.datasActifs.size()>0) {
			float meanSum = 0.0f;
			float sumUseful = 0.0f;
			float maxUseful = -1.0f;
			// Test avec le plus haut qui commande
			for(DataAgent agent : this.datasActifs) {
				if(agent.getUsefulnessForData(agent.getInputObj()) > maxUseful) {
					this.input = agent.getInputObj();
					maxUseful = agent.getUsefulnessForData(agent.getInputObj());
				}
			}
			
			for(DataAgent agent : this.datasActifs) {
				meanSum += agent.askMorphUsefulness(this.input)*agent.askMorphedValue(this.input);
				sumUseful += agent.askMorphUsefulness(this.input);
			}
			meanSum = meanSum / sumUseful;
			this.inputConstraint = this.cav.getInputConstraint(this.input);
			this.proposition = meanSum;
			Offer myOffer = new Offer(this, inputConstraint, this.cav.getCurrentTime(), maxUseful+ADVANTAGE);
			this.sendOffer(myOffer);
			//this.inputConstraint.addOffer(new Offer(this, inputConstraint, this.cav.getCurrentStep(), maxUseful+ADVANTAGE));
		}
	}



	public void act() {
		this.cav.sendProposition(this.input, this.proposition);
	}

	public boolean proposeNewAgent(String asker) {
		boolean accept = true;
		float sumUse = 0.f;
		for(DataAgent data : this.datas.values()) {
			sumUse += data.getUsefulnessForData(asker);
			if(data.getUsefulnessForData(asker) <= SEUIL_REJECT) {
				accept = false;
			}
		}
		if(accept) {
			if(sumUse / this.datas.size() > SEUIL_ACCEPT) {
				this.addData(this.cav.addDataAgentToCoalition(asker,this));
			}
		}
		return accept;
	}

	public List<DataAgent> getOtherDataAgent(DataAgent data) {
		List<DataAgent> res = new ArrayList<>(this.datas.values());
		res.remove(data);
		return res;
	}

	/**
	 * Remove a datagent
	 * 
	 * @param dataAgent
	 * 
	 * 		the dataAgent to remove
	 */
	public void leave(DataAgent dataAgent) {
		this.datas.remove(dataAgent);
		if(this.datas.size() < 2) {
			destroy();
		}
	}

	/**
	 * Put all the reference to null
	 */
	private void destroy() {
		for(String s : this.datas.keySet()) {
			this.datas.get(s).coalitionDestroyed();
		}
		this.datas.clear();
		this.cav.coalitionDestroyed(this);
	}

	@Override
	public String getData() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CoalitionAgent other = (CoalitionAgent) obj;
		if (id != other.id)
			return false;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public void sendOffer(Offer myOffer) {
		if(!this.inputConstraint.hasMyOffer(this)) {
			if(this.inputConstraint.isOfferBetter(myOffer)) {
				this.inputConstraint.addOffer(myOffer);
			}
		}
		else {
			// Cas remove
			if(!this.inputConstraint.isSatisfied()) {
				if(!this.inputConstraint.isOfferBetter(myOffer)) {
					this.inputConstraint.removeOffer(myOffer);
				}
				else {
					System.out.println("MERDEs");
				}
			}
		}


	}
	
	@Override
	public float getUsefulness() {
		float maxUseful = 0.0f;
		for(DataAgent agent : this.datasActifs) {
			maxUseful = Math.max(maxUseful, agent.askMorphUsefulness(this.input));
		}
		return maxUseful;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void cycleOffer() {
		this.cycle();
	}

	public void cycle() {
		this.perceive();
		this.decide();
		this.act();
	}

	public boolean hasData(String otherData) {
		return this.datas.containsKey(otherData);
	}

	@Override
	public String getInput() {
		return this.input;
	}

	@Override
	public float getValue() {
		return this.proposition;
	}

	public int getID() {
		return this.id;
	}

	@Override
	public String toString() {
		return "CoalitionAgent [name=" + name + ", id=" + id + ", proposition=" + proposition + ", datas=" + datas
				+ "]";
	}

	


}
