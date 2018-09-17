package com.almundo.callcenter.model;

import java.time.Duration;
import java.util.UUID;

public class Call implements Comparable<Call>{

	private UUID id;
	private Long start;
	private Long stop;
	private Attendant attendant;

	public Call(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public void setStop(Long stop) {
		this.stop = stop;
	}

	public Attendant getAttendant() {
		return attendant;
	}

	public void setAttendant(Attendant attendant) {
		this.attendant = attendant;
	}

	public long getDurationInSeconds() {
		Duration duration = Duration.ZERO.plusNanos(stop).minusNanos(start);
		return duration.getSeconds();
	}


	/**
	 * Since two calls may have been started at a same time, it's needed to tiebreak with AttendantPriority.
	 * @param otherCall
	 * @return
	 */
	@Override
	public int compareTo(Call otherCall) {
		if(this.start.equals(otherCall.start)) return this.attendant.getAttendantPriority().compareTo(otherCall.attendant.getAttendantPriority());
		if (this.start > otherCall.start) return 1;
		if (this.start < otherCall.start ) return -1;
		return 0;
	}
}
