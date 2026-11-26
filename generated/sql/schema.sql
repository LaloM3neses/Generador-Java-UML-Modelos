-- Generado automaticamente desde UML/XMI.
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS "Autor" (
  "id" INTEGER PRIMARY KEY,
  "nombre" TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "Libro" (
  "id" INTEGER PRIMARY KEY,
  "titulo" TEXT NOT NULL,
  "publicado" INTEGER NOT NULL CHECK ("publicado" IN (0, 1)),
  "editorial_id" INTEGER NOT NULL,
  FOREIGN KEY ("editorial_id") REFERENCES "Editorial"("id")
);

CREATE TABLE IF NOT EXISTS "Editorial" (
  "id" INTEGER PRIMARY KEY,
  "nombre" TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "Autor_Libro" (
  "autor_id" INTEGER,
  "libro_id" INTEGER,
  PRIMARY KEY ("autor_id", "libro_id"),
  FOREIGN KEY ("autor_id") REFERENCES "Autor"("id"),
  FOREIGN KEY ("libro_id") REFERENCES "Libro"("id")
);
