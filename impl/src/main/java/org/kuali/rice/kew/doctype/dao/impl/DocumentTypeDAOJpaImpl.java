/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.doctype.dao.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.dao.DocumentTypeDAO;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.util.Utilities;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class DocumentTypeDAOJpaImpl implements DocumentTypeDAO {

	public static final Logger LOG = Logger.getLogger(DocumentTypeDAOJpaImpl.class);

	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;


	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void delete(DocumentType documentType) {
		DocumentType docType = findByDocId(documentType.getDocumentTypeId());
		entityManager.remove(documentType);
	}

	public DocumentType findByDocId(Long docId) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.eq("documentTypeId", docId);
		return (DocumentType) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
	}

	public DocumentType findByName(String name){
		return findByName(name, true); // by default find by name is case sensitive
	}

	public DocumentType findByName(String name, boolean caseSensitive) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		if(!caseSensitive){
			crit.like("UPPER(name)", ("%" + name.trim() + "%").toUpperCase());

		}else{
			crit.eq("name", name);
		}
		crit.eq("currentInd", Boolean.TRUE);
		try{
			DocumentType docType = (DocumentType) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
			return docType;
		}catch (javax.persistence.NoResultException e){
		   return null;
		}
	}

	public Integer getMaxVersionNumber(String docTypeName) {
		return getMostRecentDocType(docTypeName).getVersion();
	}

	public List getChildDocumentTypeIds(Long parentDocumentTypeId) {
		List childrenIds = new ArrayList();
		try {
			String sql = "select DOC_TYP_ID from KREW_DOC_TYP_T where CUR_IND = 1 and PARNT_ID = " + parentDocumentTypeId;
			Query query = entityManager.createNativeQuery(sql);
			List resultIds = query.getResultList();
			for (Object id:resultIds){
				childrenIds.add(new Long(id.toString()));
			}

		} catch (Exception e) {
			LOG.error("Error occured fetching children document type ids for document type " + parentDocumentTypeId, e);
			throw new RuntimeException(e);
		}

		return childrenIds;
	}

	public DocumentType getMostRecentDocType(String docTypeName) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.eq("name", docTypeName);
		crit.orderBy("version", false);

		Iterator docTypes = new QueryByCriteria(entityManager, crit).toQuery().getResultList().iterator();
		while (docTypes.hasNext()) {
			return (DocumentType) docTypes.next();
		}
		return null;
	}

	public void save(DocumentType documentType) {
		Collection<DocumentTypePolicy> docPolicies = documentType.getPolicies();
		documentType.setPolicies(new ArrayList<DocumentTypePolicy>());

		if (documentType.getDocumentTypeId() == null){
			entityManager.persist(documentType);
		} else {
			for(org.kuali.rice.kew.engine.node.Process process:(List<org.kuali.rice.kew.engine.node.Process>)documentType.getProcesses()){
				if(process.getInitialRouteNode().getRouteNodeId()==null){
					process.getInitialRouteNode().setDocumentTypeId(documentType.getDocumentTypeId());
					entityManager.persist(process.getInitialRouteNode());
				} else {
					OrmUtils.merge(entityManager, process.getInitialRouteNode());
				}
			}
			OrmUtils.merge(entityManager, documentType);
		}

		for (DocumentTypePolicy docTypePolicy:docPolicies){
			if (docTypePolicy.getDocumentTypeId() == null){
				docTypePolicy.setDocumentTypeId(documentType.getDocumentTypeId());
				entityManager.persist(docTypePolicy);
			} else {
				OrmUtils.merge(entityManager, docTypePolicy);
			}
		}

		documentType.setPolicies(docPolicies);
	}

	public List findByRouteHeaderId(Long routeHeaderId) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.eq("routeHeaderId", routeHeaderId);
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

	public Collection find(DocumentType documentType, DocumentType docTypeParent, boolean climbHierarchy) {
		LOG.debug("documentType: "+ documentType);
		LOG.debug("docTypeParent: "+ docTypeParent);
		LOG.debug("climbHierarchy: " + climbHierarchy);

		Criteria crit = new Criteria(DocumentType.class.getName());
		if (documentType != null && !Utilities.isEmpty(documentType.getLabel())) {
			crit.like("UPPER(label)", documentType.getLabel().trim().toUpperCase());
		}
		if (documentType != null && !Utilities.isEmpty(documentType.getName())) {
			String docTypeName = documentType.getName();
			crit.like("UPPER(name)", ("%" + docTypeName.trim() + "%").toUpperCase());
		}
		if (documentType != null && documentType.getActive() != null) {
			crit.eq("active", documentType.getActive());
		}
		if (documentType != null && documentType.getDocumentTypeId() != null) {
			crit.eq("documentTypeId", documentType.getDocumentTypeId());
		}
		if (documentType != null && documentType.getActualServiceNamespace() != null){
			crit.eq("serviceNamespace", documentType.getActualServiceNamespace());
		}
		if (docTypeParent != null) {
			if (!"".equals(docTypeParent.getName()) && docTypeParent.getName() != null) {
				Criteria parentCrit = new Criteria(DocumentType.class.getName());
				//addParentNameOrCriteria(docTypeParent.getName(), parentCrit);
				addParentIdOrCriteria(docTypeParent.getDocumentTypeId(), parentCrit);
				if (climbHierarchy) {
					assembleChildrenCriteria(docTypeParent.getChildrenDocTypes(), parentCrit);
				}
				parentCrit.eq("currentInd", Boolean.TRUE);
				crit.and(parentCrit);
			}
		} else {
			if (documentType != null && !Utilities.isEmpty(documentType.getName())) {
				DocumentType searchDocumentType = findByName(documentType.getName());
				if ((searchDocumentType != null) && climbHierarchy) {
					LOG.debug("searchDocumentType: "+ searchDocumentType);
					Criteria criteria = new Criteria(DocumentType.class.getName());
					//addParentNameOrCriteria(searchDocumentType.getName(), criteria);
                    addParentIdOrCriteria(searchDocumentType.getDocumentTypeId(), criteria);
                    assembleChildrenCriteria(searchDocumentType.getChildrenDocTypes(), criteria);
					criteria.eq("currentInd", Boolean.TRUE);
					crit.or(criteria);
				}
			}
		}
		crit.eq("currentInd", Boolean.TRUE);
		return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

    private void addParentIdOrCriteria(Long parentId, Criteria mainCriteria) {
        Criteria parentCriteria = new Criteria(DocumentType.class.getName());
        parentCriteria.eq("docTypeParentId", parentId);
        mainCriteria.or(parentCriteria);
    }

	private void assembleChildrenCriteria(Collection childrenDocTypes, Criteria crit) {
		if (childrenDocTypes != null) {
			Iterator childrenDocTypesIter = childrenDocTypes.iterator();
			while (childrenDocTypesIter.hasNext()) {
				DocumentType child = (DocumentType) childrenDocTypesIter.next();
				addParentIdOrCriteria(child.getDocumentTypeId(), crit);
				assembleChildrenCriteria(child.getChildrenDocTypes(), crit);
			}
		}
	}

	public DocumentType getMostRecentDocType(Long documentTypeId) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.eq("documentTypeId", documentTypeId);
		crit.orderBy("version", false);

		Iterator docTypes = new QueryByCriteria(entityManager, crit).toQuery().getResultList().iterator();
		while (docTypes.hasNext()) {
			return (DocumentType) docTypes.next();
		}
		return null;
	}

	public List findAllCurrentRootDocuments() {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.isNull("docTypeParentId");
		return findAllCurrent(crit);
	}

    public List findAllCurrent() {
    	return findAllCurrent(new Criteria(DocumentType.class.getName()));
    }

    public List findAllCurrentByName(String name) {
		Criteria crit = new Criteria(DocumentType.class.getName());
		crit.eq("name", name);
		return findAllCurrent(crit);
    }

    public List<DocumentType> findPreviousInstances(String name) {
        Criteria crit = new Criteria(DocumentType.class.getName());
        crit.eq("name", name);
        crit.eq("currentInd", Boolean.FALSE);
        return findAll(crit);
    }

    private List findAllCurrent(Criteria crit) {
        crit.eq("currentInd", Boolean.TRUE);
        return findAll(crit);
    }

    private List findAll(Criteria crit) {
        return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public List findDocumentTypeAttributes(RuleAttribute ruleAttribute) {
    	Criteria crit = new Criteria(DocumentTypeAttribute.class.getName());
    	crit.eq("ruleAttributeId", ruleAttribute.getRuleAttributeId());
    	return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public Long findDocumentTypeIdByDocumentId(Long documentId) {
    	//FIXME: I don't think this does what it's supposed to
    	Criteria crit = new Criteria(DocumentType.class.getName());
    	crit.eq("routeHeaderId", documentId);

    	List<DocumentType> docTypes = new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    	for (DocumentType docType:docTypes){
    	    return docType.getDocumentTypeId();
    	}
    	return null;
    }

}
