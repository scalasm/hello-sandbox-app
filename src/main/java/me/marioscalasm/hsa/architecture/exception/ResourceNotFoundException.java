package me.marioscalasm.hsa.architecture.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends ApplicationException {

    private final Class<?> resourceClass;

    private final Object resourceId;

    public ResourceNotFoundException(Class<?> resourceClass, Object resourceId) {
        super("exception.resource_not_found");
        this.resourceClass = resourceClass;
        this.resourceId = resourceId;
    }
}
