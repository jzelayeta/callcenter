import java.util.Arrays;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher implements Observer {
	private ExecutorService priorityJobPoolExecutor;
	private ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
	private PriorityBlockingQueue<Attendant> attendantsQueue;
	private BlockingQueue<Call> pendingCallsQueue;
	private BlockingQueue<Call> finishedCallsQueue;
	private static final Logger LOGGER = LoggerFactory.getLogger(Attendant.class);

	public Dispatcher(Integer poolSize) {
		priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
		attendantsQueue = new PriorityBlockingQueue<>(poolSize, Comparator.comparing(Attendant::getAttendantPriority));
		pendingCallsQueue = new LinkedBlockingQueue<>();
		finishedCallsQueue = new LinkedBlockingQueue<>();

		priorityJobScheduler.execute(() -> {
			while (true) {
				try {
					Attendant attendant = attendantsQueue.take();
					Call callToDispatch = pendingCallsQueue.take();
					attendant.assignCall(callToDispatch);
					priorityJobPoolExecutor.execute(attendant);
				} catch (InterruptedException e) {
					// exception needs special handling
					break;
				}
			}
		});
	}

	public void dispatchCall(Call call) {
		pendingCallsQueue.add(call);
	}

	public void addAttendant(Attendant attendant) {
		attendantsQueue.add(attendant);
	}

	public void addAttendants(Attendant... attendants) {
		attendantsQueue.addAll(Arrays.asList(attendants));
	}

	public BlockingQueue<Call> getFinishedCallsQueue() {
		return finishedCallsQueue;
	}

	public BlockingQueue<Call> getPendingCallsQueue() {
		return pendingCallsQueue;
	}

	@Override
	public void update(Observable o, Object arg) {
		LOGGER.info("UPDATED!");
		Attendant attendant = (Attendant) arg;
		attendantsQueue.add(attendant);
		finishedCallsQueue.add(attendant.getCall());
	}

	protected void close(ExecutorService scheduler) {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
		}
	}

	public void closeScheduler() {
		close(priorityJobPoolExecutor);
		close(priorityJobScheduler);
	}
}
