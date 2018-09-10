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
import com.almundo.callcenter.AttendantPriority;
import com.almundo.callcenter.ICall;
import com.almundo.callcenter.impl.Attendant;
import com.almundo.callcenter.impl.Call;
import com.almundo.callcenter.impl.Dispatcher;

public class DispatcherTest {

	private static final int POOL_SIZE = 10;
	private static Dispatcher dispatcher;
	List<Attendant> operators;
	List<Attendant> supervisors;
	List<Attendant> directors;

	@Before
	public void callCenterSetUp() {

		dispatcher = new Dispatcher(POOL_SIZE);

		operators = spawnAttendantsWithPriority(6, AttendantPriority.OPERATOR);
		supervisors = spawnAttendantsWithPriority(3, AttendantPriority.SUPERVISOR);
		directors = spawnAttendantsWithPriority(1, AttendantPriority.DIRECTOR);

		dispatcher.addAttendants(operators);
		dispatcher.addAttendants(supervisors);
		dispatcher.addAttendants(directors);

	}

	@After
	public void closeScheduler() {
		dispatcher.closeScheduler();
	}

	@Test
	public void oneCallOnQueueMustBeDispatchedFromOperator() {
		Call call = new Call(UUID.randomUUID());

		dispatcher.dispatchCall(call);

		waitMillis(10000);

		assertEquals(1, dispatcher.getFinishedCallsQueue().size());
		assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().peek().getAttendant().getAttendantPriority());

	}

	@Test
	public void sameAmountOfIncomingCallsAsAttendantsAvailability() {
		List<ICall> calls = spawnCalls(10);
		assertCallsAreDispatched(calls);
	}

	@Test
	public void moreAmountOfIncomingCallsThanAttendantsAvailability() {
		List<ICall> calls = spawnCalls(15);
		assertCallsAreDispatched(calls);
	}

	private void assertCallsAreDispatched(List<ICall> calls) {
		calls.forEach(dispatcher::dispatchCall);
		waitMillis(10000);
		assertEquals(calls.size(), dispatcher.getFinishedCallsQueue().size());

		List<ICall> finishedCalls = dispatcher.getFinishedCallsQueue().stream().sorted(Comparator.comparing(ICall::getStart)).collect(Collectors.toList());

		assertFirstIncomingCallsAreDispatchedAccordingAttendantsPriority(finishedCalls);

	}

	private void assertFirstIncomingCallsAreDispatchedAccordingAttendantsPriority(List<ICall> finishedCalls) {
		finishedCalls.subList(0, operators.size()).forEach(call -> assertEquals(AttendantPriority.OPERATOR, call.getAttendant().getAttendantPriority()));
		finishedCalls.subList(operators.size(), operators.size() + supervisors.size()).forEach(call -> assertEquals(AttendantPriority.SUPERVISOR, call.getAttendant().getAttendantPriority()));
		finishedCalls.subList(operators.size() + supervisors.size(), operators.size() + supervisors.size() + 1).forEach(call -> assertEquals(AttendantPriority.DIRECTOR, call.getAttendant().getAttendantPriority()));
	}

	private List<ICall> spawnCalls(int amount) {
		return Stream.generate(() -> new Call(UUID.randomUUID()))
				.limit(amount)
				.collect(Collectors.toList());
	}

	private List<Attendant> spawnAttendantsWithPriority(int amount, AttendantPriority priority) {
		return Stream.generate(() -> new Attendant(new RandomDataGenerator().nextLong(1, 500), priority))
				.limit(amount)
				.peek(attendant -> attendant.addObserver(dispatcher))
				.collect(Collectors.toList());
	}

	private static void waitMillis(long amountInMs) {
		while (dispatcher.getPendingCallsQueue().size() != 0) ;

		try {
			Thread.sleep(amountInMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
