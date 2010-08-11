/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.bo;

import org.junit.Test;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.bo.StateImpl;
import org.kuali.test.KNSTestCase;


/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StateImplTest extends KNSTestCase{
	
	State dummyState;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyState = new StateImpl();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyState = null;
	}
	
	@Test
	public void testPostalStateCode(){
		dummyState.setPostalStateCode("22222");
		assertEquals("Testing PostalStateCode in StateImplTest","22222",dummyState.getPostalStateCode());
	}
	
	@Test
	public void testPostalStateName(){
		dummyState.setPostalStateName("Col");
		assertEquals("Testing PostalStateName in StateImplTest","Col",dummyState.getPostalStateName());
	}
	
	@Test
	public void testPostalCountryCode(){
		dummyState.setPostalCountryCode("c101");
		assertEquals("Testing PostalCountryCode in StateImplTest","c101",dummyState.getPostalCountryCode());
	}
	
	@Test
	public void testCodeAndDescription(){
		assertEquals("Testing CodeAndDescription in StateImplTest",dummyState.getPostalStateCode()+" - "+dummyState.getPostalStateName(),dummyState.getCodeAndDescription());
	}
	
	@Test
	public void testActive(){
		dummyState.setActive(true);
		assertTrue("Testing ActiveCode in StateImplTest", dummyState.isActive());
	}
}
