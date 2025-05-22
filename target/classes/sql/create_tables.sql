-- Criação do banco de dados
CREATE DATABASE receitasfaceis;

-- Conexão ao banco de dados
\c receitasfaceis;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acesso TIMESTAMP
);

-- Tabela de Categorias de Receitas
CREATE TABLE IF NOT EXISTS categorias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao TEXT
);

-- Tabela de Receitas
CREATE TABLE IF NOT EXISTS receitas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    modo_preparo TEXT NOT NULL,
    tempo_preparo INTEGER NOT NULL,
    porcoes INTEGER NOT NULL,
    dificuldade VARCHAR(20) NOT NULL,
    imagem_url VARCHAR(255),
    usuario_id INTEGER NOT NULL,
    categoria_id INTEGER,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
);

-- Tabela de Ingredientes
CREATE TABLE IF NOT EXISTS ingredientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    unidade_medida VARCHAR(20)
);

-- Tabela de Receitas_Ingredientes (relacionamento N-M)
CREATE TABLE IF NOT EXISTS receitas_ingredientes (
    receita_id INTEGER NOT NULL,
    ingrediente_id INTEGER NOT NULL,
    quantidade DECIMAL(10,2) NOT NULL,
    observacao VARCHAR(100),
    PRIMARY KEY (receita_id, ingrediente_id),
    FOREIGN KEY (receita_id) REFERENCES receitas(id) ON DELETE CASCADE,
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id) ON DELETE CASCADE
);

-- Tabela de Favoritos
CREATE TABLE IF NOT EXISTS favoritos (
    usuario_id INTEGER NOT NULL,
    receita_id INTEGER NOT NULL,
    data_adicionado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, receita_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (receita_id) REFERENCES receitas(id) ON DELETE CASCADE
);

-- Tabela de Comentários
CREATE TABLE IF NOT EXISTS comentarios (
    id SERIAL PRIMARY KEY,
    receita_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    texto TEXT NOT NULL,
    avaliacao INTEGER CHECK (avaliacao >= 1 AND avaliacao <= 5),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receita_id) REFERENCES receitas(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Inserir algumas categorias básicas
INSERT INTO categorias (nome, descricao) VALUES 
('Entrada', 'Pratos para iniciar uma refeição'),
('Prato Principal', 'Pratos principais para refeições'),
('Sobremesa', 'Doces e sobremesas'),
('Bebida', 'Bebidas e drinks'),
('Vegetariana', 'Receitas sem carne'),
('Vegana', 'Receitas sem produtos de origem animal'),
('Rápidas', 'Receitas de preparo rápido, menos de 30 minutos'); 