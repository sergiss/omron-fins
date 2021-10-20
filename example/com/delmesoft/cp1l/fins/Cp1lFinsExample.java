package com.delmesoft.cp1l.fins;

public class Cp1lFinsExample {

	public static void main(String[] args) throws Exception {

		String host = "169.254.0.10";				
		int port = 9600;
		
		Cp1lFins cp1lFins = new Cp1lFinsImpl(host, port);
		cp1lFins.setInputListener(new InputListener() {
			@Override
			public void onEvent(int ch, int pin, boolean state) {
				System.out.printf("onEvent: ch=%d, pin=%d, state=%b\n", ch, pin, state);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
		
		// Subscribe to input
		cp1lFins.subscribe(new CIO(0, 0)); // channel 0 pin 0 (0.00)
		cp1lFins.subscribe(new CIO(1, 0)); // channel 1 pin 0 (1.00)
		
		cp1lFins.subscribe(new CIO(102, 02)); // Custom

		cp1lFins.connect();
		System.out.printf("Connected (host: '%s', port: %d)\n", host, port);
		System.out.println(cp1lFins.getControllerData());
		
		// Write Output
		cp1lFins.write(101, 00, true); // channel 100 pin 0
		
		boolean state = false;
		for(int i = 0; i < 10; ++i) { // blink
			Thread.sleep(500);
			state = !state;
			cp1lFins.write(100, 01, state); // channel 100 pin 1
		}		

	}

}
