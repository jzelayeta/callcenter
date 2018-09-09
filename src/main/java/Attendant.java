import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attendant implements Runnable{

	private Long id;
	private AttendantPriority attendantPriority;
	private Call call;
	private static final Logger LOGGER = LoggerFactory.getLogger(Attendant.class);

	public Attendant(Long id, AttendantPriority attendantPriority) {
		this.id = id;
		this.attendantPriority = attendantPriority;
	}

	public void run() {
		try {
			int callDuration = ThreadLocalRandom.current().nextInt(5000, 10000 + 1);
			Thread.sleep(callDuration);
			LOGGER.info("Call ended after: " + callDuration + " seconds. Attendant id: " + this.id + " with priority : " + attendantPriority.name());
		} catch (InterruptedException ignored) {
		}
		this.call.setStop(System.nanoTime());
	}

	public AttendantPriority getAttendantPriority() {
		return attendantPriority;
	}

	public void assignCall(Call call) {
		this.call = call;
	}

}
