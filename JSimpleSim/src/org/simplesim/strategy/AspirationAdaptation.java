/**
 * 
 */
package org.simplesim.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This class implements the aspiration adaptation algorithm.
 * <p>
 * Aspiration adaptation is a heuristic algorithm for multi-goal optimization.
 * <ul>
 * <li>There is a given set of goal variables that have to be <u>maximized</u>.
 * <li>There is a given set of actions that influence these goal variables.
 * <li>The algorithm evaluates how an action influences the goal variables by means of an influence scheme.
 * <li>The algorithm aims the goal variables to achieve certain <i>aspiration levels</i>.
 * <li>The urgency of the goals varies according to a given urgency order and depending on their actual aspiration level.
 * </ul>
 * The algorithm returns the action supporting the most urgent goal with the least negative and most positive effects on all goals
 * <p>   
 *  @see <a href="https://www.sciencedirect.com/science/article/abs/pii/S0022249697912050">Aspiration Adaptation Theory</a>
 *  @see <a href="https://www.jstor.org/stable/40748622?seq=1">First publication (as of 1962, German)</a>
 *
 */
public class AspirationAdaptation {
	
	public interface GoalVariable {
		// aspiration scale
		public float getMin(); // minimum value of aspiration level
		public float getMax(); // maximum value of aspiration level
		public float getStep(); // step size of aspiration adaptation
		// aspiration limits and priority to compute urgency order
		public float getLimit(); // limit for aspiration adaptation scheme
		// current value of the goal variable to correct influence scheme
		public double getValue(); // current value of the goal variable  
		public default boolean isBelowLimit() { return getLimit()<getValue(); }
	}
	
	public interface Action {
	
		public abstract void doAction();
	
	}
	
	public enum INFLUENCE { negative, none, positive }

	private class AspirationLevel {
		
		private final float a[];
	
		AspirationLevel(float init[]) { a=init; }	
		AspirationLevel(int size) { this(new float[size]); }
		float get(int index) { return a[index]; }
		void set(int index, float value) { a[index]=value; }
		void decrease(int index) {
			a[index]-=goalList.get(index).getStep();
		}
		
		void increase(int index) {
			a[index]+=goalList.get(index).getStep();
		}
				
		boolean contains(AspirationLevel other) {
			for (int i=0; i<a.length; i++) if (other.a[i]>a[i]) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int hash=Float.hashCode(a[0]);
			for (int i=1; i<a.length;i++) hash=(hash*71)^Float.hashCode(a[i]);
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			// checks for same class, null and same array length omitted because of private visibility
			for (int i=0; i<a.length; i++) if (a[i]!=((AspirationLevel) obj).a[i]) return false;
			return true;
		}
	}

	private final List<GoalVariable> goalList;
	private final Map<Action,INFLUENCE[]> influenceScheme=new IdentityHashMap<>();
	private final Map<AspirationLevel,List<Action>> expectedFeasibleSet=new HashMap<>();
	
	private final int[] urgencyOrder;
	private final double[] g1; // vector of last goal variable values, g1=g(t-1)
	private final AspirationLevel aspirationLevel; // current aspiration level 
	
	private Action lastAction;
	private final int dim; // number of goal variables

	
	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Note: Goal variables have to be ordered with descending priority. The priority cannot be changed later on. 
	 * 
	 * @param goals list of goal variables 
	 */
	public AspirationAdaptation(List<GoalVariable> goals) {	
		// init goal and action lists
		goalList=new ArrayList<>(goals); // copy goal variables
		dim=goalList.size();
		urgencyOrder=new int[dim];
		
		aspirationLevel=new AspirationLevel(dim);
			
		g1=new double[dim]; 
		final INFLUENCE[] noInfluence=new INFLUENCE[dim];
		
		// initial values of goal variables as g(t-1)=g(t)
		for (int index=0; index<dim; index++) {
			g1[index]=goalList.get(index).getValue(); // init values of goal variables
			noInfluence[index]=INFLUENCE.none; // create neutral "no action" inflúence
		}
		
		
		
		// add a neutral "no action" element with zero influence to the action set
		lastAction=addAction(new Action() { @Override
		public void doAction() {}},noInfluence);
	}
	
	public Action addAction(Action action,INFLUENCE influence[]) {
		if (influence.length!=dim) return null;
		influenceScheme.put(action,influence);
		return action;
	}
		
	public Action decideAction() {
		updateInfluenceScheme();		// correct influence scheme
		constructExpectedFeasibleSet();	// construct set of feasible aspiration levels
		calcCurrentAspirationLevel();	// calc current aspiration level form values of goal variables
		updateUrgencyOrder();			// update urgency order
		maximizeAspirationLevel();		// upward aspiration adaptation
		lastAction=selectBestAction(expectedFeasibleSet.get(aspirationLevel));
		return lastAction; 
	}
	
	/**
	 * Sets the aspiration level according to the current value of the goal variables.
	 * <p>
	 * Note: Rounding off should put the aspiration level automatically within the comprehensive hull of the feasibility set. 
	 */
	private void calcCurrentAspirationLevel() {
		for (int index=0; index<dim; index++)
			aspirationLevel.set(index,round(goalList.get(index).getValue(),goalList.get(index).getStep()));
	}

