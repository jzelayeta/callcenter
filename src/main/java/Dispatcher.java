import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher {
	private ExecutorService priorityJobPoolExecutor;
	private ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
	private PriorityBlockingQueue<Attendant> attendantsQueue;
	private BlockingQueue<Call> callsQueue;

	public Dispatcher(Integer poolSize) {
		priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
		attendantsQueue = new PriorityBlockingQueue<>(poolSize, Comparator.comparing(Attendant::getAttendantPriority));
		callsQueue = new LinkedBlockingDeque<>();
	}

	public void dispatchCall(Call call) {
		callsQueue.add(call);
		priorityJobScheduler.execute(()->{
			while (!callsQueue.isEmpty()) {
				try {
					Attendant attendant = attendantsQueue.take();
					Call callToDispatch = callsQueue.take();
					attendant.assignCall(callToDispatch);
					priorityJobPoolExecutor.execute(attendant);
				} catch (InterruptedException e) {
					// exception needs special handling
					break;
				}
			}
		});
	}

	public void addAttendant(Attendant attendant) {
		attendantsQueue.add(attendant);
	}

	public void addAttendants(Attendant... attendants) {
		attendantsQueue.addAll(Arrays.asList(attendants));
	}

}
