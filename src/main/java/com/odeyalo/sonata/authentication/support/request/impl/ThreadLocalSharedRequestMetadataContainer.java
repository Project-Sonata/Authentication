package com.odeyalo.sonata.authentication.support.request.impl;

import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadataContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * SharedRequestMetadataContainer implementation that uses ThreadLocal to store
 * {@link SharedRequestMetadata}
 *
 * @see ThreadLocal
 * @see SharedRequestMetadataContainer
 */
@Component
public class ThreadLocalSharedRequestMetadataContainer implements SharedRequestMetadataContainer {
    private final ThreadLocal<SharedRequestMetadata> holder = new ThreadLocal<>();

    @Override
    public void setSharedRequestMetadata(SharedRequestMetadata metadata) {
        Assert.notNull(metadata, "Metadata must be not null!");
        holder.set(metadata);
    }

    @Override
    public SharedRequestMetadata getCurrentRequest() {
        return holder.get();
    }
}
