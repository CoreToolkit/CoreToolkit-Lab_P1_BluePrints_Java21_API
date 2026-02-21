package edu.eci.arsw.blueprints.filters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilterProfileSelectionDefaultTest {

    @Autowired
    private BlueprintsFilter filter;

    @Test
    void identityBeanIsDefault() {
        assertThat(filter).isInstanceOf(IdentityFilter.class);
    }
}
