/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.core.scheduling;

/**
 * Provides time and calendar functionality to scale and navigate the
 * simulation's time axis.
 * <p>
 * This class serves also as a wrapper around the time format. Currently, ticks
 * are stored as a {@code long} primitive, but the format my be changed because
 * within the simulator, only the {@code Time} wrapper is used.
 * <p>
 * This class is immutable and thus tread safe.
 *
 */
public final class Time implements Comparable<Time> {

	// basic coords of the time system
	public static final int SECONDS_PER_MINUTE=60;
	public static final int MINUTES_PER_HOUR=60;
	public static final int HOURS_PER_DAY=24;
	public static final int DAYS_PER_WEEK=7;
	public static final int DAYS_PER_MONTH=30;
	public static final int MONTHS_PER_YEAR=12;
	public static final int DAYS_PER_YEAR=DAYS_PER_MONTH*MONTHS_PER_YEAR; // =360

	// as a result, duration of hours, days, months...
	public static final int SECOND=1;
	public static final int MINUTE=SECOND*SECONDS_PER_MINUTE; // calibrating to one, so an integer can even represent fractions of hours
	public static final int HOUR=MINUTES_PER_HOUR*MINUTE;
	public static final int DAY=HOURS_PER_DAY*HOUR;
	public static final int WEEK=DAYS_PER_WEEK*DAY;
	public static final int MONTH=DAYS_PER_MONTH*DAY;
	public static final int YEAR=MONTHS_PER_YEAR*MONTH;

	public static final Time ZERO=new Time(0);
	public static final Time INFINITY=new Time(Long.MAX_VALUE);

	/**
	 * This is an immutable class, so ticks are final (multiple references to a
	 * changing time instance are prone to hard to find bugs)
	 */
	private final long ticks;

	public Time(long value) {
		ticks=value;
	}

	// copy constructor
	public Time(Time time) {
		this(time.ticks);
	}

	public Time(int year, int month, int day, int hour, int min, int sec) {
		this((year*YEAR)+(month*MONTH)+(day*DAY)+(hour*HOUR)+(min*MINUTE)+(sec*SECOND));
	}

	public long getTicks() {
		return ticks;
	}

	public Time add(long value) {
		return new Time(getTicks()+value);
	}

	public Time add(Time time) {
		return add(time.getTicks());
	}

	public Time sub(long value) {
		return new Time(getTicks()-value);
	}

	public Time sub(Time time) {
		return sub(time.getTicks());
	}

	public long seconds() {
		return seconds(getTicks());
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

	public static int seconds(long ticks) {
		return (int) ((ticks%MINUTE)/SECOND);
	}
	
	public static int minutes(long ticks) {
		return (int) ((ticks%HOUR)/MINUTE);
	}

	public static int hours(long ticks) {
		return (int) ((ticks%DAY)/HOUR);
	}

	public static int days(long ticks) {
		return (int) ((ticks%MONTH)/DAY);
	}

	public static int months(long ticks) {
		return (int) ((ticks%YEAR)/MONTH);
	}

	public static int years(long ticks) {
		return (int) (ticks/YEAR);
	}

	@Override
	public String toString() {
		return String.format("[%02d.%02d.%02d %02d:%02d.%02d]",years(),months(),days(),hours(),minutes(),seconds());
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
		if (this.getTicks()<other.getTicks()) return -1;
		else if (this.getTicks()>other.getTicks()) return 1;
		else return 0;
	}

}