	/**
	 * Maximizes the aspiration level along the boundary of the feasible set.
	 * <p>
	 * Tries to maximize the aspiration level (AL) of the most urgent goal variable. Then the AL of the second most urgent
	 * is maximized and so on. Result should be the optimal AL that can be reached by the given set of actions.
	 */
	private void maximizeAspirationLevel() {
		for (int index=0; index<dim; index++) {
			while (isAspirationLevelFeasible()) {
				aspirationLevel.increase(urgencyOrder[index]);
			}
			aspirationLevel.decrease(urgencyOrder[index]);
		}
	}

	/**
	 * Adapts the aspiration level so that it is within the comprehensive hull of the feasible set
	 */
	private void adjustAspirationLevel() {
		while(!isAspirationLevelFeasible()) {
			aspirationLevel.decrease(getRetreatVariable());
			updateUrgencyOrder();
		}
	}
	
	private int getRetreatVariable() {
		return urgencyOrder[dim];
	}
	
	
	private boolean isAspirationLevelFeasible() {
		for (AspirationLevel entry : expectedFeasibleSet.keySet()) {
			if (!entry.contains(aspirationLevel)) return false;
		}
		return true;
	}

	/**
	 * Generates a permutation of goal indices according to the current aspiration level and sorted by descending urgency.
	 * <p>
	 * The new urgency order contains the indexes of the goal variable sorted with descending urgency, the last element
	 * indicating the retreat variable
	 * 
	 * @param a an adaptation level
	 */
	private void updateUrgencyOrder() {
		int index=0;
		// goals with adaptation levels below the limit are more urgent than the ones above
		for (int i=0; i<dim; i++) {
			if (aspirationLevel.get(i)<=goalList.get(i).getLimit()) urgencyOrder[index++]=i;
		}
		for (int i=0; i<dim; i++) {
			if (aspirationLevel.get(i)>goalList.get(i).getLimit()) urgencyOrder[index++]=i;
		}
	}

	/**
	 * Finds the best action to realize the given aspiration level.
	 * <p>
	 * An aspiration level might be assigned to several actions. The action list is narrowed by the following criteria:
	 * (1) select the actions with the least negative influences
	 * (2) of this subset, select the action with the most positive influences
	 * 
	 */
	private Action selectBestAction(List<Action> actionList) {
		// list actions with the least negative influences
		int best=Integer.MAX_VALUE;
		for (Action action : actionList) best=Math.min(best,
				countInfluence(influenceScheme.get(action),INFLUENCE.negative));
		for (Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
		    if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.negative)>best)
		    	iter.remove();
		}
		// of this subset, select actions with most positive influences
		best=0;
		for (Action action : actionList) best=Math.max(best,
				countInfluence(influenceScheme.get(action),INFLUENCE.positive));
		for (Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
		    if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.positive)<best)
		    	iter.remove();
		}
		// return first element, since all remaining elements are tantamount
		return actionList.get(0);	
	}
	
	private int countInfluence(INFLUENCE[] influence,INFLUENCE value) {
		int counter=0;
		for (INFLUENCE item : influence) {
			if (item==value) counter++;
		}
		return counter;
	}

	
	/**
	 * Constructs a set of aspiration levels that can be reached by the available actions.
	 */
	private void constructExpectedFeasibleSet() {
		final float[] aLow=new float[dim]; // lower aspiration bound
		final float[] aMid=new float[dim]; // mid value
		final float[] aHi=new float[dim];  // higher aspiration bound

		// calculate feasible aspiration levels 
		for (int i=0; i<dim; i++) { 
			final float step=goalList.get(i).getStep();
			final float a=round(g1[i],step);
			if (a<goalList.get(i).getMax()) aHi[i]=a+step;
			else aHi[i]=a;
			if ((a==g1[i])&&(a>goalList.get(i).getMin())) aLow[i]=a-step;
			else aLow[i]=a;
			aMid[i]=a;
		}
		// assign feasible aspirations form influence scheme to actions
		expectedFeasibleSet.clear();
		for (Action action : influenceScheme.keySet()) {
			AspirationLevel fal=new AspirationLevel(dim); // feasible aspiration level
			for (int index=0; index<dim; index++) {
				switch(influenceScheme.get(action)[index]) {
				case negative : fal.set(index,aLow[index]); break;
				case positive : fal.set(index,aHi[index]); break;
				default:		fal.set(index,aMid[index]);
				}
			}
			List<Action> actionList=expectedFeasibleSet.get(fal);
			if (actionList==null) {
				actionList=new LinkedList<>();
				expectedFeasibleSet.put(fal,actionList);
			}
			actionList.add(action);
		}
	}
	
	/**
	 * Updates the influence of the last action in the influence scheme and saves the current values of the goal variables.
	 */
	private void updateInfluenceScheme() {
		final INFLUENCE[] influenceRow=influenceScheme.get(lastAction);
		for (int index=0; index<dim; index++) {
			final double g=goalList.get(index).getValue();
			if (g<g1[index]) influenceRow[index]=INFLUENCE.negative;
			else if (g>g1[index]) influenceRow[index]=INFLUENCE.positive;
			else influenceRow[index]=INFLUENCE.none;
			g1[index]=g; // save for next round
		}
	}
	
	private static float round(double value, float step) {
		return ((float) Math.floor(value/step))*step;
	}
	

}
