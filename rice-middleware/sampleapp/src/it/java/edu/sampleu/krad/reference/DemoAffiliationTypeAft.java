/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sampleu.krad.reference;

import com.thoughtworks.selenium.SeleneseTestBase;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoAffiliationTypeAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Affiliation%20Type&channelUrl="
     *   + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
     *   "org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo&returnLocation="
     *   + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Affiliation%20Type&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
  
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
       waitAndClickKRAD();
       waitAndClickByLinkText("Affiliation Type");        
    }

    //Code for KRAD Test Package.
    protected void testDemoAffiliationType() throws Exception { 
      selectFrameIframePortlet();
      waitAndClickButtonByText("Search");
      Thread.sleep(3000);
      assertTextPresent("AFLT");
      assertTextPresent("FCLTY");
      assertTextPresent("STAFF");
      waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
      waitAndClickButtonByText("Search");
      Thread.sleep(3000);
      if(isTextPresent("AFLT"))
      {
          fail("Conditions not working !");
      }
      waitAndTypeByName("lookupCriteria[code]","AFLT");
      waitAndClickButtonByText("Search");
      Thread.sleep(3000);
      if(isTextPresent("FCLTY"))
      {
          fail("Conditions not working !");
      }
    }

    @Test
    public void testDemoAffiliationTypeBookmark() throws Exception {
        testDemoAffiliationType();
        passed();
    }

    @Test
    public void testDemoAffiliationTypeNav() throws Exception {
        testDemoAffiliationType();
        passed();
    }
}