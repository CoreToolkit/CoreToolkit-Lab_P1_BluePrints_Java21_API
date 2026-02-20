package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.entities.BlueprintEntity;
import edu.eci.arsw.blueprints.persistence.entities.PointEntity;
import edu.eci.arsw.blueprints.persistence.repository.BlueprintRepository;
import edu.eci.arsw.blueprints.persistence.BlueprintAlreadyExistsException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final BlueprintRepository blueprintRepository;

    public PostgresBlueprintPersistence(BlueprintRepository blueprintRepository) {
        this.blueprintRepository = blueprintRepository;
    }

    @Override
    @Transactional
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprintRepository.existsByAuthorAndName(bp.getAuthor(), bp.getName())) {
            throw new BlueprintAlreadyExistsException(
                "Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName()
            );
        }

        BlueprintEntity entity = toEntity(bp);
        blueprintRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        BlueprintEntity entity = blueprintRepository.findByAuthorAndName(author, name)
            .orElseThrow(() -> new BlueprintNotFoundException(
                "Blueprint not found: " + author + "/" + name
            ));

        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<BlueprintEntity> entities = blueprintRepository.findByAuthor(author);
        
        if (entities.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }

        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Blueprint> getAllBlueprints() {
        return blueprintRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        BlueprintEntity entity = blueprintRepository.findByAuthorAndName(author, name)
            .orElseThrow(() -> new BlueprintNotFoundException(
                "Blueprint not found: " + author + "/" + name
            ));

        int nextOrder = entity.getPoints().size();

        PointEntity pointEntity = new PointEntity(x, y, nextOrder);
        entity.addPoint(pointEntity);

        blueprintRepository.save(entity);
    }

    
    private BlueprintEntity toEntity(Blueprint bp) {
        BlueprintEntity entity = new BlueprintEntity(bp.getAuthor(), bp.getName());

        List<Point> points = bp.getPoints();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            PointEntity pointEntity = new PointEntity(p.x(), p.y(), i);
            entity.addPoint(pointEntity);
        }

        return entity;
    }

    private Blueprint toDomain(BlueprintEntity entity) {
        List<Point> points = entity.getPoints().stream()
            .map(pe -> new Point(pe.getX(), pe.getY()))
            .toList();

        return new Blueprint(entity.getAuthor(), entity.getName(), points);
    }
}
