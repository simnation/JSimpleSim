/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

/**
 * This is a calendar class to scale and navigate the simulation's time axis.<p>
 * This class serves also as a wrapper around the time format. Currently, ticks
 * are stored as a {@code long} primitive, but the format my be changed because
 * within the simulator, only the {@code Time} wrapper is used.
 *<p>
 * Note: This class is immutable.
 *
 * @author Rene Kuhlemann
 *
 */
public final class Time implements Comparable<Time> {

	// basic coords of the time system
	public static final int MINUTES_PER_HOUR=60;
	public static final int HOURS_PER_DAY=24;
	public static final int DAYS_PER_WEEK=7;
	public static final int DAYS_PER_MONTH=30;
	public static final int MONTHS_PER_YEAR=12;
	public static final int DAYS_PER_YEAR=DAYS_PER_MONTH*MONTHS_PER_YEAR; // =360

	// as a result, duration of hours, days, months...
	public static final int MINUTE=1; // calibrating to one, so an integer can even represent fractions of hours
	public static final int HOUR=MINUTES_PER_HOUR*MINUTE;
	public static final int DAY=HOURS_PER_DAY*HOUR;
	public static final int WEEK=DAYS_PER_WEEK*DAY;
	public static final int MONTH=DAYS_PER_MONTH*DAY;
	public static final int YEAR=MONTHS_PER_YEAR*MONTH;
	
	public static final Time ZERO=new Time(0);

	/*
	 * This is an immutable class, so ticks are final (multiple references to a
	 * changing time instance are prone to hard to find bugs)
	 */
	private final long ticks;

	public Time(long ticks) {
		this.ticks=ticks;
	}

	// copy constructor
	public Time(Time time) {
		this(time.ticks);
	}

	public Time(int year, int month, int day, int hour, int min) {
		this((year*YEAR)+(month*MONTH)+(day*DAY)+(hour*HOUR)+min);
	}

	public long getTicks() {
		return ticks;
	}

	public Time add(long t) {
		return new Time(this.getTicks()+t);
	}

	public Time add(Time time) {
		return add(time.getTicks());
	}

	public Time sub(long t) {
		return new Time(this.getTicks()-t);
	}

	public Time sub(Time time) {
		return sub(time.getTicks());
	}

	public long minutes() {
		return minutes(getTicks());
	}

	public long hours() {
		return hours(getTicks());
	}

	public long days() {
		return days(getTicks());
	}

	public long months() {
		return months(getTicks());
	}

	public long years() {
		return years(getTicks());
	}

	public static String toString(long time) {
		return new Time(time).toString();
	}

	public static long minutes(long ticks) {
		return ticks%HOUR;
	}

	public static long hours(long ticks) {
		return (ticks%DAY)/HOUR;
	}

	public static long days(long ticks) {
		return (ticks%MONTH)/DAY;
	}

	public static long months(long ticks) {
		return (ticks%YEAR)/MONTH;
	}

	public static long years(long ticks) {
		return ticks/YEAR;
	}
	
	public static Time getZero() {
		return ZERO;
	}

	@Override
	public String toString() {
		return String.format("[%02d.%02d.%02d %02d:%02d]",years(),months(),days(),hours(),minutes());
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj==null)||!(obj instanceof Time)) return false;
		return this.getTicks()==((Time) obj).getTicks();
	}
	
	@Override
	public int hashCode() {
		return (int) getTicks();
	}

	@Override
	public int compareTo(Time other) {
		if (this.getTicks()<other.getTicks())
			return -1;
		else if (this.getTicks()>other.getTicks())
			return 1;
		else return 0;
	}

}
