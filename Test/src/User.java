
public class User implements Comparable<User> {

	private Integer priority;
	private String username;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int compareTo(User user) {
//        System.out.println("比较结果"+this.priority.compareTo(user.getPriority()));
		return this.priority.compareTo(user.getPriority());
	}
}
