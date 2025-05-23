# Receitas Fáceis - Sistema de Gerenciamento de Receitas

Sistema web de cadastro e busca de receitas desenvolvido com Java, Spark Framework e PostgreSQL, seguindo o padrão MVC.

## Funcionalidades

- Cadastro e autenticação de usuários
- Criação, visualização, edição e exclusão de receitas
- Adicionar receitas aos favoritos
- Buscar receitas por ingredientes disponíveis

## Tecnologias Utilizadas

### Backend
- Java
- Spark Framework
- PostgreSQL
- BCrypt (para criptografia de senhas)
- Gson (para manipulação de JSON)

### Frontend
- HTML5
- CSS3
- JavaScript (Vanilla)

## Pré-requisitos

- Java Development Kit (JDK) 11 ou superior
- PostgreSQL 12 ou superior
- Maven 3.6 ou superior

## Configuração do Banco de Dados

1. Instale o PostgreSQL em sua máquina
2. Crie um banco de dados chamado 'receitasfaceis'
3. Execute o script SQL que está em `src/main/resources/sql/create_tables.sql` para criar as tabelas

Ou simplesmente execute o script completo que já inclui a criação do banco de dados.

## Configuração da Aplicação

1. Abra o arquivo `src/main/java/com/receitasfaceis/config/DatabaseConfig.java`
2. Ajuste as constantes `URL`, `USER` e `PASSWORD` de acordo com sua configuração local do PostgreSQL

## Como Executar

1. Clone o repositório
2. Na raiz do projeto, execute:
   ```
   mvn clean install
   ```
3. Execute o servidor com:
   ```
   mvn exec:java -Dexec.mainClass="com.receitasfaceis.Application"
   ```
4. Acesse a aplicação em: http://localhost:8080

Ou execute o arquivo jar gerado:
```
java -jar target/receitasfaceis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Estrutura do Projeto

O projeto está organizado em duas pastas principais:

- **backend**: Contém todo o código Java da aplicação, incluindo a API REST e a camada de acesso a dados.
- **frontend**: Contém todos os arquivos da interface web (HTML, CSS e JavaScript).

## Iniciando o Projeto

### Backend

1. Entre na pasta backend:
   ```
   cd backend
   ```

2. Compile e execute o projeto usando Maven:
   ```
   mvn clean compile exec:java
   ```

### Frontend

O frontend é servido pelo backend automaticamente quando o servidor Spark é iniciado.
A pasta frontend contém os arquivos estáticos da interface web que são acessados diretamente pelo navegador.

## API REST

A API está disponível em `http://localhost:8080/api` e oferece os seguintes endpoints:

- **Usuários**: `/api/usuarios`
  - GET: Lista todos os usuários
  - POST: Cria um novo usuário
  - GET `/:id`: Busca um usuário pelo ID
  - PUT `/:id`: Atualiza um usuário
  - DELETE `/:id`: Remove um usuário
  - POST `/login`: Autentica um usuário

- **Receitas**: `/api/receitas`
  - GET: Lista todas as receitas
  - POST: Cria uma nova receita
  - GET `/:id`: Busca uma receita pelo ID
  - PUT `/:id`: Atualiza uma receita
  - DELETE `/:id`: Remove uma receita
  - GET `/busca/ingredientes`: Busca receitas por ingredientes

## Páginas Implementadas

O projeto contém as seguintes páginas:

1. **Página Inicial (index.html)**
   - Página inicial com apresentação do sistema e receitas em destaque.

2. **Lista de Receitas (receitas.html)**
   - Exibe todas as receitas disponíveis com opções de filtro por categoria e dificuldade.

3. **Detalhes da Receita (receita-detalhes.html)**
   - Mostra informações completas de uma receita específica, incluindo ingredientes e modo de preparo.

4. **Nova Receita (nova-receita.html)**
   - Formulário para cadastro de novas receitas (requer autenticação).

5. **Cadastro (cadastro.html)**
   - Página para criação de novas contas de usuário.

6. **Login (login.html)**
   - Página para autenticação de usuários.

## Recursos Implementados

1. **Listagem de Receitas**
   - Visualização de todas as receitas cadastradas com filtros por categoria e dificuldade.

