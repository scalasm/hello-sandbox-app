package me.marioscalasm.hsa.architecture.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface SelfDescribingException {
    String getMessage(MessageSource messageSource, Locale locale);
}
