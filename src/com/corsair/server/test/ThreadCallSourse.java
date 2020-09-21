package com.corsair.server.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadCallSourse {
	private Lock lock = new ReentrantLock();

	public void procedure(int ct) {
		lock.lock();
		try {
			System.out.println("thread " + ct + " do something1 begin");
			Test.testlist.add("added by thread:" + ct);
			String ms = Test.testlist.get(Test.testlist.size() - 1);
			// System.out.println("Read:" + ms);
			Thread.sleep(1000);
			System.out.println("thread " + ct + " do something1 end");
			lock.unlock();
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	public void procedure2(int ct) {
		lock.lock();
		try {
			System.out.println("thread " + ct + " do something2 begin");
			Test.testlist.add("added by thread:" + ct);
			String ms = Test.testlist.get(Test.testlist.size() - 1);
			// System.out.println("Read:" + ms);
			Thread.sleep(1000);
			System.out.println("thread " + ct + " do something2 end");
			lock.unlock();
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

}
