package ibm_advanced;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;//双向队列
import java.util.concurrent.LinkedBlockingQueue;//单向队列
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * 正常,没有问题,"高并发"多线程 加入enum
 * 
 * 发现一个问题:就是线程执行完之后,还是线程数字还是会从消失的开始! 每个线程的名字要用一个unix时间戳作为名字(然而事实上还是有很多重复的)
 * 解决办法:使用UUID保证线程是唯一的名字!能保证是唯一的!
 * 
 * Basic_test_6_2 改为线程池，应当充分的利用Java自带的数据结构与内置API
 * 
 * Advanced_test_6_2 加入执行时间
 * 
 * 
 * 发现一个问题：多线程运行打印的东西，与理论执行的可能不一样。解释就是虽然是在一个代码块里面执行，但是这个不是原子操作，也就是说如果加入与打印两个操作执行不是紧接着的。有可能打印出来了，但是CPU时间片已经切走了，被别的线程抢了。
 * 解决办法：加锁！但是结果与想象的不一样啊！可能是线程执行的时候那个纳秒级导致的。所以应该加大时间加以区分
 *
 */

enum Dispathers {
	A, B, C
}

public class Advanced_test_6_2 {
	private static Logger loger;

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

		Dispather dispather = new Advanced_test_6_2().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product

		// 提交者这里是多线程随机提交三种不同的任务（任务带有标记）

		// 这里就一个线程
		new Advanced_test_6_2().new SumbmitFactory(dispather).start();

	}

	class SumbmitFactory extends Thread {
		private Dispather dispather;
		int ThreadNumber=0;
		int TaskNumer=1;
		double Time=0;

		public SumbmitFactory() {

		}

		public SumbmitFactory(Dispather dispather) {

			this.dispather = dispather;

		}

		public void run() {
		
			// 读取xml文件
			try {
				File f = new File("config.xml");
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(f);
				
				 ThreadNumber =Integer.parseInt( doc.getElementsByTagName("NUMBER").item(0).getFirstChild().getNodeValue());
				 TaskNumer = Integer.parseInt( doc.getElementsByTagName("NUMBER").item(1).getFirstChild().getNodeValue());
				 Time =Double.parseDouble( doc.getElementsByTagName("NUMBER").item(2).getFirstChild().getNodeValue());
				

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 这里启动10个线程,每个线程里面再提交10个任务
			for (int i = 0; i < ThreadNumber; ++i) {

				new Thread(new Runnable() {

					@Override
					public void run() {

						int index = 0;
						while (index < TaskNumer) {

							// char c = ' ';
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
								loger.log(Level.WARNING, "c = null !");
								break;
							}

							// execute time shi
							// 这个执行的时间越大，越好，太小的很混乱，这个涉及到CPU时间片的切换
							long time = (long) (Math.random() * Time);

							String uuid = UUID.randomUUID().toString().replaceAll("-", "");
							Submitter submitter = new Advanced_test_6_2().new Submitter(c, uuid, time);

							new Thread(new Advanced_test_6_2().new Submit(dispather, submitter),
									"SubmitThread：".concat(uuid)).start();
							index++;

						}

					}
				}).start();

				// ++index;

			}
		}
	}

	class Dispather implements Runnable {

		// 无界队列的实现，由于队列逻辑上为无穷大（其实物理上受限于内存等实际大小）， 其实查看了源代码之后发现还是存在一个capacity的，只是当capacity
		// full 会进入等待然后再处理。
		// 从某这角度来说，LinkedBlockingDeque是一个双向队列，所以在此处就不是太重要
		// private BlockingQueue<Submitter> bq = new
		// LinkedBlockingDeque<Submitter>();

		// 更改成有固定大小的，对于超出大小的任务就需要等待，然后再加入任务中。LinkedBlockingQueue是单向队列。
		// 无界队列
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

			// 该线程池

			// LinkedBlockingQueue 的大小需要合适设置，如果大小不合适，会导致任务自动抛弃
			// LinkedBlockingQueue<Runnable> AQueue = new
			// LinkedBlockingQueue<Runnable>();
			// LinkedBlockingQueue<Runnable> BQueue = new
			// LinkedBlockingQueue<Runnable>();
			// LinkedBlockingQueue<Runnable> CQueue = new
			// LinkedBlockingQueue<Runnable>();
			// RejectedExecutionHandler handler = new
			// ThreadPoolExecutor.DiscardPolicy();

			// ThreadPoolExecutor AthreadPool = new ThreadPoolExecutor(2, 5, 60,
			// TimeUnit.SECONDS, AQueue, handler);

			// ThreadPoolExecutor BthreadPool = new ThreadPoolExecutor(3, 6, 60,
			// TimeUnit.SECONDS, BQueue, handler);

			// ThreadPoolExecutor CthreadPool = new ThreadPoolExecutor(4, 7, 60,
			// TimeUnit.SECONDS, CQueue, handler);

			// 指定时间的线程

			ScheduledThreadPoolExecutor AScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
			ScheduledThreadPoolExecutor BScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
			ScheduledThreadPoolExecutor CScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4);

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
						case A:
							// new Thread(new Basic_test_2().new
							// HandlerA('A')).start();
							// threadPoolA.excecute(new
							// TaskRunnable(submitter));
							// AthreadPool.execute(new TaskRunnable(submitter));
							AScheduledThreadPoolExecutor.schedule(new TaskRunnable(submitter), submitter.getTime(),
									TimeUnit.MICROSECONDS);
							break;
						case B:
							// new Thread(new Basic_test_2().new
							// HandlerB('B')).start();
							// BthreadPool.execute(new TaskRunnable(submitter));
							BScheduledThreadPoolExecutor.schedule(new TaskRunnable(submitter), submitter.getTime(),
									TimeUnit.MICROSECONDS);
							break;
						case C:
							// new Thread(new Basic_test_2().new
							// HandlerC('C')).start();
							// CthreadPool.execute(new TaskRunnable(submitter));
							CScheduledThreadPoolExecutor.schedule(new TaskRunnable(submitter), submitter.getTime(),
									TimeUnit.MICROSECONDS);
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

		private Submitter submitter;

		public TaskRunnable() {

		}

		TaskRunnable(Submitter submitter) {

			this.submitter = submitter;

		}

		@Override
		public void run() {

			loger.log(Level.INFO,
					String.format("%s \t Handler_%s, Task:%s executeTime:%s \n", Thread.currentThread().getName(),
							String.valueOf(submitter.getFlag()), submitter.getTask(), submitter.getTime()));

		}

	}

	class Submit implements Runnable {

		private Dispather dispather;
		private Submitter submitter;

		public Submit(Dispather dispather, Submitter submitter) {
			this.dispather = dispather;
			this.submitter = submitter;
		}

		@Override
		public void run() {
			synchronized (this) {
				loger.log(Level.INFO,
						String.format("%s \t Submitt:%s\t Task:%s \t executeTime:%s\n",
								Thread.currentThread().getName(), String.valueOf(submitter.getFlag()),
								submitter.getTask(), submitter.getTime()));

				dispather.put(submitter);
			}
		}

	}

	class Submitter {

		private Dispathers flag;
		private String task;
		private long time;

		public Submitter() {
		}

		public Submitter(Dispathers flag, String task, long time) {
			this.flag = flag;
			this.task = task;
			this.time = time;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
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

	}

}
