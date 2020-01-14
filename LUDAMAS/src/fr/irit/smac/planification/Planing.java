package fr.irit.smac.planification;

import java.util.Map;
import java.util.TreeMap;

public class Planing {

	Map<Integer,Result> plan;
	private Integer nbRes;
	
	public Planing() {
		plan = new TreeMap<Integer,Result>();
		this.nbRes = 0;
	}
	
	public void addRes(Result res) {
		this.plan.put(this.nbRes,res);
		this.nbRes++;
	}
	
	public Result getResAtTime(Integer time) {
		if(time > this.nbRes)
			return this.plan.get(time);
		else
			return null;
	}
	
	public void setResAtTime(Integer time,Result res) {
		if(this.nbRes > time) {
			this.plan.put(time, res);
		}
	}
}
