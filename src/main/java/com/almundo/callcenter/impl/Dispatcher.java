package com.almundo.callcenter.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.almundo.callcenter.IAttendant;
import com.almundo.callcenter.ICall;
import com.almundo.callcenter.IDispatcher;

public class Dispatcher implements IDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

	private ExecutorService priorityJobPoolExecutor;
	private ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
	private PriorityBlockingQueue<IAttendant> attendantsQueue;
	private BlockingQueue<ICall> pendingCallsQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<ICall> finishedCallsQueue = new LinkedBlockingQueue<>();


	public Dispatcher(Integer poolSize) {
		priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
		attendantsQueue = new PriorityBlockingQueue<>(poolSize, Comparator.comparing(IAttendant::getAttendantPriority));

		priorityJobScheduler.execute(() -> {
			while (true) {
				try {
					IAttendant attendant = attendantsQueue.take();
					ICall callToDispatch = pendingCallsQueue.take();
					attendant.assignCall(callToDispatch);
					priorityJobPoolExecutor.execute(attendant);
				} catch (InterruptedException e) {
					LOGGER.error("FAILED! " + e);
					break;
				}
			}
		});
	}

	@Override
	public void dispatchCall(ICall call) {
		pendingCallsQueue.add(call);
	}

	public void addAttendants(List<Attendant> attendants) {
		attendantsQueue.addAll(attendants);
	}

	public BlockingQueue<ICall> getFinishedCallsQueue() {
		return finishedCallsQueue;
	}

	public BlockingQueue<ICall> getPendingCallsQueue() {
		return pendingCallsQueue;
	}

	@Override
	public void update(Observable o, Object arg) {
		Attendant attendant = (Attendant) arg;
		attendantsQueue.add(attendant);
		finishedCallsQueue.add(attendant.getCurrentCall());
	}

	private void close(ExecutorService scheduler) {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
		}
	}

	@Override
	public void closeScheduler() {
		close(priorityJobPoolExecutor);
		close(priorityJobScheduler);
	}
}
