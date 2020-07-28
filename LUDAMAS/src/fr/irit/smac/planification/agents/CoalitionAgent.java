package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Offer;
import fr.irit.smac.planification.system.CAV;
import javafx.geometry.Pos;

public class CoalitionAgent implements CompetitiveAgent{

	private String name;

	private int id;

	private CAV cav;

	private String input;

	private float proposition;

	private Map<String,DataAgent> datas;

	private List<DataAgent> datasActifs;

	private Map<String,AVTAgent> avtAgents;

	private InputConstraint inputConstraint;

	private DataUnicityConstraint dataConstraint;

	private static final float SEUIL_REJECT = 0.2f;

	private static final float SEUIL_ACCEPT = 0.5f;

	private static final float ADVANTAGE = 0.5f;

	private static final float SEUIL_MERGE = 0.7f;


	public CoalitionAgent(int id, CAV cav, DataAgent data1, DataAgent data2) {
		this.id = id;
		this.cav = cav;
		this.name = "COALITION: "+this.id;
		this.datas = new TreeMap<>();
		this.avtAgents = new TreeMap<>();
		this.datas.put(data1.getDataName(),data1);
		this.datas.put(data2.getDataName(),data2);

		data1.bindToCoalition(this);
		data2.bindToCoalition(this);

		this.avtAgents.put(data1.getDataName(), new AVTAgent(this, data1));
		this.avtAgents.put(data2.getDataName(), new AVTAgent(this, data2));

		this.datasActifs = new ArrayList<>();
	}

	public CoalitionAgent(int id, CAV cav, DataAgent data1) {
		this.id = id;
		this.cav = cav;
		this.name = "COALITION: "+this.id;
		this.datas = new TreeMap<>();
		this.avtAgents = new TreeMap<>();
		this.datas.put(data1.getDataName(),data1);

		this.dataConstraint = new DataUnicityConstraint(data1.getDataName());
		data1.bindToCoalition(this);

		this.avtAgents.put(data1.getDataName(), new AVTAgent(this, data1));

		this.datasActifs = new ArrayList<>();
	}

	public void addData(DataAgent data) {
		this.datas.put(data.getDataName(),data);
		if(!this.avtAgents.containsKey(data.getDataName())) {
			this.avtAgents.put(data.getDataName(), new AVTAgent(this, data));
		}
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
		/*if(this.datasActifs.size()>0) {
			float meanSum = 0.0f;
			float sumUseful = 0.0f;
			float maxUseful = -1.0f;
			Offer myOffer = null;
			if(input != null) {
				if(this.inputConstraint.hasMyOffer(this)) {
					myOffer = new Offer(this, inputConstraint, this.cav.getCurrentTime(), maxUseful,this.proposition);
				}
				else {
					input = null;
				}
			}
			if(input == null) {
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
				//Offer myOffer = new Offer(this, inputConstraint, this.cav.getCurrentTime(), maxUseful+ADVANTAGE,this.proposition);
				myOffer = new Offer(this, inputConstraint, this.cav.getCurrentTime(), maxUseful,this.proposition);
			}
			this.sendOffer(myOffer);
			//this.inputConstraint.addOffer(new Offer(this, inputConstraint, this.cav.getCurrentStep(), maxUseful+ADVANTAGE));
		}
		 */
		float valueofProposition = 0.0f;
		float sumWeight = 0.0f;
		for(DataAgent data : this.datasActifs) {
			data.computeValueForInput(this.input);
			this.avtAgents.get(data.getDataName()).cycle();
			//valueofProposition += this.avtAgents.get(data.getDataName()).getValue();
			valueofProposition += data.askMorphedValue(input) * data.askMorphUsefulness(input);
			//sumWeight += this.avtAgents.get(data.getDataName()).getWeight();
			sumWeight += data.askMorphUsefulness(input);
		}
		this.proposition = valueofProposition / sumWeight;
	}


	public void act() {

		this.cav.sendProposition(this.input, this.proposition);
	}

