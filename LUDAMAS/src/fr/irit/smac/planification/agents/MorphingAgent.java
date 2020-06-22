package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.Input;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.tools.LinearRegression;

public class MorphingAgent implements CompetitiveAgent{


	private String dataName;

	private String inputName;

	private float morphValue;

	private String name;

	private EffectorAgent superiorAgent;

	private Matrix matrix;

	private Float value;

	private float usefulness;

	private Map<Integer, Pair<Float,Float>> historic;

	private Map<Float,Float> distribution;

	private InputConstraint inputConstraint;

	private DataUnicityConstraint dataConstraint;

	private List<MorphingAgent> neighbours;

	private LinearRegression lr;

	private final float sensibility = 10.f;

	private float etendu;

	private boolean isActif;

	public MorphingAgent(String dataName, String inputName, EffectorAgent eff, Matrix mat) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.superiorAgent = eff;
		this.matrix = mat;
		this.morphValue = 1.0f;
		this.usefulness = 0.5f;
		this.etendu = 1.0f;


		this.name = inputName+":"+dataName;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
		this.neighbours = new ArrayList<>();
	}

	public MorphingAgent(String dataName, String inputName) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.morphValue = 1.0f;
		this.usefulness = 0.5f;
		this.etendu = 1.0f;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
	}

	public MorphingAgent(String dataName, String inputName, EffectorAgent eff, Matrix mat, float value) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.superiorAgent = eff;
		this.matrix = mat;
		this.morphValue = 1.0f;
		this.usefulness = value;
		this.etendu = 1.0f;

		this.name = inputName+":"+dataName;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
		this.neighbours = new ArrayList<>();
	}

	public void perceive() {
		this.value = null;
		// voit sa valeur
		this.value = this.superiorAgent.askValue(this.dataName);

		// voit son utilite
		//this.usefulness = this.matrix.getMatrix().get(new Input(this.inputName,0)).get(this.dataName);

		// Recupere les deux contraintes
		this.dataConstraint = this.superiorAgent.getDataUnicityConstraint(this.dataName);
		this.inputConstraint = this.superiorAgent.getInputConstraint(this.inputName);

		List<MorphingAgent> others = new ArrayList<>(this.superiorAgent.getMorphlingActive());
		others.remove(this);

		this.neighbours.clear();
		this.neighbours.addAll(others);
	}

	public void decide() {
		// si valeur != null
		if(this.value !=null) {
			Offer myOffer = new Offer(this,this.inputConstraint,this.superiorAgent.getCurrentStep(),this.usefulness);
			if(!this.dataConstraint.hasMyOffer(this) && !this.inputConstraint.hasMyOffer(this)) {
				if(this.dataConstraint.isOfferBetter(myOffer) && this.inputConstraint.isOfferBetter(myOffer)) {
					this.dataConstraint.addOffer(myOffer);
					this.inputConstraint.addOffer(myOffer);
				}
			}
			else {
				// Cas remove
				if(!this.inputConstraint.isSatisfied()) {
					if(!this.dataConstraint.isOfferBetter(myOffer) || !this.inputConstraint.isOfferBetter(myOffer)) {
						this.dataConstraint.removeOffer(myOffer);
						this.inputConstraint.removeOffer(myOffer);
					}
					else {
						System.out.println("MERDEs");
					}
				}
				if(!this.dataConstraint.isSatisfied()) {
					if(!this.inputConstraint.isSatisfied()) {
						this.dataConstraint.removeOffer(myOffer);
						this.inputConstraint.removeOffer(myOffer);
					}
					else {
						// SOLVE SNC
						MorphingAgent oth = null;
						for(MorphingAgent morph: this.neighbours) {
							if(morph.inputName.equals(this.inputName) && morph.dataConstraint.isSatisfied()) {
								if(oth == null || morph.usefulness > oth.usefulness) {
									oth = morph;
								}
							}
						}
						if(oth != null) {
							this.dataConstraint.removeOffer(myOffer);
							this.inputConstraint.removeOffer(myOffer);
							oth.decide();
						}
					}
				}
			}
		}

	}

	public void act() {
		// si lie
		this.morphValue = this.linearRegression();
		// alors envoyer valeur transformee
		if(this.inputConstraint.hasMyOffer(this) && this.dataConstraint.isSatisfied() && this.inputConstraint.isSatisfied()) {
			//this.morphValue = this.dico();
			//float valueToSend = this.value * this.morphValue;
			float valueToSend = this.value;
			if(this.lr != null) {
				valueToSend = this.morphValue;
			}
			this.superiorAgent.sendValueToDecisionProcessLinks(this,valueToSend);
		}
		//System.out.println(valueToSend);
	}

	public void sendFeedback(float correctValue) {
		this.addMorph(this.value, correctValue);
		if(correctValue == this.value * this.morphValue || (this.lr != null && correctValue == this.lr.predict(this.value))) {
			this.usefulness = Math.min(1.0f, this.usefulness+0.1f);
		}
		else {
			this.usefulness = Math.max(.0f, this.usefulness-0.1f);
			if(lr != null && this.dataName.contains(this.inputName)) {
				/*System.out.println("--------------"+inputName + "-> "+ dataName);
				System.out.println("CORRECT :"+correctValue);
				System.out.println("MINE :"+this.value);
				System.out.println("MORPHED :"+this.lr.predict(this.value));
				System.out.println(this.historic);*/
				/*try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
		this.superiorAgent.updateMatrix(this.inputName,this.dataName,this.usefulness);

	}


	public void sendFeedback(Float correctValue, boolean tolerant) {
		this.addMorph(this.value, correctValue);
		float diffPourcent = this.sensibility+1;

		if(lr != null) {
			diffPourcent = Math.abs(((correctValue - this.lr.predict(this.value))/this.lr.predict(this.value)*100));
			if(lr != null && this.dataName.contains(this.inputName)) {
				System.out.println(this.name);
				System.out.println("DIFF:"+diffPourcent);
				System.out.println("VALUE:"+this.value);
				System.out.println("CORRECT:"+correctValue);
				System.out.println("predict:"+this.lr.predict(this.value));
				System.out.println("ETENDU:"+this.etendu);
				if(diffPourcent < this.sensibility*this.etendu/100) {
					System.out.println("UP");
				}
			}
		}
		if(correctValue == this.value * this.morphValue || (this.lr != null && correctValue == this.lr.predict(this.value)) 
				||  diffPourcent < this.sensibility*this.etendu/100) {
			this.usefulness = Math.min(1.0f, this.usefulness+0.05f);
		}
		else {
			this.usefulness = Math.max(.0f, this.usefulness-0.05f);
			if(lr != null && this.dataName.contains(this.inputName)) {
				/*System.out.println("--------------"+inputName + "-> "+ dataName);
				System.out.println("CORRECT :"+correctValue);
				System.out.println("MINE :"+this.value);
				System.out.println("MORPHED :"+this.lr.predict(this.value));
				System.out.println(this.historic);*/
				/*try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
		this.superiorAgent.updateMatrix(this.inputName,this.dataName,this.usefulness);

	}

	/**
	 * Recherche dans l'historique la valeur la plus proche
	 * et renvoi la valeur de transformation
	 * @return morphedValue
	 * 		la valeur de transformation
	 */
	private float dico() {
		float morphedValue = 1.0f;
		List<Float> toDico = new ArrayList<>(this.distribution.keySet());
		Collections.sort(toDico);
		//System.out.println(toDico);
		boolean found = false;
		int ind =toDico.size() /2;
		int borneSup = toDico.size()-1;
		int borneInf = 0;
		while (!found && borneSup > borneInf && toDico.size() > 0) {
			ind = (borneSup+borneInf)/2;
			if(toDico.get(ind) == this.value) {
				found = true;
				morphedValue = this.distribution.get(toDico.get(ind));
			}
			else {
				if(borneSup - borneInf   == 1) {
					found = true;
					if(this.value -toDico.get(borneInf) < toDico.get(borneSup) - this.value) {
						ind = borneInf;
						morphedValue = this.distribution.get(toDico.get(ind));
					}
					else {
						ind = borneSup;
						morphedValue = this.distribution.get(toDico.get(ind));
					}
				}
				else {
					if(this.value > toDico.get(ind)) {
						borneInf = ind;
					}
					else {
						borneSup = ind;
					}
				}
			}
		}
		if(toDico.size() == 1) {
			morphedValue = this.distribution.get(toDico.get(0));
		}
		return morphedValue;
	}

	private float linearRegression() {
		double x [] = new double[this.historic.keySet().size()];
		double y [] = new double[this.historic.keySet().size()];

		float morphedValue = 1.0f;

		int i =0;
		//System.out.println(this.historic);
		for(Integer cycle : this.historic.keySet()) {
			x[i] = this.historic.get(cycle).getLeft();
			y[i] = this.historic.get(cycle).getRight();
			i++;
		}

		if(x.length > 1) {
			this.lr = new LinearRegression(x, y);

			morphedValue = (float) lr.predict(this.value);
			/*System.out.println(lr.slope());
			System.out.println(lr.R2());
			System.out.println(lr.intercept());
			System.out.println(this.value);
			System.out.println(lr.predict(this.value));*/
		}
		else {
			morphedValue = 1.0f;
		}

		return morphedValue;
	}

	/**
	 * A REVOIR
	 * @param myValue
	 * @param otherValue
	 */
	@Deprecated
	public void computeMorph(Float myValue, Float otherValue) {
		Float inf = null;
		Float sup = null;
		float ratio = myValue/otherValue;
		for(Float borne: this.distribution.keySet()) {
			if(inf == null && borne < myValue) {
				inf = borne;
			}
			if(inf != null && borne > inf && borne < myValue ) {
				inf = borne;
			}
			if(sup == null && borne > myValue) {
				sup = borne;
			}
		}
		if(inf != null && sup != null) {
			if(this.distribution.get(inf) != ratio ||this.distribution.get(sup) != ratio  ) {
				this.distribution.put(myValue, ratio);
			}
		}
		else {
			if ((inf == null && sup != null)) {
				this.distribution.put(myValue, ratio);
				if(this.distribution.keySet().size()>2 && this.distribution.get(sup)== ratio) {
					this.distribution.remove(sup);
				}
			}
			if ((inf != null && sup == null)) {
				this.distribution.put(myValue, ratio);
				if(this.distribution.keySet().size()>2 && this.distribution.get(inf)== ratio) {
					this.distribution.remove(inf);
				}
			}
		}
		if(this.distribution.keySet().size()==0) {
			this.distribution.put(myValue, ratio);
		}
	}


	public void addMorph(Float myValue, Float otherValue) {
		if(otherValue !=0)
			this.distribution.put(myValue, myValue/otherValue);
		else
			this.distribution.put(myValue, 0.f);

		this.historic.put(this.historic.keySet().size(), Pair.of(myValue, otherValue));

		Float max = null;
		Float min = null;
		if(this.historic.keySet().size()>1) {
			for(Pair<Float,Float> p : this.historic.values()) {
				if(max == null || p.getRight() > max) {
					max = p.getRight();
				}
				if(min == null || p.getRight() < min) {
					min = p.getRight();
				}
			}
		}
		if(min != null && max != null) {
			this.etendu = max -min;
		}
	}

	public static void main(String args[]) {
		MorphingAgent morphling = new MorphingAgent("Data", "Input");
		for(int i =0; i < 10; i++) {
			morphling.addMorph(10.f*(i+1), 15.f*(i+1)+i*10);
		}
		morphling.value = 35.f;
		//morphling.act();
		morphling.linearRegression();
		//System.out.println(morphling.morphValue);
	}

	public String getData() {
		return this.dataName;
	}

	public String getInput() {
		return this.inputName;
	}

	public void start(int currentStep) {
		this.perceive();
		this.decide();
		this.act();
	}

	@Override
	public String toString() {
		return "MorphingAgent:"+this.inputName+":"+this.dataName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataName == null) ? 0 : dataName.hashCode());
		result = prime * result + ((inputName == null) ? 0 : inputName.hashCode());
		result = prime * result + ((superiorAgent == null) ? 0 : superiorAgent.hashCode());
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
		MorphingAgent other = (MorphingAgent) obj;
		if (dataName == null) {
			if (other.dataName != null)
				return false;
		} else if (!dataName.equals(other.dataName))
			return false;
		if (inputName == null) {
			if (other.inputName != null)
				return false;
		} else if (!inputName.equals(other.inputName))
			return false;
		if (superiorAgent == null) {
			if (other.superiorAgent != null)
				return false;
		} else if (!superiorAgent.equals(other.superiorAgent))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public Float getMorphValue() {
		return this.morphValue;
	}

	public String getMorphLRFormula() {
		if(this.lr != null) {
			return this.lr.slope() +" * x + "+this.lr.intercept();
		}
		return "1.0";
	}

	public float getPredict() {
		if(lr != null) {
			return this.lr.predict(this.value);
		}
		else {
			return this.value;
		}
	}

	public void increaseUsefull() {
		this.usefulness = Math.min(1.0f, this.usefulness+0.05f);
	}
	
	public void decreaseUsefull() {
		this.usefulness = Math.max(.0f, this.usefulness-0.05f);
	}

	public boolean isActif() {
		return this.isActif;
	}
	
	public void activate() {
		this.isActif = true;
	}
	
	public void desactivate() {
		this.isActif = false;
	}
	
	public float getUsefulness() {
		return this.usefulness;
	}

	public float morph(float myValue) {
		if(lr != null) {
			return this.lr.predict(myValue);
		}
		else {
			return myValue;
		}
	}

	@Override
	public void sendOffer(Offer offer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAvailable() {
		return this.dataConstraint.isSatisfied();
	}


	@Override
	public void cycleOffer() {
		this.decide();
	}

	@Override
	public float getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCompetitiveName() {
		return this.name;
	}
}
