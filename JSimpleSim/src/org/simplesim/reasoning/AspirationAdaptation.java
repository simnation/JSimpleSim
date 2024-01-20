/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.reasoning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class implements a variation of the aspiration adaptation algorithm.
 * <p>
 * Aspiration adaptation is a heuristic algorithm for multi-goal optimization by
 * Reinhard Selten.
 * <p>
 * The algorithm has the following propositions:
 * <ul>
 * <li>There is a given set of <i>goal variables</i> that have to be
 * <i>maximized</i>.
 * <li>There is a given set of <i>actions</i> that influence the goal variables.
 * <li>Goal variables have certain <i>limits</i>, below its limit a goal becomes
 * <i>urgent</i>.
 * <li>An <i>urgency order</i> is formed for each combination of goal values to
 * prioritize goals.
 * <li>The algorithm evaluates how an action influences the goal variables by
 * means of an <i>influence scheme</i>.
 * </ul>
 * The algorithm returns the action supporting the most urgent goal with the
 * most positive effects on all other goals. If there is no such action, it
 * returns the action that has not been used for the longest period of time. Rational: When at an impasse, an action
 * that has not been used for a long time, it may now have a positive effect. To
 * set up an urgency order, there are several evaluation strategies.
 * <p>
 * <ul>
 * <li>SUM: select the action with the highest sum of influence over <i>all</i>
 * goals
 * <li>PRIO: select the action with highest influence on the <i>prioritized</i>
 * goal
 * <li>GEOMETRIC: select the action with best relative improvement overall
 * (<i>geometric mean</i>)
 * <li>RANDOM: select a random action
 * </ul>
 *
 * @see <a href=
 *      "https://www.sciencedirect.com/science/article/abs/pii/S0022249697912050">Aspiration
 *      Adaptation Theory</a>
 * @see <a href="https://www.jstor.org/stable/40748622?seq=1">First publication
 *      (as of 1962, German)</a>
 *
 */
public class AspirationAdaptation {

	public enum STRATEGY {
		SUM, PRIO, GEOMETRIC, RANDOM, SELTEN
	}

	/** abstraction layer for the goal variables / objectives */
	public interface GoalVariable {

		/**
		 * When the limit is reached, the goal variable no longer is urgent. The limit
		 * has to be a positive value.
		 */
		double getLimit();

		/** The current value of the goal variable. */
		double getValue();

		/** The step size of scale of the goal variable. */
		double getIncrement();

		/** A goal is urgent as long as its value is below the limit. */
		default boolean isUrgent() {
			return getValue()<=getLimit();
		}

	}

	/** abstraction layer, has to be implemented by the callers actions */
	public interface Action {
		void doAction();
	}

	private final GoalVariable[] goal; // constant array of goal variables

	/**
	 * Mapping actions to their presumed influence on the goal variables. The
	 * influence is measured relative to the limit. In other words: What share of
	 * the limit does an action change?
	 */
	private final Map<Action, double[]> influenceScheme=new IdentityHashMap<>();
	private final Deque<Action> history=new ArrayDeque<>();

	private final double[] g0; // goal variable values of last iteration, g0=g(t-1)

	private Action lastAction=null;
	private final STRATEGY strategy;
	private final int dim; // number of goal variables

	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Note: Goal variables have to be ordered with descending priority. The
	 * priority cannot be changed later on.
	 *
	 * @param goals array of goal variables
	 */
	public AspirationAdaptation(GoalVariable goals[], STRATEGY s) {
		// init goals and action lists
		strategy=s;
		goal=goals;
		dim=goals.length; // number of goals determines dimension of other schemes
		g0=new double[dim];
	}

	public AspirationAdaptation(GoalVariable goals[]) { this(goals,STRATEGY.GEOMETRIC); }

	/**
	 * Adds a new action with estimated influences to the list of actions.
	 * <p>
	 * Use values -1, 0 and 1 to initialize the influence scheme with a best
	 * educated guess of an action's effect.
	 * <p>
	 * Best educated guessing of the influence is okay, since the influence is
	 * adapted later on. Filling the influence scheme only with zeros may lead to a
	 * longer "warm-up" time of the algorithm.
	 *
	 * @param action    the action
	 * @param influence estimated influence of the action on the goal variables
	 * @return the action for further usage, {@code null} if an error occurred
	 */
	public Action addAction(Action action, double influence[]) {
		if (influence.length!=dim) return null;
		influenceScheme.put(action,influence);
		history.add(action);
		return action;
	}

	/**
	 * Adds a new action without any estimation of its influence.
	 * <p>
	 * Note: This may lead to a longer "warm-up" time of the algorithm.
	 *
	 */
	public Action addAction(Action action) {
		final double[] inf=new double[dim];
		for (int i=0; i<dim; i++) inf[i]=0;
		return addAction(action,inf);
	}

	public static double calcInfluence(GoalVariable goal, double oldValue) {
		return (goal.getValue()-oldValue)/goal.getLimit();
	}

