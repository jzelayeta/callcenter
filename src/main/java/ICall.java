import java.time.Duration;
import java.util.UUID;

public interface ICall {

	UUID getId();

	Long getStart();

	Long getStop();

	Attendant getAttendant();

	void setStart(Long time);

	void setStop(Long time);

	void setAttendant(Attendant attendant);

	default Long getDuration() {
		Duration duration = Duration.ZERO.plusNanos(getStop()).minusNanos(getStart());
		return duration.getSeconds();
	}
}
