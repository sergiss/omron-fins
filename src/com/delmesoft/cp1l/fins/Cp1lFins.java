package com.delmesoft.cp1l.fins;

/**
 * 2021 Sergio S. - sergi.ss4@gmail.com
 * @author Sergio S.
 *
 */
public interface Cp1lFins {
	
	/**
	 * Establishes connection with the device
	 * @throws Exception Error opening connection
	 */
	void connect() throws Exception;
	
	/**
	 * Connection status
	 * @return true connected, false disconnected
	 */
	boolean isConnected();
	
	/**
	 * Close current connection
	 */
	void disconnect();

	/**
	 * Controller information data
	 * @return ControllerData
	 * @throws Exception Error getting ControllerData
	 */
	ControllerData getControllerData() throws Exception;
	
	/**
	 * Read the state of the indicated bit
	 * @param position Memory position
	 * @param bit Bit position
	 * @return bit status
	 * @throws Exception Error reading
	 */
	boolean read(int position, int bit) throws Exception;
	
	/**
	 * Read the state of the indicated bit
	 * @param cio Channel Input Output
	 * @return bit status
	 * @throws Exception Error reading
	 */
	boolean read(CIO cio) throws Exception;
	
	/**
	 * Write the status of the indicated input
	 * @param position Memory position
	 * @param bit Bit position
	 * @param value New state
	 * @throws Exception Error writing 
	 */
	void write(int position, int bit, boolean value) throws Exception;
	
	/**
	 * Write the status of the indicated input
	 * @param cio Channel Input Output
	 * @param value
	 * @throws Exception Exception Error writing 
	 */
	void write(CIO cio, boolean value) throws Exception;
	
	/**
	 * 
	 * @param listener
	 */
	void setInputListener(InputListener listener);

	/**
	 * 
	 * @return
	 */
	InputListener getInputListener();

	/**
	 * Adds a subscription to the state change events of the indicated bit
	 * @param cio Channel Input Output
	 */
	void subscribe(CIO cio);

	/**
	 * Remove bit subscription
	 * @param cio Channel Input Output
	 * @return true if there was a subscription  
	 */
	boolean unsuscribe(CIO cio);

	/**
	 * Adds a subscription to the state change events of the indicated bit
	 * @param position
	 * @param bit
	 */
	void subscribe(int position, int bit);

	/**
	 * Remove bit subscription
	 * @param position
	 * @param bit
	 */
	void unsuscribe(int position, int bit);

	/**
	 * Remove all bit subscriptions
	 */
	void unsuscribeAll();

}
