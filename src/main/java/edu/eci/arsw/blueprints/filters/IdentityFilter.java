package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Predeterminado: devuelve el blueprint sin cambios.
 * Esto coincide con el comportamiento base del laboratorio de referencia antes de que los estudiantes implementen filtros personalizados.
 */
@Component
@Profile("!redundancy & !undersampling") // only active when no custom filter profile is enabled
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint bp) { return bp; }
}
