package com.delmesoft.cp1l.fins;

/**
 * 2021 Sergio S. - sergi.ss4@gmail.com
 * @author Sergio S.
 *
 */
public class CIO { // Channel Input Output

	private int position;
	private int bit;
	
	transient boolean state;
	
	public CIO() {}

	public CIO(int position, int bit) {
		this.position = position;
		this.bit = bit;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getBit() {
		return bit;
	}

	public void setBit(int bit) {
		this.bit = bit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bit;
		result = prime * result + position;
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
		CIO other = (CIO) obj;
		if (bit != other.bit)
			return false;
		if (position != other.position)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CIO [position=");
		builder.append(position);
		builder.append(", bit=");
		builder.append(bit);
		builder.append("]");
		return builder.toString();
	}

}
