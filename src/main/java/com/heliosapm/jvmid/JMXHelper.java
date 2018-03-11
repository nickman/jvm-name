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

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * <p>Title: JMXHelper</p>
 * <p>Description: JMX utility methods</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jvmid.JMXHelper</code></p>
 */

public class JMXHelper {
	/** The JMX ObjectName for the Hotspot Internals MBean */
	public static final ObjectName HOTSPOT_INTERNAL_ON = JMXHelper.on("sun.management:type=HotspotInternal");
	/** The Hotspot Internals MBean class name */
	public static final String HOTSPOT_INTERNAL_CLASS = "sun.management.HotspotInternal";
	/** The Hotspot Runtime JMX ObjectName */
	public static final ObjectName HOTSPOT_RUNTIME = on("sun.management:type=HotspotRuntime");

	/**
	 * Creates a JMX ObjectName from the passed stringy
	 * @param name The name to build from
	 * @return a JMX ObjectName
	 */
	public static ObjectName on(CharSequence name) {
		if(name==null) throw new IllegalArgumentException("The passed name was null");
		String _name = name.toString().trim();
		if(_name.isEmpty()) throw new IllegalArgumentException("The passed name was empty");
		try {
			return new ObjectName(_name);
		} catch (MalformedObjectNameException e) {
			throw new IllegalArgumentException("Invalid ObjectName [" + name + "]");
		}
	}

	/**
	 * Installs the Hotspot Internals MBean
	 * @param server The MBeanServer to install into
	 */
	public static synchronized void installHotspot(MBeanServerConnection server) {
		if(server==null) throw new IllegalArgumentException("Passed MBeanServerConnection was null");
		try {
			if (!server.isRegistered(HOTSPOT_INTERNAL_ON)) {
				server.createMBean(HOTSPOT_INTERNAL_CLASS, HOTSPOT_INTERNAL_ON);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to install HotspotInternal", ex);
		}
	}
	
	  /**
	   * Gets an attribute value from an mbean in the passed MBeanServer
	   * 
	   * @param server
	   *          The MBeanServer to get the attribute from
	   * @param on
	   *          the object name
	   * @param name
	   *          the name of the attribute
	   * @return the value of the attribute
	   */
	  @SuppressWarnings("unchecked")
	  public static <T> T getAttribute(final MBeanServerConnection server, final ObjectName on, final String name) {
	    try {
	      return (T)server.getAttribute(on, name);
	    } catch (Exception e) {
	      throw new RuntimeException("Failed to get attribute", e);
	    }
	  }
	

}
