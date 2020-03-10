package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Planing {

	List<Result> plan;
	private Integer nbRes;
	
	public Planing() {
		plan = new ArrayList<Result>();
		this.nbRes = 0;
	}
	
	public void addRes(Result res) {
		this.plan.add(res);
		this.nbRes++;
	}
	
	public Result getResAtTime(Integer time) {
		if(time < this.nbRes)
			return this.plan.get(time);
		else
			return null;
	}
	
	public Result getNextResult(Integer time) {
		Result res = null;
		int i =0;
		boolean found = false;
		while(i < this.nbRes && !found)
			if(this.plan.get(i).getStep() > time) {
				found = true;
				res = this.plan.get(i);
		}
		return res;
	}
	
	public void setResAtTime(Integer time,Result res) {
		if(this.nbRes > time) {
			this.plan.add(time, res);
		}
	}
	
	public void insertObjAtTime(Integer time, Result res) {
		this.plan.add(time,res);
		this.nbRes++;
	}
	
	public int size() {
		return this.nbRes;
	}
	
	@Override
	public String toString() {
		return "Planing [plan=" + plan + ", nbRes=" + nbRes + "]";
	}

}
