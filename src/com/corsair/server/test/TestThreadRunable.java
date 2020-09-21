package com.corsair.server.test;

public class TestThreadRunable implements Runnable {
	private int thid;
	private ThreadCallSourse tcs;

	public TestThreadRunable(int thid) {
		this.thid = thid;
	}

	public TestThreadRunable(ThreadCallSourse tcs, int thid) {
		this.thid = thid;
		this.tcs = tcs;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// (new ThreadCallSourse()).procedure(thid);
		tcs.procedure(thid);
	}
}
