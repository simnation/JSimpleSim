/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.scheduling;

/**
 * Provides time and calendar functionality to scale and navigate the
 * simulation's time axis.
 * <p>
 * Ticks are stored as {@code long} primitive. A time scale is provided,
 * standardized with one second set to one. The scale may be changed because
 * within the simulator, only the {@code Time} wrapper is used.
 * <p>
 * This class is immutable and thus tread safe.
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
	public static final int TICKS_PER_SECOND=1;
	public static final int TICKS_PER_MINUTE=TICKS_PER_SECOND*SECONDS_PER_MINUTE;
	public static final int TICKS_PER_HOUR=MINUTES_PER_HOUR*TICKS_PER_MINUTE;
	public static final int TICKS_PER_DAY=HOURS_PER_DAY*TICKS_PER_HOUR;
	public static final int TICKS_PER_WEEK=DAYS_PER_WEEK*TICKS_PER_DAY;
	public static final int TICKS_PER_MONTH=DAYS_PER_MONTH*TICKS_PER_DAY;
	public static final int TICKS_PER_YEAR=MONTHS_PER_YEAR*TICKS_PER_MONTH;

	// some basic Time objects, for multiples see static methods below...
	public static final Time ZERO=new Time(0);
	public static final Time TICK=new Time(TICKS_PER_SECOND);
	public static final Time INFINITY=new Time(Long.MAX_VALUE);
	
	public static final Time HOUR=new Time(TICKS_PER_HOUR);
	public static final Time DAY=new Time(TICKS_PER_DAY);
	public static final Time WEEK=new Time(TICKS_PER_WEEK);
	public static final Time MONTH=new Time(TICKS_PER_MONTH);
	public static final Time YEAR=new Time(TICKS_PER_YEAR);

	/**
	 * This is an immutable class, so ticks are final (multiple references to a
	 * changing time instance are prone to hard to find bugs)
	 */
	private final long ticks;

	/**
	 * Basic constructor
	 *
	 * @param value number of ticks
	 */
	public Time(long value) {
		ticks=value;
	}

	/**
	 * Copy constructor
	 *
	 * @param time time to duplicate
	 */
	public Time(Time time) {
		this(time.ticks);
	}

	/**
	 * Constructor converting a given date into ticks
	 *
	 * @param year  the year (counting starts at zero)
	 * @param month the month (between 0 and 12, counting starts at zero)
	 * @param day   the day (between 0 and 30, counting starts at zero)
	 * @param hour  the hour (between 0 and 24)
	 * @param min   the minute (between 0 and 60)
	 * @param sec   the second (between 0 and 60)
	 */
	public Time(int year, int month, int day, int hour, int min, int sec) {
		this((year*TICKS_PER_YEAR)+(month*TICKS_PER_MONTH)+(day*TICKS_PER_DAY)+(hour*TICKS_PER_HOUR)
				+(min*TICKS_PER_MINUTE)+(sec*TICKS_PER_SECOND));
	}

	/**
	 * Constructor converting a given date into ticks, facilitating shorter time
	 * spans.
	 *
	 * @param day  the day (counting starts at zero)
	 * @param hour the hour (between 0 and 24)
	 * @param min  the minute (between 0 and 60)
	 */
	public Time(int day, int hour, int min) {
		this((day*TICKS_PER_DAY)+(hour*TICKS_PER_HOUR)+(min*TICKS_PER_MINUTE));
	}

	public long getTicks() { return ticks; }

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
	
	public boolean isBefore(Time other) {
		return (this.getTicks()<other.getTicks());
	}
	
	public boolean isAfter(Time other) {
		return (this.getTicks()>other.getTicks());
	}

	public static long minutes(int minutes) {
		return minutes*TICKS_PER_MINUTE;
	}

	public static long hours(int hours) {
		return hours*TICKS_PER_HOUR;
	}

	public static long days(int days) {
		return days*TICKS_PER_DAY;
	}

	public static long months(int months) {
		return months*TICKS_PER_MONTH;
	}

	public static long years(int years) {
		return years*TICKS_PER_YEAR;
	}

	@Override
	public String toString() {
		final int years=(int) (getTicks()/TICKS_PER_YEAR);
		final int months=(int) ((getTicks()%TICKS_PER_YEAR)/TICKS_PER_MONTH);
		final int days=(int) ((getTicks()%TICKS_PER_MONTH)/TICKS_PER_DAY);
		final int hours=(int) ((getTicks()%TICKS_PER_DAY)/TICKS_PER_HOUR);
		final int minutes=(int) ((getTicks()%TICKS_PER_HOUR)/TICKS_PER_MINUTE);
		final int seconds=(int) ((getTicks()%TICKS_PER_MINUTE)/TICKS_PER_SECOND);

		return String.format("[%02d.%02d.%02d %02d:%02d.%02d]",years,months,days,hours,minutes,seconds);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj==null)||!(obj instanceof Time)) return false;
		return this.getTicks()==((Time) obj).getTicks();
	}

	@Override
	public int hashCode() {
		return Long.hashCode(getTicks());
	}

	@Override
	public int compareTo(Time other) {
		if (this.getTicks()<other.getTicks()) return -1;
		else if (this.getTicks()>other.getTicks()) return 1;
		else return 0;
	}

}
