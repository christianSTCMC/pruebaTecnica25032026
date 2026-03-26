package com.accenture.franquicias.compartido.infrastructure.logging;

import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * Registra una sola linea por request HTTP con contexto util para operacion:
 * request id, metodo, ruta, estado y latencia.
 */
@Component
public class RequestLoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger("backend.api.http");
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final int DEFAULT_SUCCESS_STATUS = 200;
    private static final int DEFAULT_ERROR_STATUS = 500;
    private static final int CLIENT_CLOSED_REQUEST_STATUS = 499;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = resolveRequestId(exchange.getRequest().getHeaders());
        long startedAt = System.nanoTime();

        exchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long durationInMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
                    int statusCode = resolveStatusCode(exchange, signalType);

                    LOGGER.info(
                            "requestId={} method={} path={} status={} durationMs={} signal={}",
                            requestId,
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath().pathWithinApplication().value(),
                            statusCode,
                            durationInMillis,
                            signalType);
                });
    }

    /**
     * WebFlux puede dejar el codigo HTTP en null cuando la respuesta termina con
     * el valor por defecto 200. Este fallback evita registrar un estado 0.
     */
    private int resolveStatusCode(ServerWebExchange exchange, reactor.core.publisher.SignalType signalType) {
        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
        if (statusCode != null) {
            return statusCode.value();
        }

        return switch (signalType) {
            case ON_COMPLETE -> DEFAULT_SUCCESS_STATUS;
            case CANCEL -> CLIENT_CLOSED_REQUEST_STATUS;
            default -> DEFAULT_ERROR_STATUS;
        };
    }

    private String resolveRequestId(HttpHeaders headers) {
        String incomingRequestId = headers.getFirst(REQUEST_ID_HEADER);
        if (StringUtils.hasText(incomingRequestId)) {
            return incomingRequestId;
        }

        return UUID.randomUUID().toString();
    }
}
