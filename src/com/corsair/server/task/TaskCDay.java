package com.corsair.server.task;

import java.util.TimerTask;

import com.corsair.server.weixin.WXMsgSend;

/**
 * @author Administrator
 *         每天执行一次的任务
 */
public class TaskCDay extends TimerTask {

	@Override
	public void run() {

		try {
			// WXMsgSend.upDateTempleMsgList();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
