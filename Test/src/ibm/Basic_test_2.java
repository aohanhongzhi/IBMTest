package ibm;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Basic_test_2 {
	static Logger loger;
	static int index = 0;

	public static void main(String[] args) {

		loger = Logger.getLogger("This is log for developer!");
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
		loger.log(Level.INFO, "Main Thread has started!so the process has work!");

		// First:dispatcher Service

		Dispather dispather = new Basic_test_2().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product
		// boolean flag= true;

		// while (flag) {
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

			new Thread(new Basic_test_2().new Submitter(dispather, c)).start();
			index++;

		}

	}

	class Dispather implements Runnable {

		private BlockingQueue<Character> bq = new LinkedBlockingDeque<Character>();

		public void put(Character c) {
			try {
				bq.put(c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			ThreadPoolA threadPoolA = ThreadPoolA.geThreadPoolA();
			ThreadPoolB threadPoolB = ThreadPoolB.geThreadPoolB();
			ThreadPoolC threadPoolC = ThreadPoolC.geThreadPoolB();

			while (true) {

				Character character = null;
				if (bq != null) {

					while (!bq.isEmpty()) {

						try {
							character = bq.take();
						} catch (InterruptedException e) {

							e.printStackTrace();
						}

						switch (character) {
						case 'A':
							// new Thread(new Basic_test_2().new
							// HandlerA('A')).start();
							threadPoolA.execute(new Runnable() {

								@Override
								public void run() {

									loger.log(Level.INFO, String.format("Hander_A\n"));
								}
							});
							break;
						case 'B':
							// new Thread(new Basic_test_2().new
							// HandlerB('B')).start();
							threadPoolB.execute(new Runnable() {

								@Override
								public void run() {

									loger.log(Level.INFO, String.format("Hander_B\n"));
								}
							});
							break;
						case 'C':
							// new Thread(new Basic_test_2().new
							// HandlerC('C')).start();
							threadPoolC.execute(new Runnable() {

								@Override
								public void run() {

									loger.log(Level.INFO, String.format("Hander_C\n"));
								}
							});
							break;

						default:
							break;
						}

					}
				} else
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

			}

		}
	}

	class Submitter implements Runnable {

		Dispather dispather;
		char c;

		public Submitter(Dispather dispather, char c) {

			this.dispather = dispather;
			this.c = c;

		}

		@Override
		public void run() {

			dispather.put(c);
			// System.out.format("Submitter%c\n", c);
			loger.log(Level.INFO, String.format("Submitter%c\n", c));

		}

	}

	class HandlerA implements Runnable {
		char c;

		public HandlerA(char c) {

			this.c = c;
		}

		@Override
		public void run() {

			// System.out.format("Hander%c\n", c);

			loger.log(Level.INFO, String.format("Hander%c\n", c));

		}

	}

	class HandlerB implements Runnable {
		char c;

		public HandlerB(char c) {

			this.c = c;
		}

		@Override
		public void run() {

			// System.out.format("Hander%c\n", c);

			loger.log(Level.INFO, String.format("Hander%c\n", c));
		}

	}

	class HandlerC implements Runnable {
		char c;

		public HandlerC(char c) {

			this.c = c;
		}

		@Override
		public void run() {

			// System.out.format("Hander%c\n", c);
			loger.log(Level.INFO, String.format("Hander%c\n", c));

		}

	}

}

