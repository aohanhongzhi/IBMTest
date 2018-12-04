package ibm;


import java.util.LinkedList;


final class ThreadPool {
	private static  int worker_num = 5;
	private LinkedList<Runnable> taskQueue = new LinkedList<Runnable>();
	private static ThreadPool threadPool;
	private WorkThread[] workThreads;

	private ThreadPool(int num) {
		worker_num =num;
		workThreads = new WorkThread[worker_num];
		for (int i = 0; i < worker_num; ++i) {
			workThreads[i] = new WorkThread();
			workThreads[i].start();
		}
	}

	// 单例模型
	public static ThreadPool geThreadPool(int i) {

		if (threadPool == null) {
			threadPool = new ThreadPool(i);
		}
		return threadPool;

	}

	public void excecute(Runnable task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
		}
	}

	class WorkThread extends Thread {

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

