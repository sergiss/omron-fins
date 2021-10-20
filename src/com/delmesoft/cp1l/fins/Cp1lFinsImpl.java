package com.delmesoft.cp1l.fins;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 2021 Sergio S. - sergi.ss4@gmail.com
 * @author Sergio S.
 *
 */
public class Cp1lFinsImpl implements Cp1lFins {
	
	private static final int TIMEOUT = 10_000;
	
	public static final byte READ_ID  = 2;
	public static final byte WRITE_ID = 4;
	
	private byte[] tmp = new byte[1024];
	
	private String host;
	private int port;
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;

	private Thread thread;
	private InputListener listener;
	
	private Map<Integer, CIO> cioMap = new HashMap<>();
	
	private long refreshTime;
	
	public Cp1lFinsImpl() {
		this("169.254.0.1");
	}

	public Cp1lFinsImpl(String host) {
		this(host, 9600);
	}

	public Cp1lFinsImpl(String host, int port) {
		this.host = host;
		this.port = port;
		
		this.refreshTime = 25; // Default time
	}

	@Override
	public synchronized void connect() throws Exception {
		
		if(!isConnected()) {
			socket = new Socket(host, port);
			socket.setSoTimeout(TIMEOUT);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			
			byte[] message = { 
					0x46, 0x49, 0x4E, 0x53, // Header
					0x00, 0x00, 0x00, 0x0C, // CMD length
					0x00, 0x00, 0x00, 0x00, // Frame command
					0x00, 0x00, 0x00, 0x00, // Error
					0x00, 0x00, 0x00, 0x00
			};

			os.write(message, 0, 20);
			os.flush();
			
			int n = readResponse(tmp);
			if(n != 24 || tmp[23] != 1) { // check
				disconnect();
				throw new RuntimeException("connection message error");
			}
			thread = new Thread() {
				public void run() {
					
					try {
						
						boolean value;
						// Initial state
						synchronized (cioMap) {
							for (CIO cio : cioMap.values()) { // iterate
								value = read(cio.getPosition(), cio.getBit()); // read state
								cio.state = value; // update state
							}
						}	

						while (!isInterrupted()) {							
							Thread.sleep(refreshTime);
							// Update state
							synchronized (cioMap) {
								for (CIO cio : cioMap.values()) { // iterate
									value = read(cio.getPosition(), cio.getBit()); // read state
									if (value != cio.state) { // check
										cio.state = value; // update state
										if (listener != null) listener.onEvent(cio.getPosition(), cio.getBit(), value); // notify
									}
								}
							}
						}
						
					} catch (InterruptedException e) {
						// Ignore
					} catch (Exception e) {
						if(listener != null && isConnected()) {
							listener.onError(e);
						}
					} finally {
						disconnect();
					}
				}
			};
			thread.start();
		}
		
	}

	@Override
	public synchronized boolean isConnected() {
		return socket != null;
	}

	@Override
	public synchronized void disconnect() {
		if(isConnected()) {
			try {
				thread.interrupt();
			} catch (Exception e) {
			} finally {
				thread = null;
			}
			try {
				socket.close();
			} catch (Exception e) {
			} finally {
				socket = null;
				is = null;
				os = null;
			}
		}
	}
	
	@Override
	public synchronized ControllerData getControllerData() throws Exception {
		
		byte[] message = { 
				0x46, 0x49, 0x4E, 0x53, // Header
				0x00, 0x00, 0x00, 0x15, // CMD length
				0x00, 0x00, 0x00, 0x02, // Frame command
				0x00, 0x00, 0x00, 0x00, // Error
				/* 0 */	(byte) 0x80, // ICF	- Display frame information -
				/* 1 */	 0x00, // RSV - Reserved by system -
				/* 2 */	 0x02, // GCT - Permissible number of gateways -
				/* 3 */	 0x00, // DNA - Destination network address -
				/* 4 */	 0x01, // DA1 - Ethernet Unit FINS NODE NUMBER -
				/* 5 */	 0x00, // DA2 - Destination unit address -
				/* 6 */	 0x00, // SNA - Source network address -
				/* 7 */	 0x00, // SA1 - WS FINS NODE NUMBER -
				/* 8 */	 0x00, // SA2 - Source unit address -
				/* 9 */	 READ_ID, // SID - Service ID -
				/* ------- FINS COMMAND -------------- */
				/* 10 */ 0x05, // MRC - Main Request code -
				/* 11 */ 0x01, // SRC - Sub-request code -
				/* ------- SEND DATA ----------------- */
				/* 12 */ 0x00, // VARIABLE TYPE: DM
		};

		os.write(message, 0, 29);
		os.flush();

		readResponse(tmp);
		if(tmp[25] != READ_ID || tmp[23] != 1) { // check
			throw new RuntimeException("read message error");
		}

		String model = new String(tmp, 30, 20).trim();
		String version = new String(tmp, 50, 10).trim();
		// String v2 = new String(tmp, 60, 10).trim();
		return new ControllerData(model, version);
	}
	
	@Override
	public boolean read(CIO cio) throws Exception {
		return read(cio.getPosition(), cio.getBit());
	}

