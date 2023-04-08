package com.odeyalo.sonata.authentication.support.request;

/**
 * Interface that provide ability to get current {@link SharedRequestMetadata} for current thread.
 *
 * Note: The implementations must be thread-safe
 */
public interface SharedRequestMetadataProvider {
    /**
     * Return current SharedRequestMetadata
     * @return - current {@link SharedRequestMetadata}, null otherwise
     */
    SharedRequestMetadata getCurrentRequest();
}
