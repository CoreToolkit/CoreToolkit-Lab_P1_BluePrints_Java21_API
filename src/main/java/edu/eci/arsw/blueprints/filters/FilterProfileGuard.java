package edu.eci.arsw.blueprints.filters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Falla rápido si ambos perfiles de filtro están activos simultáneamente.
 * Política: no elegir uno silenciosamente para evitar forma de datos inesperada.
 */
@Configuration
@Profile("redundancy & undersampling")
public class FilterProfileGuard {

    @Bean
    public BlueprintsFilter conflictingFilter() {
        throw new IllegalStateException(
                "Profiles 'redundancy' and 'undersampling' cannot be active together. Choose one.");
    }
}
