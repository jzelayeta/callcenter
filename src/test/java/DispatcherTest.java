import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.almundo.callcenter.model.Attendant;
import com.almundo.callcenter.model.AttendantPriority;
import com.almundo.callcenter.model.Call;
import com.almundo.callcenter.model.Dispatcher;

public class DispatcherTest {

	private static final int POOL_SIZE = 10;
	private Dispatcher dispatcher;
	private List<Attendant> operators;
	private List<Attendant> supervisors;
	private List<Attendant> directors;

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
	public void oneCallOnQueueMustBeDispatchedByOperator() {
		Call call = new Call(UUID.randomUUID());

		dispatcher.dispatchCall(call);

		waitMillis(10000);

		assertEquals(1, dispatcher.getFinishedCallsQueue().size());
		assertEquals(AttendantPriority.OPERATOR, dispatcher.getFinishedCallsQueue().peek().getAttendant().getAttendantPriority());
		assertAllAttendantsAreAvailableAfterProcessingAllCalls();

	}

	@Test
	public void sameAmountOfIncomingCallsAsAttendantsInitialAvailability() {
		List<Call> calls = spawnCalls(10);
		assertCallsAreDispatched(calls);
		assertAllAttendantsAreAvailableAfterProcessingAllCalls();
	}

	@Test
	public void moreAmountOfIncomingCallsThanAttendantsAvailability() {
		List<Call> calls = spawnCalls(15);
		assertCallsAreDispatched(calls);
		assertAllAttendantsAreAvailableAfterProcessingAllCalls();

	}

	@Test
	public void assertIncomingCallsFromDifferentThreadsAreDispatched() {

		final int AMOUNT_OF_THREADS = 4;
		final int AMOUNT_OF_CALLS_PER_THREAD = 5;
		final int TOTAL_AMOUNT_OF_CALLS = AMOUNT_OF_THREADS * AMOUNT_OF_CALLS_PER_THREAD;

		List<Thread> threads = spawnThreadWithIncomingCalls(AMOUNT_OF_THREADS, spawnCalls(AMOUNT_OF_CALLS_PER_THREAD));

		threads.forEach(Thread::start);

		waitMillis(25000);

		assertEquals(TOTAL_AMOUNT_OF_CALLS, dispatcher.getFinishedCallsQueue().size());

		assertAllAttendantsAreAvailableAfterProcessingAllCalls();
	}

	private void assertCallsAreDispatched(List<Call> calls) {
		calls.forEach(dispatcher::dispatchCall);

		waitMillis(10000);

		assertEquals(calls.size(), dispatcher.getFinishedCallsQueue().size());

		assertFirstIncomingCallsAreDispatchedAccordingAttendantsPriority();

	}

	/**
	 * Given a list of finished calls, this method will assert that the first N calls, being N the number of initial attendants available, those calls
	 * will be attended according the priority of those N attendants
	 */
	private void assertFirstIncomingCallsAreDispatchedAccordingAttendantsPriority() {
		List<Call> finishedCallsOrderedByAttendantPriorityAsc = getFinishedCallsOrderedByAttendantPriorityAsc();

		finishedCallsOrderedByAttendantPriorityAsc.subList(0,
				operators.size()).forEach(call -> assertEquals(AttendantPriority.OPERATOR, call.getAttendant().getAttendantPriority()));

		finishedCallsOrderedByAttendantPriorityAsc.subList(operators.size(),
				operators.size() + supervisors.size()).forEach(call -> assertEquals(AttendantPriority.SUPERVISOR, call.getAttendant().getAttendantPriority()));

		finishedCallsOrderedByAttendantPriorityAsc.subList(operators.size() + supervisors.size(),
				operators.size() + supervisors.size() + directors.size()).forEach(call -> assertEquals(AttendantPriority.DIRECTOR, call.getAttendant().getAttendantPriority()));
	}

	/**
	 * Assert that after processing all pending calls, attendants will be idle on {@code attendantsQueue} waiting to receive more incoming calls
	 */
	private void assertAllAttendantsAreAvailableAfterProcessingAllCalls(){
		int totalAttendantsSize = operators.size() + supervisors.size() + directors.size();
		assertEquals(totalAttendantsSize, dispatcher.getAttendantsQueue().size());
	}

	private List<Call> spawnCalls(int amount) {
		return Stream.generate(() -> new Call(UUID.randomUUID()))
				.limit(amount)
				.collect(Collectors.toList());
	}

	private List<Attendant> spawnAttendantsWithPriority(int amount, AttendantPriority priority) {
		return Stream.generate(() -> new Attendant(new RandomDataGenerator().nextLong(1, Integer.MAX_VALUE), priority))
				.limit(amount)
				.peek(attendant -> attendant.addObserver(dispatcher))
				.collect(Collectors.toList());
	}

	private List<Thread> spawnThreadWithIncomingCalls(int amountOfThreads, List<Call> calls){
		return Stream.generate(() -> new Thread(() -> calls.forEach(dispatcher::dispatchCall)))
				.limit(amountOfThreads)
				.collect(Collectors.toList());
	}

	private List<Call> getFinishedCallsOrderedByAttendantPriorityAsc() {
		return dispatcher.getFinishedCallsQueue().stream().sorted().collect(Collectors.toList());
	}

	private void waitMillis(long amountInMs) {
		while (dispatcher.getPendingCallsQueue().size() != 0) ;

		try {
			Thread.sleep(amountInMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
