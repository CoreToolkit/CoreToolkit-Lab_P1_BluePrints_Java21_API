package edu.eci.arsw.blueprints.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "points")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id", nullable = false)
    private BlueprintEntity blueprint;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(name = "point_order", nullable = false)
    private Integer pointOrder;

    public PointEntity() {
    }

    public PointEntity(Integer x, Integer y, Integer pointOrder) {
        this.x = x;
        this.y = y;
        this.pointOrder = pointOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BlueprintEntity getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(BlueprintEntity blueprint) {
        this.blueprint = blueprint;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getPointOrder() {
        return pointOrder;
    }

    public void setPointOrder(Integer pointOrder) {
        this.pointOrder = pointOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
