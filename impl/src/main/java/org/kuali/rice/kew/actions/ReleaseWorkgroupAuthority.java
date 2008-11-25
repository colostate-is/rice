/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.actions;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * This is the inverse of the {@link TakeWorkgroupAuthority} action.  This puts the document back 
 * in all the peoples action lists that have the document routed to them.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ReleaseWorkgroupAuthority extends ActionTakenEvent {

    private KimGroup group;
    /**
     * @param routeHeader
     * @param user
     */
    public ReleaseWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(KEWConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD, routeHeader, user);
    }
    
    /**
     * @param routeHeader
     * @param user
     * @param annotation
     * @param workgroup
     */
    public ReleaseWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, KimGroup group) {
        super(KEWConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD, routeHeader, user, annotation);
        this.group = group;
    }
    
    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws KEWUserNotFoundException {
        if (group == null) {
            return "User cannot Release Workgroup Authority without a given workgroup";
        } else {
            return performReleaseWorkgroupAuthority(true);
        }
    }

    public void recordAction() throws InvalidActionTakenException, KEWUserNotFoundException {
        String error = performReleaseWorkgroupAuthority(false);
        if (!Utilities.isEmpty(error)) {
            throw new InvalidActionTakenException(error);
        }
        
        queueDocumentProcessing();
    }
    
    private String performReleaseWorkgroupAuthority(boolean forValidationOnly) throws KEWUserNotFoundException {
        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(getUser().getWorkflowId(), group.getGroupId())){
            return (getUser().getAuthenticationUserId() + " not a member of workgroup " + group.getGroupName());
        }
        
        List actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());
        //List groupRequestsToActivate = new ArrayList();//requests for this group that need action items
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            //we left the group active from take authority action.  pending havent been normally activated yet
            if (actionRequest.isGroupRequest() && actionRequest.isActive() && actionRequest.getGroupId().equals(group.getGroupId())) {
                if (actionRequest.getActionItems().size() == 1) {
                    ActionItem actionItem = (ActionItem) actionRequest.getActionItems().get(0);
                    if (! actionItem.getWorkflowId().equals(getUser().getWorkflowId())) {
                        return "User attempting to release workgroup authority did not take it.";
                    } else if (!forValidationOnly) {
                        actionRequest.setStatus(KEWConstants.ACTION_REQUEST_INITIALIZED);//to circumvent check in service during activation
                        getActionRequestService().activateRequest(actionRequest);
                    }
                }
            }
        }
        return "";
    }
}