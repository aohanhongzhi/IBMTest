package ibm;

import java.util.LinkedList;

import ibm.ThreadPoolC.WorkThreadC;

final class ThreadPoolC {
	private static final int worker_num = 3;
	private LinkedList<Runnable> taskQueue = new LinkedList<Runnable>();
	private static ThreadPoolC threadPoolC;
	private WorkThreadC[] workThreadCs;

	private ThreadPoolC() {
		workThreadCs = new WorkThreadC[worker_num];
		for (int i = 0; i < worker_num; ++i) {
			workThreadCs[i] = new WorkThreadC("ThreadPoolC:"+String.valueOf(i));
			workThreadCs[i].start();
		}
	}

	// 单例模型
	public static ThreadPoolC geThreadPoolB() {

		if (threadPoolC == null) {
			threadPoolC = new ThreadPoolC();
		}
		return threadPoolC;

	}

	public void execute(Runnable task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
		}
	}

	class WorkThreadC extends Thread {
		public WorkThreadC(String name) {
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
