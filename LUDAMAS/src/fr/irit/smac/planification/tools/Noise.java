package fr.irit.smac.planification.tools;

import java.util.Random;

public class Noise {


	public static float nextGaussian() {
		float nextNextGaussian = 0.0f;
		boolean haveNextNextGaussian = false;
		Random rand = new Random();
		if (haveNextNextGaussian) {
			haveNextNextGaussian = false;
			return nextNextGaussian;
		} else {
			float v1, v2, s;
			do {
				v1 = (float) (2 * rand.nextDouble() - 1);   // between -1.0 and 1.0
				v2 = (float) (2 * rand.nextDouble() - 1);   // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			float multiplier = (float) StrictMath.sqrt(-2 * StrictMath.log(s)/s);
			nextNextGaussian = (float) (v2 * multiplier);
			haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}
	
	public static void main(String args[]) {
		for(int i =0; i < 10; i++) {
			System.out.println(Noise.nextGaussian());
		}
	}
}
