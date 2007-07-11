/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

/**
 * A class for iteration of ports, specified in special format, like:
 * 1,5-7,35-40
 *
 * @author anton
 */
public final class PortIterator implements Cloneable {
	
	private int[] portRangeStart;
	private int[] portRangeEnd;
	
	private int rangeCountMinus1;
	private int rangeIndex;
	private int currentPort;
	
	private boolean hasNext;
	
	/**
	 * Constructs the PortIterator instance
	 * @param portString the port string to parse
	 */
	public PortIterator(String portString) {

		if (portString != null && (portString = portString.trim()).length() > 0) {
			String[] portRanges = portString.split("[\\s\t\n\r,;]+");
			
			// initialize storage
			portRangeStart = new int[portRanges.length+1];	// +1 for optimiation of 'next' method, prevents ArrayIndexOutOfBoundsException
			portRangeEnd = new int[portRanges.length];
	
			// parse ints
			for (int i = 0; i < portRanges.length; i++) {
				String range = portRanges[i];
				int dashPos = range.indexOf('-') + 1;
				int endPort = Integer.parseInt(range.substring(dashPos));
				portRangeEnd[i] = endPort;
				portRangeStart[i] = dashPos == 0 ? endPort : Integer.parseInt(range.substring(0, dashPos-1));
				if (endPort <= 0 || endPort >= 65536) {
					throw new NumberFormatException(endPort + " port is out of range");
				}
			}
			
			currentPort = portRangeStart[0];
			rangeCountMinus1 = portRanges.length - 1;
			hasNext = rangeCountMinus1 >= 0;
		}
	}
	
	/**
	 * @return true if there are more ports left
	 */
	public boolean hasNext() {
		return hasNext;
	}
	
	/**
	 * @return next port number
	 */
	public int next() {
		int returnPort = currentPort++;
		
		if (currentPort > portRangeEnd[rangeIndex]) {
			hasNext = rangeIndex < rangeCountMinus1;
			rangeIndex++;
			currentPort = portRangeStart[rangeIndex];
		}
		
		return returnPort;
	}

	/**
	 * Clones the PortIterator instance.
	 * @return a shallow copy
	 */
	public PortIterator copy() {
		try {
			return (PortIterator) super.clone();
		}
		catch (CloneNotSupportedException e) {
			assert false : "this should never happen";
			return null;
		}
	}
	
}