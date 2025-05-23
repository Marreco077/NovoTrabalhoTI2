package com.receitasfaceis.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.receitasfaceis.dao.UsuarioDAO;
import com.receitasfaceis.model.Usuario;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para operações relacionadas a usuários.
 */
public class UsuarioController {

    private static final Gson gson = new Gson();
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Rota para listar todos os usuários.
     */
    public static final Route listarUsuarios = (Request req, Response res) -> {
        try {
            return gson.toJson(usuarioDAO.listar());
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao listar usuários: " + e.getMessage()));
        }
    };

    /**
     * Rota para buscar um usuário pelo ID.
     */
    public static final Route buscarUsuarioPorId = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            Usuario usuario = usuarioDAO.buscarPorId(id);
            
            if (usuario != null) {
                // Não retornar a senha
                usuario.setSenha(null);
                return gson.toJson(usuario);
            } else {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Usuário não encontrado"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao buscar usuário: " + e.getMessage()));
        }
    };

    /**
     * Rota para criar um novo usuário.
     */
    public static final Route criarUsuario = (Request req, Response res) -> {
        try {
            System.out.println("Recebendo requisição para criar usuário");
            System.out.println("Body: " + req.body());
            
            // Vamos usar um JsonObject para extrair apenas os campos necessários
            JsonObject jsonObject = gson.fromJson(req.body(), JsonObject.class);
            
            // Extrair os dados em vez de deserializar diretamente para Usuario
            String nome = jsonObject.has("nome") ? jsonObject.get("nome").getAsString() : null;
            String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : null;
            String senha = jsonObject.has("senha") ? jsonObject.get("senha").getAsString() : null;
            
            // Validação
            if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty()) {
                
                res.status(400);
                return gson.toJson(criarResposta("erro", "Nome, email e senha são obrigatórios"));
            }
            
            // Criar objeto Usuario simples sem LocalDateTime
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            
            // Verifica se já existe usuário com o mesmo email
            if (usuarioDAO.buscarPorEmail(email) != null) {
                res.status(409); // Conflict
                return gson.toJson(criarResposta("erro", "Email já cadastrado"));
            }
            
            // A dataCriação será definida no DAO
            usuario = usuarioDAO.inserir(usuario);
            res.status(201); // Created
            
            // Limpa a senha antes de retornar
            usuario.setSenha(null);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("id", usuario.getId());
            responseJson.addProperty("nome", usuario.getNome());
            responseJson.addProperty("email", usuario.getEmail());
            
            return gson.toJson(responseJson);
        } catch (Exception e) {
            e.printStackTrace(); // Log para debug
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao criar usuário: " + e.getMessage()));
        }
    };

    /**
     * Rota para atualizar um usuário existente.
     */
    public static final Route atualizarUsuario = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            Usuario usuarioAtual = usuarioDAO.buscarPorId(id);
            
            if (usuarioAtual == null) {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Usuário não encontrado"));
            }
            
            Usuario usuarioAtualizado = gson.fromJson(req.body(), Usuario.class);
            usuarioAtualizado.setId(id);
            
            // Mantém a senha atual
            usuarioAtualizado.setSenha(usuarioAtual.getSenha());
            
            if (usuarioDAO.atualizar(usuarioAtualizado)) {
                // Não retornar a senha
                usuarioAtualizado.setSenha(null);
                return gson.toJson(usuarioAtualizado);
            } else {
                res.status(500);
                return gson.toJson(criarResposta("erro", "Falha ao atualizar usuário"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao atualizar usuário: " + e.getMessage()));
        }
    };

    /**
     * Rota para excluir um usuário.
     */
    public static final Route excluirUsuario = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            
            if (usuarioDAO.buscarPorId(id) == null) {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Usuário não encontrado"));
            }
            
            if (usuarioDAO.remover(id)) {
                return gson.toJson(criarResposta("sucesso", "Usuário excluído com sucesso"));
            } else {
                res.status(500);
                return gson.toJson(criarResposta("erro", "Falha ao excluir usuário"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao excluir usuário: " + e.getMessage()));
        }
    };

    /**
     * Rota para autenticar um usuário.
     */
    public static final Route autenticarUsuario = (Request req, Response res) -> {
        try {
            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            
            if (!body.has("email") || !body.has("senha")) {
                res.status(400);
                return gson.toJson(criarResposta("erro", "Email e senha são obrigatórios"));
            }
            
            String email = body.get("email").getAsString();
            String senha = body.get("senha").getAsString();
            
            Usuario usuario = usuarioDAO.autenticar(email, senha);
            
            if (usuario != null) {
                // Não retornar a senha nem objetos com LocalDateTime
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("id", usuario.getId());
                responseJson.addProperty("nome", usuario.getNome());
                responseJson.addProperty("email", usuario.getEmail());
                
                return gson.toJson(responseJson);
            } else {
                res.status(401); // Unauthorized
                return gson.toJson(criarResposta("erro", "Email ou senha inválidos"));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log para debug
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao autenticar: " + e.getMessage()));
        }
    };
    
    /**
     * Cria uma resposta padronizada.
     * 
     * @param tipo Tipo da resposta ("sucesso" ou "erro")
     * @param mensagem Mensagem da resposta
     * @return Mapa com a resposta
     */
    private static Map<String, String> criarResposta(String tipo, String mensagem) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("tipo", tipo);
        resposta.put("mensagem", mensagem);
        return resposta;
    }
} 