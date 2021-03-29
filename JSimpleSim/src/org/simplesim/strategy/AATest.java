/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.strategy;

import java.util.ArrayList;
import java.util.List;

import org.simnation.agents.firm.Management.Action;
import org.simnation.agents.firm.Management.GoalVariable;
import static org.simnation.agents.firm.Management.INFLUENCE;;

/**
 *
 */
public class AATest {
	
	private int round=0;
	private final Management aat;

	
	public AATest() {
		List<GoalVariable> goals= new ArrayList<>();
		goals.add(new GoalVariable() {
			// goal (r)eturn
			public float getMin() { return -1; }
			public float getMax() { return Float.POSITIVE_INFINITY; }
			public float getStep() { return 0.01f; }
			public float getLimit() { return 0.06f;	}
			public double getValue() { 
				if (round==0) return 0.068; else return 0.065;
			}
		});
		goals.add(new GoalVariable() {
			// goal (e)quity
			public float getMin() { return 0; }
			public float getMax() { return 1; }
			public float getStep() { return 0.1f; }
			public float getLimit() { return 0.3f;	}
			public double getValue() { 
				if (round==0) return 0.24; else return 0.27;
			}
		});
		goals.add(new GoalVariable() {
			// goal (m)arket share
			public float getMin() { return 0; }
			public float getMax() { return 1; }
			public float getStep() { return 0.05f; }
			public float getLimit() { return 0.7f;	}
			public double getValue() { 
				if (round==0) return 0.17; else return 0.16;
			}
		});
		
		aat=new Management(goals, new float[] {0.06f,0.3f,0.2f});
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 1: Price decrease"); }
		}, new INFLUENCE[] {INFLUENCE.negative, INFLUENCE.none, INFLUENCE.positive });
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 2: Price increase"); }
		}, new INFLUENCE[] {INFLUENCE.positive, INFLUENCE.none, INFLUENCE.negative });
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 3: Cost reduction"); }
		}, new INFLUENCE[] {INFLUENCE.positive, INFLUENCE.negative , INFLUENCE.none});
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 4: Broadening product line"); }
		}, new INFLUENCE[] {INFLUENCE.negative, INFLUENCE.negative , INFLUENCE.positive});
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 5: Narrowing product line"); }
		}, new INFLUENCE[] {INFLUENCE.positive, INFLUENCE.positive  , INFLUENCE.negative});
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 6: Easier customer credit"); }
		}, new INFLUENCE[] {INFLUENCE.negative, INFLUENCE.none , INFLUENCE.positive});
		aat.addAction(new Action() {
			public void doAction() { System.out.println("Action 7: Harder customer credit"); }
		}, new INFLUENCE[] {INFLUENCE.positive,  INFLUENCE.none , INFLUENCE.negative});	
	}
	
	private void run() {
		aat.decideAction().doAction();
		round++;
		aat.decideAction().doAction();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AATest test = new AATest();
		test.run();
	}

}
