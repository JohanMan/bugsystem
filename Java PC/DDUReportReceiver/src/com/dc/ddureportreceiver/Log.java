package com.dc.ddureportreceiver;

public class Log {

	private int id;
	private String time;
	private String logPath;
	private int state;

	public Log() {

	}

	public Log(int id, String time, String logPath, int state) {
		super();
		this.id = id;
		this.time = time;
		this.logPath = logPath;
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
