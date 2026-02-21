package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RedundancyFilterTest {

    private final RedundancyFilter filter = new RedundancyFilter();

    @Test
    void returnsSameBlueprintWhenEmpty() {
        Blueprint blueprint = new Blueprint("author", "empty", List.of());

        Blueprint result = filter.apply(blueprint);

        assertSame(blueprint, result);
        assertEquals(List.of(), result.getPoints());
    }

    @Test
    void keepsSinglePointBlueprintUnchanged() {
        Blueprint blueprint = new Blueprint("author", "single", List.of(new Point(1, 1)));

        Blueprint result = filter.apply(blueprint);

        assertEquals(List.of(new Point(1, 1)), result.getPoints());
    }

    @Test
    void removesConsecutiveDuplicatesOnly() {
        Blueprint blueprint = new Blueprint("author", "dup-consecutive", List.of(
                new Point(0, 0),
                new Point(0, 0),
                new Point(1, 1),
                new Point(1, 1),
                new Point(2, 2)
        ));

        Blueprint result = filter.apply(blueprint);

        assertEquals(List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2)), result.getPoints());
    }

    @Test
    void keepsNonConsecutiveDuplicates() {
        Blueprint blueprint = new Blueprint("author", "dup-non-consecutive", List.of(
                new Point(0, 0),
                new Point(1, 1),
                new Point(0, 0)
        ));

        Blueprint result = filter.apply(blueprint);

        assertEquals(blueprint.getPoints(), result.getPoints());
    }
}
