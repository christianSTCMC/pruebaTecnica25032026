package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error;

import com.accenture.franquicias.sucursal.application.service.FranquiciaNoEncontradaException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.Instant;

/**
 * Centraliza respuestas de error 400 y 404 con formato homogeneo.
 */
@RestControllerAdvice
@Order(-2)
public class GlobalExceptionHandler {

    private static final String MENSAJE_SOLICITUD_INVALIDA = "Solicitud invalida";

    /**
     * Maneja errores de validacion de request body para responder 400.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorValidacion(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {
        String mensaje = ex.getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(MENSAJE_SOLICITUD_INVALIDA);

        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje, exchange);
    }

    /**
     * Maneja errores de conversion (por ejemplo UUID invalido en path).
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorEntrada(
            ServerWebInputException ex,
            ServerWebExchange exchange) {
        String mensaje = ex.getReason() == null ? MENSAJE_SOLICITUD_INVALIDA : MENSAJE_SOLICITUD_INVALIDA;
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje, exchange);
    }

    /**
     * Maneja validaciones de negocio lanzadas como IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorNegocio(
            IllegalArgumentException ex,
            ServerWebExchange exchange) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    /**
     * Maneja el caso de franquicia inexistente al crear una sucursal.
     */
    @ExceptionHandler(FranquiciaNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarFranquiciaNoEncontrada(
            FranquiciaNoEncontradaException ex,
            ServerWebExchange exchange) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    private ResponseEntity<ApiErrorResponse> construirRespuesta(
            HttpStatus httpStatus,
            String mensaje,
            ServerWebExchange exchange) {
        ApiErrorResponse error = new ApiErrorResponse(
                Instant.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                mensaje,
                exchange.getRequest().getPath().value()
        );
        return ResponseEntity.status(httpStatus).body(error);
    }
}
