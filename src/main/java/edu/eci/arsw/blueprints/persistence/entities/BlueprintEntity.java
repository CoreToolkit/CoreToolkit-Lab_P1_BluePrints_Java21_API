package edu.eci.arsw.blueprints.persistence.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "blueprints", uniqueConstraints = @UniqueConstraint(columnNames = {"author", "name"}))
public class BlueprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("pointOrder ASC")
    private List<PointEntity> points = new ArrayList<>();

    public BlueprintEntity() {
    }

    public BlueprintEntity(String author, String name) {
        this.author = author;
        this.name = name;
    }

    
    public void addPoint(PointEntity point) {
        points.add(point);
        point.setBlueprint(this);
    }

    public void removePoint(PointEntity point) {
        points.remove(point);
        point.setBlueprint(null);
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointEntity> getPoints() {
        return points;
    }

    public void setPoints(List<PointEntity> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlueprintEntity bp)) return false;
        return Objects.equals(author, bp.author) && Objects.equals(name, bp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }

}
