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

```
/src
  /main
    /java
      /com/receitasfaceis
        /config         # Configurações do sistema
        /controller     # Controladores REST
        /dao            # Acesso a dados
        /model          # Modelos de dados
        /service        # Regras de negócio
        /util           # Classes utilitárias
        Application.java # Classe principal
    /resources
      /public           # Arquivos estáticos (HTML, CSS, JS)
      /sql              # Scripts SQL
```

## API REST

### Usuários
- `GET /api/usuarios` - Lista todos os usuários
- `GET /api/usuarios/:id` - Busca usuário por ID
- `POST /api/usuarios` - Cria um novo usuário
- `PUT /api/usuarios/:id` - Atualiza um usuário existente
- `DELETE /api/usuarios/:id` - Remove um usuário
- `POST /api/usuarios/login` - Autentica um usuário

### Receitas
- `GET /api/receitas` - Lista todas as receitas
- `GET /api/receitas/:id` - Busca receita por ID
- `POST /api/receitas` - Cria uma nova receita
- `PUT /api/receitas/:id` - Atualiza uma receita existente
- `DELETE /api/receitas/:id` - Remove uma receita
- `GET /api/receitas/busca/ingredientes?ids=1,2,3` - Busca receitas por ingredientes disponíveis

## Próximos Passos

- Implementar categorias para receitas
- Adicionar sistema de avaliação
- Melhorar o sistema de busca com filtros adicionais
- Adicionar upload de imagens para receitas 