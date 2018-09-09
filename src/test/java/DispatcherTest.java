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

		dispatcher.addAttendants(operator1, operator2, operator3, operator4, operator5, operator6, supervisor7, supervisor8, supervisor9, director);

	}

	@Test
	public void dispatchOne() {
		Call call = new Call(1L);
		dispatcher.dispatchCall(call);
		assertTrue(true);
	}

}
