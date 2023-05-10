package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.PostMetadata;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {PostMetadata.class},
        resourceType = {PostMetadataImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PostMetadataImpl implements PostMetadata {
    protected static final String RESOURCE_TYPE = "wknd/components/postmetadata";
    @Inject
    private Page currentPage;
    @SlingObject
    private ResourceResolver resourceResolver;
    @ValueMapValue(name = com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment.PN_PATH,
            injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    private ContentFragment contentFragment;

    @PostConstruct
    private void initModel() {
        if (StringUtils.isNotEmpty(fragmentPath)) {
            assert resourceResolver != null;
            Resource fragmentResource = resourceResolver.getResource(fragmentPath);
            if (fragmentResource != null) {
                this.contentFragment = fragmentResource.adaptTo(ContentFragment.class);
            }
        }
    }

    public String getName() {
        return contentFragment.getElement("name").getContent();
    }

    public String getAvatar() {
        return contentFragment.getElement("avatar").getContent();
    }

    public Date getPublishedDate() {
        assert currentPage != null;
        return ((Calendar) currentPage.getProperties().get("cq:lastReplicated")).getTime();
    }
    public boolean isEmpty() {
        return contentFragment == null;
    }
}
