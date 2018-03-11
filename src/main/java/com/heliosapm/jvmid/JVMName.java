/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.jvmid;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import javax.management.MBeanServer;

import sun.management.counter.Counter;
import sun.management.counter.perf.PerfByteArrayCounter;
import sun.management.counter.perf.PerfStringCounter;

/**
 * <p>Title: JVMName</p>
 * <p>Description: Utility to change the JVM display name</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jvmid.JVMName</code></p>
 * The display byte limit typically reports as 244.
 */

public class JVMName {

	public static final Charset CHARSET = Charset.defaultCharset();

	public static final String RT_COUNTER_NAME = "sun.rt.javaCommand";

	public static final byte NULL_BYTE = '\0';

	private static final MBeanServer MBEANSERVER = ManagementFactory.getPlatformMBeanServer();

	private static final Field bufferField;
	
	private static final PerfStringCounter rtCounter;
	
	static {
		try {
			bufferField = PerfByteArrayCounter.class.getDeclaredField("bb");
			bufferField.setAccessible(true);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get name ByteBuffer field. Stack trace follows", ex);
		}
		rtCounter = getDisplayCounter();
	}
	
	/**
	 * Sets the JVM's display name
	 * @param display The display name to set to
	 * @return true if set, false otherwise
	 */
	public static boolean setDisplay(String display) {
		if(display==null || display.trim().isEmpty()) {
			throw new IllegalArgumentException("The passed display was null or empty");
		}
		ByteBuffer nameBuffer;
		try {
			nameBuffer = (ByteBuffer)bufferField.get(rtCounter);
			byte[] nameBytes = display.getBytes(CHARSET);
			if(nameBytes.length > nameBuffer.limit()) {
				throw new IllegalArgumentException("The passed display was longer than the max of " + nameBuffer.limit());
			}
			// Null out the name buffer
			for(int i = 0; i < nameBuffer.limit(); i++) {
				nameBuffer.put(i, NULL_BYTE);
			}
			nameBuffer.position(0);
			nameBuffer.put(nameBytes);
			nameBuffer.flip();
			return display.equals(rtCounter.getValue());
		} catch (Exception ex) {
			if(ex instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)ex;
			}
			ex.printStackTrace(System.err);
			return false;
		}
	}
	
	public static int displayByteLimit() {
		try {
		return ((ByteBuffer)bufferField.get(rtCounter)).limit();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get display byte limit", ex);
		}
	}

	/**
	 * Retrieves the performance string counter named <code>InternalRuntimeCounters</code>.
	 * @return a performance string counter
	 */
	public static PerfStringCounter getDisplayCounter() {
		JMXHelper.installHotspot(MBEANSERVER);
		final List<Counter> counters = JMXHelper.getAttribute(MBEANSERVER, JMXHelper.HOTSPOT_RUNTIME, "InternalRuntimeCounters");
		Counter rtCounter = null;
		for (Counter ctr : counters) {
			if (RT_COUNTER_NAME.equals(ctr.getName())) {
				rtCounter = ctr;
				break;
			}
		}
		return (PerfStringCounter)rtCounter;
	}

	/**
	 * Returns the JVM's current display name
	 * @return the display name
	 */
	public static String getDisplay() {
		return (String)getDisplayCounter().getValue();
	}
}
