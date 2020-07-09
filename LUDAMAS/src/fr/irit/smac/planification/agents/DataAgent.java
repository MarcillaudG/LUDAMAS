package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.system.CAV;

public class DataAgent {

	private CAV cav;

	/**
	 * For the tests
	 */
	private EffectorAgent effector;

	private String dataName;

	private Float myValue;

	private Float min;

	private Float max;

	private float morphedValue;

	private boolean actif;

	private Map<String,DataMorphAgent> morphs;

	private List<DataMorphAgent> morphActifs;

	private Map<String,DataAgent> neighbours;

	private List<String> allInputs;

	private List<String> inputInSituation;

	private List<String> dataInSituation;

	private boolean submissed;

	private CoalitionAgent coalition;

	private boolean wantToCoal;

	private DataUnicityConstraint dataConstraint;

	private String inputObj;

	private float maxUseful;

	private static final float SEUILMORPH = 5.0f;

	private static final float SEUIL_COAL = 0.7f;

	private static final float SEUIL_LEAVE = 0.5f;

	public DataAgent(CAV cav, String dataName, List<String> allInputs) {
		this.cav = cav;
		this.dataName = dataName;
		this.morphs = new TreeMap<>();
		this.morphActifs = new ArrayList<>();
		this.submissed = false;
		this.neighbours = new TreeMap<>();
		this.inputInSituation = new ArrayList<>();
		this.dataInSituation = new ArrayList<>();
		this.allInputs = new ArrayList<>(allInputs);
		//this.dataConstraint = new DataUnicityConstraint(this.dataName);

		if(this.allInputs.contains(dataName)) {
			this.morphs.put(dataName,new DataMorphAgent(dataName, dataName,this));
		}
		else {
			for(String s : allInputs) {
				this.morphs.put(s,new DataMorphAgent(dataName, s,this));
			}
		}
	}

	/**
	 * For the test
	 * @param cav
	 * @param dataName
	 * @param allInputs
	 * @param effector
	 */
	public DataAgent(CAV cav, String dataName, List<String> allInputs, EffectorAgent effector) {
		this.cav = cav;
		this.dataName = dataName;
		this.morphs = new TreeMap<>();
		this.morphActifs = new ArrayList<>();
		this.submissed = false;
		this.neighbours = new TreeMap<>();
		this.inputInSituation = new ArrayList<>();
		this.dataInSituation = new ArrayList<>();

		this.effector = effector;

		/*for(String s : allInputs) {
			this.morphs.put(s,new MorphingAgent(dataName, s));
		}*/
	}

	public void perceive() {
		// si ma donnee est disponible, je suis actif
		this.myValue = null;
		// Restart the constaint
		this.dataConstraint.restart();		

		////////////////////////////////////////////////////////////////////
		// changement, on essai avec dataperceivedinsituation
		//this.actif = this.cav.getCurrentSituation().getInformationAvailable(this.cav.getCurrentTime()).contains(this.dataName);
		this.actif = this.cav.getDataPerceivedInSituation().contains(this.dataName);

		// reinit des collections
		this.inputInSituation.clear();
		this.dataInSituation.clear();
		this.inputInSituation.addAll(this.cav.getInputInSituation());
		this.dataInSituation.addAll(this.cav.getDataPerceivedInSituation());

		//on cree les morphs
		if(!this.morphs.keySet().containsAll(this.dataInSituation)) {
			for(String other : this.cav.getDataPerceivedInSituation()) {
				if(!this.morphs.containsKey(other) && !other.equals(this.dataName)) {
					this.morphs.put(other, new DataMorphAgent(this.dataName, other, this));
				}
			}
		}

		this.wantToCoal = false;
		this.morphActifs.clear();
		this.submissed = this.coalition != null;


		//creationMorphingAgent ?
		if(this.actif) {
			this.myValue = this.cav.getValueOfData(this.dataName);


			// j'active les morphs pour toutes les donnes disponibles
			for(DataMorphAgent morph : this.morphs.values()) {
				if(this.dataInSituation.contains(morph.getInput())) {
					this.morphActifs.add(morph);
				}
			}

			if(min == null || this.min > this.myValue) {
				this.min = this.myValue;
			}
			if(max == null || this.max < this.myValue) {
				this.max = this.myValue;
			}
		}
	}

