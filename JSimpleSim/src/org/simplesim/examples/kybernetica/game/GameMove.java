/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.kybernetica.game;

/**
 * 
 *
 */
public class GameMove {
	
	private final int technology, production, education, lifeQuality;
	
	public GameMove(int t, int p, int e, int lq) {
		technology=t;
		production=p;
		education=e;
		lifeQuality=lq;
	}

	/**
	 * Points spent in production
	 * <p>
	 * Note: Production may be reduced and value might be negative. 
	 */
	public int getProduction() {
		return production;
	}

	/**
	 * Points spent in life quality
	 */
	public int getLifeQuality() {
		return lifeQuality;
	}
	
	/**
	 * Points spent in technology
	 */
	public int getTechnology() {
		return technology;
	}
	
	
	/**
	 * Points spent in education.
	 */
	public int getEducation() {
		return education;
	}
	
	
	/**
	 * Calculates the overall points spent.
	 * <p>
	 * Note: Production may be reduced and value might be negative. 
	 */
	public int getTotal() {
		return getTechnology()+Math.abs(getProduction())+getEducation()+getLifeQuality();
	}

}
