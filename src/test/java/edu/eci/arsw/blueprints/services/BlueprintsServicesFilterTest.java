package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filters.RedundancyFilter;
import edu.eci.arsw.blueprints.filters.UndersamplingFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlueprintsServicesFilterTest {

    @Mock
    BlueprintPersistence persistence;

    private static final Blueprint RAW_BP = new Blueprint("john", "shape",
            List.of(new Point(0, 0), new Point(0, 0), new Point(1, 1), new Point(2, 2)));

    @Test
    void redundancyProfileFiltersAllGets() throws Exception {
        BlueprintsServices services = new BlueprintsServices(persistence, new RedundancyFilter());
        when(persistence.getAllBlueprints()).thenReturn(Set.of(RAW_BP));
        when(persistence.getBlueprintsByAuthor(anyString())).thenReturn(Set.of(RAW_BP));
        when(persistence.getBlueprint(anyString(), anyString())).thenReturn(RAW_BP);

        var expected = List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2));

        assertThat(services.getAllBlueprints().iterator().next().getPoints()).isEqualTo(expected);
        assertThat(services.getBlueprintsByAuthor("john").iterator().next().getPoints()).isEqualTo(expected);
        assertThat(services.getBlueprint("john", "shape").getPoints()).isEqualTo(expected);
    }

    @Test
    void undersamplingProfileFiltersAllGets() throws Exception {
        BlueprintsServices services = new BlueprintsServices(persistence, new UndersamplingFilter());
        when(persistence.getAllBlueprints()).thenReturn(Set.of(RAW_BP));
        when(persistence.getBlueprintsByAuthor(anyString())).thenReturn(Set.of(RAW_BP));
        when(persistence.getBlueprint(anyString(), anyString())).thenReturn(RAW_BP);

        var expected = List.of(new Point(0, 0), new Point(1, 1));

        assertThat(services.getAllBlueprints().iterator().next().getPoints()).isEqualTo(expected);
        assertThat(services.getBlueprintsByAuthor("john").iterator().next().getPoints()).isEqualTo(expected);
        assertThat(services.getBlueprint("john", "shape").getPoints()).isEqualTo(expected);
    }
}
