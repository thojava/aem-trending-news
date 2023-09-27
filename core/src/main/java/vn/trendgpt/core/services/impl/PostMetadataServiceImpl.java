package vn.trendgpt.core.services.impl;

import org.osgi.service.component.annotations.Component;

import vn.trendgpt.core.services.PostMetadataService;

@Component(service = PostMetadataService.class)
public class PostMetadataServiceImpl implements PostMetadataService {

    @Override
    public int getTotalViews() {
        return 22101;
    }

}
