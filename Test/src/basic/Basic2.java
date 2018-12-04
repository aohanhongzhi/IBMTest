package basic;

import java.util.Iterator;
import java.util.Vector;

public class Basic2 {

	public static void main(String[] args) {

		// First:dispatcher Service

		Dispather dispather = new Basic2().new Dispather();

		new Thread(dispather).start();

		// Then : consumption

		// Multi-thread
		// 1.Thread pool
		// 2. simple multi-thread

		// Final : product

		while (true) {
			
			try {
				Thread.sleep((long) (Math.random()*1500));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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

			new Thread(new Basic2().new Submitter(dispather, c)).start();

		}

	}

	class Dispather implements Runnable {
	 volatile Vector<Character> vector = new Vector<Character>();

		public Dispather() {

		}

		public void put(Character c) {
			vector.addElement(c);
		}

		public Character get(Character c) {
			return vector.firstElement();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (true) {
				
				synchronized (vector) {
					
				while (!vector.isEmpty()) {

					Character character = vector.firstElement();
				
					switch (character) {
					case 'A':
						new Thread(new Basic2().new HandlerA( 'A')).start();
						break;
					case 'B':
						new Thread(new Basic2().new HandlerB( 'B')).start();
						break;
					case 'C':
						new Thread(new Basic2().new HandlerC( 'C')).start();
						break;

					default:
						break;
					}

				}
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
