package com.almundo.callcenter;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import com.almundo.callcenter.impl.Attendant;

public interface IDispatcher extends Observer{

	void dispatchCall(ICall call);

	void addAttendants(List<Attendant> attendants);

	void closeScheduler();

	BlockingQueue<ICall> getPendingCallsQueue();

	BlockingQueue<ICall> getFinishedCallsQueue();
}
