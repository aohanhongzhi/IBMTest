package ibm;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Basic_ok_4 {
	static Logger loger;
	static int index = 0;

	public static void main(String[] args) {

		// 打印出线程名

		loger = Logger.getLogger("This is a log for developer!");
		FileHandler fh = null;
		try {
			fh = new FileHandler("Test.log", true);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		loger.addHandler(fh);
		loger.setLevel(Level.ALL);
		SimpleFormatter sf = new SimpleFormatter();
		fh.setFormatter(sf);
		loger.log(Level.INFO, "Main Thread has started!so the process has worked!");

		// 如果要成立一个物流公司，先得去成立一个物流中心，招募快递员，最后才能接收包裹订单。
		// First:dispatcher Service

		Dispather dispather = new Basic_ok_4().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product
		// boolean flag= true;

		// while (flag) {

		// 提交者这里是多线程随机提交三种不同的任务（任务带有标记）

		while (index < 20) {

			try {
				Thread.sleep((long) (Math.random() * 0));
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			char c = ' ';
			int x = (int) (1 + Math.random() * 3);

			switch (x) {
			case 1:
				c = 'A';
				break;
			case 2:
				c = 'B';
				break;
			case 3:
				c = 'C';
				break;
			default:
				break;
			}
			Submitter submitter = new Basic_ok_4().new Submitter(c, String.valueOf(index));

			new Thread(new Basic_ok_4().new Submit(dispather, submitter), "提交线程：".concat(String.valueOf(index)))
					.start();
			index++;

		}

	}

	class Dispather implements Runnable {

		// 无界队列的实现，由于队列逻辑上为无穷大（其实物理上受限于内存等实际大小）， 其实查看了源代码之后发现还是存在一个capacity的，只是当capacity
		// full 会进入等待然后再处理。
		// 从某这角度来说，LinkedBlockingDeque是一个双向队列，所以在此处就不是太重要
		// private BlockingQueue<Submitter> bq = new LinkedBlockingDeque<Submitter>();

		// 更改成有固定大小的，对于超出大小的任务就需要等待，然后再加入任务中。LinkedBlockingQueue是单向队列。

		private BlockingQueue<Submitter> bq = new LinkedBlockingQueue<>();

		public void put(Submitter c) {
			try {
				bq.put(c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			// 分别获得三种上线的滴滴司机，对应设计了三个线程池，每个线程池有不同固定数量的线程在等待任务。
			// 因为这个是单例模式，所以不好合并成一个线程池。

			ThreadPoolA threadPoolA = ThreadPoolA.geThreadPoolA();
			ThreadPoolB threadPoolB = ThreadPoolB.geThreadPoolB();
			ThreadPoolC threadPoolC = ThreadPoolC.geThreadPoolB();

			// dispatcher 一直分析队列里有没有数据，如果有立刻进行dispatch
			while (true) {

				Submitter submitter = null;
				if (bq != null && !bq.isEmpty()) {

					while (!bq.isEmpty()) {

						try {
							submitter = bq.take();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// 依据任务类型开始分配任务到不同的地方

						switch (submitter.getFlag()) {
						case 'A':
							// new Thread(new Basic_test_2().new
							// HandlerA('A')).start();
							threadPoolA.execute(new TaskRunnable(submitter));
							break;
						case 'B':
							// new Thread(new Basic_test_2().new
							// HandlerB('B')).start();
							threadPoolB.execute(new TaskRunnable(submitter));
							break;
						case 'C':
							// new Thread(new Basic_test_2().new
							// HandlerC('C')).start();
							threadPoolC.execute(new TaskRunnable(submitter));
							break;

						default:
							break;
						}

					}
				} else
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

			}

		}
	}

	class TaskRunnable implements Runnable {

		Submitter submitter;

		public TaskRunnable() {
			// TODO Auto-generated constructor stub
		}

		TaskRunnable(Submitter submitter) {

			this.submitter = submitter;

		}

		@Override
		public void run() {

			loger.log(Level.INFO, String.format("%s \t Handler_%c, Task:%s\n", Thread.currentThread().getName(),
					submitter.getFlag(), submitter.getTask()));

		}

	}

	class Submit implements Runnable {

		Dispather dispather;
		Submitter submitter;

		public Submit(Dispather dispather, Submitter submitter) {
			this.dispather = dispather;
			this.submitter = submitter;
		}

		@Override
		public void run() {

			loger.log(Level.INFO, String.format("%s \t Submitt:%c\t Task:%s\n", Thread.currentThread().getName(),
					submitter.getFlag(), submitter.getTask()));

			dispather.put(submitter);

		}

	}

	class Submitter {

		Character flag;
		String task;

		public Submitter() {
		}

		public Submitter(Character flag, String task) {
			this.flag = flag;
			this.task = task;
		}

		public Character getFlag() {
			return flag;
		}

		public void setFlag(Character flag) {
			this.flag = flag;
		}

		public String getTask() {
			return task;
		}

		public void setTask(String task) {
			this.task = task;
		}

	}

}
