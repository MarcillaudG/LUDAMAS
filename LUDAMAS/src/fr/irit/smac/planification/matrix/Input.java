package fr.irit.smac.planification.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Input implements Comparable{

	
	private String data;
	
	private int position;

	public Input(String data, int position) {
		this.data = data;
		this.position = position;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		Input other = (Input) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o) {
		Input in = (Input) o;
		if(in.position < this.position) {
			return 1;
		}
		if(in.position > this.position) {
			return -1;
		}
		return 0;
	}

	public static void main(String args[]) {
		Map<Input,Integer> test = new TreeMap<>();
		for(int i =0; i < 5; i++) {
			test.put(new Input("data"+i, i), i*10);
		}
		System.out.println(test.get(new Input("data2",0)));
	}
	
	
}
