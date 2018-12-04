package ibm;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;//双向队列
import java.util.concurrent.LinkedBlockingQueue;//单向队列
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



/**
 * 正常,没有问题,"高并发"多线程
 * 加入enum
 * 
 * 发现一个问题:就是线程执行完之后,还是线程数字还是会从消失的开始!
 * 每个线程的名字要用一个unix时间戳作为名字(没有用UUID)
 * 
 * @author deepin
 *
 */


/*
enum Dispathers {
    A,B,C
}
*/
public class Basic_test_6 {
	static Logger loger;
	//static volatile int index = 0;

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

		Dispather dispather = new Basic_test_6().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product

		// 提交者这里是多线程随机提交三种不同的任务（任务带有标记）
		
		
		//这里就一个线程
		new Basic_test_6().new SumbmitFactory(dispather).start();


	}
	
	class SumbmitFactory extends Thread{
		Dispather dispather;
		
		public SumbmitFactory() {
			
		}
		public SumbmitFactory(Dispather dispather) {
			
			this.dispather =dispather;
			
		}
		
		public void run() {
			
			//这里启动10个线程,每个线程里面再提交10个任务
			for(int i =0 ; i <10;++i) {
				
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						int index=0;
						while (index < 20) {

							try {
								Thread.sleep((long) (Math.random() * 0));
							} catch (InterruptedException e) {

								e.printStackTrace();
							}

							//char c = ' ';
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
							Submitter submitter = new Basic_test_6().new Submitter(c, String.valueOf(index));

							new Thread(new Basic_test_6().new Submit(dispather, submitter), "提交线程：".concat(Thread.currentThread().getName()))
									.start();
							index++;

						}
						
						
					}
				}).start();
				
				
				//++index;
				
			}
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
						case A:
							// new Thread(new Basic_test_2().new
							// HandlerA('A')).start();
							threadPoolA.execute(new TaskRunnable(submitter));
							break;
						case B:
							// new Thread(new Basic_test_2().new
							// HandlerB('B')).start();
							threadPoolB.execute(new TaskRunnable(submitter));
							break;
						case C:
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
			
		}

		TaskRunnable(Submitter submitter) {

			this.submitter = submitter;

		}

		@Override
		public void run() {

			loger.log(Level.INFO, String.format("%s \t Handler_%s, Task:%s\n", Thread.currentThread().getName(),
					String.valueOf(submitter.getFlag()), submitter.getTask()));

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

			loger.log(Level.INFO, String.format("%s \t Submitt:%s\t Task:%s\n", Thread.currentThread().getName(),
					String.valueOf(submitter.getFlag()), submitter.getTask()));

			dispather.put(submitter);

		}

	}

	class Submitter {

		Dispathers flag;
		String task;

		public Submitter() {
		}

		public Submitter(Dispathers flag, String task) {
			this.flag = flag;
			this.task = task;
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