2. **Visualização Detalhada**
   - Página com detalhes completos de cada receita, incluindo ingredientes e modo de preparo.

3. **Cadastro de Receitas**
   - Formulário para usuários logados adicionarem novas receitas ao sistema.

4. **Autenticação de Usuários**
   - Sistema de login e cadastro para identificação de usuários.

## Próximos Passos

- Implementar categorias para receitas
- Adicionar sistema de avaliação
- Melhorar o sistema de busca com filtros adicionais
- Adicionar upload de imagens para receitas

# Projeto Receitas Fáceis - Frontend

Este projeto é a interface de usuário (frontend) para uma plataforma de compartilhamento de receitas culinárias. Ele foi desenvolvido com foco em HTML, CSS e JavaScript puros, buscando uma experiência de usuário moderna e responsiva.

## Funcionalidades Principais

*   **Visualização de Receitas:**
    *   Página inicial com receitas em destaque.
    *   Página de listagem de todas as receitas com filtros por categoria e dificuldade.
    *   Página de detalhes para cada receita, exibindo ingredientes, modo de preparo, tempo, porções, dificuldade e autor.
*   **Busca de Receitas:** Funcionalidade de busca por termo e filtros combinados.
*   **Gerenciamento de Usuários (Simulado/Frontend):**
    *   Cadastro de novos usuários.
    *   Login de usuários existentes.
    *   Persistência do status de login via `localStorage`.
*   **Gerenciamento de Receitas (para usuários logados):**
    *   Criação de novas receitas através de um formulário detalhado.
    *   Edição de receitas existentes.
    *   Exclusão de receitas cadastradas pelo usuário.
    *   Página de perfil do usuário exibindo suas receitas cadastradas.
*   **Interface Moderna:**
    *   Design responsivo.
    *   Temas claro (Latte) e escuro (Mocha) com seletor de alternância e persistência da preferência.
    *   Variáveis CSS para fácil customização do tema.

## Tecnologias Utilizadas

*   HTML5
*   CSS3 (com variáveis CSS para theming)
*   JavaScript (Vanilla JS, ES6+)

## Estrutura do Projeto (Frontend)

*   **`/` (raiz):**
    *   `index.html`: Página inicial.
    *   `receitas.html`: Página de listagem de todas as receitas.
    *   `receita-detalhes.html`: Página para visualização detalhada de uma receita.
    *   `busca.html`: Página de busca de receitas.
    *   `nova-receita.html`: Formulário para cadastro e edição de receitas.
    *   `login.html`: Página de login.
    *   `cadastro.html`: Página de cadastro de usuários.
    *   `perfil.html`: Página de perfil do usuário.
    *   `README.md`: Este arquivo.
*   **`css/`**
    *   `styles.css`: Folha de estilos principal.
*   **`js/`**
    *   `main.js`: Script principal com funções globais (autenticação, criação de cards, etc.).
    *   `index.js`: Lógica específica para a `index.html`.
    *   `receitas.js`: Lógica para a `receitas.html` (carregamento, filtros).
    *   `receita-detalhes.js`: Lógica para a `receita-detalhes.html`.
    *   `busca.js`: Lógica para a `busca.html`.
    *   `nova-receita.js`: Lógica para o formulário de `nova-receita.html`.
    *   `login.js`: Lógica para a `login.html`.
    *   `cadastro.js`: Lógica para a `cadastro.html`.
    *   `perfil.js`: Lógica para a `perfil.html`.
    *   `theme-toggle.js`: Script para alternância de tema.
*   **`img/`**
    *   Imagens de placeholder ou ícones utilizados.

## Como Executar (Frontend)

1.  Certifique-se de que o backend Java correspondente esteja em execução (por padrão, na porta 8081, conforme configurado nos scripts JS).
2.  Abra qualquer um dos arquivos `.html` diretamente em um navegador web.
    *   Recomenda-se começar pelo `index.html`.

## Considerações

*   A comunicação com o backend é feita através de uma API REST (endereço base `http://localhost:8081/api` nos scripts).
*   A autenticação é simulada no frontend e gerenciada via `localStorage`. Para um ambiente de produção, um sistema de autenticação robusto no backend seria necessário. 