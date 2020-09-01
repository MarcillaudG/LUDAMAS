package fr.irit.smac.planification.agents;

public class AVTAgent {

	private enum Feedback {UP,DOWN,EQUALS};
	
	private CoalitionAgent coalition;
	
	private DataAgent dataAgent;
	
	private Float weight;
	
	private float currentValue;

	private final int WINDOW_TIME = 10;
	
	private final float accelerationCoeff;

	private final float deccelerationCoeff;
	
	private float delta;
	
	private final float delta_min = 0.01f;
	
	private final float delta_max = 0.20f;
	
	private Float variation;
	
	private Float weightedValue;
	
	private Feedback historic[]; 
	
	private int indHisto;
	
	public AVTAgent(CoalitionAgent coal, DataAgent dataAgent) {
		this.coalition = coal;
		this.dataAgent = dataAgent;
		this.historic = new Feedback[WINDOW_TIME];
		this.indHisto = 0;
		this.weight = 1.0f;
		this.accelerationCoeff = 2.0f;
		this.deccelerationCoeff = 1.0f/3.0f; 
		this.delta = 0.1f;
	}
	
	public AVTAgent(CoalitionAgent coal, DataAgent dataAgent, float acceCoeff, float deceCoeff) {
		this.coalition = coal;
		this.dataAgent = dataAgent;
		this.historic = new Feedback[WINDOW_TIME];
		this.indHisto = 0;
		this.weight = 1.0f;
		this.accelerationCoeff = acceCoeff;
		this.deccelerationCoeff = deceCoeff;
		this.delta = 0.1f;
	}

	public CoalitionAgent getCoalition() {
		return coalition;
	}

	public DataAgent getDataAgent() {
		return dataAgent;
	}

	public Float getWeight() {
		return weight;
	}
	
	public void cycle() {
		this.perceive();
		this.decide();
		this.act();
	}


	private void perceive() {
		this.currentValue = this.dataAgent.askMorphedValue(this.coalition.getInput());
	}
	
	private void decide() {
		this.weightedValue = this.currentValue * this.weight;
	}

	private void act() {
		this.coalition.sendValueFromAVTAgent(this.weightedValue,this.dataAgent.getDataName());
	}
	
	/**
	 * Get the feedback and the value of the coalition to add in historic
	 * 
	 * @param feed
	 * 			The feedback can be -1 0 and 1
	 * @param value
	 * 			The value of the coalition
	 */
	public void sendFeedback(int feed, float value) {
		if(feed > 0) {
			if(value > this.currentValue) {
				this.historic[this.indHisto] = Feedback.UP;
			}
			if(value <= this.currentValue) {
				this.historic[this.indHisto] = Feedback.DOWN;
			}
		}
		if(feed < 0) {
			if(value < this.currentValue) {
				this.historic[this.indHisto] = Feedback.UP;
			}
			if(value >= this.currentValue) {
				this.historic[this.indHisto] = Feedback.DOWN;
			}
		}
		if(feed == 0) {
			this.historic[this.indHisto] = Feedback.EQUALS;
		}
		this.adaptWeightAVT();
		//this.adaptWeight(value);
	}

	public void sendFeedback(Float trueValueForInput) {
		this.adaptWeight(trueValueForInput);
		
	}

	private void adaptWeight(float value) {
		this.weight = 1.0f - Math.abs((this.currentValue - value)) / value;
	}

	/**
	 * Add one to the indHisto
	 */
	private int advanceIndHisto() {
		return (this.indHisto + 1) % WINDOW_TIME;
	}

	/**
	 * Use AVT to adapt the weight and delta
	 * 
	 * Yes I know, it is a long method, I hate it too
	 */
	public void adaptWeightAVT() {
		if(this.historic[this.indHisto] != null) {
			if(this.historic[this.advanceIndHisto()] != null) {
				switch(this.historic[this.indHisto]) {
				case DOWN:
					switch(this.historic[this.advanceIndHisto()]) {
					case DOWN:
						this.delta = this.delta * this.accelerationCoeff;
						this.weight -= this.delta;
						break;
					case EQUALS:
						this.delta = this.delta * this.deccelerationCoeff;
						break;
					case UP:
						this.delta = this.delta * this.deccelerationCoeff;
						this.weight += this.delta;
						break;
					default:
						break;
					}
					break;
				case EQUALS:
					switch(this.historic[this.advanceIndHisto()]) {
					case DOWN:
						this.weight -= this.delta;
						break;
					case EQUALS:
						this.delta = this.delta * this.deccelerationCoeff;
						break;
					case UP:
						this.weight += this.delta;
						break;
					default:
						break;
					}
					break;
				case UP:
					switch(this.historic[this.advanceIndHisto()]) {
					case DOWN:
						this.delta = this.delta * this.deccelerationCoeff;
						this.weight -= this.delta;
						break;
					case EQUALS:
						this.delta = this.delta * this.deccelerationCoeff;
						break;
					case UP:
						this.delta = this.delta * this.accelerationCoeff;
						this.weight += this.delta;
						break;
					default:
						break;
					}
					break;
				default:
					break;
				
				}
			}
			else {
				switch(this.historic[this.indHisto]) {
				case DOWN:
					this.weight = this.weight - this.delta;
					break;
				case EQUALS:
					break;
				case UP:
					this.weight = this.weight + this.delta;
					break;
				default:
					break;
				
				}
			}
		}
		
			
		this.indHisto = this.advanceIndHisto();
	}

	public float getValue() {
		return this.weightedValue;
	}


}
