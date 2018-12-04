
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorTest2 {

	private static class TestTask implements Runnable {
		private String TAG = "";
		private int index;


		public TestTask(int index,String tag) {
			TAG = tag;
			 this.index =index;

		}

		@Override
		public void run() {

			System.out.format("%d \t UUID:%s \n", index,TAG);
		}
	}

	public static void main(String[] args) {
		ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);

		int time = 3; // 延迟3秒执行

		// TestTask phil = new TestTask("phil");
		int index = 0;

		while ((index++) < 50) {

			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			long times = (long)(Math.random()*1000);
			System.out.format("%d \t %s\n",index,times);

			TestTask zhang = new TestTask(index,uuid);

			mScheduledThreadPoolExecutor.schedule(zhang, times, TimeUnit.MICROSECONDS);

		}

		// 再上一个任务的3秒后执行
		// mScheduledThreadPoolExecutor.schedule(phil, time * 2, TimeUnit.SECONDS);
	}
}
