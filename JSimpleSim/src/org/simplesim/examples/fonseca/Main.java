/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.fonseca;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.simplesim.decisionmaking.AspirationAdaptation;
import org.simplesim.decisionmaking.AspirationAdaptation.Action;
import org.simplesim.decisionmaking.AspirationAdaptation.GoalVariable;
import org.simplesim.decisionmaking.AspirationAdaptation.STRATEGY;

/**
 * Testing the aspiration adaptation strategy by a three-parameter optimization
 * of a two-objective problem.
 * <p>
 * The Fonseca-Fleming Problem is a two-objective problem that can be used with
 * a variable number of parameters.
 *
 * @see <a href=
 *      "http://www.mathlayer.com/support/benchmark-problems-fonseca-fleming.html">Fonseca-Fleming
 *      Problem </a>
 * @see <a href=
 *      "https://www.al-roomi.org/benchmarks/multi-objective/unconstrained-list/321-fonseca-fleming-s-function-fon">Fonseca-Fleming
 *      Problem </a>
 * 
 */
public class Main {

	private static final NumberFormat nf2=NumberFormat.getInstance();
	private static final NumberFormat nf6=NumberFormat.getInstance();
	private static final int PARETO_SIZE=50; // number of dots for pareto front
	private static final double a=1/Math.sqrt(3.0d);
	private static final int dim=2; // number of goal variables

	private final AspirationAdaptation aat;
	private final FonsecaChart chart=new FonsecaChart("Fonseca-Fleming-Problem");

	final GoalVariable goal[]=new GoalVariable[dim];
	double x[]=new double[3]; // parameter vector

	public Main() {
		initX();
		defineGoals();
		aat=new AspirationAdaptation(goal,STRATEGY.GEOMETRIC);
		addActions();
		addParetoFront();
	}

	private void addParetoFront() {
		final double[] f1pareto=new double[PARETO_SIZE];
		final double[] f2pareto=new double[PARETO_SIZE];
		for (int i=0; i<PARETO_SIZE; i++) {
			double f1=Math.exp(-4)+(i*((1-(2*Math.exp(-4)))/PARETO_SIZE));
			f1pareto[i]=f1;
			f2pareto[i]=Math.exp(-Math.pow(2-Math.sqrt(-Math.log(f1)),2));
		}
		chart.addParetoFront(f1pareto,f2pareto);
	}

	private void initX() {
		x[0]=2.0d-ThreadLocalRandom.current().nextDouble(4.0d);
		x[1]=2.0d-ThreadLocalRandom.current().nextDouble(4.0d);
		x[2]=2.0d-ThreadLocalRandom.current().nextDouble(4.0d);
	}

	private void addActions() {
		// define actions
		final List<Action> actionList=new ArrayList<>();
		actionList.add(() -> x[0]+=0.1d);
		actionList.add(() -> x[0]-=0.1d);
		actionList.add(() -> x[1]+=0.1d);
		actionList.add(() -> x[1]-=0.1d);
		actionList.add(() -> x[2]+=0.1d);
		actionList.add(() -> x[2]-=0.1d);
		// save current values of x vector and goals
		final double x0[]=x.clone(); // save old x vector
		final double g0[]=new double[dim];
		for (int i=0; i<dim; i++) g0[i]=goal[i].getValue();
		// calc initial influence of each action
		for (Action action : actionList) {
			final double influence[]=new double[dim];
			action.doAction();
			for (int i=0; i<dim; i++) influence[i]=AspirationAdaptation.calcInfluence(goal[i],g0[i]);
			aat.addAction(action,influence);
			x=x0; // restore values of x vector
		}
	}

	private void defineGoals() {
		goal[0]=new GoalVariable() {
			@Override
			public double getLimit() { return 0.7d; }

			@Override
			public double getValue() { return f1(x); }

			@Override
			public String toString() { return "f1="+nf6.format(getValue()); }

			@Override
			public double getIncrement() { return 0.01d; }
		};
		goal[1]=new GoalVariable() {
			@Override
			public double getLimit() { return 0.7d; }

			@Override
			public double getValue() { return f2(x); }

			@Override
			public String toString() { return "f2="+nf6.format(getValue()); }

			@Override
			public double getIncrement() { return 0.01d; }
		};
	}

	private void run() {
		for (int i=0; i<1000; i++) {
			aat.decideAction().doAction();
			System.out.println("x="+nf2.format(x[0])+"  "+"y="+nf2.format(x[1])+"  "+"z="+nf2.format(x[2])+"  "
					+goal[0].toString()+"   "+goal[1].toString());
			chart.update(goal[0].getValue(),goal[1].getValue());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		nf2.setMaximumFractionDigits(2);
		nf2.setMinimumFractionDigits(2);
		nf6.setMaximumFractionDigits(6);
		nf6.setMinimumFractionDigits(6);
		final Main test=new Main();
		test.run();
	}

	public double f1(double x[]) { return Math.exp(-Math.pow(x[0]-a,2)-Math.pow(x[1]-a,2)-Math.pow(x[2]-a,2)); }

	public double f2(double x[]) { return Math.exp(-Math.pow(x[0]+a,2)-Math.pow(x[1]+a,2)-Math.pow(x[2]+a,2)); }

}
