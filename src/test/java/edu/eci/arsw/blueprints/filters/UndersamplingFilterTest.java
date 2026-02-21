package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class UndersamplingFilterTest {

    private final UndersamplingFilter filter = new UndersamplingFilter();

    @Test
    void returnsSameBlueprintWhenEmpty() {
        Blueprint blueprint = new Blueprint("author", "empty", List.of());

        Blueprint result = filter.apply(blueprint);

        assertSame(blueprint, result);
        assertEquals(List.of(), result.getPoints());
    }

    @Test
    void returnsSameBlueprintWhenSinglePoint() {
        Blueprint blueprint = new Blueprint("author", "single", List.of(new Point(5, 5)));

        Blueprint result = filter.apply(blueprint);

        assertSame(blueprint, result);
        assertEquals(List.of(new Point(5, 5)), result.getPoints());
    }

    @Test
    void returnsSameBlueprintWhenTwoPoints() {
        Blueprint blueprint = new Blueprint("author", "two-points", List.of(new Point(1, 1), new Point(2, 2)));

        Blueprint result = filter.apply(blueprint);

        assertSame(blueprint, result);
        assertEquals(List.of(new Point(1, 1), new Point(2, 2)), result.getPoints());
    }

    @Test
    void keepsEvenIndexedPointsWhenMoreThanTwo() {
        Blueprint blueprint = new Blueprint("author", "many", List.of(
                new Point(0, 0), // 0
                new Point(1, 1), // 1
                new Point(2, 2), // 2
                new Point(3, 3), // 3
                new Point(4, 4)  // 4
        ));

        Blueprint result = filter.apply(blueprint);

        assertEquals(List.of(new Point(0, 0), new Point(2, 2), new Point(4, 4)), result.getPoints());
    }

    @Test
    void samplingIsPositionBasedNotValueBased() {
        Blueprint blueprint = new Blueprint("author", "positions", List.of(
                new Point(0, 0), // kept
                new Point(0, 0), // dropped (odd index)
                new Point(0, 0), // kept
                new Point(1, 1)  // dropped
        ));

        Blueprint result = filter.apply(blueprint);

        assertEquals(List.of(new Point(0, 0), new Point(0, 0)), result.getPoints());
    }
}
