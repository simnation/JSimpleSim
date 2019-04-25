/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy way.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.neuronalnetwork;

import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 * @author Rene Kuhlemann
 *
 */
public class Neuron extends AbstractAgent<NeuronState, Object> {

	/* (non-Javadoc)
	 * @see org.simplesim.model.AbstractAgent#createState()
	 */
	protected NeuronState createState() {
		return new NeuronState();
	}

	/* (non-Javadoc)
	 * @see org.simplesim.model.AbstractAgent#createInternalEventQueue()
	 */
	protected IEventQueue<Object> createInternalEventQueue() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.model.AbstractAgent#doEvent(org.simplesim.core.scheduling.Time)
	 */
	protected Time doEvent(Time time) {
		// TODO Auto-generated method stub
		return null;
	}

}
