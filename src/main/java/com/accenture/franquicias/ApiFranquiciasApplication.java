package com.accenture.franquicias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio.
 * Mantiene el arranque aislado para que las siguientes fases agreguen
 * adaptadores, casos de uso y persistencia sin acoplarse al framework.
 */
@SpringBootApplication
public class ApiFranquiciasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiFranquiciasApplication.class, args);
    }
}