	public void decide() {
		if(this.actif) {
			// decide which input
			// Decider de faire une coalition



			// evaluer sa coalition
			float sumUse = 0.0f;
			int nbOther = 0;
			if(this.submissed) {

				if(this.coalition.getAllData().size()>1) {
					for(DataAgent other : this.coalition.getOtherDataAgent(this)) {
						sumUse += this.morphs.get(other.dataName).getUsefulness();
						nbOther++;
					}
					if(sumUse / nbOther < SEUIL_LEAVE) {
						this.coalition.leave(this);
						System.gc();
					}
				}

				//Copier coller foireux
				/*for(DataAgent other : this.coalition.getOtherDataAgent(this)) {
					sumUse += this.morphs.get(other.dataName).getUsefulness();
					nbOther++;
				}
				if(sumUse / nbOther < SEUIL_LEAVE) {
					this.coalition.leave(this);
					this.coalition = null;
					this.submissed = false;
					System.gc();
				}
				else {
				}*/
			}

			if(!this.submissed) {
				DataMorphAgent best = null;
				float bestUseful = -1.0f;
				for(DataMorphAgent morph : this.morphActifs) {
					if(!morph.getInput().equals(this.dataName) && morph.getUsefulness() > bestUseful) {
						bestUseful = morph.getUsefulness();
						best = morph;
					}
				}
				// morphvalue for the input
				this.morphedValue = best.morph(this.myValue);

				if(bestUseful > SEUIL_COAL) {
					this.wantToCoal = true;
					//this.neighbours.get(best.getInput()).proposeCoalition(this.dataName, this.morphedValue);
					if(this.cav.applyForCoalition(this.dataName, best.getMorphValue(), best.getInput())) {
						this.submissed = true;
					}
				}

			}
			// met a jour l'objectif pour la coalition
			this.maxUseful = -1.0f;
			for(DataMorphAgent agent : this.morphActifs) {

				agent.cycleValue(agent.getInput());

				if(this.inputInSituation.contains(agent.getInput()) && agent.getUsefulness()> this.maxUseful) {
					this.maxUseful = agent.getUsefulness();
					this.inputObj = agent.getInput();


				}
			}
		}
	}

	public void RemoveFromCoalition() {
		this.coalition = null;
		this.submissed = false;
		this.cav.createOwnCoalition(dataName);
	}

	/**
	 * Receive a proposition to create a coalition btween the asker and itself
	 * 
	 * @param asker
	 * 			The other datagent initiating the coalition
	 * @param value
	 * 			The value of the asker the closest to this value
	 * @return 
	 */
	public boolean proposeCoalition(String asker, float value) {
		this.submissed = this.coalition != null;
		this.myValue = this.cav.getValueOfData(this.dataName);
		if(!this.submissed) {
			if(this.valueClosedToMine(value) && this.morphs.get(asker).getUsefulness() > SEUIL_LEAVE) {
				this.cav.createCoalition(this.dataName, asker);
				return true;
			}

		}
		else {
			if(this.valueClosedToMine(value)) {
				return this.coalition.proposeNewAgent(asker);
			}
		}
		return false;
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
		/*System.out.println("MINE : "+this.myValue);
		System.out.println("ITS : "+value);
		System.out.println("MAX : "+this.max + " MINI "+this.min);
		System.out.println(""+Math.abs(value-this.myValue)+" < "+DataAgent.SEUILMORPH * (this.max- this.min)/100);*/
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
		if(!this.morphs.containsKey(asker)) {
			System.out.println(this.dataName);
			System.out.println(asker);
		}
		return this.morphs.get(asker).getUsefulness();
	}

	public void mergeToCoalition(CoalitionAgent coalition2) {
		//this.coalition = coalition2;

		coalition2.addData(this);
		//this.submissed = true;
		this.bindToCoalition(coalition2);
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

		//this.cycle();
	}

	public void cycle() {
		this.perceive();
		this.decide();
		this.act();

	}

	public Float askValue() {
		return myValue;
	}

	public DataUnicityConstraint getDataUnicityConstraint() {
		return this.dataConstraint;
	}

