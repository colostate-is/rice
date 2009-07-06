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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAddressImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAffiliationImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEmploymentInformationImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityExternalIdentifierImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPhoneImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPrivacyPreferencesImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.control.TextControlDefinition;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UiDocumentServiceImpl implements UiDocumentService {
	private static final Logger LOG = Logger.getLogger(UiDocumentServiceImpl.class);

	private RoleService roleService;
	private RoleManagementService roleManagementService;
	private BusinessObjectService businessObjectService;
	private IdentityService identityService;
	private IdentityManagementService identityManagementService;
	private GroupService groupService;
	private ResponsibilityService responsibilityService;
	private KimTypeInfoService kimTypeInfoService;
    private DocumentHelperService documentHelperService;
    

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	public void saveEntityPerson(
			IdentityManagementPersonDocument identityManagementPersonDocument) {
		KimEntityImpl kimEntity = new KimEntityImpl();
		KimEntityImpl origEntity = ((IdentityServiceImpl)getIdentityService()).getEntityImpl(identityManagementPersonDocument.getEntityId());
		if (origEntity == null) {
			origEntity = new KimEntityImpl();
			kimEntity.setActive(true);
		} else {
			// TODO : in order to resolve optimistic locking issue. has to get entity and set the version number if entity records matched
			// Need to look into this.
			kimEntity.setActive(origEntity.isActive());
			kimEntity.setVersionNumber(origEntity.getVersionNumber());
		}

		kimEntity.setEntityId(identityManagementPersonDocument.getEntityId());

		boolean inactivatingPrincipal = setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
		setupExtId(identityManagementPersonDocument, kimEntity, origEntity.getExternalIdentifiers());
		setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
		setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
		List<KimEntityEntityTypeImpl> entityTypes = new ArrayList<KimEntityEntityTypeImpl>();
		KimEntityEntityTypeImpl entityType = new KimEntityEntityTypeImpl();
		entityType.setEntityId(identityManagementPersonDocument.getEntityId());
		entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
		entityType.setActive(true);
		entityTypes.add(entityType);
		KimEntityEntityTypeImpl origEntityType = new KimEntityEntityTypeImpl();
		for (KimEntityEntityTypeImpl type : origEntity.getEntityTypes()) {
			// should check entity.entitytypeid, but it's not persist in persondoc yet
			if (type.getEntityTypeCode()!=null && StringUtils.equals(type.getEntityTypeCode(), entityType.getEntityTypeCode())) {
				origEntityType = type;
				entityType.setVersionNumber(type.getVersionNumber());
				entityType.setActive(type.isActive());
			}
		}
		kimEntity.setEntityTypes(entityTypes);
		setupPhone(identityManagementPersonDocument, entityType, origEntityType.getPhoneNumbers());
		setupEmail(identityManagementPersonDocument, entityType, origEntityType.getEmailAddresses());
		setupAddress(identityManagementPersonDocument, entityType, origEntityType.getAddresses());
		List <GroupMemberImpl>  groupPrincipals = populateGroupMembers(identityManagementPersonDocument);
		List <RoleMemberImpl>  rolePrincipals = populateRoleMembers(identityManagementPersonDocument);
		List <KimDelegationImpl> personDelegations = populateDelegations(identityManagementPersonDocument);
		List <PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();
		List <RoleResponsibilityActionImpl> roleRspActions = populateRoleRspActions(identityManagementPersonDocument);
		List <RoleMemberAttributeDataImpl> blankRoleMemberAttrs = getBlankRoleMemberAttrs(rolePrincipals);
		bos.add(kimEntity);
		bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		bos.addAll(roleRspActions);
		bos.addAll(personDelegations);
		// boservice.save(bos) does not handle deleteawarelist
		getBusinessObjectService().save(bos);
		
		//KIMServiceLocator.getIdentityManagementService().flushEntityPrincipalCaches();
		IdentityManagementNotificationService service = (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(KimConstants.NAMESPACE_CODE, "IdentityManagementNotificationService"));
		service.principalUpdated();
		
		if (!blankRoleMemberAttrs.isEmpty()) {
			getBusinessObjectService().delete(blankRoleMemberAttrs);
		}
		if ( inactivatingPrincipal ) {
			//when a person is inactivated, inactivate their group, role, and delegation memberships
			KIMServiceLocator.getRoleManagementService().principalInactivated(identityManagementPersonDocument.getPrincipalId());
		}
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#getAttributeEntries(AttributeDefinitionMap)
	 */
	public Map<String,Object> getAttributeEntries( AttributeDefinitionMap definitions ) {
		Map<String,Object> attributeEntries = new HashMap<String,Object>();
		if(definitions!=null){
	        for (String key : definitions.keySet()) {
				AttributeDefinition definition = definitions.get(key);
				Map<String,Object> attribute = new HashMap<String,Object>();
				if (definition instanceof KimDataDictionaryAttributeDefinition) {
	//				AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefinition)
	//						.getDataDictionaryAttributeDefinition();
					ControlDefinition control = definition.getControl();
					if (control.isSelect()) {
						Map<String,Object> controlMap = new HashMap<String,Object>();
			            controlMap.put("select", "true");
			            controlMap.put("valuesFinder", control.getValuesFinderClass().getName());
			            if (control.getBusinessObjectClass() != null) {
			                controlMap.put("businessObject", control.getBusinessObjectClass().getName());
			            }
			            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
			                controlMap.put("keyAttribute", control.getKeyAttribute());
			            }
			            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
			                controlMap.put("labelAttribute", control.getLabelAttribute());
			            }
			            if (control.getIncludeKeyInLabel() != null) {
			                controlMap.put("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
			            }
						attribute.put("control", controlMap);
			        } else {
			        	// FIXME: Huh!?!?, control is a Map in the above code but a ControlDefinition here?!?!?
			        	// Maybe this should use the AttributesMapBuilder code to create this
			        	attribute.put("control", definition.getControl());
			        }
					attribute.put("label", definition.getLabel());
					attribute.put("shortLabel", definition.getShortLabel());
					attribute.put("maxLength", definition.getMaxLength());
					attribute.put("required", definition.isRequired());
					attributeEntries.put(definition.getName(),attribute);
				} else {
					TextControlDefinition control = new TextControlDefinition();
					control.setSize(10);
					attribute.put("control", control);
					attribute.put("label", definition.getLabel());
					attribute.put("maxLength", 20);
					attribute.put("required", true);
					attribute.put("shortLabel", definition.getLabel());
					attributeEntries.put(definition.getName(),attribute);
				}
			}
		}
        return attributeEntries;
	}


	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(IdentityManagementPersonDocument, String)
	 */
	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
        KimPrincipalImpl principal = ((IdentityServiceImpl)getIdentityService()).getPrincipalImpl(principalId);
        if(principal==null)
        	throw new RuntimeException("Principal does not exist for principal id:"+principalId);

        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
		KimEntityImpl kimEntity = ((IdentityServiceImpl)getIdentityService()).getEntityImpl(principal.getEntityId());
		identityManagementPersonDocument.setEntityId(kimEntity.getEntityId());
		if ( ObjectUtils.isNotNull( kimEntity.getPrivacyPreferences() ) ) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames( principalId, kimEntity.getNames(), identityManagementPersonDocument.getPrivacy().isSuppressName() ));
		KimEntityEntityTypeImpl entityType = null;
		for (KimEntityEntityTypeImpl type : kimEntity.getEntityTypes()) {
			if (KimConstants.EntityTypes.PERSON.equals(type.getEntityTypeCode())) {
				entityType = type;
			}
		}

		for (KimEntityExternalIdentifierImpl extId : kimEntity.getExternalIdentifiers()){
			if (KimConstants.PersonExternalIdentifierTypes.TAX.equals(extId.getExternalIdentifierTypeCode())) {
				identityManagementPersonDocument.setTaxId(extId.getExternalId());
			}
		}
		if(entityType!=null){
			identityManagementPersonDocument.setEmails(loadEmails(principalId, entityType.getEmailAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressEmail()));
			identityManagementPersonDocument.setPhones(loadPhones(principalId, entityType.getPhoneNumbers(), identityManagementPersonDocument.getPrivacy().isSuppressPhone()));
			identityManagementPersonDocument.setAddrs(loadAddresses(principalId, entityType.getAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressAddress()));
		}

		List<? extends Group> groups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		loadGroupToPersonDoc(identityManagementPersonDocument, groups);
		loadRoleToPersonDoc(identityManagementPersonDocument);
		loadDelegationsToPersonDoc(identityManagementPersonDocument);
	}

    @SuppressWarnings("unchecked")
	public List<KimDelegationImpl> getPersonDelegations(String principalId){
		if(principalId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, principalId);
		List<KimDelegationMemberImpl> delegationMembers = (List<KimDelegationMemberImpl>)getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
		List<KimDelegationImpl> delegations = new ArrayList<KimDelegationImpl>();
		List<String> delegationIds = new ArrayList<String>();
		if(ObjectUtils.isNotNull(delegationMembers)){
			for(KimDelegationMemberImpl delegationMember: delegationMembers){
				if(!delegationIds.contains(delegationMember.getDelegationId())){
					delegationIds.add(delegationMember.getDelegationId());
					criteria = new HashMap<String,String>(1);
					criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
					delegations.add((KimDelegationImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria));
				}
			}
		}
		return delegations;
	}


    protected void loadDelegationsToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		List<KimDelegationImpl> origDelegations = getPersonDelegations(identityManagementPersonDocument.getPrincipalId());
		if(ObjectUtils.isNotNull(origDelegations)){
			for(KimDelegationImpl del: origDelegations){
				if(del.isActive()){
					documentDelegation = new RoleDocumentDelegation();
					documentDelegation.setActive(del.isActive());
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(
							loadDelegationMembers(identityManagementPersonDocument, 
									del.getMembers(), (RoleImpl)getMember(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, del.getRoleId())));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		identityManagementPersonDocument.setDelegations(delList);
		setDelegationMembersInDocument(identityManagementPersonDocument);
	}
    
	public void setDelegationMembersInDocument(IdentityManagementPersonDocument identityManagementPersonDocument){
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation delegation: identityManagementPersonDocument.getDelegations()){
				if(CollectionUtils.isNotEmpty(delegation.getMembers())){
					for(RoleDocumentDelegationMember member: delegation.getMembers()){
						member.setDelegationTypeCode(delegation.getDelegationTypeCode());
						identityManagementPersonDocument.getDelegationMembers().add(member);
					}
				}
			}
		}
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(
    		IdentityManagementPersonDocument identityManagementPersonDocument, List<KimDelegationMemberImpl> members, RoleImpl roleImpl){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember = new RoleDocumentDelegationMember();
		RoleMemberImpl roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(KimDelegationMemberImpl member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				pndMember.setRoleImpl(roleImpl);
				if(pndMember.isActive()){
					KimCommonUtils.copyProperties(pndMember, member);
					pndMember.setRoleMemberId(member.getRoleMemberId());
					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementPersonDocument, pndMember.getAttributesHelper().getDefinitions(), member.getAttributes()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}
    
    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementPersonDocument identityManagementPersonDocument,
    		AttributeDefinitionMap origAttributeDefinitions, List<KimDelegationMemberAttributeDataImpl> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributeDefinitions!=null){
			for(String key: origAttributeDefinitions.keySet()) {
				origAttributeId = identityManagementPersonDocument.getKimAttributeDefnId(origAttributeDefinitions.get(key));
				if(ObjectUtils.isNotNull(attributeDataList)){
					for(KimDelegationMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getKimAttributeId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getDelegationMemberId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}

	/**
	 *
	 * This method load related group data to pending document when usert initiate the 'edit'.
	 *
	 * @param identityManagementPersonDocument
	 * @param groups
	 */
	protected void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends Group> groups) {
		List <PersonDocumentGroup> docGroups = new ArrayList <PersonDocumentGroup>();
		if(ObjectUtils.isNotNull(groups)){
			List<String> directMemberPrincipalIds;
			Collection<GroupMembershipInfo> groupMemberships;
			for (Group group: groups) {
				directMemberPrincipalIds = getGroupService().getDirectMemberPrincipalIds(group.getGroupId());
				if(ObjectUtils.isNotNull(directMemberPrincipalIds)){
					for (String memberId: directMemberPrincipalIds) {
						// other more direct methods for this ?
						// can't cast group to 'GroupImpl' because list is GroupInfo type
						if (StringUtils.equals(memberId, identityManagementPersonDocument.getPrincipalId())) {
							PersonDocumentGroup docGroup = new PersonDocumentGroup();
							docGroup.setGroupId(group.getGroupId());
							docGroup.setGroupName(group.getGroupName());
							docGroup.setNamespaceCode(group.getNamespaceCode());
							docGroup.setPrincipalId(memberId);
							List<String> groupIds = new ArrayList<String>();
							groupIds.add(group.getGroupId());
							groupMemberships = getGroupService().getGroupMembers(groupIds);
							if(ObjectUtils.isNotNull(groupMemberships)){
								for (GroupMembershipInfo groupMember: groupMemberships) {
									if (groupMember.isActive() && StringUtils.equals(groupMember.getMemberId(), identityManagementPersonDocument.getPrincipalId()) && 
										StringUtils.equals(groupMember.getMemberTypeCode(), KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
										docGroup.setGroupMemberId(groupMember.getGroupMemberId());
										docGroup.setActiveFromDate(groupMember.getActiveFromDate());
										docGroup.setActiveToDate(groupMember.getActiveToDate());
									}
								}
							}
							docGroup.setEdit(true);
							//docGroup.setGroupMemberId(((GroupImpl));
							if(docGroup.isActive())
								docGroups.add(docGroup);
						}
					}
				}
			}
		}
		identityManagementPersonDocument.setGroups(docGroups);
	}

	protected void loadRoleToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <PersonDocumentRole> docRoles = new ArrayList <PersonDocumentRole>();
		List<RoleImpl> roles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());
		List<String> roleIds = new ArrayList<String>();
		if(ObjectUtils.isNotNull(roles)){
	        for (RoleImpl role : roles) {
	        	if (!roleIds.contains(role.getRoleId())) {
		        	PersonDocumentRole docRole = new PersonDocumentRole();
		        	docRole.setKimTypeId(role.getKimTypeId());
		        	docRole.setActive(role.isActive());
		        	docRole.setNamespaceCode(role.getNamespaceCode());
		        	docRole.setEdit(true);
		        	docRole.setRoleId(role.getRoleId());
		        	docRole.setRoleName(role.getRoleName());
		        	docRole.setRolePrncpls(populateDocRolePrncpl(role.getMembers(), identityManagementPersonDocument.getPrincipalId(), getAttributeDefinitionsForRole(docRole)));
		        	docRole.refreshReferenceObject("assignedResponsibilities");
		        	if(docRole.getRolePrncpls()!=null && !docRole.getRolePrncpls().isEmpty()){
		        		docRoles.add(docRole);
		        		roleIds.add(role.getRoleId());
		        	}
	        	}
	        }
		}
		for (PersonDocumentRole role : docRoles) {
			role.setDefinitions(getAttributeDefinitionsForRole(role));
        	// when post again, it will need this during populate
            role.setNewRolePrncpl(new KimDocumentRoleMember());
            if(role.getDefinitions()!=null){
	            for (String key : role.getDefinitions().keySet()) {
	            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	            	//qualifier.setQualifierKey(key);
	            	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
	            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
	            }
            }
            loadRoleRstAction(role);
            role.setAttributeEntry( getAttributeEntries( role.getDefinitions() ) );
		}
        //

        identityManagementPersonDocument.setRoles(docRoles);
	}

	protected AttributeDefinitionMap getAttributeDefinitionsForRole(PersonDocumentRole role) {
    	KimTypeService kimTypeService = KimCommonUtils.getKimTypeService( role.getKimRoleType() );
    	if ( kimTypeService != null ) {
    		return kimTypeService.getAttributeDefinitions(role.getKimTypeId());
    	}
    	return new AttributeDefinitionMap();
	}

	protected void loadRoleRstAction(PersonDocumentRole role) {
		if(role!=null && CollectionUtils.isNotEmpty(role.getRolePrncpls())){
			for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
				List<RoleResponsibilityActionImpl> actions = getRoleRspActions( roleMbr.getRoleMemberId());
				if(ObjectUtils.isNotNull(actions)){
					for (RoleResponsibilityActionImpl entRoleRspAction :actions) {
						KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
						roleRspAction.setRoleResponsibilityId(entRoleRspAction.getRoleResponsibilityId());
						roleRspAction.setActionTypeCode(entRoleRspAction.getActionTypeCode());
						roleRspAction.setActionPolicyCode(entRoleRspAction.getActionPolicyCode());
						roleRspAction.setPriorityNumber(entRoleRspAction.getPriorityNumber());
						roleRspAction.setRoleResponsibilityActionId(entRoleRspAction.getRoleResponsibilityActionId());
						roleRspAction.refreshReferenceObject("roleResponsibility");
						roleMbr.getRoleRspActions().add(roleRspAction);
					}
				}
			}
		}
	}

	protected void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier, AttributeDefinition definition) {
    	qualifier.setKimAttrDefnId(getAttributeDefnId(definition));
    	qualifier.refreshReferenceObject("kimAttribute");
    }

	protected String getAttributeDefnId(AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	}
    }

    @SuppressWarnings("unchecked")
	protected List<RoleImpl> getRolesForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", RoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<RoleImpl>)getBusinessObjectService().findMatching(RoleImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRoleMembersForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleMemberImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("memberId", principalId);
		criteria.put("memberTypeCode", RoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
	}

	public RoleMemberImpl getRoleMember(String roleMemberId) {
		if ( roleMemberId == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("roleMemberId", roleMemberId);
		return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
	}
	
    @SuppressWarnings("unchecked")
	protected List<RoleResponsibilityActionImpl> getRoleRspActions(String roleMemberId) {
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId);
		return (List<RoleResponsibilityActionImpl>)getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

    protected List<KimDocumentRoleMember> populateDocRolePrncpl(List <RoleMemberImpl> roleMembers, String principalId, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleMember> docRoleMembers = new ArrayList <KimDocumentRoleMember>();
		if(ObjectUtils.isNotNull(roleMembers)){
	    	for (RoleMemberImpl rolePrincipal : roleMembers) {
	    		if (rolePrincipal.isActive() && RoleImpl.PRINCIPAL_MEMBER_TYPE.equals(rolePrincipal.getMemberTypeCode()) && 
	    				StringUtils.equals(rolePrincipal.getMemberId(), principalId)) {
	        		KimDocumentRoleMember docRolePrncpl = new KimDocumentRoleMember();
	        		docRolePrncpl.setMemberId(rolePrincipal.getMemberId());
	        		docRolePrncpl.setRoleMemberId(rolePrincipal.getRoleMemberId());
	        		docRolePrncpl.setActive(rolePrincipal.isActive());
	        		docRolePrncpl.setRoleId(rolePrincipal.getRoleId());
	        		docRolePrncpl.setActiveFromDate(rolePrincipal.getActiveFromDate());
	        		docRolePrncpl.setActiveToDate(rolePrincipal.getActiveToDate());
	         		docRolePrncpl.setQualifiers(populateDocRoleQualifier(rolePrincipal.getAttributes(), definitions));
	         		docRolePrncpl.setEdit(true);
	        		docRoleMembers.add(docRolePrncpl);
	    		 }
	    	}
		}
    	return docRoleMembers;
    }

    // UI layout for rolequalifier is a little different from kimroleattribute set up.
    // each principal may have member with same role multiple times with different qualifier, but the role
    // only displayed once, and the qualifier displayed multiple times.
    protected List<KimDocumentRoleQualifier> populateDocRoleQualifier(List <RoleMemberAttributeDataImpl> qualifiers, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleQualifier> docRoleQualifiers = new ArrayList <KimDocumentRoleQualifier>();
		if(definitions!=null){
			for (String key : definitions.keySet()) {
				AttributeDefinition definition = definitions.get(key);
				String attrDefId=null;
				if (definition instanceof KimDataDictionaryAttributeDefinition) {
					attrDefId = ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
				} else {
					attrDefId = ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
				}
				boolean qualifierFound = false;
				if(ObjectUtils.isNotNull(qualifiers)){
					for (RoleMemberAttributeDataImpl qualifier : qualifiers) {
						if (attrDefId!=null && StringUtils.equals(attrDefId, qualifier.getKimAttributeId())) {
				    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
				    		docRoleQualifier.setAttrDataId(qualifier.getAttributeDataId());
				    		docRoleQualifier.setAttrVal(qualifier.getAttributeValue());
				    		docRoleQualifier.setKimAttrDefnId(qualifier.getKimAttributeId());
				    		docRoleQualifier.setKimAttribute(qualifier.getKimAttribute());
				    		docRoleQualifier.setKimTypId(qualifier.getKimTypeId());
				    		docRoleQualifier.setRoleMemberId(qualifier.getRoleMemberId());
				    		docRoleQualifier.setEdit(true);
				    		docRoleQualifiers.add(docRoleQualifier);
				    		qualifierFound = true;
				    		break;
						}
					}
				}
				if (!qualifierFound) {
		    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
		    		docRoleQualifier.setAttrVal("");
		    		docRoleQualifier.setKimAttrDefnId(attrDefId);
		    		docRoleQualifier.refreshReferenceObject("kimAttribute");
		    		docRoleQualifiers.add(docRoleQualifier);
				}
			}
		}
    	return docRoleQualifiers;
    }

    protected List<PersonDocumentName> loadNames( String principalId, List <KimEntityNameImpl> names, boolean suppressDisplay ) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		if(ObjectUtils.isNotNull(names)){
			for (KimEntityNameImpl name: names) {
				if(name.isActive()){
					PersonDocumentName docName = new PersonDocumentName();
					docName.setNameTypeCode(name.getNameTypeCode());
					docName.setEntityNameType(name.getEntityNameType());
					// unmask the user name data if the user can edit or see past the privacy preferences
					if ( !suppressDisplay || canOverrideEntityPrivacyPreferences( principalId ) ) {
						docName.setFirstName(name.getFirstNameUnmasked());
						docName.setLastName(name.getLastNameUnmasked());
						docName.setMiddleName(name.getMiddleNameUnmasked());
						docName.setTitle(name.getTitleUnmasked());
						docName.setSuffix(name.getSuffixUnmasked());
					} else {
						docName.setFirstName(name.getFirstName());
						docName.setLastName(name.getLastName());
						docName.setMiddleName(name.getMiddleName());
						docName.setTitle(name.getTitle());
						docName.setSuffix(name.getSuffix());
					}
					docName.setActive(name.isActive());
					docName.setDflt(name.isDefault());
					docName.setEdit(true);
					docName.setEntityNameId(name.getEntityNameId());
					docNames.add(docName);
				}
			}
		}
		return docNames;
	}

	public boolean canModifyEntity( String principalId ) {
		return getIdentityManagementService().isAuthorized(
				GlobalVariables.getUserSession().getPrincipalId(), 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionNames.MODIFY_ENTITY, 
				null,
				new AttributeSet(KimAttributes.PRINCIPAL_ID, principalId) );
	}

	public boolean canOverrideEntityPrivacyPreferences( String principalId ){
		return getIdentityManagementService().isAuthorized(
				GlobalVariables.getUserSession().getPrincipalId(), 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
				null,
				new AttributeSet(KimAttributes.PRINCIPAL_ID, principalId) );
	}

    protected List<PersonDocumentAffiliation> loadAffiliations(List <KimEntityAffiliationImpl> affiliations, List<KimEntityEmploymentInformationImpl> empInfos) {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
		if(ObjectUtils.isNotNull(affiliations)){
			for (KimEntityAffiliationImpl affiliation: affiliations) {
				if(affiliation.isActive()){
					PersonDocumentAffiliation docAffiliation = new PersonDocumentAffiliation();
					docAffiliation.setAffiliationTypeCode(affiliation.getAffiliationTypeCode());
					docAffiliation.setCampusCode(affiliation.getCampusCode());
					docAffiliation.setActive(affiliation.isActive());
					docAffiliation.setDflt(affiliation.isDefault());
					docAffiliation.setEntityAffiliationId(affiliation.getEntityAffiliationId());
					docAffiliation.refreshReferenceObject("affiliationType");
					// EntityAffiliationImpl does not define empinfos as collection
					docAffiliations.add(docAffiliation);
					docAffiliation.setEdit(true);
					// employment informations
					List<PersonDocumentEmploymentInfo> docEmploymentInformations = new ArrayList<PersonDocumentEmploymentInfo>();
					if(ObjectUtils.isNotNull(empInfos)){
						for (KimEntityEmploymentInformationImpl empInfo: empInfos) {
							if (empInfo.isActive() && StringUtils.equals(docAffiliation.getEntityAffiliationId(), empInfo.getEntityAffiliationId())) {
								PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
								docEmpInfo.setEntityEmploymentId(empInfo.getEntityEmploymentId());
								docEmpInfo.setEmployeeId(empInfo.getEmployeeId());
								docEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								docEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
								docEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
								docEmpInfo.setEmployeeStatusCode(empInfo.getEmployeeStatusCode());
								docEmpInfo.setEmployeeTypeCode(empInfo.getEmployeeTypeCode());
								docEmpInfo.setActive(empInfo.isActive());
								docEmpInfo.setPrimary(empInfo.isPrimary());
								docEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliationId());
								docEmpInfo.setVersionNumber(empInfo.getVersionNumber());
								docEmpInfo.setEdit(true);
								docEmpInfo.refreshReferenceObject("employmentType");
								docEmploymentInformations.add(docEmpInfo);
							}
						}
					}
					docAffiliation.setEmpInfos(docEmploymentInformations);
				}
			}
		}
		return docAffiliations;

	}

    protected boolean setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimPrincipalImpl> origPrincipals) {
    	boolean inactivatingPrincipal = false;
		List<KimPrincipalImpl> principals = new ArrayList<KimPrincipalImpl>();
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setPrincipalName(identityManagementPersonDocument.getPrincipalName());
		principal.setPassword(identityManagementPersonDocument.getPassword());
		principal.setActive(identityManagementPersonDocument.isActive());
		if(ObjectUtils.isNotNull(origPrincipals)){
			for (KimPrincipalImpl prncpl : origPrincipals) {
				if (prncpl.getPrincipalId()!=null && StringUtils.equals(prncpl.getPrincipalId(), principal.getPrincipalId())) {
					principal.setVersionNumber(prncpl.getVersionNumber());
					// check if inactivating the principal
					if ( prncpl.isActive() && !principal.isActive() ) {
						inactivatingPrincipal = true;
					}
				}
			}
		}
		principals.add(principal);

		kimEntity.setPrincipals(principals);
		return inactivatingPrincipal;
	}

    protected void setupExtId(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimEntityExternalIdentifierImpl> origExtIds) {
    	if(StringUtils.isNotEmpty(identityManagementPersonDocument.getTaxId())){
			List<KimEntityExternalIdentifierImpl> extIds = new ArrayList<KimEntityExternalIdentifierImpl>();
			KimEntityExternalIdentifierImpl extId = new KimEntityExternalIdentifierImpl();
			extId.setEntityId(identityManagementPersonDocument.getEntityId());
			extId.setExternalId(identityManagementPersonDocument.getTaxId());
			extId.setExternalIdentifierTypeCode(KimConstants.PersonExternalIdentifierTypes.TAX);
			if(ObjectUtils.isNotNull(origExtIds)){
				for (KimEntityExternalIdentifierImpl origExtId : origExtIds) {
					if (origExtId.getExternalIdentifierTypeCode()!=null && StringUtils.equals(origExtId.getExternalIdentifierTypeCode(), extId.getExternalIdentifierTypeCode())) {
						extId.setVersionNumber(origExtId.getVersionNumber());
						extId.setEntityExternalIdentifierId(origExtId.getEntityExternalIdentifierId());
					}
				}
			}
			extIds.add(extId);
			kimEntity.setExternalIdentifiers(extIds);
    	}
	}

    protected void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, KimEntityPrivacyPreferencesImpl origPrivacy) {
		KimEntityPrivacyPreferencesImpl privacyPreferences = new KimEntityPrivacyPreferencesImpl();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences
				.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		if (ObjectUtils.isNotNull(origPrivacy)) {
			privacyPreferences.setVersionNumber(origPrivacy.getVersionNumber());
		}
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
    protected PersonDocumentPrivacy loadPrivacyReferences(KimEntityPrivacyPreferencesImpl privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		docPrivacy.setEdit(true);
		return docPrivacy;
	}

    protected void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimEntityNameImpl> origNames) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressName() || canOverrideEntityPrivacyPreferences( identityManagementPersonDocument.getPrincipalId() ) ) {
	    	List<KimEntityNameImpl> entityNames = new ArrayList<KimEntityNameImpl>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getNames())){
				for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
					KimEntityNameImpl entityName = new KimEntityNameImpl();
					entityName.setNameTypeCode(name.getNameTypeCode());
					entityName.setFirstName(name.getFirstName());
					entityName.setLastName(name.getLastName());
					entityName.setMiddleName(name.getMiddleName());
					entityName.setTitle(name.getTitle());
					entityName.setSuffix(name.getSuffix());
					entityName.setActive(name.isActive());
					entityName.setDefault(name.isDflt());
					entityName.setEntityNameId(name.getEntityNameId());
					if(ObjectUtils.isNotNull(origNames)){
						for (KimEntityNameImpl origName : origNames) {
							if (origName.getEntityNameId()!=null && StringUtils.equals(origName.getEntityNameId(), entityName.getEntityNameId())) {
								entityName.setVersionNumber(origName.getVersionNumber());
							}
			
						}
					}
					entityNames.add(entityName);
				}
			}
			kimEntity.setNames(entityNames);
    	}
	}

    protected void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity,List<KimEntityAffiliationImpl> origAffiliations, List<KimEntityEmploymentInformationImpl> origEmpInfos) {
		List<KimEntityAffiliationImpl> entityAffiliations = new ArrayList<KimEntityAffiliationImpl>();
		// employment informations
		List<KimEntityEmploymentInformationImpl> entityEmploymentInformations = new ArrayList<KimEntityEmploymentInformationImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAffiliations())){
			for (PersonDocumentAffiliation affiliation : identityManagementPersonDocument.getAffiliations()) {
				KimEntityAffiliationImpl entityAffiliation = new KimEntityAffiliationImpl();
				entityAffiliation.setAffiliationTypeCode(affiliation.getAffiliationTypeCode());
				entityAffiliation.setCampusCode(affiliation.getCampusCode());
				entityAffiliation.setActive(affiliation.isActive());
				entityAffiliation.setDefault(affiliation.isDflt());
				entityAffiliation.setEntityId(identityManagementPersonDocument.getEntityId());
				entityAffiliation.setEntityAffiliationId(affiliation.getEntityAffiliationId());
				if(ObjectUtils.isNotNull(origAffiliations)){
				// EntityAffiliationImpl does not define empinfos as collection
					for (KimEntityAffiliationImpl origAffiliation : origAffiliations) {
						if(isSameAffiliation(origAffiliation, entityAffiliation)){
							entityAffiliation.setEntityAffiliationId(origAffiliation.getEntityAffiliationId());
						}
						if (origAffiliation.getEntityAffiliationId()!=null && StringUtils.equals(origAffiliation.getEntityAffiliationId(), entityAffiliation.getEntityAffiliationId())) {
							entityAffiliation.setVersionNumber(origAffiliation.getVersionNumber());
						}
					}
				}
				entityAffiliations.add(entityAffiliation);
				int employeeRecordCounter = origEmpInfos==null?0:origEmpInfos.size();
				if(CollectionUtils.isNotEmpty(affiliation.getEmpInfos())){
					for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
						KimEntityEmploymentInformationImpl entityEmpInfo = new KimEntityEmploymentInformationImpl();
						entityEmpInfo.setEntityEmploymentId(empInfo.getEntityEmploymentId());
						entityEmpInfo.setEmployeeId(empInfo.getEmployeeId());
						entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
						entityEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
						entityEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
						entityEmpInfo.setEmployeeStatusCode(empInfo.getEmployeeStatusCode());
						entityEmpInfo.setEmployeeTypeCode(empInfo.getEmployeeTypeCode());
						entityEmpInfo.setActive(empInfo.isActive());
						entityEmpInfo.setPrimary(empInfo.isPrimary());
						entityEmpInfo.setEntityId(identityManagementPersonDocument.getEntityId());
						entityEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliationId());
						if(ObjectUtils.isNotNull(origEmpInfos)){
							for (KimEntityEmploymentInformationImpl origEmpInfo : origEmpInfos) {
								if(isSameEmpInfo(origEmpInfo, entityEmpInfo)){
									entityEmpInfo.setEntityEmploymentId(entityEmpInfo.getEntityEmploymentId());
								}
			
								if (origEmpInfo.getEntityEmploymentId()!=null && StringUtils.equals(origEmpInfo.getEntityEmploymentId(), entityEmpInfo.getEntityEmploymentId())) {
									entityEmpInfo.setVersionNumber(origEmpInfo.getVersionNumber());
									entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								}
							}
						}
						if(StringUtils.isEmpty(entityEmpInfo.getEmploymentRecordId())){
							employeeRecordCounter++;
							entityEmpInfo.setEmploymentRecordId(employeeRecordCounter+"");
						}
						entityEmploymentInformations.add(entityEmpInfo);
					}
				}
			}
		}
		kimEntity.setEmploymentInformation(entityEmploymentInformations);
		kimEntity.setAffiliations(entityAffiliations);
	}

   private boolean isSameAffiliation(KimEntityAffiliationImpl origAffiliation, KimEntityAffiliationImpl entityAffiliation){
    	//entityId
    	//affiliationTypeCode
    	//campusCode
    	return (origAffiliation!=null && entityAffiliation!=null) && 
    	(StringUtils.isNotEmpty(origAffiliation.getCampusCode()) && StringUtils.equals(origAffiliation.getCampusCode(), entityAffiliation.getCampusCode())) 
    	&& 
    	(StringUtils.isNotEmpty(origAffiliation.getAffiliationTypeCode()) && StringUtils.equals(origAffiliation.getAffiliationTypeCode(), entityAffiliation.getAffiliationTypeCode())) 
 		&& 
 		(StringUtils.isNotEmpty(origAffiliation.getEntityId()) && StringUtils.equals(origAffiliation.getEntityId(), entityAffiliation.getEntityId()));
    }
    
    private boolean isSameEmpInfo(KimEntityEmploymentInformationImpl origEmpInfo, KimEntityEmploymentInformationImpl entityEmpInfo){
    	//emp_info:
    		//employmentRecordId
    		//entityId
    		//These should be unique - add a business rule
    	return (origEmpInfo!=null && entityEmpInfo!=null) 
    			&& (StringUtils.isNotEmpty(origEmpInfo.getEmploymentRecordId()) 
    					&& StringUtils.equals(origEmpInfo.getEmploymentRecordId(), entityEmpInfo.getEmploymentRecordId() )
    				)
    			&& StringUtils.equals( origEmpInfo.getEntityId(),entityEmpInfo.getEntityId());
    }
    
    protected void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityEntityTypeImpl entityType, List<KimEntityPhone> origPhones) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressPhone() || canOverrideEntityPrivacyPreferences(identityManagementPersonDocument.getPrincipalId()) ) {
			List<KimEntityPhone> entityPhones = new ArrayList<KimEntityPhone>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getPhones())){
				for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
					KimEntityPhoneImpl entityPhone = new KimEntityPhoneImpl();
					entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
					entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
					entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
					entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
					entityPhone.setPhoneNumber(phone.getPhoneNumber());
					entityPhone.setCountryCode(phone.getCountryCode());
					entityPhone.setExtension(phone.getExtension());
					entityPhone.setExtensionNumber(phone.getExtensionNumber());
					entityPhone.setActive(phone.isActive());
					entityPhone.setDefault(phone.isDflt());
					if(ObjectUtils.isNotNull(origPhones)){
						for (KimEntityPhone origPhone : origPhones) {
							if (origPhone.getEntityPhoneId()!=null && StringUtils.equals(origPhone.getEntityPhoneId(), entityPhone.getEntityPhoneId())) {
								entityPhone.setVersionNumber(((KimEntityPhoneImpl)origPhone).getVersionNumber());
							}
						}
					}
					entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
					entityPhones.add(entityPhone);
				}
			}
			entityType.setPhoneNumbers(entityPhones);
    	}
	}

    protected List<PersonDocumentPhone> loadPhones(String principalId, List<KimEntityPhone> entityPhones, boolean suppressDisplay ) {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
		if(ObjectUtils.isNotNull(entityPhones)){
			for (KimEntityPhone phone: entityPhones) {
				if(phone.isActive()){
					PersonDocumentPhone docPhone = new PersonDocumentPhone();
					docPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
					docPhone.setPhoneType(((KimEntityPhoneImpl)phone).getPhoneType());
					docPhone.setEntityTypeCode(phone.getEntityTypeCode());
					if ( !suppressDisplay || canOverrideEntityPrivacyPreferences(principalId) ) {
						docPhone.setPhoneNumber(phone.getPhoneNumberUnmasked());
						docPhone.setCountryCode(phone.getCountryCodeUnmasked());
						docPhone.setExtensionNumber(phone.getExtensionNumberUnmasked());
					} else {
						docPhone.setPhoneNumber(phone.getPhoneNumber());
						docPhone.setCountryCode(phone.getCountryCode());
						docPhone.setExtensionNumber(phone.getExtensionNumber());
					}
					docPhone.setActive(phone.isActive());
					docPhone.setDflt(phone.isDefault());
					docPhone.setEntityPhoneId(phone.getEntityPhoneId());
					docPhone.setEdit(true);
					docPhones.add(docPhone);
				}
			}
		}
		return docPhones;

	}

    protected void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			KimEntityEntityTypeImpl entityType, List<KimEntityEmail> origEmails) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressEmail() || canOverrideEntityPrivacyPreferences(identityManagementPersonDocument.getPrincipalId()) ) {
			List<KimEntityEmail> entityEmails = new ArrayList<KimEntityEmail>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getEmails())){
				for (PersonDocumentEmail email : identityManagementPersonDocument.getEmails()) {
					KimEntityEmailImpl entityEmail = new KimEntityEmailImpl();
					entityEmail.setEntityId(identityManagementPersonDocument.getEntityId());
					entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
					entityEmail.setEmailTypeCode(email.getEmailTypeCode());
					entityEmail.setEmailAddress(email.getEmailAddress());
					entityEmail.setActive(email.isActive());
					entityEmail.setDefault(email.isDflt());
					entityEmail.setEntityEmailId(email.getEntityEmailId());
					if(ObjectUtils.isNotNull(origEmails)){
						for (KimEntityEmail origEmail : origEmails) {
							if (origEmail.getEntityEmailId()!=null && StringUtils.equals(origEmail.getEntityEmailId(), entityEmail.getEntityEmailId())) {
								entityEmail.setVersionNumber(((KimEntityEmailImpl)origEmail).getVersionNumber());
							}
						}
					}
					entityEmails.add(entityEmail);
				}
			}
			entityType.setEmailAddresses(entityEmails);
    	}
	}
    protected List<PersonDocumentEmail> loadEmails(String principalId, List<KimEntityEmail> entityEmails, boolean suppressDisplay ) {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
		if(ObjectUtils.isNotNull(entityEmails)){
			for (KimEntityEmail email: entityEmails) {
				if(email.isActive()){
					PersonDocumentEmail docEmail = new PersonDocumentEmail();
					//docEmail.setEntityId(email.getEntityId());
					docEmail.setEntityTypeCode(email.getEntityTypeCode());
					docEmail.setEmailTypeCode(email.getEmailTypeCode());
					docEmail.setEmailType(((KimEntityEmailImpl)email).getEmailType());
					if ( !suppressDisplay || canOverrideEntityPrivacyPreferences(principalId) ) {
						docEmail.setEmailAddress(email.getEmailAddressUnmasked());
					} else {
						docEmail.setEmailAddress( email.getEmailAddress() );
					}					
					docEmail.setActive(email.isActive());
					docEmail.setDflt(email.isDefault());
					docEmail.setEntityEmailId(email.getEntityEmailId());
					docEmail.setEdit(true);
					emails.add(docEmail);
				}
			}
		}
		return emails;
	}

    protected void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			KimEntityEntityTypeImpl entityType, List<KimEntityAddress> origAddresses) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressAddress() || canOverrideEntityPrivacyPreferences(identityManagementPersonDocument.getPrincipalId()) ) {
			List<KimEntityAddress> entityAddresses = new ArrayList<KimEntityAddress>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAddrs())){
				for (PersonDocumentAddress address : identityManagementPersonDocument.getAddrs()) {
					KimEntityAddressImpl entityAddress = new KimEntityAddressImpl();
					entityAddress.setEntityId(identityManagementPersonDocument.getEntityId());
					entityAddress.setEntityTypeCode(entityType.getEntityTypeCode());
					entityAddress.setAddressTypeCode(address.getAddressTypeCode());
					entityAddress.setLine1(address.getLine1());
					entityAddress.setLine2(address.getLine2());
					entityAddress.setLine3(address.getLine3());
					entityAddress.setStateCode(address.getStateCode());
					entityAddress.setPostalCode(address.getPostalCode());
					entityAddress.setCountryCode(address.getCountryCode());
					entityAddress.setCityName(address.getCityName());
					entityAddress.setActive(address.isActive());
					entityAddress.setDefault(address.isDflt());
					entityAddress.setEntityAddressId(address.getEntityAddressId());
					if(ObjectUtils.isNotNull(origAddresses)){
						for (KimEntityAddress origAddress : origAddresses) {
							if (origAddress.getEntityAddressId()!=null && StringUtils.equals(origAddress.getEntityAddressId(), entityAddress.getEntityAddressId())) {
								entityAddress.setVersionNumber(((KimEntityAddressImpl)origAddress).getVersionNumber());
							}
						}
					}
					entityAddresses.add(entityAddress);
				}
			}
			entityType.setAddresses(entityAddresses);
    	}
	}

    protected List<PersonDocumentAddress> loadAddresses(String principalId, List<KimEntityAddress> entityAddresses, boolean suppressDisplay ) {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
		if(ObjectUtils.isNotNull(entityAddresses)){
			for (KimEntityAddress address: entityAddresses) {
				if(address.isActive()){
					PersonDocumentAddress docAddress = new PersonDocumentAddress();
					docAddress.setEntityTypeCode(address.getEntityTypeCode());
					docAddress.setAddressTypeCode(address.getAddressTypeCode());
					docAddress.setAddressType(((KimEntityAddressImpl)address).getAddressType());
					if ( !suppressDisplay || canOverrideEntityPrivacyPreferences(principalId) ) {
						docAddress.setLine1(address.getLine1Unmasked());
						docAddress.setLine2(address.getLine2Unmasked());
						docAddress.setLine3(address.getLine3Unmasked());
						docAddress.setStateCode(address.getStateCodeUnmasked());
						docAddress.setPostalCode(address.getPostalCodeUnmasked());
						docAddress.setCountryCode(address.getCountryCodeUnmasked());
						docAddress.setCityName(address.getCityNameUnmasked());
					} else {
						docAddress.setLine1(address.getLine1());
						docAddress.setLine2(address.getLine2());
						docAddress.setLine3(address.getLine3());
						docAddress.setStateCode(address.getStateCode());
						docAddress.setPostalCode(address.getPostalCode());
						docAddress.setCountryCode(address.getCountryCode());
						docAddress.setCityName(address.getCityName());
					}
					docAddress.setActive(address.isActive());
					docAddress.setDflt(address.isDefault());
					docAddress.setEntityAddressId(address.getEntityAddressId());
					docAddress.setEdit(true);
					docAddresses.add(docAddress);
				}
			}
		}
		return docAddresses;
	}

    protected List <GroupMemberImpl> populateGroupMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <GroupMemberImpl>  groupPrincipals = new ArrayList<GroupMemberImpl>();
