package com.receitasfaceis.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.receitasfaceis.dao.ReceitaDAO;
import com.receitasfaceis.model.Receita;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para operações relacionadas a receitas.
 */
public class ReceitaController {

    private static final Gson gson = new Gson();
    private static final ReceitaDAO receitaDAO = new ReceitaDAO();

    /**
     * Rota para listar todas as receitas.
     */
    public static final Route listarReceitas = (Request req, Response res) -> {
        try {
            return gson.toJson(receitaDAO.listar());
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao listar receitas: " + e.getMessage()));
        }
    };

    /**
     * Rota para buscar uma receita pelo ID.
     */
    public static final Route buscarReceitaPorId = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            Receita receita = receitaDAO.buscarPorId(id);
            
            if (receita != null) {
                return gson.toJson(receita);
            } else {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao buscar receita: " + e.getMessage()));
        }
    };

    /**
     * Rota para criar uma nova receita.
     */
    public static final Route criarReceita = (Request req, Response res) -> {
        try {
            Receita receita = gson.fromJson(req.body(), Receita.class);
            
            if (receita.getTitulo() == null || receita.getTitulo().trim().isEmpty() ||
                receita.getModoPreparo() == null || receita.getModoPreparo().trim().isEmpty() ||
                receita.getUsuarioId() <= 0) {
                
                res.status(400);
                return gson.toJson(criarResposta("erro", "Título, modo de preparo e usuário são obrigatórios"));
            }
            
            receita = receitaDAO.inserir(receita);
            res.status(201); // Created
            
            return gson.toJson(receita);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao criar receita: " + e.getMessage()));
        }
    };

    /**
     * Rota para atualizar uma receita existente.
     */
    public static final Route atualizarReceita = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            Receita receitaAtual = receitaDAO.buscarPorId(id);
            
            if (receitaAtual == null) {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
            
            Receita receitaAtualizada = gson.fromJson(req.body(), Receita.class);
            receitaAtualizada.setId(id);
            
            // Manter o usuário original da receita
            receitaAtualizada.setUsuarioId(receitaAtual.getUsuarioId());
            
            if (receitaDAO.atualizar(receitaAtualizada)) {
                return gson.toJson(receitaAtualizada);
            } else {
                res.status(500);
                return gson.toJson(criarResposta("erro", "Falha ao atualizar receita"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao atualizar receita: " + e.getMessage()));
        }
    };

    /**
     * Rota para excluir uma receita.
     */
    public static final Route excluirReceita = (Request req, Response res) -> {
        try {
            int id = Integer.parseInt(req.params("id"));
            
            if (receitaDAO.buscarPorId(id) == null) {
                res.status(404);
                return gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
            
            if (receitaDAO.remover(id)) {
                return gson.toJson(criarResposta("sucesso", "Receita excluída com sucesso"));
            } else {
                res.status(500);
                return gson.toJson(criarResposta("erro", "Falha ao excluir receita"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao excluir receita: " + e.getMessage()));
        }
    };

    /**
     * Rota para buscar receitas por ingredientes disponíveis.
     */
    public static final Route buscarPorIngredientes = (Request req, Response res) -> {
        try {
            String ingredientesParam = req.queryParams("ids");
            
            if (ingredientesParam == null || ingredientesParam.trim().isEmpty()) {
                res.status(400);
                return gson.toJson(criarResposta("erro", "Parâmetro 'ids' é obrigatório"));
            }
            
            List<Integer> ingredientesIds = new ArrayList<>();
            String[] ids = ingredientesParam.split(",");
            
            for (String id : ids) {
                try {
                    ingredientesIds.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // Ignorar IDs inválidos
                }
            }
            
            if (ingredientesIds.isEmpty()) {
                res.status(400);
                return gson.toJson(criarResposta("erro", "Nenhum ID de ingrediente válido fornecido"));
            }
            
            List<Receita> receitas = receitaDAO.buscarPorIngredientes(ingredientesIds);
            return gson.toJson(receitas);
        } catch (SQLException e) {
            res.status(500);
            return gson.toJson(criarResposta("erro", "Erro ao buscar receitas: " + e.getMessage()));
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