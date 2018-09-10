public interface ICall {

	Long getStart();

	Long getStop();

	default Long getDuration() {
		return getStop() - getStart();
	}
}
