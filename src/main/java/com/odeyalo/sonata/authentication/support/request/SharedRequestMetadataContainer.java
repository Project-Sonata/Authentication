package com.odeyalo.sonata.authentication.support.request;

/**
 * An extension to {@link SharedRequestMetadataProvider} that add ability to set current {@link SharedRequestMetadata}
 * NOTE: Implementations must be thread-safe
 *
 * @see SharedRequestMetadataProvider
 * @see SharedRequestMetadata
 */
public interface SharedRequestMetadataContainer extends SharedRequestMetadataProvider {
    /**
     * Set the current {@link SharedRequestMetadata}
     * @param metadata - metadata to set. Never null
     */
    void setSharedRequestMetadata(SharedRequestMetadata metadata);
}
