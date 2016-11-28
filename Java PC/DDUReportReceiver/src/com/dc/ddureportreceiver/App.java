package com.dc.ddureportreceiver;


public class App {
	
	public static void main(String[] args) {
		ReportFrame reportFrame = new ReportFrame();
		ReportConnectioner reportConnectioner = new ReportConnectioner();
		reportConnectioner.setReportLisetner(reportFrame);
		reportConnectioner.startListener();
	}
	
}
