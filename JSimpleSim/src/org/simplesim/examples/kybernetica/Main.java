/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.kybernetica;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.kybernetica.game.Environment;
import org.simplesim.examples.kybernetica.game.EnvironmentState;
import org.simplesim.examples.kybernetica.game.GameMove;

/**
 * 
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Environment game=new Environment(new EnvironmentState());
		
		System.out.print(game.getState().toString());
		GameMove move=new GameMove(4, 0, 4, 0);
		Message msg=new Message(game,move);
		while(!game.getState().finished()) {
			game.getInport().write(msg);
			game.doEventSim(Time.ZERO);
			System.out.print(game.getState().toString());
				
		}
		
		// TODO Auto-generated method stub

	}

}
