package basic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Basic {

	public static void main(String[] args) {

		// First:dispatcher Service

		Dispather dispather = new Basic().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product

		while (true) {
			
			try {
				Thread.sleep((long) (Math.random()*2000));
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

			new Thread(new Basic().new Submitter(dispather, c)).start();

		}

	}

	class Dispather implements Runnable {
	 volatile Vector<Character> vector = new Vector<Character>();
	 //SynchronousQueue
	// private BlockingQueue<Character> bq = new SynchronousQueue<Character>();
	 
	 private BlockingQueue<Character> bq = new LinkedBlockingQueue<Character>();
	 
	 
	 Queue<Character> queue = new Queue<Character>() {

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<Character> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends Character> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean add(Character e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean offer(Character e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Character remove() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Character poll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Character element() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Character peek() {
			// TODO Auto-generated method stub
			return null;
		}
	};

		public Dispather() {

		}

		public void put(Character c) {
		//	vector.addElement(c);
			try {
				bq.put(c);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public Character get(Character c) {
			return vector.firstElement();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (true) {
				
			

					Character character=null;
					if(bq!=null) {
						
					
					while(!bq.isEmpty()) {
						
					
					try {
						character = bq.take();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					switch (character) {
					case 'A':
						new Thread(new Basic().new HandlerA( 'A')).start();
						break;
					case 'B':
						new Thread(new Basic().new HandlerB( 'B')).start();
						break;
					case 'C':
						new Thread(new Basic().new HandlerC( 'C')).start();
						break;

					default:
						break;
					}

					}
					} else
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
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
			System.out.format("Submitter%c\n",c);

		}

	}

	class HandlerA implements Runnable {
		char c;
		public HandlerA( char c) {
			// TODO Auto-generated constructor stub
			this.c = c;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.format("Hander%c\n", c);
		}

	}

	class HandlerB implements Runnable {
		char c;
		public HandlerB( char c) {
			// TODO Auto-generated constructor stub
			this.c =c;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.format("Hander%c\n", c);
		}

	}

	class HandlerC implements Runnable {
		char c;
		public HandlerC(char c) {
			// TODO Auto-generated constructor stub
			this.c =c;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.format("Hander%c\n", c);

		}

	}

}
