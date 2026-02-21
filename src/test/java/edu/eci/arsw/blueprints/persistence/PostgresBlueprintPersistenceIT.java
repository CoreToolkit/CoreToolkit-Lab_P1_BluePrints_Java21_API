package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.repository.BlueprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class PostgresBlueprintPersistenceIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("blueprintsdb")
            .withUsername("blueprintuser")
            .withPassword("blueprintpass");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private PostgresBlueprintPersistence persistence;

    @Autowired
    private BlueprintRepository repository;

    @Test
    void savesAndReadsBlueprintWithPoints() throws Exception {
        Blueprint bp = new Blueprint("anna", "bridge", List.of(
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2)
        ));

        persistence.saveBlueprint(bp);

        Blueprint stored = persistence.getBlueprint("anna", "bridge");
        assertThat(stored.getPoints()).containsExactly(
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2)
        );
    }

    @Test
    void detectsDuplicateByAuthorAndName() throws Exception {
        Blueprint bp = new Blueprint("dupe", "plan", List.of(new Point(0, 0)));
        persistence.saveBlueprint(bp);

        assertThrows(BlueprintAlreadyExistsException.class,
                () -> persistence.saveBlueprint(bp));
    }

    @Test
    void keepsPointsOrderWhenReading() throws Exception {
        Blueprint bp = new Blueprint("ordered", "plan", List.of(
                new Point(5, 5),
                new Point(6, 6),
                new Point(7, 7)
        ));
        persistence.saveBlueprint(bp);

        // verify order via repository as well
        var entity = repository.findByAuthorAndName("ordered", "plan").orElseThrow();
        assertThat(entity.getPoints()).extracting("pointOrder")
                .containsExactly(0, 1, 2);
    }
}
