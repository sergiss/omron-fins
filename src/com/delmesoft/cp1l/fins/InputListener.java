package com.delmesoft.cp1l.fins;

/**
 * 2021 Sergio S. - sergi.ss4@gmail.com
 * @author Sergio
 *
 */
public interface InputListener {
	
	void onEvent(int ch, int pin, boolean state);
	
	void onError(Exception e);

}
