package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.irit.smac.planification.system.CAV;

public class CoalitionAgent {

	private String name;
	
	private int id;
	
	private CAV cav;
	
	private List<MorphingAgent> morphs;

	private List<MorphingAgent> morphsActifs;
	
	
	public CoalitionAgent(int id, CAV cav, MorphingAgent morph1, MorphingAgent morph2) {
		this.id = id;
		this.cav = cav;
		
		this.morphs = new ArrayList<>();
		this.morphs.add(morph1);
		this.morphs.add(morph2);
		
		this.morphsActifs = new ArrayList<>();
	}
	
	public void addMorph(MorphingAgent morph) {
		this.morphs.add(morph);
	}
	
	public void removeMorph(MorphingAgent morph) {
		this.morphs.remove(morph);
	}
	
	public void perceive() {
		this.morphsActifs.clear();
		for(MorphingAgent morph : this.morphs) {
			if(morph.isActif()) {
				this.morphsActifs.add(morph);
			}
		}
	}
	
	public void decide() {
		
		// Find the morph that would be their champion
		Collections.shuffle(this.morphsActifs);
		MorphingAgent best = null;
		float useful = -1.0f;
		for(MorphingAgent morph: this.morphsActifs) {
			if(morph.getUsefulness() > useful) {
				best = morph;
				useful = morph.getUsefulness();
			}
		}
		float ponderedSum = 0.0f;
		for(MorphingAgent morph: this.morphsActifs) {
			ponderedSum += morph.getMorphValue();
		}
	}
	
	public void act() {
		
	}
}
