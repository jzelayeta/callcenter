package com.almundo.callcenter.impl;

import java.util.UUID;
import com.almundo.callcenter.ICall;

public class Call implements ICall {

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

	public Long getStop() {
		return stop;
	}

	public void setId(UUID id) {
		this.id = id;
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
}
