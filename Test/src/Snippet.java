import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Snippet {
	
	
	
	//ScheduledThreadPoolExecuter是一个任务执行器，它可以指定在未来某一个时间点执行一次任务，
		//也可以间隔指定时间反复执行
		public static void scheduledThreadPoolExecuter() throws InterruptedException{
			ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
			
			//只会被执行一次的任务
			Runnable onceTask = new Runnable() {
				public void run() {
					System.out.println("onceTask任务得到执行");
				}
			};
			
			//会被反复执行的任务
			Runnable FixedTask = new Runnable() {
				public void run() {
					System.out.println("FixedTask任务得到执行");
				}
			};
			//3秒后任务得到一次执行
			executor.schedule(onceTask, 3000, TimeUnit.MILLISECONDS);
			//3秒后任务得到一次执行,后面每隔2秒执行一次
		//	executor.scheduleWithFixedDelay(FixedTask, 3000, 2000, TimeUnit.MILLISECONDS);
			
			executor.scheduleAtFixedRate(FixedTask, 2000, 2000, TimeUnit.MILLISECONDS);
			
			//10秒后停止所有线程
		//	TimeUnit.MILLISECONDS.sleep(10000);
		//	executor.shutdown();
		}
		
		
		public static void main(String args[]) {
			
			try {
				Snippet.scheduledThreadPoolExecuter();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
}