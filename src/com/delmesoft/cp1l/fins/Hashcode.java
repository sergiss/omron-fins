package com.delmesoft.cp1l.fins;

public class Hashcode {
	
	private int ch, pin;

	public int getCh() {
		return ch;
	}

	public void setCh(int ch) {
		this.ch = ch;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ch;
		result = prime * result + pin;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hashcode other = (Hashcode) obj;
		if (ch != other.ch)
			return false;
		if (pin != other.pin)
			return false;
		return true;
	}

}
