package fr.irit.smac.planification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.planification.matrix.Offer;

public class Planing {

	List<Result> plan;
	private Integer nbRes;

	private Map<String,String> exteroChosen;

	private final float tolerance = 50.f;

	public Planing() {
		plan = new ArrayList<Result>();
		this.exteroChosen = new TreeMap<>();
		this.nbRes = 0;
	}

	public Planing(Planing other) {
		this.plan = new ArrayList<>(other.plan);
		this.nbRes = this.plan.size();
		this.exteroChosen = new TreeMap<>();
		for(String s : other.exteroChosen.keySet()) {
			this.exteroChosen.put(s, other.exteroChosen.get(s));
		}
	}

	public void addRes(Result res) {
		this.plan.add(res);
		this.nbRes++;
	}

	public Result getResAtTime(Integer time) {
		Result ret = null;
		for(Result res : this.plan) {
			if(res.getStep()==time) {
				ret = res;
			}
		}
		return ret;
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
		else {
			this.plan.add(time,res);
			nbRes++;
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

	public Result getFirstRes() {
		return this.plan.get(0);
	}

	public Result getLastRes() {
		return this.plan.get(this.plan.size()-1);
	}

	public void addExteroData(String data, String extero) {
		this.exteroChosen.put(data, extero);
	}

	public List<Result> getPlan() {
		return plan;
	}

	public Integer getNbRes() {
		return nbRes;
	}

	public Map<String, String> getExteroChosen() {
		return exteroChosen;
	}

	public boolean isIdenticalToLast(Planing other) {
		boolean res = true;
		for(int i =0; i < this.plan.size()-1 && i < other.size();i++) {
			res = false;
		}

		return res;
	}

	public boolean isTolerant(Planing other) {
		boolean res = true;
		for(int i =0; i < this.plan.size()-1 && i < other.size();i++) {
			if(this.plan.get(i).getValue() != other.plan.get(i+1).getValue()) {
				float diffPourcent = Math.abs(((this.plan.get(i).getValue() - other.plan.get(i+1).getValue())/other.plan.get(i+1).getValue()*100));
				if(diffPourcent > this.tolerance) {
					res = false;
				}
			}
		}
		return res;
	}

	public float isAlmostIdenticalToLast(Planing other) {
		boolean res = true;
		float diffCumulative = 0.0f;
		for(int i =0; i < this.plan.size()-1 && i < other.size();i++) {
			if(this.plan.get(i).getValue() != other.plan.get(i+1).getValue()) {
				diffCumulative += Math.abs(this.plan.get(i).getValue() - other.plan.get(i+1).getValue());
			}
		}
		return diffCumulative;
	}

	public static void main(String args[]) {
		Planing p1 = new Planing();
		Planing p2 = new Planing();
		for(int i =0; i < 5;i++) {
			p1.addRes(new Result(i, (float)i));
		}
		for(int i =0; i < 5;i++) {
			p2.addRes(new Result(i, (float)i+1));
		}
		System.out.println(p1.isIdenticalToLast(p2));
	}

	public void setExteroChosen(String input, String data) {
		this.exteroChosen.put(input, data);
	}

	public boolean isUnderstandedInput(String in) {
		return this.exteroChosen.get(in).equals(in);
	}

}
