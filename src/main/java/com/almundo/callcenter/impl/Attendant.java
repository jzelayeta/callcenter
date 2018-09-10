package com.almundo.callcenter.impl;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.almundo.callcenter.AttendantPriority;
import com.almundo.callcenter.IAttendant;
import com.almundo.callcenter.ICall;

public class Attendant extends Observable implements IAttendant {

	private Long id;
	private AttendantPriority attendantPriority;
	private ICall currentCall;
	private static final Logger LOGGER = LoggerFactory.getLogger(Attendant.class);

	public Attendant(Long id, AttendantPriority attendantPriority) {
		this.id = id;
		this.attendantPriority = attendantPriority;
	}

	public void run() {
		this.currentCall.setStart(System.nanoTime());
		LOGGER.info("Call " + currentCall.getId() + " started by Attendant id: " + this.id + " with priority : " + attendantPriority.name());
		int callDuration = ThreadLocalRandom.current().nextInt(5000, 10000);

		try {
			Thread.sleep(callDuration);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		this.currentCall.setStop(System.nanoTime());
		this.currentCall.setAttendant(this);
		LOGGER.info("Call ended after: " + this.currentCall.getDuration() + " seconds. Attendant id: " + this.id);
		setChanged();
		notifyObservers(this);
	}

	@Override
	public AttendantPriority getAttendantPriority() {
		return attendantPriority;
	}

	@Override
	public void assignCall(ICall call) {
		this.currentCall = call;
	}

	@Override
	public ICall getCurrentCall() {
		return currentCall;
	}

	@Override
	public Long getId() {
		return id;
	}


}
