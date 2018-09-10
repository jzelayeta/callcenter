import static org.junit.Assert.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.almundo.callcenter.ICall;
import com.almundo.callcenter.IDispatcher;
import com.almundo.callcenter.impl.Attendant;
import com.almundo.callcenter.AttendantPriority;
import com.almundo.callcenter.impl.Call;
import com.almundo.callcenter.impl.Dispatcher;

public class DispatcherTest {

	private static final int POOL_SIZE = 10;
	private static IDispatcher dispatcher;

	@Before
	public void callCenterSetUp() {

		dispatcher = new Dispatcher(POOL_SIZE);

		List<Attendant> operators = attendantWithPrioritySpawner(6, AttendantPriority.OPERATOR);
		List<Attendant> supervisors = attendantWithPrioritySpawner(3, AttendantPriority.SUPERVISOR);
		List<Attendant> director = attendantWithPrioritySpawner(3, AttendantPriority.DIRECTOR);


		dispatcher.addAttendants(operators);
		dispatcher.addAttendants(supervisors);
		dispatcher.addAttendants(director);

	}

	@After
	public void closeScheduler() {
		dispatcher.closeScheduler();
	}

	@Test
	public void oneCallOnQueueMustBeDispatchedFromOperator() {
		Call call = new Call(UUID.randomUUID());

		dispatcher.dispatchCall(call);

		waitAmount(10000);

		assertEquals(1, dispatcher.getFinishedCallsQueue().size());
		assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().peek().getAttendant().getAttendantPriority());

	}

	@Test
	public void tenCalls() {
		List<ICall> calls = callSpawner(10);

		calls.forEach(dispatcher::dispatchCall);

		waitAmount(10000);

		assertEquals(10, dispatcher.getFinishedCallsQueue().size());

		List<ICall> finishedCalls = dispatcher.getFinishedCallsQueue().stream().sorted(Comparator.comparing(ICall::getStart)).collect(Collectors.toList());

		assertFirstTenCalls(finishedCalls);

	}

	@Test
	public void moreThanTenCalls() {
		List<ICall> calls = callSpawner(15);

		calls.forEach(dispatcher::dispatchCall);

		waitAmount(10000);

		assertEquals(15, dispatcher.getFinishedCallsQueue().size());

		List<ICall> finishedCalls = dispatcher.getFinishedCallsQueue().stream().sorted(Comparator.comparing(ICall::getStart)).collect(Collectors.toList());

		assertFirstTenCalls(finishedCalls);

	}

	private List<ICall> callSpawner(int amount) {
		return Stream.generate(() -> new Call(UUID.randomUUID()))
				.limit(amount)
				.collect(Collectors.toList());
	}

	private List<Attendant> attendantWithPrioritySpawner(int amount, AttendantPriority priority) {
		return Stream.generate(() -> new Attendant(new RandomDataGenerator().nextLong(1, 500), priority))
				.limit(amount)
				.peek(attendant -> attendant.addObserver(dispatcher))
				.collect(Collectors.toList());
	}

	private static void waitAmount(long amountInMs) {
		while (dispatcher.getPendingCallsQueue().size() != 0) ;

		try {
			Thread.sleep(amountInMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void assertFirstTenCalls(List<ICall> finishedCalls) {
		finishedCalls.subList(0, 6).forEach(call -> assertEquals(AttendantPriority.OPERATOR, call.getAttendant().getAttendantPriority()));
		finishedCalls.subList(6, 9).forEach(call -> assertEquals(AttendantPriority.SUPERVISOR, call.getAttendant().getAttendantPriority()));
		finishedCalls.subList(9, 10).forEach(call -> assertEquals(AttendantPriority.DIRECTOR, call.getAttendant().getAttendantPriority()));
	}

}
