package vn.trendgpt.core.models;

import java.util.Date;

public interface PostMetadata {
    String getName();
    String getAvatar();
    Date getPublishedDate();
    boolean isEmpty();
    int getTotalViews();
}
