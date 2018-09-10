import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attendant extends Observable implements Runnable  {

	private Long id;
	private AttendantPriority attendantPriority;
	private ICall call;
	private static final Logger LOGGER = LoggerFactory.getLogger(Attendant.class);

	public Attendant(Long id, AttendantPriority attendantPriority) {
		this.id = id;
		this.attendantPriority = attendantPriority;
	}

	public void run() {
		this.call.setStart(System.nanoTime());
		LOGGER.info("Call " + call.getId() + " started by Attendant id: " + this.id + " with priority : " + attendantPriority.name());
		int callDuration = ThreadLocalRandom.current().nextInt(5000, 10000 + 1);

		try {
			Thread.sleep(callDuration);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		this.call.setAttendant(this);
		this.call.setStop(System.nanoTime());
		LOGGER.info("Call ended after: " + this.call.getDuration() + " seconds. Attendant id: " + this.id);
		setChanged();
		notifyObservers(this);
	}

	public AttendantPriority getAttendantPriority() {
		return attendantPriority;
	}

	public void assignCall(ICall call) {
		this.call = call;
	}

	public ICall getCall() {
		return call;
	}
}
