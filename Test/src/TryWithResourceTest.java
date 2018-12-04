import java.io.*;

public class TryWithResourceTest {
	
	
	public static void main(String[] args) {
		
		
		//Jdk7以上的方法，这种方式不需要在finally关闭了。
		
	    try (FileInputStream inputStream = new FileInputStream(new File("test"))) {
	        System.out.println(inputStream.read());
	    } catch (IOException e) {
	    	
	    	//抓到的异常如果你不处理，一定要再次抛出给后来者处理成用户有用的信息。
	    	
	        throw new RuntimeException(e.getMessage(), e);
	    }
	}
	

}
