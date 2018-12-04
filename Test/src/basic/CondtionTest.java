package basic;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jintx on 2017/9/29.
 */
public class CondtionTest {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition conditionA = lock.newCondition();
    public static Condition conditionB = lock.newCondition();
    public static Condition conditionC = lock.newCondition();
    public static int index = 0;
    public static void main(String[] args){
        ThreadA threadA = new ThreadA();
        ThreadB threadB = new ThreadB();
        ThreadC threadC = new ThreadC();

        threadA.start();
        threadB.start();
        threadC.start();
    }

    public static class ThreadA extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                for(int i = 1 ; i <= 5; i++) {
                    for (int j = 1; j <= 5; j++) {
                    /*不需要担心++index同步的问题，因为我们已经保证了每次只能有一个线程在++index*/
                        System.out.println("A进程输出" + " : " + ++index);
                    }
                    conditionB.signal();//第一次调用该方法没什么意义，因为线程B还没等待队列中
                    conditionA.await();//本质是释放了对lock的同步控制
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    public static class ThreadB extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                for(int i = 1 ; i <= 5; i++) {
                    for (int j = 1; j <= 5; j++) {
                        System.out.println("B进程输出" + " : " + ++index);
                    }
                    conditionC.signal();
                    conditionB.await();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    public static class ThreadC extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                for(int i = 1 ; i <= 5; i++) {
                    for (int j = 1; j <= 5; j++) {
                        System.out.println("C进程输出" + " : " + ++index);
                    }
                    conditionA.signal();
                    conditionC.await();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

}
