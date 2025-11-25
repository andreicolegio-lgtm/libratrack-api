-- ==================================================================================
-- 1. INSERTAR TIPOS (Standard English Keys)
-- ==================================================================================
INSERT IGNORE INTO tipos (nombre) VALUES ('Anime');
INSERT IGNORE INTO tipos (nombre) VALUES ('Movie');
INSERT IGNORE INTO tipos (nombre) VALUES ('Video Game');
INSERT IGNORE INTO tipos (nombre) VALUES ('Manga');
INSERT IGNORE INTO tipos (nombre) VALUES ('Manhwa');
INSERT IGNORE INTO tipos (nombre) VALUES ('Book');
INSERT IGNORE INTO tipos (nombre) VALUES ('Series');

-- ==================================================================================
-- 2. INSERTAR GÉNEROS (Standard English Keys)
-- ==================================================================================
-- General / Universal
INSERT IGNORE INTO generos (nombre) VALUES ('Action'), ('Adventure'), ('Comedy'), ('Drama'), ('Fantasy'), ('Horror'), ('Mystery'), ('Romance'), ('Sci-Fi'), ('Slice of Life'), ('Psychological'), ('Thriller'), ('Historical'), ('Crime'), ('Family'), ('War'), ('Cyberpunk'), ('Post-Apocalyptic');

-- Asian / Anime / Manga specific
INSERT IGNORE INTO generos (nombre) VALUES ('Shonen'), ('Shojo'), ('Seinen'), ('Josei'), ('Isekai'), ('Mecha'), ('Harem'), ('Ecchi'), ('Yaoi'), ('Yuri'), ('Martial Arts'), ('School');

-- Gaming specific
INSERT IGNORE INTO generos (nombre) VALUES ('RPG'), ('Shooter'), ('Platformer'), ('Strategy'), ('Puzzle'), ('Fighting'), ('Sports'), ('Racing'), ('Open World'), ('Roguelike'), ('MOBA'), ('Battle Royale'), ('Simulator'), ('Survival Horror');

-- Literature specific
INSERT IGNORE INTO generos (nombre) VALUES ('Biography'), ('Essay'), ('Poetry'), ('Self-Help'), ('Business'), ('Noir'), ('Magical Realism');

-- ==================================================================================
-- 3. VINCULACIÓN MASIVA INTELIGENTE (Auto-linking)
-- ==================================================================================

-- GRUPO A: Géneros Universales (Aplican a TODOS los tipos)
-- Action, Adventure, Comedy, Drama, Fantasy, Horror, Mystery, Romance, Sci-Fi, Psychological, Thriller, Historical, Crime, Family, War, Cyberpunk, Post-Apocalyptic
INSERT IGNORE INTO tipo_genero (tipo_id, genero_id)
SELECT t.id, g.id FROM tipos t, generos g
WHERE t.nombre IN ('Anime', 'Movie', 'Video Game', 'Manga', 'Manhwa', 'Book', 'Series')
AND g.nombre IN ('Action', 'Adventure', 'Comedy', 'Drama', 'Fantasy', 'Horror', 'Mystery', 'Romance', 'Sci-Fi', 'Psychological', 'Thriller', 'Historical', 'Crime', 'Family', 'War', 'Cyberpunk', 'Post-Apocalyptic');

-- GRUPO B: Específico Asiático (Anime, Manga, Manhwa)
-- Incluye Slice of Life, Martial Arts, School y los demográficos japoneses
INSERT IGNORE INTO tipo_genero (tipo_id, genero_id)
SELECT t.id, g.id FROM tipos t, generos g
WHERE t.nombre IN ('Anime', 'Manga', 'Manhwa')
AND g.nombre IN ('Shonen', 'Shojo', 'Seinen', 'Josei', 'Isekai', 'Mecha', 'Harem', 'Ecchi', 'Yaoi', 'Yuri', 'Martial Arts', 'School', 'Slice of Life', 'Sports');

-- GRUPO C: Videojuegos (Exclusivos o mecánicas de juego)
INSERT IGNORE INTO tipo_genero (tipo_id, genero_id)
SELECT t.id, g.id FROM tipos t, generos g
WHERE t.nombre = 'Video Game'
AND g.nombre IN ('RPG', 'Shooter', 'Platformer', 'Strategy', 'Puzzle', 'Fighting', 'Sports', 'Racing', 'Open World', 'Roguelike', 'MOBA', 'Battle Royale', 'Simulator', 'Survival Horror', 'Martial Arts');

-- GRUPO D: Libros (Literarios específicos)
INSERT IGNORE INTO tipo_genero (tipo_id, genero_id)
SELECT t.id, g.id FROM tipos t, generos g
WHERE t.nombre = 'Book'
AND g.nombre IN ('Biography', 'Essay', 'Poetry', 'Self-Help', 'Business', 'Noir', 'Magical Realism', 'Slice of Life');

-- GRUPO E: Excepciones Cruzadas (Cine/Series que comparten géneros de libros o juegos)
INSERT IGNORE INTO tipo_genero (tipo_id, genero_id)
SELECT t.id, g.id FROM tipos t, generos g
WHERE t.nombre IN ('Movie', 'Series')
AND g.nombre IN ('Biography', 'Noir', 'Magical Realism', 'Sports', 'Martial Arts', 'Slice of Life');