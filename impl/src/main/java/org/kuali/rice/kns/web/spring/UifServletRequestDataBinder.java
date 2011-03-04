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
package org.kuali.rice.kns.web.spring;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.service.ViewService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * This is class is overridden in order to hook in the UifBeanPropertyBindingResult
 * which instantiates a custom BeanWrapperImpl. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifServletRequestDataBinder extends ServletRequestDataBinder {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifServletRequestDataBinder.class);

	private UifBeanPropertyBindingResult bindingResult;
	private ConversionService conversionService;
	
	protected ViewService viewService;


	public UifServletRequestDataBinder(Object target) {
        super(target);
    }
	
	public UifServletRequestDataBinder(Object target, String name) {
        super(target, name);
    }
	
    /**
     * This overridden method allows for a custom binding result class.
     * 
     * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
     */
    @Override
	public void initBeanPropertyAccess() {
		Assert.state(this.bindingResult == null,
				"DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
		this.bindingResult = new UifBeanPropertyBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths());
		if (this.conversionService != null) {
			this.bindingResult.initConversion(this.conversionService);
		}
	}

    /**
     * This overridden method allows for the setting attributes to use to find the data dictionary data from Kuali
     * 
     * @see org.springframework.validation.DataBinder#getInternalBindingResult()
     */
    @Override
	protected AbstractPropertyBindingResult getInternalBindingResult() {
		if (this.bindingResult == null) {
			initBeanPropertyAccess();
		}
		return this.bindingResult;
	}

	/**
     * This overridden method disallows direct field access for Kuali.
     * 
     * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
     */
    @Override
	public void initDirectFieldAccess() {
    	LOG.error("Direct Field access is not allowed in UifServletRequestDataBinder.");
		throw new RuntimeException("Direct Field access is not allowed in Kuali");
	}

    @Override
    @SuppressWarnings("unchecked")
    public void bind(ServletRequest request) {
        super.bind(request);
        UifFormBase form = (UifFormBase) this.getTarget();
        
        // create initial view, should only happen if this is the first request
        if(form.getView() == null) {
            String viewId = request.getParameter(UifParameters.VIEW_ID);
            if (viewId != null) {
                form.setView(getViewService().getView(viewId, request.getParameterMap()));
            }
            else {
                String viewTypeName = request.getParameter(UifParameters.VIEW_TYPE_NAME);
                if(viewTypeName == null) {
                    viewTypeName = form.getViewTypeName();
                }
                
                form.setView(getViewService().getViewByType(viewTypeName,
                        WebUtils.translateRequestParameterMap(request.getParameterMap())));
            }
            
            form.setViewId(form.getView().getId());
        }
        
        form.postBind((HttpServletRequest)request);
    }
    
    public ViewService getViewService() {
        if(viewService == null) {
            viewService = KNSServiceLocator.getViewService();
        }
        return this.viewService;
    }

    public void setViewService(ViewService viewService) {
        this.viewService = viewService;
    }

}
