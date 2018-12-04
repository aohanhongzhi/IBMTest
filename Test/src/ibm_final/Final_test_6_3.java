package ibm_final;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;//双向队列
import java.util.concurrent.LinkedBlockingQueue;//单向队列
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 正常,没有问题,"高并发"多线程 加入enum
 * 
 * 发现一个问题:就是线程执行完之后,还是线程数字还是会从消失的开始! 每个线程的名字要用一个unix时间戳作为名字(然而事实上还是有很多重复的)
 * 解决办法:使用UUID保证线程是唯一的名字!能保证是唯一的!
 * 
 * 6_2 改为线程池，应当充分的利用Java自带的数据结构与内置API
 * 
 * 
 * Final_test_6_3 : 先考虑时间，还是先考虑优先级？如果放大时间，那应该是先按时间处理，再处理优先级，如果是从优先级角度思考，同一优先级当然是按时间来分配。
 * 最后设计，先区分优先级，再区分指定时间。
 * 
 * @author deepin
 *
 */
/*
enum Dispathers {
	A, B, C
}
*/
public class Final_test_6_3 {
	static Logger loger;

	public static void main(String[] args) {

		// 打印出线程名

		loger = Logger.getLogger("This is a log for developer!");
		FileHandler fh = null;
		try {

			fh = new FileHandler(Thread.currentThread().getStackTrace()[1].getClassName().concat(".log"), true);
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

		Dispather dispather = new Final_test_6_3().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product

		// 提交者这里是多线程随机提交三种不同的任务（任务带有标记）

		// 这里就一个线程
		new Final_test_6_3().new SumbmitFactory(dispather).start();

	}

	class SumbmitFactory extends Thread {
		Dispather dispather;

		public SumbmitFactory() {

		}

		public SumbmitFactory(Dispather dispather) {

			this.dispather = dispather;

		}

		public void run() {

			// 这里启动10个线程,每个线程里面再提交10个任务
			for (int i = 0; i < 10; ++i) {

				new Thread(new Runnable() {

					@Override
					public void run() {

						int index = 0;
						while (index < 20) {


							Dispathers c = null;
							int x = (int) (1 + Math.random() * 3);

							switch (x) {
							case 1:
								c = Dispathers.A;
								break;
							case 2:
								c = Dispathers.B;
								break;
							case 3:
								c = Dispathers.C;
								break;
							default:
								loger.log(Level.WARNING, "c == null !");
								break;
							}
							
							//time 
							long time = (long) (Math.random() * 1000000);

							// priority
							int max = 10;
							int min = 1;
							Random random = new Random();
							int priority = random.nextInt(max) % (max - min + 1) + min;

							String uuid = UUID.randomUUID().toString().replaceAll("-", "");
							Submitter submitter = new Final_test_6_3().new Submitter(c, uuid,time, priority);


							new Thread(new Final_test_6_3().new Submit(dispather, submitter), "提交线程：".concat(uuid))
									.start();
							index++;

						}

					}
				}).start();


			}
		}
	}

	class Dispather implements Runnable {

		// 无界队列的实现，由于队列逻辑上为无穷大（其实物理上受限于内存等实际大小）， 其实查看了源代码之后发现还是存在一个capacity的，只是当capacity
		// full 会进入等待然后再处理。
		// 从某这角度来说，LinkedBlockingDeque是一个双向队列，所以在此处就不是太重要
		// private BlockingQueue<Submitter> bq = new LinkedBlockingDeque<Submitter>();

		// 更改成有固定大小的，对于超出大小的任务就需要等待，然后再加入任务中。LinkedBlockingQueue是单向队列。
		// 无界队列
		// private BlockingQueue<Submitter> bq = new LinkedBlockingQueue<>();

		private PriorityBlockingQueue<Submitter> pq = new PriorityBlockingQueue<>();

		public void put(Submitter c) {
			// bq.put(c);
			pq.add(c);
		}

		@Override
		public void run() {

			// 分别获得三种上线的滴滴司机，对应设计了三个线程池，每个线程池有不同固定数量的线程在等待任务。
			// 因为这个是单例模式，所以不好合并成一个线程池。

		

			// LinkedBlockingQueue 的大小需要合适设置，如果大小不合适，会导致任务自动抛弃
//			LinkedBlockingQueue<Runnable> AQueue = new LinkedBlockingQueue<Runnable>();
//			LinkedBlockingQueue<Runnable> BQueue = new LinkedBlockingQueue<Runnable>();
			LinkedBlockingQueue<Runnable> CQueue = new LinkedBlockingQueue<Runnable>();
			RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

//			ThreadPoolExecutor AthreadPool = new ThreadPoolExecutor(2, 5, 60, TimeUnit.NANOSECONDS, AQueue, handler);
//
//			ThreadPoolExecutor BthreadPool = new ThreadPoolExecutor(3, 6, 60, TimeUnit.NANOSECONDS, BQueue, handler);

			ThreadPoolExecutor CthreadPool = new ThreadPoolExecutor(4, 7, 60, TimeUnit.NANOSECONDS, CQueue, handler);

			// dispatcher 一直分析队列里有没有数据，如果有立刻进行dispatch
			while (true) {
				Submitter submitter = null;
				if (pq != null && !pq.isEmpty()) {
					while (!pq.isEmpty()) {
						
						submitter = pq.poll();
						CthreadPool.execute(new TaskRunnable(submitter));
		
					}
				} else;

			}

		}
	}

	class TaskRunnable implements Runnable {

		Submitter submitter;

		public TaskRunnable() {

		}

		TaskRunnable(Submitter submitter) {

			this.submitter = submitter;

		}

		@Override
		public void run() {

			loger.log(Level.INFO,
					String.format("%s \t Handler_%s, Task:%s \tTime:%s \t Priority:%d\n", Thread.currentThread().getName(),
							String.valueOf(submitter.getFlag()), submitter.getTask(),submitter.getTime(), submitter.getPriority()));

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
			synchronized (this) {
				loger.log(Level.INFO,
						String.format("%s \t Submitt:%s\t Task:%s \t Time:%s \tPriority：%d\n", Thread.currentThread().getName(),
								String.valueOf(submitter.getFlag()), submitter.getTask(),submitter.getTime(), submitter.getPriority()));
				dispather.put(submitter);
				
			}
		}

	}

	class Submitter implements Comparable<Submitter> {

		// 安全性设计

		private Dispathers flag;
		private String task;
		private Integer priority;
		private long time;

		// 加入优先级

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public Submitter() {
		}

		public Submitter(Dispathers flag, String task) {
			this.flag = flag;
			this.task = task;
		}

		public Submitter(Dispathers flag, String task, Integer priority) {
			this.flag = flag;
			this.task = task;
			this.priority = priority;
		}
		

		public Submitter(Dispathers flag, String task,long time, Integer priority) {
			this.flag = flag;
			this.task = task;
			this.time = time;
			this.priority = priority;
		}

		public Integer getPriority() {
			return priority;
		}

		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		public Dispathers getFlag() {
			return flag;
		}

		public void setFlag(Dispathers flag) {
			this.flag = flag;
		}

		public String getTask() {
			return task;
		}

		public void setTask(String task) {
			this.task = task;
		}

		@Override
		public int compareTo(Submitter o) {
			// TODO Auto-generated method stub
			return this.priority.compareTo(o.getPriority());
		}

	}

}
