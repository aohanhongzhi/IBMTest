package ibm;

import java.util.LinkedList;

import ibm.ThreadPoolB.WorkThreadB;

final class ThreadPoolB {
	private static final int worker_num = 4;
	private LinkedList<Runnable> taskQueue = new LinkedList<Runnable>();
	private static ThreadPoolB threadPoolB;
	private WorkThreadB[] workThreadBs;

	private ThreadPoolB() {
		workThreadBs = new WorkThreadB[worker_num];
		for (int i = 0; i < worker_num; ++i) {
			workThreadBs[i] = new WorkThreadB("ThreadPoolB:"+String.valueOf(i));
			workThreadBs[i].start();
		}
	}

	// 单例模型
	public static ThreadPoolB geThreadPoolB() {

		if (threadPoolB == null) {
			threadPoolB = new ThreadPoolB();
		}
		return threadPoolB;

	}

	public void execute(Runnable task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
		}
	}

	class WorkThreadB extends Thread {
		
		public WorkThreadB(String name) {
			// TODO Auto-generated constructor stub
			super(name);
		}

		public void run() {
			Runnable r = null;
			while (true) {
				synchronized (taskQueue) {
					while (taskQueue.isEmpty()) {

						try {
							taskQueue.wait(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (!taskQueue.isEmpty()) {
						r = taskQueue.remove();
					}
					if (r != null) {
						r.run();
					}
					r = null;

				}

			}

		}

	}

}

