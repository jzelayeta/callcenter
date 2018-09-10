package com.almundo.callcenter.model;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attendant extends Observable implements Runnable {

	private Long id;
	private AttendantPriority attendantPriority;
	private Call currentCall;
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
		LOGGER.info("Call ended after: " + this.currentCall.getDurationInSeconds() + " seconds. Attendant id: " + this.id);
		setChanged();
		notifyObservers(this);
	}

	public AttendantPriority getAttendantPriority() {
		return attendantPriority;
	}

	public void assignCall(Call call) {
		this.currentCall = call;
	}

	public Call getCurrentCall() {
		return currentCall;
	}

}
