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