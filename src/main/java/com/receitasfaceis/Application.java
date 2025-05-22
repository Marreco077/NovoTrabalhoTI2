package com.receitasfaceis;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.receitasfaceis.controller.UsuarioController;
import com.receitasfaceis.controller.ReceitaController;

/**
 * Classe principal da aplicação que configura o servidor Spark e as rotas.
 */
public class Application {

    private static final int PORT = 8080;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        // Configuração do servidor Spark
        port(PORT);
        staticFiles.location("/public");
        
        // Configuração CORS para permitir requisições de origens diferentes
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            
            return "OK";
        });
        
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Content-Length");
            response.type("application/json");
        });

        // Configuração de rotas para API REST
        path("/api", () -> {
            // Rota inicial para verificar se API está funcionando
            get("/status", (req, res) -> {
                res.type("application/json");
                return "{\"status\": \"online\", \"message\": \"API de Receitas Fáceis funcionando!\"}";
            });
            
            // Rotas para usuários
            path("/usuarios", () -> {
                get("", UsuarioController.listarUsuarios);
                post("", UsuarioController.criarUsuario);
                get("/:id", UsuarioController.buscarUsuarioPorId);
                put("/:id", UsuarioController.atualizarUsuario);
                delete("/:id", UsuarioController.excluirUsuario);
                post("/login", UsuarioController.autenticarUsuario);
            });
            
            // Rotas para receitas
            path("/receitas", () -> {
                get("", ReceitaController.listarReceitas);
                post("", ReceitaController.criarReceita);
                get("/:id", ReceitaController.buscarReceitaPorId);
                put("/:id", ReceitaController.atualizarReceita);
                delete("/:id", ReceitaController.excluirReceita);
                get("/busca/ingredientes", ReceitaController.buscarPorIngredientes);
            });
            
            // Tratamento de exceções
            exception(Exception.class, (e, request, response) -> {
                response.status(500);
                response.body(gson.toJson(new ErrorResponse(e.getMessage())));
            });
        });
        
        System.out.println("Servidor iniciado na porta " + PORT);
    }
    
    /**
     * Classe interna para representar respostas de erro
     */
    private static class ErrorResponse {
        private String mensagem;
        
        public ErrorResponse(String mensagem) {
            this.mensagem = mensagem;
        }
        
        public String getMensagem() {
            return mensagem;
        }
    }
} 