	@Override
	public synchronized boolean read(int pos, int bit) throws Exception {
		
		byte[] message = {
				0x46, 0x49, 0x4E, 0x53, // Header
				0x00, 0x00, 0x00, 0x1A, // CMD length
				0x00, 0x00, 0x00, 0x02, // Frame command
				0x00, 0x00, 0x00, 0x00, // Error
				/* 0 */	(byte) 0x80, // ICF	- Display frame information -
				/* 1 */	 0x00, // RSV - Reserved by system -
				/* 2 */	 0x02, // GCT - Permissible number of gateways -
				/* 3 */	 0x00, // DNA - Destination network address -
				/* 4 */	 0x01, // DA1 - Ethernet Unit FINS NODE NUMBER -
				/* 5 */	 0x00, // DA2 - Destination unit address -
				/* 6 */	 0x00, // SNA - Source network address -
				/* 7 */	 0x00, // SA1 - WS FINS NODE NUMBER -
				/* 8 */	 0x00, // SA2 - Source unit address -
				/* 9 */	 READ_ID, // SID - Service ID -
				/* ------- FINS COMMAND -------------- */
				/* 10 */ 0x01, // MRC - Main Request code -
				/* 11 */ 0x01, // SRC - Sub-request code - (READ)
				/* ------- SEND DATA ----------------- */
				/* 12 */ 0x30, // VARIABLE TYPE: DM
				/* 13 */ 0x00,
				/* 14 */ (byte) pos, // READ START ADDRESS
				/* 15 */ (byte) bit,
				/* 16 */ 0x00, // WORDS READ
				/* 17 */ 0x01
			};
				
		os.write(message, 0, 34);
		os.flush();

		int n = readResponse(tmp);
		if(n != 31 || tmp[25] != READ_ID || tmp[23] != 1) { // check
			throw new RuntimeException("read message error");
		}
		return tmp[30] == 1;
	}
	
	@Override
	public void write(CIO cio, boolean value) throws Exception {
		write(cio.getPosition(), cio.getBit(), value);
	}

	@Override
	public synchronized void write(int pos, int bit, boolean value) throws Exception {
		
		byte[] message = {
				0x46, 0x49, 0x4E, 0x53, // Header
				0x00, 0x00, 0x00, 0x1B, // CMD length
				0x00, 0x00, 0x00, 0x02, // Frame command
				0x00, 0x00, 0x00, 0x00, // Error
				/* 0 */	(byte) 0x80, // ICF	- Display frame information -
				/* 1 */	 0x00, // RSV - Reserved by system -
				/* 2 */	 0x02, // GCT - Permissible number of gateways -
				/* 3 */	 0x00, // DNA - Destination network address -
				/* 4 */	 0x01, // DA1 - Ethernet Unit FINS NODE NUMBER -
				/* 5 */	 0x00, // DA2 - Destination unit address -
				/* 6 */	 0x00, // SNA - Source network address -
				/* 7 */	 0x00, // SA1 - WS FINS NODE NUMBER -
				/* 8 */	 0x00, // SA2 - Source unit address -
				/* 9 */	 WRITE_ID, // SID - Service ID -
				/* ------- FINS COMMAND -------------- */
				/* 10 */ 0x01, // MRC - Main Request code -
				/* 11 */ 0x02, // SRC - Sub-request code - (WRITE)
				/* ------- SEND DATA ----------------- */
				/* 12 */ 0x30, // VARIABLE TYPE: DM
				/* 13 */ 0x00,
				/* 14 */ (byte) pos, // WRITE START ADDRESS
				/* 15 */ (byte) bit,
				/* 16 */ 0x00, // WORDS WRITE
				/* 17 */ 0x01,
				/* 18 */ (byte) (value ? 0x01 : 0x00) // WRITE VALUE
		};
		
		os.write(message, 0, 35);
		os.flush();

		int n = readResponse(tmp);
		if(n != 30 || tmp[25] != WRITE_ID || tmp[23] != 1) { // check
			throw new RuntimeException("write message error");
		}
		
	}
	
	@Override
	public void subscribe(CIO cio) {
		synchronized (cioMap) {
			if(isConnected()) {
				try {
					cio.state = read(cio); // initial state
				} catch (Exception e) {}
			}
			cioMap.put(cio.hashCode(), cio);
		}
	}
	
	@Override
	public boolean unsuscribe(CIO cio) {
		synchronized (cioMap) {
			return cioMap.remove(cio.hashCode()) != null;
		}
	}

	@Override
	public void subscribe(int position, int bit) {
		subscribe(new CIO(position, bit));
	}
	
	@Override
	public void unsuscribe(int position, int bit) {
		unsuscribe(new CIO(position, bit));
	}
	
	@Override
	public void unsuscribeAll() {
		synchronized (cioMap) {
			cioMap.clear();
		}	
	}
	
	@Override
	public void setInputListener(InputListener listener) {
		this.listener = listener;
	}
	
	@Override
	public InputListener getInputListener() {
		return listener;
	}
	
	private int readResponse(byte[] data) throws IOException {
		readFully(is, data, 0, 8);
		int n = (data[6] << 1) | (data[7] & 0xFF);
		readFully(is, data, 8, n);
		return n + 8;
	}
	
	private static void readFully(InputStream is, byte[] data, int off, int len) throws IOException {
		int r, n = 0;
		while(n < len) {
			r = is.read(data, off + n, len - n);
			if(r < 0) throw new EOFException();
			n += r;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = Math.max(1, refreshTime);
	}
	
}
