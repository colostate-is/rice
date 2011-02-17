package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.core.service.KualiConfigurationService;

import javax.persistence.EntityManagerFactory;

public class KNSServiceLocator {
    public static final String ATTACHMENT_SERVICE = "attachmentService";
    public static final String PERSISTENCE_SERVICE = "persistenceService";
    public static final String PERSISTENCE_STRUCTURE_SERVICE = "persistenceStructureService";
    public static final String NOTE_SERVICE = "noteService";
    public static final String BUSINESS_OBJECT_SERVICE = "businessObjectService";
    public static final String KUALI_CONFIGURATION_SERVICE = "kualiConfigurationService";
    public static final String PARAMETER_SERVICE = "parameterService";
    public static final String CLIENT_PARAMETER_SERVICE = "clientParameterService";
    public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
    public static final String APPLICATION_ENTITY_MANAGER_FACTORY = "knsApplicationEntityManagerFactory";
    public static final String XML_OBJECT_SERIALIZER_SERVICE = "xmlObjectSerializerService";
    public static final String SERIALIZER_SERVICE = "businessObjectSerializerService";
    public static final String SEQUENCE_ACCESSOR_SERVICE = "sequenceAccessorService";
    public static final String KEY_VALUES_SERVICE = "keyValuesService";


    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static AttachmentService getAttachmentService() {
        return getService(ATTACHMENT_SERVICE);
    }

    public static PersistenceService getPersistenceService() {
        return getService(PERSISTENCE_SERVICE);
    }

    public static PersistenceStructureService getPersistenceStructureService() {
        return getService(PERSISTENCE_STRUCTURE_SERVICE);
    }

    public static DateTimeService getDateTimeService() {
        return getService(CoreConstants.Services.DATETIME_SERVICE);
    }

    public static NoteService getNoteService() {
        return getService(NOTE_SERVICE);
    }

    public static BusinessObjectService getBusinessObjectService() {
        return getService(BUSINESS_OBJECT_SERVICE);
    }

    public static final EncryptionService getEncryptionService() {
        return getService(CoreConstants.Services.ENCRYPTION_SERVICE);
    }

    public static KualiConfigurationService getKualiConfigurationService() {
        return getService(KUALI_CONFIGURATION_SERVICE);
    }

    public static ClientParameterService getClientParameterService() {
        return getService(CLIENT_PARAMETER_SERVICE);
    }

    public static ParameterService getParameterService() {
        return getService(PARAMETER_SERVICE);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return getService(ENTITY_MANAGER_FACTORY);
    }

    public static EntityManagerFactory getApplicationEntityManagerFactory() {
        return getService(APPLICATION_ENTITY_MANAGER_FACTORY);
    }

    public static XmlObjectSerializerService getXmlObjectSerializerService() {
        return getService(XML_OBJECT_SERIALIZER_SERVICE);
    }

    public static BusinessObjectSerializerService getBusinessObjectSerializerService() {
        return getService(SERIALIZER_SERVICE);
    }

    public static SequenceAccessorService getSequenceAccessorService() {
        return getService(SEQUENCE_ACCESSOR_SERVICE);
    }

    public static KeyValuesService getKeyValuesService() {
        return getService(KEY_VALUES_SERVICE);
    }
}