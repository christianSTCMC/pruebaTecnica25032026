package com.accenture.franquicias.compartido.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * Registra hitos operativos del ciclo de vida para dejar trazabilidad de arranque
 * y parada del servicio sin invadir las capas de dominio o aplicacion.
 */
@Component
public class ApplicationLifecycleLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("backend.api.lifecycle");

    private final Environment environment;

    public ApplicationLifecycleLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationReady() {
        LOGGER.info(
                "event=application_ready service={} profiles={} port={}",
                resolveApplicationName(),
                resolveProfiles(),
                resolveServerPort());
    }

    @EventListener(ContextClosedEvent.class)
    public void logApplicationStopping() {
        LOGGER.info(
                "event=application_stopping service={} profiles={}",
                resolveApplicationName(),
                resolveProfiles());
    }

    private String resolveApplicationName() {
        return environment.getProperty("spring.application.name", "api-franquicias");
    }

    private String resolveProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return "default";
        }

        return String.join(",", activeProfiles);
    }

    private String resolveServerPort() {
        return environment.getProperty("local.server.port", environment.getProperty("server.port", "8080"));
    }
}
