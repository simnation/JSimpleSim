/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import static org.simplesim.examples.elevator.shared.Limits.LOBBY;
import static org.simplesim.examples.elevator.shared.Limits.MAX_FLOOR;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.AbstractSimulator;

/**
 *
 *
 */
@SuppressWarnings("serial")
public class View extends JFrame implements Listener<AbstractSimulator> {

	public static final int WINDOW_DX=1024;
	public static final int WINDOW_DY=768;
	private static final int OFFSET=20;
	private static final int FLOOR_HEIGHT=Math.floorDiv(WINDOW_DY-(2*OFFSET),MAX_FLOOR+1);
	private static final int ICON_SIZE=OFFSET; // FLOOR_HEIGHT-1;

	private static final int LEFT_FLOOR_START=OFFSET;
	private static final int LEFT_FLOOR_END=((WINDOW_DX-ICON_SIZE)>>1)-OFFSET;
	private static final int RIGHT_FLOOR_START=WINDOW_DX-LEFT_FLOOR_END;
	private static final int RIGHT_FLOOR_END=WINDOW_DX-OFFSET;

	private static final int ELEVATOR_X=LEFT_FLOOR_END+OFFSET;
	private static final int RIGHT_ICON_COUNT=Math.floorDiv(RIGHT_FLOOR_END-RIGHT_FLOOR_START,ICON_SIZE+OFFSET);
	private static final int LEFT_ICON_COUNT=Math.floorDiv(LEFT_FLOOR_END-LEFT_FLOOR_START,ICON_SIZE+OFFSET);

	final private Image background;
	final private ElevatorState elevator;

	public View(ElevatorState es) {
		super("Simple Elevator Simulator");
		// initialize variables
		elevator=es;
		// initialize window
		final Dimension size=new Dimension(WINDOW_DX,WINDOW_DY);
		setPreferredSize(size);
		setSize(size);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		createBufferStrategy(2);
		// prepare background image to accelerate drawing process
		background=createImage(WINDOW_DX,WINDOW_DY);
		final Graphics graphics=background.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0,0,WINDOW_DX,WINDOW_DY);
		graphics.setColor(Color.LIGHT_GRAY);
		for (int floor=LOBBY; floor<=MAX_FLOOR; floor++) {
			final int y=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT);
			if (floor>LOBBY) graphics.drawLine(LEFT_FLOOR_START,y,LEFT_FLOOR_END,y);
			graphics.drawLine(RIGHT_FLOOR_START,y,RIGHT_FLOOR_END,y);
		}
	}

	@Override
	public void notifyListener(Time unused, AbstractSimulator source) {
		final BufferStrategy bs=getBufferStrategy();
		do {
			do {
				// Get a new graphics context every time through the loop
				// to make sure the strategy is validated
				final Graphics graphics=bs.getDrawGraphics();
				graphics.drawImage(background,0,0,WINDOW_DX,WINDOW_DY,null);
				drawModel(graphics,source.getSimulationTime());
				final String time="Time: "+source.getSimulationTime().toString();
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.drawString(time,LEFT_FLOOR_START,WINDOW_DY-OFFSET);
				graphics.dispose();
				// Repeat the rendering if the drawing buffer contents were restored
			} while (bs.contentsRestored());
			bs.show();
		} while (bs.contentsLost());
	}

	/**
	 * @param agent
	 */
	private void drawModel(Graphics graphics, Time simTime) {
		int dx;
		final int elevatorY=WINDOW_DY-OFFSET-(elevator.getCurrentFloor()*FLOOR_HEIGHT)-ICON_SIZE;
		graphics.setColor(Color.MAGENTA);
		graphics.fillRect(ELEVATOR_X,elevatorY,ICON_SIZE,ICON_SIZE);
		for (int floor=LOBBY; floor<=MAX_FLOOR; floor++) {
			final Queue<Request> queue=elevator.getQueue(floor);
			if (!queue.isEmpty()) {
				if (queue.size()>RIGHT_ICON_COUNT) {
					dx=Math.floorDiv(RIGHT_FLOOR_END-RIGHT_FLOOR_START,queue.size());
					if (dx<1) dx=1;
				} else dx=ICON_SIZE+OFFSET;
				final int floorY=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT)-ICON_SIZE;
				int index=0;
				for (final Request request : queue) {
					final Visitor visitor=(Visitor) request.getVisitor();
					graphics.setColor(visitor.getCurrentMood(simTime).getColor());
					graphics.fillRect(RIGHT_FLOOR_START+(index*dx),floorY,ICON_SIZE,ICON_SIZE);
					index++;
				}
			}

			graphics.setColor(Color.BLUE);
			int arrivals=elevator.getArrivals(floor);
			if (arrivals>0) {
				if (arrivals>LEFT_ICON_COUNT) {
					dx=Math.floorDiv(LEFT_FLOOR_END-LEFT_FLOOR_START,arrivals);
					if (dx<1) dx=1;
				} else dx=ICON_SIZE+OFFSET;
				final int floorY=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT)-ICON_SIZE;
				for (int index=0; index<arrivals; index++)
					graphics.fillRect(LEFT_FLOOR_END-ICON_SIZE-(index*dx),floorY,ICON_SIZE,ICON_SIZE);
			}
		}
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public static void intro() {
		System.out
				.println("\nThis example shows the simulation of an elevator as use-case of the JSimpleSim framework.");
		System.out.println();
		System.out.println("\tThe building is "+Limits.MAX_FLOOR+" floor levels tall.");
		System.out.println("\t"+Limits.VISITORS+" employees work in the offices.");
		System.out
				.println("\tThe day starts at "+Limits.START_DAY.toString()+" and ends at "+Limits.END_DAY.toString());
		System.out.println("\tWork starts at "+Limits.START_WORK.toString()+" and ends at "+Limits.END_WORK.toString());
		System.out.println("\tPeople stay at most  "+new Time(Limits.MAX_STAY_TIME).toString()+" on the same floor.");
		System.out.println("\tThe elevator has a maximium capacity of "+Limits.CAPACITY+" people.");
		System.out.println(
				"\tPeople start to get angry after "+Limits.ACCEPTABLE_WAITING_TIME.toString()+" waiting time.");
		System.out.println();
		System.out
				.println("Left side: people who exited at the last stop | Right side: people waiting for the elevator");
		System.out.println();
		System.out.println(
				"Static version: There are no model changes, the current level is stored in the agents' states.");
		System.out.println(
				"Dynamic version: Each floor is a domain, agents are moved from one domain to another dynamically during the simulation run.");
	}

}
