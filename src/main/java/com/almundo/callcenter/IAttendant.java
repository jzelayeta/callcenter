package com.almundo.callcenter;

public interface IAttendant extends Runnable{

	Long getId();

	AttendantPriority getAttendantPriority();

	ICall getCurrentCall();

	void assignCall(ICall call);

}
