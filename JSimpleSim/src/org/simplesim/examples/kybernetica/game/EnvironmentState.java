/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.kybernetica.game;

import org.simplesim.model.State;

/**
 * 
 *
 */
public class EnvironmentState implements State {
	
	// set the state to standard values
	int technology=1;
	int production=12;
	int environmentalStress=13;
	int education=4;
	int lifeQuality=10;
	int growthRate=20;
	int population=21;
	int policy=0;
	
	int actionPoints=8;
	
	boolean stop=false;
	
	public int getTechnology() {
		return technology;
	}
	
	public int getProduction() {
		return production;
	}
	
	public int getEducation() {
		return education;
	}
	
	public int getLifeQuality() {
		return lifeQuality;
	}
	
	public int getGrowthRate() {
		return growthRate;
	}
	
	public int getPolicy() {
		return policy;
	}
	
	public int getEnvironmentalStress() {
		return environmentalStress;
	}
	
	public int getPopulation() {
		return population;
	}
	
	public int getActionPoints() {
		return actionPoints;
	}
	
	public boolean finished() {
		return stop;
	}
	
	public String toString() {
		StringBuffer result=new StringBuffer();
		result.append("\n\nTechnology: ");
		result.append(getTechnology());
		result.append("\tProduction: ");
		result.append(getProduction());
		result.append("\tEnvironment: ");
		result.append(getEnvironmentalStress());
		result.append("\tEducation: ");
		result.append(getEducation());
		
		result.append("\nLife Quality: ");
		result.append(getLifeQuality());
		result.append("\tGrowth Rate: ");
		result.append(getGrowthRate());
		result.append("\tPopulation: ");
		result.append(getPopulation());
		result.append("\t\tPolicy: ");
		result.append(getPolicy());
		
		result.append("\nAction Points: ");
		result.append(getActionPoints());
		
		return result.toString();
	}
	
}
