import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DispatcherTest {

	private static final int POOL_SIZE = 10;
	private static Dispatcher dispatcher = new Dispatcher(POOL_SIZE);

	@Before
	public void callCenterSetUp(){

		Attendant operator1 = new Attendant(1L, AttendantPriority.OPERATOR);
		Attendant operator2 = new Attendant(2L, AttendantPriority.OPERATOR);
		Attendant operator3 = new Attendant(3L, AttendantPriority.OPERATOR);
		Attendant operator4 = new Attendant(4L, AttendantPriority.OPERATOR);
		Attendant operator5 = new Attendant(5L, AttendantPriority.OPERATOR);
		Attendant operator6 = new Attendant(6L, AttendantPriority.OPERATOR);
		Attendant supervisor7 = new Attendant(7L, AttendantPriority.SUPERVISOR);
		Attendant supervisor8 = new Attendant(8L, AttendantPriority.SUPERVISOR);
		Attendant supervisor9 = new Attendant(9L, AttendantPriority.SUPERVISOR);
		Attendant director = new Attendant(10L, AttendantPriority.DIRECTOR);

		operator1.addObserver(dispatcher);
		operator2.addObserver(dispatcher);
		operator3.addObserver(dispatcher);
		operator4.addObserver(dispatcher);
		operator5.addObserver(dispatcher);
		operator6.addObserver(dispatcher);
		supervisor7.addObserver(dispatcher);
		supervisor8.addObserver(dispatcher);
		supervisor9.addObserver(dispatcher);
		director.addObserver(dispatcher);

		dispatcher.addAttendants(operator1, operator2, operator3, operator4, operator5, operator6, supervisor7, supervisor8, supervisor9, director);

	}

	@Test
	public void oneCallOnQueueMustBeDispatchedFromOperator() {
		Call call = new Call(1L);
		dispatcher.dispatchCall(call);

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}

		assertEquals(1, dispatcher.getFinishedCallsQueue().size());
		try {
			assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().take().getAttendant().getAttendantPriority());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		dispatcher.closeScheduler();
	}

	@Test
	public void tenCalls() {
		Call call1 = new Call(1L);
		Call call2 = new Call(2L);
		Call call3 = new Call(3L);
		Call call4 = new Call(4L);
		Call call5 = new Call(5L);
		Call call6 = new Call(6L);
		Call call7 = new Call(7L);
		Call call8 = new Call(8L);
		Call call9 = new Call(9L);
		Call call10 = new Call(10L);

		dispatcher.dispatchCall(call1);
		dispatcher.dispatchCall(call2);
		dispatcher.dispatchCall(call3);
		dispatcher.dispatchCall(call4);
		dispatcher.dispatchCall(call5);
		dispatcher.dispatchCall(call6);
		dispatcher.dispatchCall(call7);
		dispatcher.dispatchCall(call8);
		dispatcher.dispatchCall(call9);
		dispatcher.dispatchCall(call10);

		while (dispatcher.getPendingCallsQueue().size() != 0);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(10, dispatcher.getFinishedCallsQueue().size());
		try {
			assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().take().getAttendant().getAttendantPriority());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		dispatcher.closeScheduler();
	}

	@Test
	public void moreThanTenCalls() {
		Call call1 = new Call(1L);
		Call call2 = new Call(2L);
		Call call3 = new Call(3L);
		Call call4 = new Call(4L);
		Call call5 = new Call(5L);
		Call call6 = new Call(6L);
		Call call7 = new Call(7L);
		Call call8 = new Call(8L);
		Call call9 = new Call(9L);
		Call call10 = new Call(10L);
		Call call11 = new Call(11L);
		Call call12 = new Call(12L);
		Call call13 = new Call(13L);
		Call call14 = new Call(14L);
		Call call15 = new Call(15L);

		dispatcher.dispatchCall(call1);
		dispatcher.dispatchCall(call2);
		dispatcher.dispatchCall(call3);
		dispatcher.dispatchCall(call4);
		dispatcher.dispatchCall(call5);
		dispatcher.dispatchCall(call6);
		dispatcher.dispatchCall(call7);
		dispatcher.dispatchCall(call8);
		dispatcher.dispatchCall(call9);
		dispatcher.dispatchCall(call10);
		dispatcher.dispatchCall(call11);
		dispatcher.dispatchCall(call12);
		dispatcher.dispatchCall(call13);
		dispatcher.dispatchCall(call14);
		dispatcher.dispatchCall(call15);

		while (dispatcher.getPendingCallsQueue().size() != 0);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(1, dispatcher.getFinishedCallsQueue().size());
		try {
			assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().take().getAttendant().getAttendantPriority());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		dispatcher.closeScheduler();
	}

}