	/**
	 * Look forward to merge more data to increase the usefulness overall
	 */
	public void lookForOtherCoalition() {
		float maxMeanUsefulness = 0.0f;
		CoalitionAgent coal = null;
		for(CoalitionAgent neighbour : this.cav.getOtherCoalitionAgent(this)) {
			float meanUsefulness = 0.0f;
			int nbData = 0;
			//if(neighbour.isInCompetitionWithMe(this.input)) {
			for(String data : neighbour.getAllData()) {
				for(DataAgent agent : this.datas.values()) {
					if(agent.hasMorphForData(data)) {
						meanUsefulness += agent.getUsefulnessForData(data);
						nbData++;
					}
				}
			}
			//}
			if(nbData > 0) {
				meanUsefulness = meanUsefulness/nbData;
				if(coal == null || meanUsefulness > maxMeanUsefulness) {
					coal = neighbour;
					maxMeanUsefulness = meanUsefulness;
				}
			}
		}
		if(coal != null && maxMeanUsefulness > SEUIL_MERGE) {
			if(coal.proposeMerging(this)) {
				System.gc();
			}
		}
	}

	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	private boolean proposeMerging(CoalitionAgent neighbour) {
		float meanUsefulness = 0.0f;
		int nbData = 0;
		for(String data : neighbour.getAllData()) {
			for(DataAgent agent : this.datas.values()) {
				meanUsefulness += agent.getUsefulnessForData(data);
				nbData++;
			}
		}
		meanUsefulness = meanUsefulness/nbData;	
		if(meanUsefulness > SEUIL_MERGE) {
			for(DataAgent data : this.datas.values()) {
				data.mergeToCoalition(neighbour);
				//data.removeAllOffer();
			}
			this.datas.clear();
			this.cav.coalitionDestroyed(this);
			return true;
		}
		return false;
	}

	/**
	 * Return true if the two coalition are in competition for the same input
	 * @param input2
	 * @return
	 */
	private boolean isInCompetitionWithMe(String input2) {
		if(this.input == null) {
			return false;
		}
		return this.input.equals(input2);
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
		if(this.inputConstraint != null && this.inputConstraint.hasMyOffer(this)) {
			this.inputConstraint.removeOffer(this);
		}
		dataAgent.removeAllOffer();
		dataAgent.RemoveFromCoalition();
		this.datas.remove(dataAgent.getDataName());
		
		//TEST
		if(this.datas.size() < 1) {
			destroy();
		}
	}

	/**
	 * Put all the reference to null
	 */
	private void destroy() {
		if(this.inputConstraint != null && this.inputConstraint.hasMyOffer(this)) {
			this.inputConstraint.removeOffer(this);
		}
		for(String s : this.datas.keySet()) {
			this.datas.get(s).coalitionDestroyed();
		}
		this.cav.coalitionDestroyed(this, this.datas.keySet());
		this.datas.clear();
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
	public void cycleValue(String input) {
		this.input = input;
		this.cycle();
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
		//this.cycle();
		this.datasActifs.clear();
		for(DataAgent data : this.datas.values()) {
			if(data.isActif()) {
				this.datasActifs.add(data);
			}
		}
		Collections.shuffle(datasActifs);
		for(DataAgent data : this.datasActifs) {
			data.cycleOffer();
		}
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
		/*float meanSum = 0.0f;
		float sumUseful = 0.0f;
		for(DataAgent agent : this.datasActifs) {
			meanSum += agent.askMorphUsefulness(this.input)*agent.askMorphedValue(this.input);
			sumUseful += agent.askMorphUsefulness(this.input);
		}
		this.proposition = meanSum / sumUseful;*/
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

	public String getName() {
		return this.name;
	}

	public Collection<? extends String> getAllData() {
		return this.datas.keySet();
	}
	public Collection<? extends DataAgent> getDataActifs() {
		return this.datasActifs;
	}

	@Override
	public String getCompetitiveName() {
		return this.name;
	}

	public void sendValueFromAVTAgent(Float weightedValue, String dataName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareToNegociate() {
		this.input = null;
		this.inputConstraint = null;
	}

	/**
	 * Send the feedback to the avts
	 * 
	 * @param trueValueForInput
	 * @param input
	 */
	public void sendFeedbackToAVT(Float trueValueForInput) {
		int feed = 0;
		if(trueValueForInput > this.proposition) {
			feed = -1;
		}
		if(trueValueForInput < this.proposition) {
			feed = 1;
		}
		for(DataAgent data : this.datasActifs) {
			//this.avtAgents.get(data.getDataName()).sendFeedback(feed, this.proposition);
			this.avtAgents.get(data.getDataName()).sendFeedback(trueValueForInput);
		}

	}

	public DataUnicityConstraint getConstraint() {
		return this.dataConstraint;
	}

	public AVTAgent getAVTAgent(String dataInCoal) {
		return this.avtAgents.get(dataInCoal);
	}


	public Collection<AVTAgent> getAllAVT() {
		return this.avtAgents.values();
	}

	public Collection<DataAgent> getDatas() {
		return this.datas.values();
	}
}