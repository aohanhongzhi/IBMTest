import java.io.*;

public class LegacyResourceCloseTest {
	
	
	public static void main(String[] args) {
	    FileInputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(new File("test"));
	        System.out.println(inputStream.read());
	    } catch (IOException e) {
	        throw new RuntimeException(e.getMessage(), e);
	    } finally {
	    	
	    	//虽然下面已经有了异常的抛出处理，但是还是得先预检查是否为空。
	    	//因为if的效率要比异常处理高得多！
	    	
	        if (inputStream != null) {
	            try {
	                inputStream.close();
	            } catch (IOException e) {
	                throw new RuntimeException(e.getMessage(), e);
	            }
	        }
	    }
	}
	

}
