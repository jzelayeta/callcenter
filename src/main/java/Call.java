import java.time.Duration;

public class Call {

	private Long id;
	private Long start;
	private Long stop;

	public Call(Long id, Long start, Long stop) {
		this.id = id;
		this.start = start;
		this.stop = stop;
	}

	public Call(Long id) {
		this.id = id;
		this.start = System.nanoTime();
	}

	public Long getId() {
		return id;
	}

	public Long getStart() {
		return start;
	}

	public Long getStop() {
		return stop;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public void setStop(Long stop) {
		this.stop = stop;
	}
}
