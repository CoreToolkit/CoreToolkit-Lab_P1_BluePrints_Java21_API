package edu.eci.arsw.blueprints.filters;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Fail-fast guard: si se activan simultaneamente los perfiles "redundancy" y "undersampling"
 * se aborta el arranque para evitar elegir un filtro ambiguo.
 */
@Component
@Profile("redundancy & undersampling")
public class FilterProfileGuard {

    private static final Logger log = LoggerFactory.getLogger(FilterProfileGuard.class);

    @PostConstruct
    public void ensureSingleFilterProfile() {
        String msg = "Profiles 'redundancy' and 'undersampling' are mutually exclusive; enable only one";
        log.error(msg);
        throw new IllegalStateException(msg);
    }
}
