import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
	private BlockingQueue<ICall> pendingCallsQueue;
	private BlockingQueue<ICall> finishedCallsQueue;
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
					ICall callToDispatch = pendingCallsQueue.take();
					attendant.assignCall(callToDispatch);
					priorityJobPoolExecutor.execute(attendant);
				} catch (InterruptedException e) {
					// exception needs special handling
					break;
				}
			}
		});
	}

	public void dispatchCall(ICall call) {
		pendingCallsQueue.add(call);
	}

	public void addAttendant(Attendant attendant) {
		attendantsQueue.add(attendant);
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
//		LOGGER.info("UPDATED!");
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
