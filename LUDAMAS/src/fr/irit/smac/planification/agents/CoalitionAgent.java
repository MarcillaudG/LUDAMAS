package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.irit.smac.planification.system.CAV;

public class CoalitionAgent {

	private String name;
	
	private int id;
	
	private CAV cav;
	
	private List<DataAgent> datas;

	private List<DataAgent> datasActifs;
	
	private static final float SEUIL_REJECT = 0.2f;
	
	private static final float SEUIL_ACCEPT = 0.5f;
	
	
	public CoalitionAgent(int id, CAV cav, DataAgent data1, DataAgent data2) {
		this.id = id;
		this.cav = cav;
		
		this.datas = new ArrayList<>();
		this.datas.add(data1);
		this.datas.add(data2);
		
		this.datasActifs = new ArrayList<>();
	}
	
	public void addData(DataAgent data) {
		this.datas.add(data);
	}
	
	public void datas(DataAgent data) {
		this.datas.remove(data);
	}
	
	public void perceive() {
		this.datasActifs.clear();
		for(DataAgent data : this.datas) {
			if(data.isActif()) {
				this.datasActifs.add(data);
			}
		}
	}
	
	public void decide() {
		
		// Find the data that would be their champion
		/*Collections.shuffle(this.datasActifs);
		DataAgent best = null;
		float useful = -1.0f;
		for(DataAgent morph: this.datasActifs) {
			if(data.getUsefulness() > useful) {
				best = morph;
				useful = morph.getUsefulness();
			}
		}
		float ponderedSum = 0.0f;
		for(DataAgent morph: this.datasActifs) {
			ponderedSum += morph.getMorphValue();
		}*/
	}
	


	public void act() {
		
	}

	public void proposeNewAgent(String asker) {
		boolean accept = true;
		float sumUse = 0.f;
		for(DataAgent data : this.datas) {
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
		
	}

	public List<DataAgent> getOtherDataAgent(DataAgent data) {
		List<DataAgent> res = new ArrayList<>(this.datas);
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
		this.datas.get(0).coalitionDestroyed();
		this.datas.clear();
		this.cav.coalitionDestroyed(this);
	}
	
}