	public InputConstraint getInputConstraint(String inputName) {
		return this.cav.getInputConstraint(inputName);
	}

	public void updateMatrix(String inputName, String dataName2, float usefulness) {
		// TODO Auto-generated method stub

	}

	public float askMorphedValue(String input) {
		return this.morphs.get(input).getMorphValue();
	}

	public float askMorphUsefulness(String input) {
		return this.morphs.get(input).getUsefulness();
	}


	public int getCurrentTime() {
		return this.cav.getCurrentTime();
	}

	public List<CompetitiveAgent> getNeighboursActives(String inputName) {
		return this.cav.getCompetitiveAgentActives(inputName);
	}

	public void newDataSeen(String other) {
		this.morphs.put(other, new DataMorphAgent(this.dataName, other, this));
	}

	public String getDataName() {
		return this.dataName;
	}

	public Collection<? extends CompetitiveAgent> getAllMorphActives() {
		List<DataMorphAgent> res = new ArrayList<>();
		if(!this.submissed) {
			res.addAll(this.morphActifs);
		}
		return res;
	}

	public String getInputObj() {
		return this.inputObj;
	}

	/**
	 * Gives the feedback to morphs
	 * 
	 * @param tolerant
	 */
	public void sendFeedBackToMorphs(boolean tolerant) {
		if(this.myValue != null && this.dataInSituation.contains(this.dataName)){
			for(String input : this.cav.getDataPerceivedInSituation()) {
				if(this.morphs.keySet().contains(input)) {
					this.morphs.get(input).sendFeedback(this.cav.getValueOfData(input),tolerant);
				}
			}
		}
	}

	/**
	 * Get the DataMorphAgents which are active because the input is asked
	 * @return
	 */
	public Collection<? extends CompetitiveAgent> getAllMorphInCompet() {
		List<DataMorphAgent> res = new ArrayList<>();
		//if(!submissed) {
		for(String input : this.cav.getInputInSituation()) {
			res.add(this.morphs.get(input));
		}
		//}
		return res;
	}

	public void updateMatrix(Matrix matrix) {
		for(DataMorphAgent morph : this.morphActifs) {
			matrix.setWeight(morph.getInput(), this.dataName, morph.getUsefulness());
		}
	}

	public String askMorphLR(String input) {
		if(this.morphs.keySet().contains(input)) {
			return this.morphs.get(input).getMorphLRFormula();
		}
		return "NONE";
	}

	@Override
	public String toString() {
		return "DataAgent [dataName=" + dataName + "]";
	}

	/**
	 * The agent bind to the coalition agent
	 * @param coalitionAgent
	 */
	public void bindToCoalition(CoalitionAgent coalitionAgent) {
		for(DataMorphAgent morph : this.morphs.values()) {
			morph.clearOffer();
		}
		this.coalition = coalitionAgent;
		this.submissed = true;

		this.dataConstraint = this.coalition.getConstraint();
	}

	public Collection<? extends DataMorphAgent> getAllMorphs() {
		return this.morphs.values();
	}

	/**
	 * Run the cycle offer of all morph agent actif
	 */
	public void cycleOffer() {
		this.morphActifs.clear();
		for(String input : this.cav.getInputInSituation()) {
			if(this.morphs.containsKey(input)) {
				this.morphActifs.add(this.morphs.get(input));
			}
		}
		Collections.shuffle(morphActifs);
		for(DataMorphAgent morph : morphActifs) {
			morph.cycleOffer();
		}
	}

	public boolean isSubmissed() {
		return this.coalition != null;
	}

	public CompetitiveAgent getCoalition() {
		return this.coalition;
	}

	public void computeValueForInput(String input) {
		this.morphs.get(input).cycleValue(input);

	}

	/**
	 * Return if the dataAgent has a morph agent for the given data
	 * 
	 * @param data
	 * 		The data name
	 * 
	 * @return true if the morph exist
	 */
	public boolean hasMorphForData(String data) {
		return this.morphs.containsKey(data);
	}

	/**
	 * Remove all offer from morph agent
	 */
	public void removeAllOffer() {
		for(DataMorphAgent morph : this.morphActifs) {
			if(morph == null) {
				System.out.println(this.morphActifs);
			}
			morph.clearOffer();
		}
	}


}
