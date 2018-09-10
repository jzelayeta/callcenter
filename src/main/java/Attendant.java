import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attendant extends Observable implements Runnable  {

	private Long id;
	private AttendantPriority attendantPriority;
	private Call call;
	private static final Logger LOGGER = LoggerFactory.getLogger(Attendant.class);

	public Attendant(Long id, AttendantPriority attendantPriority) {
		this.id = id;
		this.attendantPriority = attendantPriority;
	}

	public void run() {
		LOGGER.info("Call " + call.getId() + " started by Attendant id: " + this.id + " with priority : " + attendantPriority.name());
		int callDuration = ThreadLocalRandom.current().nextInt(5000, 10000 + 1);

		try {
			Thread.sleep(callDuration);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		LOGGER.info("Call ended after: " + callDuration + " seconds. Attendant id: " + this.id);
		this.call.setStop(System.nanoTime());
	}

	public AttendantPriority getAttendantPriority() {
		return attendantPriority;
	}

	public void assignCall(Call call) {
		this.call = call;
	}

	public Call getCall() {
		return call;
	}
}
