package ibm;

import java.util.LinkedList;

import ibm.ThreadPoolA.WorkThreadA;

final class ThreadPoolA {
	private static final int worker_num = 5;
	private LinkedList<Runnable> taskQueue = new LinkedList<Runnable>();
	private static ThreadPoolA threadPoolA;
	private WorkThreadA[] workThreadAs;

	private ThreadPoolA() {
		workThreadAs = new WorkThreadA[worker_num];
		for (int i = 0; i < worker_num; ++i) {
			workThreadAs[i] = new WorkThreadA("ThreadPoolA:"+String.valueOf(i));
			workThreadAs[i].start();
		}
	}

	// 单例模型
	public static ThreadPoolA geThreadPoolA() {

		if (threadPoolA == null) {
			threadPoolA = new ThreadPoolA();
		}
		return threadPoolA;

	}

	public void execute(Runnable task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
		}
	}

	class WorkThreadA extends Thread {
		public WorkThreadA(String name) {
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
