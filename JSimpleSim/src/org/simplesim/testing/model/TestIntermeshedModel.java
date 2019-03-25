/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.Message;
import org.simplesim.core.routing.SinglePort;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedListEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractState;
import org.simplesim.model.RootModel;
import org.simplesim.simulator.ParallelSimulator;

/**
 * This test build an intermeshed network, where each agent is connected with
 * each other agent. Agents have got a random event queue and send a dummy
 * messages to a randomly chosen other agent.
 *
 * Used to test general functionality of event queue, message forwarding and
 * simulator.
 *
 * @author Rene Kuhlemann
 *
 */
public final class TestIntermeshedModel {

	private static final int MAX_EVENTS=64;
	private static final Time SIMULATION_STOP=new Time(Time.DAY);

	private static class AgentState extends AbstractState {
	}

	private static class Agent extends AbstractAgent<AgentState, Integer> {

		private final AbstractPort inport;

		public Agent(String name) {
			super(name);
			inport=this.addSingleInport();
			for (int i=0; i<MAX_EVENTS; i++)
				getEventQueue().enqueue(i,new Time((long) (Math.random()*SIMULATION_STOP.getTicks())));
			getEventQueue().enqueue(MAX_EVENTS,SIMULATION_STOP);
		}

		@Override
		protected AgentState createState() {
			return new AgentState() {
			};
		}

		public AbstractPort getInport() {
			return inport;
		}

		@Override
		protected IEventQueue<Integer> createInternalEventQueue() {
			return new SortedListEventQueue<>();
		}

		@Override
		protected Time doEvent(Time time) {
			/*
			 *
			 * System.out.println(getFullName()+" received "+getInport().countValues()
			 * +" messages from:"); else
			 * System.out.println(getFullName()+" received no messages."); while
			 * (getInport().hasValue()) System.out.println(((Message)
			 * getInport().read()).getSrc());
			 */
			final SinglePort dest=(SinglePort) getRandomOutport();
			final Message<String> msg=new Message<>(getFullName(),dest.getConnection().getParent().getFullName(),
					"Hello");
			dest.write(msg);
			// System.out.println("Sent messages to: "+msg.getDest());
			getEventQueue().dequeue();
			return getEventQueue().getMin();
		}

		private AbstractPort getRandomOutport() {
			final int max=(int) (Math.random()*countOutports());
			int index=0;
			for (final AbstractPort port : getOutports()) {
				if (index>=max) return port;
				index++;
			}
			System.out.println("Error");
			return null;
		}

	}

	private static void createTestModel(int size) {
		final List<Agent> list=new ArrayList<>();
		// part I: create agents for the intermeshed network within the root model
		for (int index=1; index<=size; index++) {
			final Agent agent=new Agent("Agent"+Integer.toString(index));
			RootModel.getInstance().addModel(agent);
			list.add(agent);
		}
		// part II: connect agents with each others
		int i=0;
		for (final Agent agent : list) for (final Agent dest : list) {
			if (agent.equals(dest)) continue;
			agent.addSingleOutport().connectTo(dest.getInport());
			i++;
		}
		System.out.println("Number of connections: "+i);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final long start=System.currentTimeMillis();
		createTestModel(1000);
		System.out.println("Model-building finished!");
		// final SequentialSimulator simulator=new
		// SequentialSimulator(RootModel.getInstance());
		final ParallelSimulator simulator=new ParallelSimulator(RootModel.getInstance());
		simulator.runSimulation(SIMULATION_STOP);
		System.out.println("Simulation run finished. Runtime (ms): "+(System.currentTimeMillis()-start));
	}

}
