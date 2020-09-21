package com.corsair.server.test;

public class TestThreadRunable2 implements Runnable {
	private int thid;
	private ThreadCallSourse tcs;

	public TestThreadRunable2(int thid) {
		this.thid = thid;
	}

	public TestThreadRunable2(ThreadCallSourse tcs, int thid) {
		this.thid = thid;
		this.tcs = tcs;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// (new ThreadCallSourse()).procedure(thid);
		tcs.procedure2(thid);
	}
}
