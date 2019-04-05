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
import org.simplesim.core.routing.RoutedMessage;
import org.simplesim.core.routing.SinglePort;
import org.simplesim.core.scheduling.EventQueueEntry;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.AbstractState;
import org.simplesim.model.RootModel;
import org.simplesim.simulator.ParallelSimulator;
import org.simplesim.simulator.SequentialSimulator;

/**
 * This test build a hierarchical tree model, where agents are grouped in domains. 
 * Agents have got a random event queue and send a dummy
 * messages to a randomly chosen other agent.
 *
 * Used to test general functionality of agent/domain interaction, message routing and
 * simulators.
 *
 * @author Rene Kuhlemann
 *
 */
public final class TestTreeModel {

	
	private static final Time SIMULATION_START=new Time(0);
	private static final Time SIMULATION_STOP=new Time(Time.DAY);
	private static final double INTERVALL=SIMULATION_STOP.getTicks()/64;
	private static final int AGENTS_PER_DOMAIN=4;
	private static final int DOMAINS_PER_LEVEL=3;
	private static final int MAX_LEVELS=0;
	
	private static final List<Agent> agent_list=new ArrayList<>();

	private static class AgentState extends AbstractState {
	}

	private static class Agent extends AbstractAgent<AgentState,Integer> {

		private final AbstractPort inport,outport;		

		public Agent(int[] addr) {
			super(addr);
			inport=this.addSingleInport();
			outport=this.addSingleOutport();
			getEventQueue().enqueue(0,SIMULATION_START);
			getEventQueue().enqueue(2,SIMULATION_STOP);
		}

		@Override
		protected AgentState createState() {	return new AgentState(); }

		public AbstractPort getInport() { return inport;}
		
		public AbstractPort getOutport() { return outport;}

		@Override
		protected IEventQueue<Integer> createInternalEventQueue() {
			return new SortedEventQueue<Integer>();
		}

		@Override
		protected Time doEvent(Time time) {
			if (getInport().hasValue())
			System.out.println(getFullName()+" received "+getInport().countValues()+" messages from:"); 
			else System.out.println(getFullName()+" received no messages.");
			while (getInport().hasValue()) System.out.println(((RoutedMessage) getInport().read()).getSource());
			EventQueueEntry<Integer> entry=getEventQueue().dequeue();
			entry.setTime(time.add(1+((long) (Math.random()*INTERVALL))));
			entry.setEvent(1);
			getEventQueue().enqueue(entry);
			final Agent ag=agent_list.get((int) (Math.random()*agent_list.size()));
			final int[] src=this.getAddress();
			final int[] dest=ag.getAddress();
			final RoutedMessage msg=new RoutedMessage(src,dest,"Hello");
			this.getOutport().write(msg);
			System.out.println(getFullName()+" sent message to "+ag.getFullName()); 
			return getEventQueue().getMin();
		}

	}

	private static class Domain extends AbstractDomain {

		private final AbstractPort inport,outport;		
		
		public Domain(int[] addr) {	
			super(addr);
			outport=addSingleOutport();
			inport=this.addRoutingInPort();
			}
		
		public AbstractPort getInport() { return inport;}
		
		public AbstractPort getOutport() { return outport;}

	}
	
	private static void createTestModel() {
		// part I: prepare root domain
		final AbstractPort rootPort=RootModel.getInstance().addRoutingInPort();
		//part II: create model tree recursively within the root model
		createNextModelLevel(RootModel.getInstance(),rootPort,rootPort,RootModel.getInstance().getAddress(),0);
	}

	private static void createNextModelLevel(AbstractDomain<?> domain,AbstractPort inport, AbstractPort outport, int[] addr, int level) {
		final int[] newAddr=new int[addr.length+1];
		for (int index=0;index<addr.length;index++) newAddr[index]=addr[index];
		if (level<MAX_LEVELS) {
			
		} else {
			for (int index=0;index<AGENTS_PER_DOMAIN;index++) {
				newAddr[newAddr.length-1]=index;
				final Agent agent=new Agent(newAddr.clone());
				domain.addModel(agent);
				agent.getOutport().connectTo(outport);
				inport.connectTo(agent.getInport());
				agent_list.add(agent);
			}		
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final long start=System.currentTimeMillis();
		createTestModel();
		System.out.println("Model-building finished!");
		//final SequentialSimulator simulator=new SequentialSimulator(RootModel.getInstance());
		 final ParallelSimulator simulator=new ParallelSimulator(RootModel.getInstance());
		simulator.runSimulation(SIMULATION_STOP);
		System.out.println("Simulation run finished. Runtime (ms): "+(System.currentTimeMillis()-start));
	}

}
