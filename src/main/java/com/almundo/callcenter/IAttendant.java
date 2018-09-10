package com.almundo.callcenter;

public interface IAttendant extends Runnable{

	Long getId();

	AttendantPriority getAttendantPriority();

	ICall getCall();

	void assignCall(ICall call);

}
