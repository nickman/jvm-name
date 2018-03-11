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

import org.junit.*;

/**
 * <p>Title: TestJVMName</p>
 * <p>Description: Tests for {@link JVMName}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jvmid.TestJVMName</code></p>
 */

public class TestJVMName {

	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	/**
	 * Sets the JVM name to <code>FooBar</code> and then tests that it was set
	 */
	@Test
	public void testSet() {
		log("Display Byte Limit:" + JVMName.displayByteLimit());
		String currentName = JVMName.getDisplay();
		log("Display: [" + currentName + "]");
		String setTo = "FooBar";
		boolean set = JVMName.setDisplay(setTo);
		Assert.assertTrue("Display not set", set);
		Assert.assertEquals("Display not [" + setTo + "]", setTo, JVMName.getDisplay());
	}
	
	/**
	 * Attempts to set a jvm name that is too long for the buffer limit.
	 * Expects an IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testTooLong() {
		int max = JVMName.displayByteLimit() + 100;
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < max; i++) {
			b.append('x');
		}
		String setTo = b.toString();
		JVMName.setDisplay(setTo);
	}
	
}
