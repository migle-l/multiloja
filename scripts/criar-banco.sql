-- Uso quando o PostgreSQL é instalado direto na máquina (sem Docker).
-- Execute como superusuário (postgres):  psql -U postgres -f scripts/criar-banco.sql
CREATE USER multiloja WITH PASSWORD 'multiloja';
CREATE DATABASE multiloja OWNER multiloja;
