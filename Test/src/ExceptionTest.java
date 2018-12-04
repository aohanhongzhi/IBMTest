
public class ExceptionTest {

	public static void main(String[] args) {
		
		//System.out.println(12/0);
		ExceptionTest et = new ExceptionTest();
		try {
			et.divide(12, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//方法主动抛出异常需要申明，这样才能告诉调用者，这个方法是有异常需要处理的。
	void divide(int a,int b) throws Exception {
		
		if(b==0) {
			
				throw new ArithmeticException("除数不可以为0");
			
			
		}else {
			System.out.format("相除的结果为：%d", a/b);
		}
		
	}

}
