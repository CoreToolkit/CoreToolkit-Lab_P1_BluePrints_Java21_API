package edu.eci.arsw.blueprints.filters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("undersampling")
class FilterProfileSelectionUndersamplingTest {

    @Autowired
    private BlueprintsFilter filter;

    @Test
    void undersamplingBeanIsChosen() {
        assertThat(filter).isInstanceOf(UndersamplingFilter.class);
    }
}
