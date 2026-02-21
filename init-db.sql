
-- Crear tabla de blueprints
CREATE TABLE IF NOT EXISTS blueprints (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    UNIQUE(author, name)
);

-- Crear tabla de puntos
CREATE TABLE IF NOT EXISTS points (
    id BIGSERIAL PRIMARY KEY,
    blueprint_id BIGINT NOT NULL REFERENCES blueprints(id) ON DELETE CASCADE,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    point_order INTEGER NOT NULL
);

-- Insertar blueprints de prueba
INSERT INTO blueprints (author, name) VALUES 
    ('john', 'house'),
    ('john', 'garage'),
    ('jane', 'garden'),
    ('john', 'kitchen')
ON CONFLICT (author, name) DO NOTHING;

-- Insertar puntos para 'john/house' (id=1)
INSERT INTO points (blueprint_id, x, y, point_order) VALUES
    (1, 0, 0, 0),
    (1, 10, 0, 1),
    (1, 10, 10, 2),
    (1, 0, 10, 3);

-- Insertar puntos para 'john/garage' (id=2)
INSERT INTO points (blueprint_id, x, y, point_order) VALUES
    (2, 5, 5, 0),
    (2, 15, 5, 1),
    (2, 15, 15, 2);

-- Insertar puntos para 'jane/garden' (id=3)
INSERT INTO points (blueprint_id, x, y, point_order) VALUES
    (3, 2, 2, 0),
    (3, 3, 4, 1),
    (3, 6, 7, 2);
