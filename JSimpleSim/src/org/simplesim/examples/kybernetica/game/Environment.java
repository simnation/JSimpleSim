/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.kybernetica.game;

import org.simplesim.core.messaging.MultiPort;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 * 
 *
 */
public class Environment extends AbstractAgent<EnvironmentState, Environment.EVENT> {
	
	
	/**
	 * @param s
	 */
	public Environment(EnvironmentState s) {
		super(s);
		setInport(new SinglePort(this));
		setOutport(new SinglePort(this));
	}

	enum EVENT { start }

	/* (non-Javadoc)
	 * @see org.simplesim.model.AbstractAgent#doEvent(org.simplesim.core.scheduling.Time)
	 */
	@Override
	protected Time doEvent(Time time) {
		if (!hasExternalInput()||getState().finished()) return null;
		GameMove gm=getInport().poll().getContent();
		getState().technology+=gm.getTechnology();
		getState().production+=gm.getProduction();
		getState().education+=gm.getEducation();
		getState().lifeQuality+=gm.getLifeQuality();
		getState().actionPoints-=gm.getTotal();
		
		getState().environmentalStress+=check(curve01,getState().getTechnology());
		getState().technology+=check(curve02,getState().getTechnology());
		getState().production+=check(curve03,getState().getProduction());
		getState().environmentalStress+=check(curve04,getState().getProduction());
		getState().environmentalStress+=check(curve05,getState().getEnvironmentalStress());
		getState().lifeQuality+=check(curve06,getState().getEnvironmentalStress());
		getState().education+=check(curve07,getState().getEducation());
		getState().lifeQuality+=check(curve08,getState().getEducation());
		
		// consider self-determination effect (family planning) if education level is above 20: growth rate may also be reduced
		// try to keep it around the optimum of 15
		if ((getState().getEducation()>=21)&&(getState().growthRate>15))
			getState().growthRate-=check(curve09,getState().getEducation());
		else getState().growthRate+=check(curve09,getState().getEducation());
	
		getState().lifeQuality+=check(curve10,getState().getLifeQuality());
		getState().growthRate+=check(curve11,getState().getLifeQuality());
		getState().policy+=check(curve12,getState().getLifeQuality());
		
		getState().policy+=check(growthFactor,getState().getPopulation())*check(curve13,getState().getGrowthRate());
		
		getState().lifeQuality+=check(curve14,getState().getPopulation());
		
		getState().actionPoints+=check(curveA,getState().getPopulation());
		getState().actionPoints+=check(curveB,getState().getPolicy()+10); // offset of 10 points to array index in policy
		getState().actionPoints+=check(curveC,getState().getProduction());
		getState().actionPoints+=check(curveD,getState().getLifeQuality());
		
		return null;
	};
	
	
	
	
	/**
	 * Checks bounds of index and sets game end flag if index is out of range.
	 * 
	 * @param table the mapping table 
	 * @param index the index within the table
	 * @return look-up value form table or 0 if index out of bounds
	 */
	private int check(int[] table,int index) {
		if ((index>0)&&(index<table.length)) return table[index];
		getState().stop=true;
		return 0;
	}
	
	public final static int S=Integer.MIN_VALUE; // mapping always start with index of 1
	
	private final int[] curve01 = new int[]{ S, 0, 0,-1,-1,-1,-1,-1,-2,-2,-2,-2,-2,-3,-3,-3,-3,-3,-4,-4,-4,-5,-5,-5,-6,-6,-7,-7,-8,-9 };
	private final int[] curve02 = new int[]{ S, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1,-2,-3,-3,-4,-5,-6,-6,-6 };
	private final int[] curve03 = new int[]{ S, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1,-10};
	private final int[] curve04 = new int[]{ S, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 9,10,12,14,18,22 };
	private final int[] curve05 = new int[]{ S, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-2,-2,-2,-2,-2,-2,-3,-3,-3,-3,-4,-3,-2,-1, 0, 0 };
	private final int[] curve06 = new int[]{ S, 2, 1, 0, 0, 0, 0, 0,-1,-1,-1,-2,-2,-2,-2,-3,-3,-3,-4,-4,-5,-5,-6,-7,-8,-10,-12,-14,-18,-25};
	private final int[] curve07 = new int[]{ S, 0, 0,-1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 0 };
	private final int[] curve08 = new int[]{ S,-2,-2,-2,-2,-2,-1,-1,-1,-1, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6 };
	private final int[] curve09 = new int[]{ S, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5 };
	private final int[] curve10 = new int[]{ S, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 0, 0,-1,-1,-1,-1,-1,-2,-2,-2,-1,-1,-1, 0 };
	private final int[] curve11 = new int[]{ S,-15,-8,-6,-4,-3,-2,-1,0, 1, 2, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private final int[] curve12 = new int[]{ S,-10,-8,-6,-4,-2,-1,-1,-1,-1,0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5 };
	private final int[] curve13 = new int[]{ S,-4,-4,-3,-3,-3,-2,-2,-2,-2,-1,-1,-1,-1,-1, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 };
	private final int[] curve14 = new int[]{ S,-5,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-2,-2,-2,-2,-2,-3,-3,-3,-3,-3,-3,-3,-3,-4,-4,-4,-4,-5,-5,-6,-7,-8,-10 };

	private final int[] curveA = new int[]{ S, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9 };
 	private final int[] curveB = new int[]{ S,-5,-2,-1,-1,-1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
	private final int[] curveC = new int[]{ S,-4,-3,-2,-1, 0, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 9,10,11, 0,-5 };
	private final int[] curveD = new int[]{ S,-6,-4,-2, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5 };
	
	private final int[] growthFactor = new int[]{ S, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };

	
	
}
