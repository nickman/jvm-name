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
package com.heliosapm.jvmid.agent;

import java.lang.instrument.Instrumentation;

import com.heliosapm.jvmid.JVMName;

/**
 * <p>Title: JVMNameAgent</p>
 * <p>Description: A simple Java agent to set the JVM name with no code changes</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jvmid.agent.JVMNameAgent</code></p>
 */

public class JVMNameAgent {
	
	public static void setName(final String name) {
		if(name==null || name.trim().isEmpty()) {
			System.err.println("The passed JVM name was null or empty");
		} else {
			
			// Delay a few secs so the JVM has a chance to initialize the JMX internals
			Thread t = new Thread("JVMNameSetter") {
				public void run() {
					try { Thread.currentThread().join(3000); } catch (Exception x) {/* No Op */}
					String priorName = JVMName.getDisplay();
					
					if(JVMName.setDisplay(name.trim())) {
						System.setProperty("sun.rt.javaCommand", JVMName.getDisplay());
						System.setProperty("original.display.name", priorName);
						System.out.println("Display Name set to [" + JVMName.getDisplay() + "]");												
					} else {
						System.out.println("Failed to set Display Name");
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
	}
	
	public static void premain(String agentArgs, Instrumentation inst) {
		setName(agentArgs);
	}
	
	public static void premain(String agentArgs) {
		setName(agentArgs);
	}
	
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		setName(agentArgs);
	}
	
	public static void agentmain(String agentArgs) {
		setName(agentArgs);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("jvm-name v:" + JVMNameAgent.class.getPackage().getImplementationVersion());
	}

}
