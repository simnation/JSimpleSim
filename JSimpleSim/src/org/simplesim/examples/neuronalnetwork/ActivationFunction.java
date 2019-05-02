/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.neuronalnetwork;

/**
 * @author Rene Kuhlemann
 *
 */
public final class ActivationFunction {

	public interface IActivationFunction {
		double calc(double x);
	}

	public static class Logistic implements IActivationFunction {
		@Override
		public double calc(double x) {
			return 1.0/(1.0+Math.exp(-x));
		}
	}

	public static class TanH implements IActivationFunction {
		@Override
		public double calc(double x) {
			return Math.tanh(x);
		}
	}

	public static class InverseSquareRoot implements IActivationFunction {
		@Override
		public double calc(double x) {
			return x/Math.sqrt(1.0+(x*x));
		}
	}

}
