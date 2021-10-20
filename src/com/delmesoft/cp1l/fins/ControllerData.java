package com.delmesoft.cp1l.fins;

/**
 * 2021 Sergio S. - sergi.ss4@gmail.com
 * @author Sergio
 *
 */
public class ControllerData {
	
	private String model; 
	
	private String version;
	
	public ControllerData() {}
	
	public ControllerData(String model, String version) {
		this.model = model;
		this.version = version;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ControllerData [model=");
		builder.append(model);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
	
}
