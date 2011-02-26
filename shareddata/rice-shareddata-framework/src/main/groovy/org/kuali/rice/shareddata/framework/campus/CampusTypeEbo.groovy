/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.shareddata.framework.campus

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.campus.CampusType
import org.kuali.rice.shareddata.api.campus.CampusTypeContract

public class CampusTypeEbo extends PersistableBusinessObjectBase implements Inactivateable, CampusTypeContract, ExternalizableBusinessObject {

	def String code;
	def String name;
	def boolean active;
	
	/**
	* Converts a mutable CampusType business object to an immutable CampusType representation.
	* @param bo
	* @return an immutable CampusType
	*/
   static CampusType to(CampusTypeEbo bo) {
	 if (bo == null) { return null }
	 return CampusType.Builder.create(bo).build()
   }
 
   /**
	* Creates a CampusType business object from an immutable representation of a CampusType.
	* @param an immutable CampusType
	* @return a CampusTypeBo
	*/
   static CampusTypeEbo from(CampusType im) {
	 if (im == null) {return null}
 
	 CampusTypeEbo bo = new CampusTypeEbo()
	 bo.code = im.code
	 bo.name = im.name
	 bo.active = im.active
 
	 return bo;
   }
}