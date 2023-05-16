package com.adobe.aem.guides.wknd.core.services.impl;

import org.osgi.service.component.annotations.Component;

import com.adobe.aem.guides.wknd.core.services.PostMetadataService;

@Component(service = PostMetadataService.class)
public class PostMetadataServiceImpl implements PostMetadataService {

    @Override
    public int getTotalViews() {
        return 22101;
    }

}
