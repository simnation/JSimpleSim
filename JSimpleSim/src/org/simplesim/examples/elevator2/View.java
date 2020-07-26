/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.simplesim.core.notification.Listener;
import org.simplesim.examples.elevator.StaticModelMain;
import org.simplesim.examples.elevator2.Visitor.ACTIVITY;
import org.simplesim.model.BasicModelEntity;
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
	private static final int FLOOR_HEIGHT=Math.floorDiv(WINDOW_DY-(2*OFFSET),Building.MAX_FLOOR+1);
	private static final int ICON_SIZE=OFFSET; // FLOOR_HEIGHT-1;

	private static final int LEFT_FLOOR_START=OFFSET;
	private static final int LEFT_FLOOR_END=((WINDOW_DX-ICON_SIZE)>>1)-OFFSET;
	private static final int RIGHT_FLOOR_START=WINDOW_DX-LEFT_FLOOR_END;
	private static final int RIGHT_FLOOR_END=WINDOW_DX-OFFSET;

	private static final int ELEVATOR_X=LEFT_FLOOR_END+OFFSET;
	private static final int RIGHT_ICON_COUNT=Math.floorDiv(RIGHT_FLOOR_END-RIGHT_FLOOR_START,ICON_SIZE+OFFSET);
	private static final int LEFT_ICON_COUNT=Math.floorDiv(LEFT_FLOOR_END-LEFT_FLOOR_START,ICON_SIZE+OFFSET);

	private final Image background;
	private final Building model;
	private ElevatorState elevator=null;
	private final List<List<VisitorState>> waiting=new ArrayList<>(Building.MAX_FLOOR+1);
	private final List<List<VisitorState>> working=new ArrayList<>(Building.MAX_FLOOR+1);
	private final Color[] scale= { Color.GREEN, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.RED };

	public View(Building m) {
		super("Simple Elevator Simulator");
		model=m;
		for (int floor=0; floor<=Building.MAX_FLOOR; floor++) {
			waiting.add(new ArrayList<VisitorState>());
			working.add(new ArrayList<VisitorState>());
		}
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
		for (int floor=Building.LOBBY; floor<=Building.MAX_FLOOR; floor++) {
			final int y=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT);
			if (floor>Building.LOBBY) graphics.drawLine(LEFT_FLOOR_START,y,LEFT_FLOOR_END,y);
			graphics.drawLine(RIGHT_FLOOR_START,y,RIGHT_FLOOR_END,y);
		}
	}

	@Override
	public void notifyListener(AbstractSimulator source) {
		final BufferStrategy bs=getBufferStrategy();
		do {
			do {
				// Get a new graphics context every time through the loop
				// to make sure the strategy is validated
				final Graphics graphics=bs.getDrawGraphics();
				graphics.drawImage(background,0,0,WINDOW_DX,WINDOW_DY,null);
				prepareModel();
				drawModel(graphics);
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
	 *
	 */
	private void prepareModel() {
		for (int floor=0; floor<=StaticModelMain.MAX_FLOOR; floor++) {
			waiting.get(floor).clear();
			working.get(floor).clear();
		}
		for (final BasicModelEntity entity : model.listDomainEntities()) {
			if (entity instanceof Floor) {
				final int floor=((Floor) entity).getFloor();
				for (final BasicModelEntity visitor : ((Floor) entity).listDomainEntities()) {
					final VisitorState state=((Visitor) visitor).getState();
					if (state.getActivity()==ACTIVITY.waiting) waiting.get(floor).add(state);
					else working.get(floor).add(state);
				}
			} else if (entity instanceof Elevator) {
				elevator=((Elevator) entity).getState();
			}
		}
		for (int floor=Building.LOBBY; floor<=Building.MAX_FLOOR; floor++) {
			Collections.sort(working.get(floor),(o1, o2) -> {
				if (o1.getSatisfaction()<o2.getSatisfaction()) return -1;
				else if (o1.getSatisfaction()>o2.getSatisfaction()) return 1;
				return 0;
			});
		}
	}

	/**
	 * @param agent
	 */
	private void drawModel(Graphics graphics) {
		int dx;
		final int elevatorY=WINDOW_DY-OFFSET-(elevator.getCurrentFloor()*FLOOR_HEIGHT)-ICON_SIZE;
		graphics.setColor(Color.MAGENTA);
		graphics.fillRect(ELEVATOR_X,elevatorY,ICON_SIZE,ICON_SIZE);
		for (int floor=Building.LOBBY; floor<=Building.MAX_FLOOR; floor++) {
			List<VisitorState> queue=waiting.get(floor);
			if (!queue.isEmpty()) {
				if (queue.size()>RIGHT_ICON_COUNT) {
					dx=Math.floorDiv(RIGHT_FLOOR_END-RIGHT_FLOOR_START,queue.size());
					if (dx<1) dx=1;
				} else dx=ICON_SIZE+OFFSET;
				final int floorY=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT)-ICON_SIZE;
				graphics.setColor(Color.BLUE);
				for (int index=0; index<queue.size(); index++) {
					graphics.fillRect(RIGHT_FLOOR_START+(index*dx),floorY,ICON_SIZE,ICON_SIZE);
				}
			}
			queue=working.get(floor);
			if (!queue.isEmpty()) {
				if (queue.size()>LEFT_ICON_COUNT) {
					dx=Math.floorDiv(LEFT_FLOOR_END-LEFT_FLOOR_START,queue.size());
					if (dx<1) dx=1;
				} else dx=ICON_SIZE+OFFSET;
				final int floorY=WINDOW_DY-OFFSET-(floor*FLOOR_HEIGHT)-ICON_SIZE;
				for (int index=0; index<queue.size(); index++) {
					int color=queue.get(index).getSatisfaction();
					if (color>=scale.length) color=scale.length-1;
					graphics.setColor(scale[color]);
					graphics.fillRect(LEFT_FLOOR_END-ICON_SIZE-(index*dx),floorY,ICON_SIZE,ICON_SIZE);
				}
			}
		}
	}

	public void close() {
		setVisible(false);
		dispose();
	}

}
