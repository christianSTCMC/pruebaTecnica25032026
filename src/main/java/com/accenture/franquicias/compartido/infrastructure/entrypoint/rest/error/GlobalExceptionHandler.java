package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error;

import com.accenture.franquicias.producto.application.service.ProductoDuplicadoEnSucursalException;
import com.accenture.franquicias.producto.application.service.ProductoNoEncontradoException;
import com.accenture.franquicias.producto.application.service.ProductoNoPerteneceASucursalException;
import com.accenture.franquicias.producto.application.service.SucursalNoEncontradaException;
import com.accenture.franquicias.sucursal.application.service.FranquiciaNoEncontradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String MENSAJE_SOLICITUD_INVALIDA = "Solicitud invalida";
    private static final String MENSAJE_ERROR_INTERNO = "Error interno del servidor";

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
        // Se evita filtrar detalles tecnicos al cliente y se conserva traza de depuracion.
        LOGGER.debug("Error de conversion o parseo en la solicitud: {}", ex.getReason());
        return construirRespuesta(HttpStatus.BAD_REQUEST, MENSAJE_SOLICITUD_INVALIDA, exchange);
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
     * Maneja faltantes de recursos de negocio con respuesta 404.
     */
    @ExceptionHandler({
            FranquiciaNoEncontradaException.class,
            SucursalNoEncontradaException.class,
            ProductoNoEncontradoException.class
    })
    public ResponseEntity<ApiErrorResponse> manejarRecursoNoEncontrado(
            RuntimeException ex,
            ServerWebExchange exchange) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    /**
     * Maneja conflictos de negocio como duplicidad o pertenencia invalida.
     */
    @ExceptionHandler({
            ProductoDuplicadoEnSucursalException.class,
            ProductoNoPerteneceASucursalException.class
    })
    public ResponseEntity<ApiErrorResponse> manejarConflictoNegocio(
            RuntimeException ex,
            ServerWebExchange exchange) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    /**
     * Maneja errores no controlados para responder 500 con formato homogeneo.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorInesperado(
            Exception ex,
            ServerWebExchange exchange) {
        LOGGER.error("Error no controlado en la capa HTTP", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_ERROR_INTERNO, exchange);
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
