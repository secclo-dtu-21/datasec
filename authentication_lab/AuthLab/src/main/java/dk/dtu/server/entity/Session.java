package dk.dtu.server.entity;

public class Session {

	// This valid time is in the second scale, need to be converted to millisecond
	private int validTime;
	private final long loginBeginTime;

	public Session(int validTime) {
		loginBeginTime = System.currentTimeMillis();
		this.validTime = validTime;
	}

	public boolean isAuthenticated() {
		long currentTimeStamp = System.currentTimeMillis();
		return currentTimeStamp - loginBeginTime <= (validTime * 1000L);
	}

}
