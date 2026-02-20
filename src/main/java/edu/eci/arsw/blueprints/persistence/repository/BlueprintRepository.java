package edu.eci.arsw.blueprints.persistence.repository;

import edu.eci.arsw.blueprints.persistence.entities.BlueprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BlueprintRepository extends JpaRepository<BlueprintEntity, Long> {

    Optional<BlueprintEntity> findByAuthorAndName(String author, String name);

    Set<BlueprintEntity> findByAuthor(String author);

    boolean existsByAuthorAndName(String author, String name);
}
