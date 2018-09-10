package com.almundo.callcenter.model;

import java.time.Duration;
import java.util.UUID;

public class Call {

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
}
