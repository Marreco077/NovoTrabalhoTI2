package com.receitasfaceis;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.receitasfaceis.controller.UsuarioController;
import com.receitasfaceis.controller.ReceitaController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe principal da aplicação que configura o servidor Spark e as rotas.
 */
public class Application {

    private static final int PORT = 8081;
    public static Gson gson;

    public static void main(String[] args) {
        // Configuração do Gson para lidar com LocalDateTime
        GsonBuilder gsonBuilder = new GsonBuilder();
        
        // Deixar de usar apenas campos anotados com @Expose para debug
        // gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        
        // Serialização de LocalDateTime para String
        JsonSerializer<LocalDateTime> serializer = (src, typeOfSrc, context) -> 
            src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
        // Desserialização de String para LocalDateTime    
        JsonDeserializer<LocalDateTime> deserializer = (json, typeOfT, context) ->
            json == null ? null : LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, serializer);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, deserializer);
        
        // Adicionar configurações adicionais
        gsonBuilder.setPrettyPrinting(); // Para facilitar leitura em debug
        gsonBuilder.serializeNulls();     // Não omitir campos nulos
        
        gson = gsonBuilder.create();
        
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
                response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
            }
            
            // IMPORTANTE: Definir Access-Control-Allow-Origin no options também
            response.header("Access-Control-Allow-Origin", "*");
            return "OK";
        });
        
        before((request, response) -> {
            // Garantir que o cabeçalho Access-Control-Allow-Origin seja definido para todas as rotas
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            
            // Não definir o tipo de conteúdo como application/json para TODAS as rotas
            // Apenas para rotas da API que retornam JSON
            if (request.pathInfo() != null && request.pathInfo().startsWith("/api/")) {
                response.type("application/json");
            }
            
            // Log de todas as requisições
            System.out.println("Request: " + request.requestMethod() + " " + request.pathInfo() + " (Origin: " + request.headers("Origin") + ")");
            if (request.requestMethod().equals("POST") || request.requestMethod().equals("PUT")) {
                String body = request.body();
                System.out.println("Body: " + (body.length() > 1000 ? body.substring(0, 1000) + "..." : body));
            }
        });

        // Log de respostas
        afterAfter((request, response) -> {
            System.out.println("Response: " + response.status() + " para " + request.requestMethod() + " " + request.pathInfo());
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
                get("/:usuarioId/receitas", ReceitaController.getReceitasDoUsuario);
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
                e.printStackTrace(); // Log da exceção para debug
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