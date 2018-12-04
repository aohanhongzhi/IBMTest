import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockQueueDemo {

	public static void main(String args[]) {

		PriorityBlockingQueue<User> queue = new PriorityBlockingQueue<User>();

		for (int i = 0; i < 12; i++) {

			User user = new User();
			int max = 10;
			int min = 1;
			Random random = new Random();
			int n = random.nextInt(max) % (max - min + 1) + min;
			user.setPriority(n);
			user.setUsername("我的第".concat(String.valueOf(i)).concat("天"));
			queue.add(user);

		}

		for (int i = 0; i < 12; i++) {
			User u = queue.poll();
			System.out.format("优先级是：%d \t %s \n", u.getPriority(), u.getUsername());

		}

	}

}