//		List<? extends Group> origGroups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getGroups())){
			for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
				GroupMemberImpl groupPrincipalImpl = new GroupMemberImpl();
				groupPrincipalImpl.setGroupId(group.getGroupId());
				groupPrincipalImpl.setActiveFromDate(group.getActiveFromDate());
				groupPrincipalImpl.setActiveToDate(group.getActiveToDate());
				groupPrincipalImpl.setGroupMemberId(group.getGroupMemberId());
				groupPrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
				groupPrincipalImpl.setMemberTypeCode(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
				// get the ORM-layer optimisic locking value
				// TODO: this should be replaced with the retrieval and storage of that value
				// in the document tables and not re-retrieved here
				Collection<GroupMembershipInfo> currGroupMembers = getGroupService().getGroupMembersOfGroup(group.getGroupId());
				if(ObjectUtils.isNotNull(currGroupMembers)){
					for (GroupMembershipInfo origGroupMember: currGroupMembers) {
						if(origGroupMember.getGroupMemberId()!=null && StringUtils.equals(origGroupMember.getGroupMemberId(), group.getGroupMemberId())){
							groupPrincipalImpl.setVersionNumber(origGroupMember.getVersionNumber());
						}
					}
				}
	
				groupPrincipals.add(groupPrincipalImpl);
	
			}
		}
		return groupPrincipals;
	}

    protected List<RoleMemberImpl> populateRoleMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List<RoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleMemberImpl> roleMembers = new ArrayList<RoleMemberImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
				if(ObjectUtils.isNotNull(origRoles)){
					for (RoleImpl origRole : origRoles) {
						if (origRole.getRoleId()!=null && StringUtils.equals(origRole.getRoleId(), role.getRoleId())) {
							origRoleMembers = origRole.getMembers();
							break;
						}
					}
				}
				if (role.getRolePrncpls().isEmpty()) {
					if (!role.getDefinitions().isEmpty()) {
						RoleMemberImpl roleMemberImpl = new RoleMemberImpl();
						roleMemberImpl.setRoleId(role.getRoleId());
						roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
						roleMemberImpl.setMemberTypeCode(RoleImpl.PRINCIPAL_MEMBER_TYPE);
						roleMembers.add(roleMemberImpl);
					}
				} else {
					for (KimDocumentRoleMember roleMember : role.getRolePrncpls()) {
						RoleMemberImpl roleMemberImpl = new RoleMemberImpl();
						roleMemberImpl.setRoleId(role.getRoleId());
						// TODO : principalId is not ready here yet ?
						roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
						roleMemberImpl.setMemberTypeCode(RoleImpl.PRINCIPAL_MEMBER_TYPE);
						roleMemberImpl.setRoleMemberId(roleMember.getRoleMemberId());
						roleMemberImpl.setActiveFromDate(roleMember.getActiveFromDate());
						roleMemberImpl.setActiveToDate(roleMember.getActiveToDate());
						List<RoleMemberAttributeDataImpl> origAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
						if(ObjectUtils.isNotNull(origRoleMembers)){
							for (RoleMemberImpl origMember : origRoleMembers) {
								if (origMember.getRoleMemberId()!=null && StringUtils.equals(origMember.getRoleMemberId(), roleMember.getRoleMemberId())) {
									origAttributes = origMember.getAttributes();
									roleMemberImpl.setVersionNumber(origMember.getVersionNumber());
								}
							}
						}
						List<RoleMemberAttributeDataImpl> attributes = new ArrayList<RoleMemberAttributeDataImpl>();
						if(CollectionUtils.isNotEmpty(roleMember.getQualifiers())){
							for (KimDocumentRoleQualifier qualifier : roleMember.getQualifiers()) {
								//if (StringUtils.isNotBlank(qualifier.getAttrVal())) {
									RoleMemberAttributeDataImpl attribute = new RoleMemberAttributeDataImpl();
									attribute.setAttributeDataId(qualifier.getAttrDataId());
									attribute.setAttributeValue(qualifier.getAttrVal());
									attribute.setKimAttributeId(qualifier.getKimAttrDefnId());
									attribute.setRoleMemberId(qualifier.getRoleMemberId());
									attribute.setKimTypeId(qualifier.getKimTypId());
									if(ObjectUtils.isNotNull(origAttributes)){
										for (RoleMemberAttributeDataImpl origAttribute : origAttributes) {
											if (origAttribute.getAttributeDataId()!=null && StringUtils.equals(origAttribute.getAttributeDataId(), qualifier.getAttrDataId())) {
												attribute.setVersionNumber(origAttribute.getVersionNumber());
											}
										}
									}
									if (attribute.getVersionNumber() != null || StringUtils.isNotBlank(qualifier.getAttrVal())) {
										attributes.add(attribute);
									}
								//}
							}
						}
						roleMemberImpl.setAttributes(attributes);
						roleMembers.add(roleMemberImpl);
					}
				}
			}
		}
		return roleMembers;
	}

    @SuppressWarnings("unchecked")
	private List<KimDelegationImpl> getDelegations(String principalId){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KIMPropertyConstants.DelegationMember.MEMBER_ID, principalId);
		criteria.put( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
    }
    
	protected List<KimDelegationImpl> populateDelegations(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<KimDelegationImpl> origDelegations = getDelegations(identityManagementPersonDocument.getPrincipalId());
		List<KimDelegationImpl> kimDelegations = new ArrayList<KimDelegationImpl>();
		KimDelegationImpl newKimDelegation;
		KimDelegationImpl origDelegationImplTemp = null;
		List<KimDelegationMemberImpl> origMembers = new ArrayList<KimDelegationMemberImpl>();
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementPersonDocument.getDelegations()){
				newKimDelegation = new KimDelegationImpl();
				KimCommonUtils.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(roleDocumentDelegation.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(KimDelegationImpl origDelegationImpl: origDelegations){
						if((origDelegationImpl.getRoleId()!=null && StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId())) &&
								(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId()))){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
							origDelegationImplTemp = origDelegationImpl;
						}
					}
				}
				origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<KimDelegationMemberImpl>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, activatingInactive, newDelegationIdAssigned));
				kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}
	
    protected List <RoleMemberAttributeDataImpl> getBlankRoleMemberAttrs(List <RoleMemberImpl> rolePrncpls) {

		List <RoleMemberAttributeDataImpl>  blankRoleMemberAttrs = new ArrayList<RoleMemberAttributeDataImpl>();
		if(ObjectUtils.isNotNull(rolePrncpls)){
			for (RoleMemberImpl roleMbr : rolePrncpls) {
				List <RoleMemberAttributeDataImpl>  roleMemberAttrs = new ArrayList<RoleMemberAttributeDataImpl>();
				if (CollectionUtils.isNotEmpty(roleMbr.getAttributes())) {
					for (RoleMemberAttributeDataImpl attr : roleMbr.getAttributes()) {
						if (StringUtils.isBlank(attr.getAttributeValue())) {
							roleMemberAttrs.add(attr);
						}
					}
					if (!roleMemberAttrs.isEmpty()) {
						roleMbr.getAttributes().removeAll(roleMemberAttrs);
						blankRoleMemberAttrs.addAll(roleMemberAttrs);
					}
	
				}
			}
		}

		return blankRoleMemberAttrs;

	}

    protected List <RoleResponsibilityActionImpl> populateRoleRspActions(IdentityManagementPersonDocument identityManagementPersonDocument) {
//		List<RoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				if(CollectionUtils.isNotEmpty(role.getRolePrncpls())){
					for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
						if(CollectionUtils.isNotEmpty(roleMbr.getRoleRspActions())){
							for (KimDocumentRoleResponsibilityAction roleRspAction : roleMbr.getRoleRspActions()) {
								RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
								entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
								entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
								entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
								entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
								entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
								entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
								entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
								List<RoleResponsibilityActionImpl> actions = getRoleRspActions( roleMbr.getRoleMemberId());
								if(ObjectUtils.isNotNull(actions)){
									for(RoleResponsibilityActionImpl orgRspAction : actions) {
										if (orgRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(orgRspAction.getRoleResponsibilityActionId(), roleRspAction.getRoleResponsibilityActionId())) {
											entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
										}
									}
								}
								roleRspActions.add(entRoleRspAction);
							}
						}
					}
				}
			}
		}
		return roleRspActions;

	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}
	
	protected IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KIMServiceLocator.getIdentityService();
		}
		return identityService;
	}

	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

	protected DocumentHelperService getDocumentHelperService() {
	    if ( documentHelperService == null ) {
	        documentHelperService = KNSServiceLocator.getDocumentHelperService();
		}
	    return this.documentHelperService;
	}

	protected RoleService getRoleService() {
	   	if(roleService == null){
	   		roleService = KIMServiceLocator.getRoleService();
    	}
		return roleService;
	}

	protected RoleManagementService getRoleManagementService() {
	   	if(roleManagementService == null){
	   		roleManagementService = KIMServiceLocator.getRoleManagementService();
    	}
		return roleManagementService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	protected ResponsibilityService getResponsibilityService() {
	   	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public void setResponsibilityService(ResponsibilityService responsibilityService) {
		this.responsibilityService = responsibilityService;
	}


	/* Role document methods */
	@SuppressWarnings("unchecked")
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRoleInfo kimRole){
		KimRoleInfo kimRoleInfo = (KimRoleInfo)kimRole;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KIMPropertyConstants.Role.ROLE_ID, kimRoleInfo.getRoleId());
		RoleImpl kimRoleImpl = (RoleImpl)
			getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		identityManagementRoleDocument.setRoleId(kimRoleImpl.getRoleId());
		identityManagementRoleDocument.setKimType(kimRoleImpl.getKimRoleType());
		identityManagementRoleDocument.setRoleTypeName(kimRoleImpl.getKimRoleType().getName());
		identityManagementRoleDocument.setRoleTypeId(kimRoleImpl.getKimTypeId());
		identityManagementRoleDocument.setRoleName(kimRoleImpl.getRoleName());
		identityManagementRoleDocument.setActive(kimRoleImpl.isActive());
		identityManagementRoleDocument.setRoleNamespace(kimRoleImpl.getNamespaceCode());
		identityManagementRoleDocument.setEditing(true);
		
		identityManagementRoleDocument.setPermissions(loadPermissions((List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria)));
		identityManagementRoleDocument.setResponsibilities(loadResponsibilities((List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria)));
		loadResponsibilityRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setMembers(loadRoleMembers(identityManagementRoleDocument, kimRoleImpl.getMembers()));
		loadMemberRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setDelegations(loadRoleDocumentDelegations(identityManagementRoleDocument, getRoleDelegations(kimRoleImpl.getRoleId())));
		//Since delegation members are flattened out on the UI...
		setDelegationMembersInDocument(identityManagementRoleDocument);
		identityManagementRoleDocument.setKimType(kimRoleImpl.getKimRoleType());
	}

	public void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
			for(RoleDocumentDelegation delegation: identityManagementRoleDocument.getDelegations()){
				if(CollectionUtils.isNotEmpty(delegation.getMembers())){
					for(RoleDocumentDelegationMember member: delegation.getMembers()){
						member.setDelegationTypeCode(delegation.getDelegationTypeCode());
						identityManagementRoleDocument.getDelegationMembers().add(member);
					}
				}
			}
		}
	}

	protected List<KimDocumentRoleResponsibility> loadResponsibilities(List<RoleResponsibilityImpl> roleResponsibilities){
		List<KimDocumentRoleResponsibility> documentRoleResponsibilities = new ArrayList<KimDocumentRoleResponsibility>();
		if(ObjectUtils.isNotNull(roleResponsibilities)){
			for(RoleResponsibilityImpl roleResponsibility: roleResponsibilities){
				if(roleResponsibility.isActive()) {
					KimDocumentRoleResponsibility roleResponsibilityCopy = new KimDocumentRoleResponsibility();
					KimCommonUtils.copyProperties(roleResponsibilityCopy, roleResponsibility);
					roleResponsibilityCopy.setEdit(true);
					documentRoleResponsibilities.add(roleResponsibilityCopy);
				}
			}
		}
		return documentRoleResponsibilities;
	}

	protected List<KimDocumentRolePermission> loadPermissions(List<RolePermissionImpl> rolePermissions){
		List<KimDocumentRolePermission> documentRolePermissions = new ArrayList<KimDocumentRolePermission>();
		KimDocumentRolePermission rolePermissionCopy;
		if(ObjectUtils.isNotNull(rolePermissions)){
			for(RolePermissionImpl rolePermission: rolePermissions){
				if ( rolePermission.isActive() ) {
					rolePermissionCopy = new KimDocumentRolePermission();
					rolePermissionCopy.setRolePermissionId(rolePermission.getRolePermissionId());
					rolePermissionCopy.setRoleId(rolePermission.getRoleId());
					rolePermissionCopy.setPermissionId(rolePermission.getPermissionId());
					rolePermissionCopy.setKimPermission(rolePermission.getKimPermission().toSimpleInfo());
					rolePermissionCopy.setEdit(true);
					documentRolePermissions.add(rolePermissionCopy);
				}
			}
		}
		return documentRolePermissions;
	}

	protected static class RoleMemberNameComparator implements Comparator<KimDocumentRoleMember> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(KimDocumentRoleMember m1, KimDocumentRoleMember m2) {
			return m1.getMemberName().compareToIgnoreCase(m2.getMemberName());
		}
	}
	protected RoleMemberNameComparator roleMemberNameComparator = new RoleMemberNameComparator();
	
	protected List<KimDocumentRoleMember> loadRoleMembers(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> members){
		List<KimDocumentRoleMember> pndMembers = new ArrayList<KimDocumentRoleMember>();
		KimDocumentRoleMember pndMember = new KimDocumentRoleMember();
		if(ObjectUtils.isNotNull(members)){
			for(RoleMemberImpl member: members){
				pndMember = new KimDocumentRoleMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					pndMember.setRoleMemberId(member.getRoleMemberId());
					pndMember.setRoleId(member.getRoleId());
					pndMember.setMemberId(member.getMemberId());
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberTypeCode(member.getMemberTypeCode());
					pndMember.setQualifiers(loadRoleMemberQualifiers(identityManagementRoleDocument, member.getAttributes()));
					pndMember.setEdit(true);
					pndMembers.add(pndMember);
				}
			}
		}
		Collections.sort(pndMembers, roleMemberNameComparator);
		return pndMembers;
	}

	protected void loadResponsibilityRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility responsibility: identityManagementRoleDocument.getResponsibilities()){
				responsibility.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleResponsibilityActionImpls(responsibility.getRoleResponsibilityId())));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List<RoleResponsibilityActionImpl> getRoleResponsibilityActionImpls(String roleResponsibilityId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, "*");
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
		return (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	public List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActionImpls(String roleMemberId){
		Map<String, String> criteria = new HashMap<String, String>(1);		
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		return (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

	protected void loadMemberRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember member: identityManagementRoleDocument.getMembers()){
				member.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleMemberResponsibilityActionImpls(member.getRoleMemberId()) ) );
			}
		}
	}

	protected List<KimDocumentRoleResponsibilityAction> loadKimDocumentRoleRespActions(
			List<RoleResponsibilityActionImpl> roleRespActionImpls){
		List<KimDocumentRoleResponsibilityAction> documentRoleRespActions = new ArrayList<KimDocumentRoleResponsibilityAction>();
		KimDocumentRoleResponsibilityAction documentRoleRespAction;
		if(ObjectUtils.isNotNull(roleRespActionImpls)){
			for(RoleResponsibilityActionImpl roleRespActionImpl: roleRespActionImpls){
				documentRoleRespAction = new KimDocumentRoleResponsibilityAction();
				KimCommonUtils.copyProperties(documentRoleRespAction, roleRespActionImpl);
				// handle the roleResponsibility object being null since not all may be defined when ID value is "*"
				if ( ObjectUtils.isNotNull(roleRespActionImpl.getRoleResponsibility()) ) {
					documentRoleRespAction.setKimResponsibility(roleRespActionImpl.getRoleResponsibility().getKimResponsibility());
				}
				documentRoleRespActions.add(documentRoleRespAction);
			}
		}
		return documentRoleRespActions;
	}

    public BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = KimPrincipalImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = GroupImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.GROUP_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = RoleImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }

	public String getMemberName(String memberTypeCode, String memberId){
		if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId)) return "";
		BusinessObject member = getMember(memberTypeCode, memberId);
		return getMemberName(memberTypeCode, member);
	}

	public String getMemberNamespaceCode(String memberTypeCode, String memberId){
		if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId)) return "";
		BusinessObject member = getMember(memberTypeCode, memberId);
		return getMemberNamespaceCode(memberTypeCode, member);
	}

    public String getMemberIdByName(String memberTypeCode, String memberNamespaceCode, String memberName){
    	String memberId = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(memberName);
            if(principal!=null)
            	memberId = principal.getPrincipalId();
       } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	GroupInfo groupInfo = getIdentityManagementService().getGroupByName(memberNamespaceCode, memberName);
        	if(groupInfo!=null)
            memberId = groupInfo.getGroupId();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	memberId = getRoleManagementService().getRoleIdByName(memberNamespaceCode, memberName);
        }
        return memberId;
    }
    
    public String getMemberName(String memberTypeCode, BusinessObject member){
    	String roleMemberName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberName = ((KimPrincipalImpl)member).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberName = ((GroupImpl)member).getGroupName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberName = ((RoleImpl)member).getRoleName();
        }
        return roleMemberName;
    }

    public String getMemberNamespaceCode(String memberTypeCode, BusinessObject member){
    	String roleMemberNamespaceCode = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((GroupImpl)member).getNamespaceCode();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((RoleImpl)member).getNamespaceCode();
        }
        return roleMemberNamespaceCode;
    }

    protected List<KimDocumentRoleQualifier> loadRoleMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<RoleMemberAttributeDataImpl> attributeDataList){
		List<KimDocumentRoleQualifier> pndMemberRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndMemberRoleQualifier = new KimDocumentRoleQualifier();
		AttributeDefinitionMap origAttributes = identityManagementRoleDocument.getDefinitions();
		if ( origAttributes != null ) {
			for(String key: origAttributes.keySet()) {
				boolean attributePresent = false;
				String origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(attributeDataList!=null){
					for(RoleMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getKimAttributeId())){
							pndMemberRoleQualifier = new KimDocumentRoleQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setRoleMemberId(memberRoleQualifier.getRoleMemberId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new KimDocumentRoleQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
			}
		}
		return pndMemberRoleQualifiers;
	}

    @SuppressWarnings("unchecked")
	public List<KimDelegationImpl> getRoleDelegations(String roleId){
		if(roleId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put("roleId", roleId);
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
	}

    protected List<RoleDocumentDelegation> loadRoleDocumentDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationImpl> delegations){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		if(ObjectUtils.isNotNull(delegations)){
			for(KimDelegationImpl del: delegations){
				documentDelegation = new RoleDocumentDelegation();
				documentDelegation.setActive(del.isActive());
				if(documentDelegation.isActive()){
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(loadDelegationMembers(identityManagementRoleDocument, del.getMembers()));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		return delList;
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationMemberImpl> members){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember = new RoleDocumentDelegationMember();
		RoleMemberImpl roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(KimDelegationMemberImpl member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					KimCommonUtils.copyProperties(pndMember, member);
					pndMember.setRoleMemberId(member.getRoleMemberId());
					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementRoleDocument, member.getAttributes()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}

    protected RoleMemberImpl getRoleMemberForRoleMemberId(String roleMemberId){
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
    }
    
    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<KimDelegationMemberAttributeDataImpl> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
		AttributeDefinitionMap origAttributes = identityManagementRoleDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){
			for(String key: origAttributes.keySet()) {
				origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(attributeDataList!=null){
					for(KimDelegationMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getKimAttributeId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getDelegationMemberId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}
    
	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveRole(IdentityManagementRoleDocument identityManagementRoleDocument) {
		RoleImpl kimRole = new RoleImpl();
		Map<String, String> criteria = new HashMap<String, String>();
		String roleId = identityManagementRoleDocument.getRoleId();
		criteria.put(KIMPropertyConstants.Role.ROLE_ID, roleId);
		RoleImpl origRole = (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		
		List<RolePermissionImpl> origRolePermissions = new ArrayList<RolePermissionImpl>();
		List<RoleResponsibilityImpl> origRoleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
		List<KimDelegationImpl> origRoleDelegations = new ArrayList<KimDelegationImpl>();
		
		kimRole.setRoleId(identityManagementRoleDocument.getRoleId());
		kimRole.setKimTypeId(identityManagementRoleDocument.getRoleTypeId());
		kimRole.setNamespaceCode(identityManagementRoleDocument.getRoleNamespace());
		kimRole.setRoleName(identityManagementRoleDocument.getRoleName());

		if (origRole == null) {
			origRole = new RoleImpl();
			kimRole.setActive(true);
		} else {
			kimRole.setActive(identityManagementRoleDocument.isActive());
			kimRole.setVersionNumber(origRole.getVersionNumber());
			origRolePermissions = (List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria);
			origRoleResponsibilities = (List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
			origRoleMembers = (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
			origRoleDelegations = (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
		}

		if( getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()) == null ) {
			LOG.error( "Kim type not found for:"+identityManagementRoleDocument.getRoleTypeId(), new Throwable() );
		}

		List<PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();

		bos.add(kimRole);
		bos.addAll(getRolePermissions(identityManagementRoleDocument, origRolePermissions));
		bos.addAll(getRoleResponsibilities(identityManagementRoleDocument, origRoleResponsibilities));
		bos.addAll(getRoleResponsibilitiesActions(identityManagementRoleDocument));
		List<RoleMemberImpl> newRoleMembersList = getRoleMembers(identityManagementRoleDocument, origRoleMembers);
		bos.addAll(newRoleMembersList);
		bos.addAll(getRoleMemberResponsibilityActions(newRoleMembersList));
		//bos.addAll(getRoleMemberResponsibilityActions(identityManagementRoleDocument));
		bos.addAll(getRoleDelegations(identityManagementRoleDocument, origRoleDelegations));

		getBusinessObjectService().save(bos);
		IdentityManagementNotificationService service = (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(KimConstants.NAMESPACE_CODE, "IdentityManagementNotificationService"));
        service.roleUpdated();
		KIMServiceLocator.getResponsibilityInternalService().updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(identityManagementRoleDocument, origRoleResponsibilities));
		if(!kimRole.isActive()){
			// when a role is inactivated, inactivate the memberships of principals, groups, and roles in 
			// that role, delegations, and delegation members, and that roles memberships in other roles
			KIMServiceLocator.getRoleManagementService().roleInactivated(identityManagementRoleDocument.getRoleId());
		}
	}

	protected List<RolePermissionImpl> getRolePermissions(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RolePermissionImpl> origRolePermissions){
		List<RolePermissionImpl> rolePermissions = new ArrayList<RolePermissionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getPermissions())){
			for(KimDocumentRolePermission documentRolePermission: identityManagementRoleDocument.getPermissions()){
				RolePermissionImpl newRolePermission = new RolePermissionImpl();
				newRolePermission.setRolePermissionId(documentRolePermission.getRolePermissionId());
				newRolePermission.setRoleId(identityManagementRoleDocument.getRoleId());
				newRolePermission.setPermissionId(documentRolePermission.getPermissionId());
				newRolePermission.setActive( documentRolePermission.isActive() );
				
				newRolePermission.setActive(documentRolePermission.isActive());
				if(ObjectUtils.isNotNull(origRolePermissions)){
					for(RolePermissionImpl origPermissionImpl: origRolePermissions){
						if(!StringUtils.equals(origPermissionImpl.getRoleId(), newRolePermission.getRoleId()) &&
								StringUtils.equals(origPermissionImpl.getPermissionId(), newRolePermission.getPermissionId()) &&
								!origPermissionImpl.isActive() && newRolePermission.isActive()){
							newRolePermission.setRolePermissionId(origPermissionImpl.getRolePermissionId());
						}
						if(origPermissionImpl.getRolePermissionId()!=null && StringUtils.equals(origPermissionImpl.getRolePermissionId(), newRolePermission.getRolePermissionId())){
							newRolePermission.setVersionNumber(origPermissionImpl.getVersionNumber());
						}
					}
				}
				rolePermissions.add(newRolePermission);
			}
		}
		return rolePermissions;
	}

	protected List<RoleResponsibilityImpl> getRoleResponsibilities(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		List<RoleResponsibilityImpl> roleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		RoleResponsibilityImpl newRoleResponsibility;
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newRoleResponsibility = new RoleResponsibilityImpl();
				KimCommonUtils.copyProperties(newRoleResponsibility, documentRoleResponsibility);
				newRoleResponsibility.setActive(documentRoleResponsibility.isActive());
				newRoleResponsibility.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origRoleResponsibilities)){
					for(RoleResponsibilityImpl origResponsibilityImpl: origRoleResponsibilities){
						if(!StringUtils.equals(origResponsibilityImpl.getRoleId(), newRoleResponsibility.getRoleId()) &&
								StringUtils.equals(origResponsibilityImpl.getResponsibilityId(), newRoleResponsibility.getResponsibilityId()) &&
								!origResponsibilityImpl.isActive() && newRoleResponsibility.isActive()){
							newRoleResponsibility.setRoleResponsibilityId(origResponsibilityImpl.getRoleResponsibilityId());
						}
						if(origResponsibilityImpl.getRoleResponsibilityId()!=null && StringUtils.equals(origResponsibilityImpl.getRoleResponsibilityId(), newRoleResponsibility.getRoleResponsibilityId()))
							newRoleResponsibility.setVersionNumber(origResponsibilityImpl.getVersionNumber());
					}
				}
				roleResponsibilities.add(newRoleResponsibility);
			}
		}
		return roleResponsibilities;
	}


	protected List <RoleResponsibilityActionImpl> getRoleResponsibilitiesActions(
			IdentityManagementRoleDocument identityManagementRoleDocument){
		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
		// loop over the responsibilities assigned to the role
			for(KimDocumentRoleResponsibility roleResponsibility : identityManagementRoleDocument.getResponsibilities()){
				// only process if the actions are not assigned at the role member level
				if(!getResponsibilityService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())){
					List<KimDocumentRoleResponsibilityAction> documentRoleResponsibilityActions = roleResponsibility.getRoleRspActions();
					if( ObjectUtils.isNotNull(documentRoleResponsibilityActions) 
							&& !documentRoleResponsibilityActions.isEmpty() 
							&& StringUtils.isNotBlank(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId() ) ) {
						RoleResponsibilityActionImpl roleRspAction = new RoleResponsibilityActionImpl();
						roleRspAction.setRoleResponsibilityActionId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId());
						roleRspAction.setActionPolicyCode(documentRoleResponsibilityActions.get(0).getActionPolicyCode());
						roleRspAction.setActionTypeCode(documentRoleResponsibilityActions.get(0).getActionTypeCode());
						roleRspAction.setPriorityNumber(documentRoleResponsibilityActions.get(0).getPriorityNumber());
						roleRspAction.setForceAction(documentRoleResponsibilityActions.get(0).isForceAction());
						roleRspAction.setRoleMemberId("*");
						roleRspAction.setRoleResponsibilityId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityId());
						updateResponsibilityActionVersionNumber(roleRspAction, getRoleResponsibilityActionImpls(roleResponsibility.getRoleResponsibilityId()));
						roleRspActions.add(roleRspAction);
					}
				}
			}
		}
		return roleRspActions;
	}

	// FIXME: This should be pulling by the PK, not using another method which pulls multiple records and then finds
	// the right one here!
	protected void updateResponsibilityActionVersionNumber(RoleResponsibilityActionImpl newRoleRspAction,
			List<RoleResponsibilityActionImpl> origRoleRespActionImpls){
		if(ObjectUtils.isNotNull(origRoleRespActionImpls)){
			for(RoleResponsibilityActionImpl origRoleResponsibilityActionImpl: origRoleRespActionImpls){
				if(origRoleResponsibilityActionImpl.getRoleResponsibilityActionId()!=null && StringUtils.equals(origRoleResponsibilityActionImpl.getRoleResponsibilityActionId(), 
						newRoleRspAction.getRoleResponsibilityActionId())) {
					newRoleRspAction.setVersionNumber(origRoleResponsibilityActionImpl.getVersionNumber());
					break;
				}
			}
		}
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(List<RoleMemberImpl> newRoleMembersList){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(ObjectUtils.isNotNull(newRoleMembersList)){
			for(RoleMemberImpl roleMember: newRoleMembersList){
				roleRspActions.addAll(roleMember.getRoleRspActions());
			}
		}
		return roleRspActions;
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember roleMember: identityManagementRoleDocument.getMembers()){
				for(KimDocumentRoleResponsibilityAction roleRspAction : roleMember.getRoleRspActions()){
					RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
					entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
					entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
					entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
					entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
					entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
					entRoleRspAction.setForceAction(roleRspAction.isForceAction());
					entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
					List<RoleResponsibilityActionImpl> actions = getRoleRspActions(roleMember.getRoleMemberId());
					if(ObjectUtils.isNotNull(actions)){
						for(RoleResponsibilityActionImpl orgRspAction : actions) {
							if (orgRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(orgRspAction.getRoleResponsibilityActionId(), roleRspAction.getRoleResponsibilityActionId())) {
								entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
							}
						}
					}
					roleRspActions.add(entRoleRspAction);
				}
			}
		}
		return roleRspActions;
	}

	protected List<RoleMemberImpl> getRoleMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> origRoleMembers){
		List<RoleMemberImpl> roleMembers = new ArrayList<RoleMemberImpl>();
		RoleMemberImpl newRoleMember;
		RoleMemberImpl origRoleMemberImplTemp = null;
		List<RoleMemberAttributeDataImpl> origAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
		boolean activatingInactive = false;
		String newRoleMemberIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember documentRoleMember: identityManagementRoleDocument.getMembers()){
				newRoleMember = new RoleMemberImpl();
				KimCommonUtils.copyProperties(newRoleMember, documentRoleMember);
				newRoleMember.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origRoleMembers)){
					for(RoleMemberImpl origRoleMemberImpl: origRoleMembers){
						if((origRoleMemberImpl.getRoleId()!=null && StringUtils.equals(origRoleMemberImpl.getRoleId(), newRoleMember.getRoleId())) &&
							(origRoleMemberImpl.getMemberId()!=null && StringUtils.equals(origRoleMemberImpl.getMemberId(), newRoleMember.getMemberId())) &&
							!origRoleMemberImpl.isActive()){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newRoleMemberIdAssigned = newRoleMember.getRoleMemberId();
							newRoleMember.setRoleMemberId(origRoleMemberImpl.getRoleMemberId());
							activatingInactive = true;
						}
						if(origRoleMemberImpl.getRoleMemberId()!=null && StringUtils.equals(origRoleMemberImpl.getRoleMemberId(), newRoleMember.getRoleMemberId())){
							newRoleMember.setVersionNumber(origRoleMemberImpl.getVersionNumber());
							origRoleMemberImplTemp = origRoleMemberImpl;
						}
					}
				}
				origAttributes = (origRoleMemberImplTemp==null || origRoleMemberImplTemp.getAttributes()==null)?
									new ArrayList<RoleMemberAttributeDataImpl>():origRoleMemberImplTemp.getAttributes();
				newRoleMember.setAttributes(getRoleMemberAttributeData(documentRoleMember.getQualifiers(), origAttributes, activatingInactive, newRoleMemberIdAssigned));
				newRoleMember.setRoleRspActions(getRoleMemberResponsibilityActions(documentRoleMember, origRoleMemberImplTemp, activatingInactive, newRoleMemberIdAssigned));
				roleMembers.add(newRoleMember);
				activatingInactive = false;
			}
		}
		return roleMembers;
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(
			KimDocumentRoleMember documentRoleMember, RoleMemberImpl origRoleMemberImplTemp, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		List<RoleResponsibilityActionImpl> origActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(origRoleMemberImplTemp!=null) {
			origActions = getRoleRspActions(origRoleMemberImplTemp.getRoleMemberId());
		}
		if(CollectionUtils.isNotEmpty(documentRoleMember.getRoleRspActions())){
			for(KimDocumentRoleResponsibilityAction roleRspAction : documentRoleMember.getRoleRspActions()){
				RoleResponsibilityActionImpl newRoleRspAction = new RoleResponsibilityActionImpl();
				newRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
				newRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
				newRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
				newRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
				newRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
				newRoleRspAction.setForceAction(roleRspAction.isForceAction());
				newRoleRspAction.setRoleResponsibilityId("*");
				if(ObjectUtils.isNotNull(origActions)){
					for(RoleResponsibilityActionImpl origRspAction: origActions) {
						if(activatingInactive && StringUtils.equals(origRspAction.getRoleResponsibilityId(), newRoleRspAction.getRoleResponsibilityId()) &&
								StringUtils.equals(newRoleRspAction.getRoleMemberId(), newRoleMemberIdAssigned)){
							newRoleRspAction.setRoleMemberId(origRspAction.getRoleMemberId());
							newRoleRspAction.setRoleResponsibilityActionId(origRspAction.getRoleResponsibilityActionId());
						}
						if (origRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(origRspAction.getRoleResponsibilityActionId(), newRoleRspAction.getRoleResponsibilityActionId())) {
							newRoleRspAction.setVersionNumber(origRspAction.getVersionNumber());
						}
					}
				}
				roleRspActions.add(newRoleRspAction);
			}
		}
		return roleRspActions;
	}

	protected List<RoleMemberAttributeDataImpl> getRoleMemberAttributeData(List<KimDocumentRoleQualifier> qualifiers,
			List<RoleMemberAttributeDataImpl> origAttributes, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleMemberAttributeDataImpl> roleMemberAttributeDataList = new ArrayList<RoleMemberAttributeDataImpl>();
		RoleMemberAttributeDataImpl newRoleMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(KimDocumentRoleQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newRoleMemberAttributeData = new RoleMemberAttributeDataImpl();
					newRoleMemberAttributeData.setAttributeDataId(memberRoleQualifier.getAttrDataId());
					newRoleMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newRoleMemberAttributeData.setRoleMemberId(memberRoleQualifier.getRoleMemberId());
					newRoleMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newRoleMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(RoleMemberAttributeDataImpl origAttribute: origAttributes){
							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newRoleMemberAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newRoleMemberAttributeData.getRoleMemberId(), newRoleMemberIdAssigned)){
								newRoleMemberAttributeData.setRoleMemberId(origAttribute.getRoleMemberId());
								newRoleMemberAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
							}
							if(origAttribute.getAttributeDataId()!=null && StringUtils.equals(origAttribute.getAttributeDataId(), newRoleMemberAttributeData.getAttributeDataId())){
								newRoleMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					roleMemberAttributeDataList.add(newRoleMemberAttributeData);
				}
			}
		}
		return roleMemberAttributeDataList;
	}

	protected List<KimDelegationImpl> getRoleDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationImpl> origDelegations){
		List<KimDelegationImpl> kimDelegations = new ArrayList<KimDelegationImpl>();
		KimDelegationImpl newKimDelegation;
		KimDelegationImpl origDelegationImplTemp = null;
		List<KimDelegationMemberImpl> origMembers = new ArrayList<KimDelegationMemberImpl>();
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementRoleDocument.getDelegations()){
				newKimDelegation = new KimDelegationImpl();
				KimCommonUtils.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(KimDelegationImpl origDelegationImpl: origDelegations){
						if(StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId()) &&
								StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
							origDelegationImplTemp = origDelegationImpl;
						}
					}
				}
				origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<KimDelegationMemberImpl>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, activatingInactive, newDelegationIdAssigned));
				kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}

	protected List<KimDelegationMemberImpl> getDelegationMembers(List<RoleDocumentDelegationMember> delegationMembers,
			List<KimDelegationMemberImpl> origDelegationMembers, boolean activatingInactive, String newDelegationIdAssigned){
		List<KimDelegationMemberImpl> delegationsMembersList = new ArrayList<KimDelegationMemberImpl>();
		KimDelegationMemberImpl newDelegationMemberImpl;
		KimDelegationMemberImpl origDelegationMemberImplTemp = null;
		List<KimDelegationMemberAttributeDataImpl> origAttributes;
		String delegationMemberId = "";
		if(CollectionUtils.isNotEmpty(delegationMembers)){
			for(RoleDocumentDelegationMember delegationMember: delegationMembers){
				newDelegationMemberImpl = new KimDelegationMemberImpl();
				KimCommonUtils.copyProperties(newDelegationMemberImpl, delegationMember);
				if(ObjectUtils.isNotNull(origDelegationMembers)){
					for(KimDelegationMemberImpl origDelegationMember: origDelegationMembers){
						if(activatingInactive && StringUtils.equals(origDelegationMember.getMemberId(), newDelegationMemberImpl.getMemberId()) &&
								StringUtils.equals(newDelegationMemberImpl.getDelegationId(), newDelegationIdAssigned) &&
								!origDelegationMember.isActive()){
							newDelegationMemberImpl.setDelegationId(origDelegationMember.getDelegationId());
							delegationMemberId = newDelegationMemberImpl.getDelegationMemberId();
							newDelegationMemberImpl.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
						}
						if(origDelegationMember.getDelegationMemberId()!=null && StringUtils.equals(origDelegationMember.getDelegationMemberId(), newDelegationMemberImpl.getDelegationMemberId())){
							newDelegationMemberImpl.setVersionNumber(origDelegationMember.getVersionNumber());
							origDelegationMemberImplTemp = origDelegationMember;
						}
					}
				}
				origAttributes = (origDelegationMemberImplTemp==null || origDelegationMemberImplTemp.getAttributes()==null)?
						new ArrayList<KimDelegationMemberAttributeDataImpl>():origDelegationMemberImplTemp.getAttributes();
				newDelegationMemberImpl.setAttributes(getDelegationMemberAttributeData(delegationMember.getQualifiers(), origAttributes, activatingInactive, delegationMemberId));
				delegationsMembersList.add(newDelegationMemberImpl);
			}
		}
		return delegationsMembersList;
	}

	//TODO: implement logic same as role members - do not insert qualifiers with blank values
	protected List<KimDelegationMemberAttributeDataImpl> getDelegationMemberAttributeData(
			List<RoleDocumentDelegationMemberQualifier> qualifiers, List<KimDelegationMemberAttributeDataImpl> origAttributes,
			boolean activatingInactive, String delegationMemberId){
		List<KimDelegationMemberAttributeDataImpl> delegationMemberAttributeDataList = new ArrayList<KimDelegationMemberAttributeDataImpl>();
		KimDelegationMemberAttributeDataImpl newDelegationMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(RoleDocumentDelegationMemberQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newDelegationMemberAttributeData = new KimDelegationMemberAttributeDataImpl();
					newDelegationMemberAttributeData.setAttributeDataId(memberRoleQualifier.getAttrDataId());
					newDelegationMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newDelegationMemberAttributeData.setDelegationMemberId(memberRoleQualifier.getDelegationMemberId());
					newDelegationMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newDelegationMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(KimDelegationMemberAttributeDataImpl origAttribute: origAttributes){
							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newDelegationMemberAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newDelegationMemberAttributeData.getDelegationMemberId(), delegationMemberId)){
								newDelegationMemberAttributeData.setDelegationMemberId(origAttribute.getDelegationMemberId());
								newDelegationMemberAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
							}
							if(StringUtils.equals(origAttribute.getAttributeDataId(), newDelegationMemberAttributeData.getAttributeDataId())){
								newDelegationMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					delegationMemberAttributeDataList.add(newDelegationMemberAttributeData);
				}
			}
		}
		return delegationMemberAttributeDataList;
	}
	
	/* Group document methods */
	public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, GroupInfo groupInfo){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupInfo.getGroupId());
		GroupImpl kimGroupImpl = (GroupImpl)
			getBusinessObjectService().findByPrimaryKey(GroupImpl.class, criteria);
		identityManagementGroupDocument.setGroupId(kimGroupImpl.getGroupId());
		identityManagementGroupDocument.setKimType(kimGroupImpl.getKimTypeInfo());
		identityManagementGroupDocument.setGroupTypeName(kimGroupImpl.getKimTypeInfo().getName());
		identityManagementGroupDocument.setGroupTypeId(kimGroupImpl.getKimTypeInfo().getKimTypeId());
		identityManagementGroupDocument.setGroupName(kimGroupImpl.getGroupName());
		identityManagementGroupDocument.setActive(kimGroupImpl.isActive());
		identityManagementGroupDocument.setGroupNamespace(kimGroupImpl.getNamespaceCode());
		identityManagementGroupDocument.setMembers(loadGroupMembers(identityManagementGroupDocument, kimGroupImpl.getMembers()));
		identityManagementGroupDocument.setQualifiers(loadGroupQualifiers(identityManagementGroupDocument, kimGroupImpl.getGroupAttributes()));
		identityManagementGroupDocument.setKimType(kimGroupImpl.getKimTypeInfo());
		identityManagementGroupDocument.setEditing(true);
	}

	protected static class GroupMemberNameComparator implements Comparator<GroupDocumentMember> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(GroupDocumentMember m1, GroupDocumentMember m2) {
			return m1.getMemberName().compareToIgnoreCase(m2.getMemberName());
		}
	}

	protected GroupMemberNameComparator groupMemberNameComparator = new GroupMemberNameComparator();

	protected List<GroupDocumentMember> loadGroupMembers(
			IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberImpl> members){
		List<GroupDocumentMember> pndMembers = new ArrayList<GroupDocumentMember>();
		GroupDocumentMember pndMember = new GroupDocumentMember();
		if(ObjectUtils.isNotNull(members)){
			for(GroupMemberImpl member: members){
				pndMember = new GroupDocumentMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					pndMember.setGroupMemberId(member.getGroupMemberId());
					pndMember.setGroupId(member.getGroupId());
					pndMember.setMemberId(member.getMemberId());
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberTypeCode(member.getMemberTypeCode());
					pndMember.setEdit(true);
					pndMembers.add(pndMember);
				}
			}
		}
		Collections.sort(pndMembers, groupMemberNameComparator);
		return pndMembers;
	}

	protected List<GroupDocumentQualifier> loadGroupQualifiers(IdentityManagementGroupDocument IdentityManagementGroupDocument,
			List<GroupAttributeDataImpl> attributeDataList){
		List<GroupDocumentQualifier> pndGroupQualifiers = new ArrayList<GroupDocumentQualifier>();
		GroupDocumentQualifier pndGroupQualifier = new GroupDocumentQualifier();
		AttributeDefinitionMap origAttributes = IdentityManagementGroupDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){
			for(String key: origAttributes.keySet()) {
				origAttributeId = IdentityManagementGroupDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(CollectionUtils.isNotEmpty(attributeDataList)){
					for(GroupAttributeDataImpl groupQualifier: attributeDataList){
						if(origAttributeId!=null && ObjectUtils.isNotNull(groupQualifier.getKimAttribute()) && 
								StringUtils.equals(origAttributeId, groupQualifier.getKimAttribute().getKimAttributeId())){
							pndGroupQualifier = new GroupDocumentQualifier();
							KimCommonUtils.copyProperties(pndGroupQualifier, groupQualifier);
							pndGroupQualifier.setAttrDataId(groupQualifier.getAttributeDataId());
							pndGroupQualifier.setAttrVal(groupQualifier.getAttributeValue());
							pndGroupQualifier.setKimAttrDefnId(groupQualifier.getKimAttributeId());
							pndGroupQualifier.setKimTypId(groupQualifier.getKimTypeId());
							pndGroupQualifier.setGroupId(groupQualifier.getGroupId());
							pndGroupQualifiers.add(pndGroupQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndGroupQualifier = new GroupDocumentQualifier();
					pndGroupQualifier.setKimAttrDefnId(origAttributeId);
					pndGroupQualifiers.add(pndGroupQualifier);
				}
				attributePresent = false;
			}
		}
		return pndGroupQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument) {
		GroupImpl kimGroup = new GroupImpl();
		Map<String, String> criteria = new HashMap<String, String>();
		String groupId = identityManagementGroupDocument.getGroupId();
		criteria.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupId);
		GroupImpl origGroup = (GroupImpl)getBusinessObjectService().findByPrimaryKey(GroupImpl.class, criteria);
		List<GroupMemberImpl> origGroupMembers = new ArrayList<GroupMemberImpl>();
		if (ObjectUtils.isNull(origGroup)) {
			origGroup = new GroupImpl();
			kimGroup.setActive(true);
		} else {
			kimGroup.setVersionNumber(origGroup.getVersionNumber());
			//TODO: when a group is inactivated, inactivate the memberships of principals in that group 
			//and the memberships of that group in roles
			kimGroup.setActive(identityManagementGroupDocument.isActive());
			origGroupMembers = (List<GroupMemberImpl>)getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
		}

		kimGroup.setGroupId(identityManagementGroupDocument.getGroupId());
		KimTypeInfo kimType = getKimTypeInfoService().getKimType(identityManagementGroupDocument.getGroupTypeId());
		if( kimType == null ) {
			throw new RuntimeException("Kim type not found for:"+identityManagementGroupDocument.getGroupTypeId());
		}

		kimGroup.setKimTypeId(kimType.getKimTypeId());
		kimGroup.setNamespaceCode(identityManagementGroupDocument.getGroupNamespace());
		kimGroup.setGroupName(identityManagementGroupDocument.getGroupName());
		kimGroup.setGroupAttributes(getGroupAttributeData(identityManagementGroupDocument, origGroup.getGroupAttributes()));
		
		List<String> oldIds = null;
		List<String> newIds = null;
		List<PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();
		oldIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the actionList update
		bos.add(kimGroup);
		List<GroupMemberImpl> newGroupMembersList = getGroupMembers(identityManagementGroupDocument, origGroupMembers);
		bos.addAll(newGroupMembersList);

		getBusinessObjectService().save(bos);
		newIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the action list update

		// Do an async update of the action list for the updated groups
		KIMServiceLocator.getGroupInternalService().updateForWorkgroupChange(kimGroup.getGroupId(), oldIds, newIds);
		IdentityManagementNotificationService service = (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(KimConstants.NAMESPACE_CODE, "IdentityManagementNotificationService"));
        service.groupUpdated();
		if(!kimGroup.isActive()){
			// when a group is inactivated, inactivate the memberships of principals in that group 
			// and the memberships of that group in roles 
			KIMServiceLocator.getRoleService().groupInactivated(identityManagementGroupDocument.getGroupId());
		}

	}

	protected List<GroupMemberImpl> getGroupMembers(IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberImpl> origGroupMembers){
		List<GroupMemberImpl> groupMembers = new ArrayList<GroupMemberImpl>();
		GroupMemberImpl newGroupMember;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getMembers())){
			for(GroupDocumentMember documentGroupMember: identityManagementGroupDocument.getMembers()){
				newGroupMember = new GroupMemberImpl();
				KimCommonUtils.copyProperties(newGroupMember, documentGroupMember);
				newGroupMember.setGroupId(identityManagementGroupDocument.getGroupId());
				if(ObjectUtils.isNotNull(origGroupMembers)){
					for(GroupMemberImpl origGroupMemberImpl: origGroupMembers){
						if(StringUtils.equals(origGroupMemberImpl.getGroupId(), newGroupMember.getGroupId()) &&
								StringUtils.equals(origGroupMemberImpl.getMemberId(), newGroupMember.getMemberId()) &&
								!origGroupMemberImpl.isActive()){
							//TODO: verify if you want to add  && newGroupMember.isActive() condition to if...
							newGroupMember.setGroupMemberId(origGroupMemberImpl.getGroupMemberId());
						}
						if(origGroupMemberImpl.getGroupMemberId()!=null && StringUtils.equals(origGroupMemberImpl.getGroupMemberId(), newGroupMember.getGroupMemberId())){
							newGroupMember.setVersionNumber(origGroupMemberImpl.getVersionNumber());
						}
					}
				}
				groupMembers.add(newGroupMember);
			}
		}
		return groupMembers;
	}

	protected List<GroupAttributeDataImpl> getGroupAttributeData(IdentityManagementGroupDocument identityManagementGroupDocument,
			List<GroupAttributeDataImpl> origAttributes){
		List<GroupAttributeDataImpl> groupAttributeDataList = new ArrayList<GroupAttributeDataImpl>();
		GroupAttributeDataImpl newGroupAttributeData;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getQualifiers())){
			for(GroupDocumentQualifier groupQualifier: identityManagementGroupDocument.getQualifiers()){
				if(StringUtils.isNotBlank(groupQualifier.getAttrVal())){
					newGroupAttributeData = new GroupAttributeDataImpl();
					newGroupAttributeData.setAttributeDataId(groupQualifier.getAttrDataId());
					newGroupAttributeData.setAttributeValue(groupQualifier.getAttrVal());
					newGroupAttributeData.setGroupId(groupQualifier.getGroupId());
					newGroupAttributeData.setKimTypeId(groupQualifier.getKimTypId());
					newGroupAttributeData.setKimAttributeId(groupQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(GroupAttributeDataImpl origAttribute: origAttributes){
							if(StringUtils.equals(origAttribute.getKimAttributeId(), newGroupAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newGroupAttributeData.getGroupId(), origAttribute.getGroupId())){
							    newGroupAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
							}
							if(origAttribute.getAttributeDataId()!=null && StringUtils.equals(origAttribute.getAttributeDataId(), newGroupAttributeData.getAttributeDataId())){
							    newGroupAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					groupAttributeDataList.add(newGroupAttributeData);
				}
			}
		}
		return groupAttributeDataList;
	}

    @SuppressWarnings("unchecked")
	public KimDocumentRoleMember getKimDocumentRoleMember(String memberTypeCode, String memberId, String roleId){
    	if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId) || StringUtils.isEmpty(roleId))
    		return null;
    	KimDocumentRoleMember documentRoleMember = new KimDocumentRoleMember();
    	documentRoleMember.setRoleId(roleId);
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
    	criteria.put("mbr_id", memberId);

    	List<RoleMemberImpl> matchingRoleMembers = (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
    	if(matchingRoleMembers==null || matchingRoleMembers.size()<1) return null;

    	RoleMemberImpl roleMemberImpl = matchingRoleMembers.get(0);
    	documentRoleMember.setRoleMemberId(roleMemberImpl.getRoleMemberId());
    	BusinessObject member = getMember(memberTypeCode, memberId);
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
    		documentRoleMember.setMemberId(((KimPrincipalImpl)member).getPrincipalId());
    		documentRoleMember.setMemberName(((KimPrincipalImpl)member).getPrincipalName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	documentRoleMember.setMemberNamespaceCode(((GroupImpl)member).getNamespaceCode());
    		documentRoleMember.setMemberId(((GroupImpl)member).getGroupId());
    		documentRoleMember.setMemberName(((GroupImpl)member).getGroupName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	documentRoleMember.setMemberNamespaceCode(((RoleImpl)member).getNamespaceCode());
    		documentRoleMember.setMemberId(((RoleImpl)member).getRoleId());
    		documentRoleMember.setMemberName(((RoleImpl)member).getRoleName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
        }
    	return documentRoleMember;
    }

    protected Set<String> getChangedRoleResponsibilityIds(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		Set<String> lRet = new HashSet<String>();
		List<String> newResp = new ArrayList<String>();
		List<String> oldResp = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newResp.add(documentRoleResponsibility.getResponsibilityId());
			}
		}
		if(ObjectUtils.isNotNull(origRoleResponsibilities)){
			for(RoleResponsibilityImpl roleResp: origRoleResponsibilities){
				oldResp.add(roleResp.getResponsibilityId());
			}
		}
		lRet.addAll(newResp);
		lRet.addAll(oldResp);

		return lRet;
	}

	public KimTypeInfoService getKimTypeInfoService() {
		if ( kimTypeInfoService == null ) {
			kimTypeInfoService = KIMServiceLocator.getTypeInfoService();
		}
		return kimTypeInfoService;
	}
	
    public List<KimDocumentRoleMember> getRoleMembers(Map<String,String> fieldValues) {
		List<KimDocumentRoleMember> matchingRoleMembers = new ArrayList<KimDocumentRoleMember>();
		List<RoleMembershipInfo> matchingRoleMembersTemp = getRoleService().findRoleMembers(fieldValues);
		KimDocumentRoleMember matchingRoleMember;
		BusinessObject roleMemberObject;
		RoleMemberImpl roleMember;
		if(CollectionUtils.isNotEmpty(matchingRoleMembersTemp)){
			for(RoleMembershipInfo roleMembership: matchingRoleMembersTemp){
				roleMember = getRoleMember(roleMembership.getRoleMemberId());
				roleMemberObject = getMember(roleMember.getMemberTypeCode(), roleMember.getMemberId());
				matchingRoleMember = new KimDocumentRoleMember();
				KimCommonUtils.copyProperties(matchingRoleMember, roleMember);
				matchingRoleMember.setMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMemberObject));
				matchingRoleMember.setMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMemberObject));
				matchingRoleMember.setQualifiers(getQualifiers(roleMember.getAttributes()));
				matchingRoleMembers.add(matchingRoleMember);
			}
		}
		return matchingRoleMembers;
    }

    private List<KimDocumentRoleQualifier> getQualifiers(List<RoleMemberAttributeDataImpl> attributes){
    	if(attributes==null) return null;
    	List<KimDocumentRoleQualifier> qualifiers = new ArrayList<KimDocumentRoleQualifier>();
    	KimDocumentRoleQualifier qualifier;
    	if(ObjectUtils.isNotNull(attributes)){
	    	for(RoleMemberAttributeDataImpl attribute: attributes){
		    	qualifier = new KimDocumentRoleQualifier();
				qualifier.setAttrDataId(attribute.getAttributeDataId());
				qualifier.setAttrVal(attribute.getAttributeValue());
				qualifier.setRoleMemberId(attribute.getRoleMemberId());
				qualifier.setKimTypId(attribute.getKimTypeId());
				qualifier.setKimAttrDefnId(attribute.getKimAttributeId());
				qualifier.setKimAttribute(attribute.getKimAttribute());
				qualifiers.add(qualifier);
	    	}
    	}
    	return qualifiers;
    }
}