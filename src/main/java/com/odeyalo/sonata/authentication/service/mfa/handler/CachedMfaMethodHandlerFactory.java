package com.odeyalo.sonata.authentication.service.mfa.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * MfaMethodHandlerFactory impl that caches the handlers on bootstrap
 */
@Service
public class CachedMfaMethodHandlerFactory implements MfaMethodHandlerFactory {
    private final Map<String, MfaMethodHandler> handlers;
    private final Logger logger = LoggerFactory.getLogger(CachedMfaMethodHandlerFactory.class);

    public CachedMfaMethodHandlerFactory(List<MfaMethodHandler> handlers) {
        this.handlers = handlers.stream().collect(toMap((handler) -> handler.supportedMfaType().name().toLowerCase(), Function.identity()));
        logger.info("Initialized handlers cache with: {} elements", handlers);
    }

    @Override
    public MfaMethodHandler getHandler(String type) {
        return handlers.get(type);
    }
}