	/**
	 * Finds the best action based on the given strategy.
	 * <p>
	 * The algorithm uses the given evaluation strategy to determine the best
	 * action.
	 * <p>
	 * If there is no action with a positive influence, the action which has not
	 * been used for the longest period of time is returned as a fallback. This way,
	 * a livelock may be prevented.
	 * <p>
	 * Rational: The result of the "oldest" action (last item in history list) may
	 * be outdated and turn out as an action with positive outcome in the current
	 * situation. So, if there is no "good" action, the "oldest" action is chosen as
	 * a best educated guess.
	 */
	public Action decideAction() {
		if (lastAction!=null) updateInfluenceScheme();
		// else history.addAll(influenceScheme.keySet()); // first call --> init history deque
		lastAction=findBestAction(); // find best action according to selected strategy
		return lastAction;
	}
	
	public Collection<Action> getActions() {
		return Collections.unmodifiableCollection(influenceScheme.keySet());
	}
	
	public Collection<GoalVariable> getGoals() {
		return Collections.unmodifiableCollection(Arrays.asList(goal));
	}
	

	private Action findBestAction() {
		Action result=null;
		switch (strategy) {
		// if we get here, there is at least one action .
		// From the list of actions with positive influence on the prioritized goal select...
		case SUM: // ...the action with the highest sum of influence over all goals (positive influence the prioritized goal is guaranteed)
			result=selectBestSum();
			break;
		case PRIO: // ...the action with highest influence on the prioritized goal
			result=selectBestPrio();
			break;
		case GEOMETRIC: // ...the action with best relative improvement overall
			result=selectBestGeometricMean();
			break;
		case RANDOM: // select a random action
			result=new ArrayList<>(influenceScheme.keySet()).get(ThreadLocalRandom.current().nextInt(dim));
		case SELTEN:
			// ToDo
			break;
		}
		// fallback: select the action, which has not been used for the longest period of time
		if (result==null) result=history.removeLast();
		else history.remove(result);
		history.addFirst(result); // put current action at the head of the queue
		return result;
	}

	private Action selectBestSum() {
		double best=0; // to ensure improvement, sum should be positive.
		Action result=null;
		for (Action action : influenceScheme.keySet()) {
			final double sum=sumInfluence(action);
			if (sum>best) {
				best=sum;
				result=action;
			}
		}
		return result; // result==null --> no improvement found
	}

	private Action selectBestGeometricMean() {
		double best=1.0d; // to ensure improvement, geometric mean should be greater than one.
		Action result=null;
		for (Action action : influenceScheme.keySet()) {
			final double mean=averageInfluence(action);
			if (mean>best) {
				best=mean;
				result=action;
			}
		}
		return result; // result==null --> no improvement found
	}

	private Action selectBestPrio() {
		double best=0;
		Action result=null;
		final int[] urgencyOrder=createUrgencyOrder();	// permutation vector

		for (int prio : urgencyOrder) { // iterate in descending urgency
			for (Action action : influenceScheme.keySet()) {
				final double effect=influenceScheme.get(action)[prio]; // effect of action on prioritized goal
				if (effect>best) {
					best=effect;
					result=action;
				}
			}
			if (result!=null) return result;
		}
		// result is null, so no action with positive influence on any goal variable was found.
		return result;
	}

	/**
	 * Generates a permutation of goal indices according to the current aspiration
	 * level and sorted by descending urgency.
	 * <p>
	 * The new urgency order contains the indices of the goal variable sorted by
	 * descending urgency, the last element indicating the retreat variable.
	 *
	 */
	private int[] createUrgencyOrder() {
		int index=0;
		final int[] result=new int[dim];
		// first pass: below the limit, goals with lower indices are more urgent
		for (int i=0; i<dim; i++) if (goal[i].isUrgent()) result[index++]=i;
		// second pass: if the goal limit is reached, goals with higher indices are more urgent
		for (int i=dim-1; i>=0; i--) if (!goal[i].isUrgent()) result[index++]=i;
		return result;
	}

	/**
	 * Updates the influence of the last action in the influence scheme and saves
	 * the current values of the goal variables.
	 */
	private void updateInfluenceScheme() {
		final double influence[]=influenceScheme.get(lastAction);
		for (int i=0; i<dim; i++) {	// assess change in number of discrete steps
			influence[i]=calcInfluence(goal[i],g0[i]); // update influence
			g0[i]=goal[i].getValue();
		}

	}

	private double sumInfluence(Action action) {
		final double[] influence=influenceScheme.get(action);
		double sum=influence[0];
		for (int i=1; i<dim; i++) sum+=influence[i];
		return sum;
	}

	private double averageInfluence(Action action) { // calculate the geometric mean of influences
		final double[] influence=influenceScheme.get(action);
		double prod=influence[0]+1.0d;
		for (int i=1; i<dim; i++) prod*=influence[i]+1.0d;
		return Math.pow(prod,1.0d/dim); // return n-th root of product
	}

}
