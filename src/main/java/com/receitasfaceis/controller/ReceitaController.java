package com.receitasfaceis.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.receitasfaceis.Application;
import com.receitasfaceis.dao.ReceitaDAO;
import com.receitasfaceis.dao.IngredienteDAO;
import com.receitasfaceis.model.Receita;
import com.receitasfaceis.model.Ingrediente;
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

    private static final ReceitaDAO receitaDAO = new ReceitaDAO();
    private static final IngredienteDAO ingredienteDAO = new IngredienteDAO();

    /**
     * Rota para listar todas as receitas.
     */
    public static final Route listarReceitas = (Request req, Response res) -> {
        try {
            return Application.gson.toJson(receitaDAO.listar());
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao listar receitas: " + e.getMessage()));
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
                return Application.gson.toJson(receita);
            } else {
                res.status(404);
                return Application.gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return Application.gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao buscar receita: " + e.getMessage()));
        }
    };

    /**
     * Rota para criar uma nova receita.
     */
    public static final Route criarReceita = (Request req, Response res) -> {
        try {
            System.out.println("Iniciando criação de receita");
            System.out.println("Corpo da requisição: " + req.body());
            
            Receita receita = Application.gson.fromJson(req.body(), Receita.class);
            System.out.println("Receita deserializada: " + receita);
            
            if (receita.getTitulo() == null || receita.getTitulo().trim().isEmpty() ||
                receita.getModoPreparo() == null || receita.getModoPreparo().trim().isEmpty() ||
                receita.getUsuarioId() <= 0) {
                
                res.status(400);
                String erro = "Título, modo de preparo e usuário são obrigatórios";
                System.out.println("Erro de validação: " + erro);
                return Application.gson.toJson(criarResposta("erro", erro));
            }
            
            // Processamento dos ingredientes
            System.out.println("Ingredientes enviados: " + 
                      (receita.getIngredientes() == null ? "nulo" : receita.getIngredientes().size()));
                      
            if (receita.getIngredientes() != null && !receita.getIngredientes().isEmpty()) {
                List<Ingrediente> ingredientesProcessados = new ArrayList<>();
                
                for (Ingrediente ingrediente : receita.getIngredientes()) {
                    System.out.println("Processando ingrediente: " + ingrediente.getNome());
                    
                    // Verifica se o ingrediente já existe no banco de dados
                    Ingrediente ingredienteExistente = ingredienteDAO.buscarPorNome(ingrediente.getNome());
                    
                    if (ingredienteExistente != null) {
                        // Usa o ingrediente existente, mas mantém a quantidade informada
                        System.out.println("Ingrediente já existe: " + ingredienteExistente.getId());
                        ingredienteExistente.setQuantidade(ingrediente.getQuantidade());
                        ingredienteExistente.setObservacao(ingrediente.getObservacao());
                        ingredientesProcessados.add(ingredienteExistente);
                    } else {
                        // Cria um novo ingrediente no banco de dados
                        System.out.println("Criando novo ingrediente: " + ingrediente.getNome());
                        Ingrediente novoIngrediente = ingredienteDAO.inserir(ingrediente);
                        System.out.println("Novo ingrediente criado com ID: " + novoIngrediente.getId());
                        ingredientesProcessados.add(novoIngrediente);
                    }
                }
                
                // Substitui os ingredientes da receita pelos processados
                receita.setIngredientes(ingredientesProcessados);
            }
            
            System.out.println("Inserindo receita no banco de dados");
            receita = receitaDAO.inserir(receita);
            System.out.println("Receita inserida com ID: " + receita.getId());
            
            res.status(201); // Created
            String responseJson = Application.gson.toJson(receita);
            System.out.println("Resposta JSON: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao criar receita: " + e.getMessage()));
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
                return Application.gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
            
            Receita receitaAtualizada = Application.gson.fromJson(req.body(), Receita.class);
            receitaAtualizada.setId(id);
            
            // Manter o usuário original da receita
            receitaAtualizada.setUsuarioId(receitaAtual.getUsuarioId());
            
            if (receitaDAO.atualizar(receitaAtualizada)) {
                return Application.gson.toJson(receitaAtualizada);
            } else {
                res.status(500);
                return Application.gson.toJson(criarResposta("erro", "Falha ao atualizar receita"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return Application.gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao atualizar receita: " + e.getMessage()));
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
                return Application.gson.toJson(criarResposta("erro", "Receita não encontrada"));
            }
            
            if (receitaDAO.remover(id)) {
                return Application.gson.toJson(criarResposta("sucesso", "Receita excluída com sucesso"));
            } else {
                res.status(500);
                return Application.gson.toJson(criarResposta("erro", "Falha ao excluir receita"));
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return Application.gson.toJson(criarResposta("erro", "ID inválido"));
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao excluir receita: " + e.getMessage()));
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
                return Application.gson.toJson(criarResposta("erro", "Parâmetro 'ids' é obrigatório"));
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
                return Application.gson.toJson(criarResposta("erro", "Nenhum ID de ingrediente válido fornecido"));
            }
            
            List<Receita> receitas = receitaDAO.buscarPorIngredientes(ingredientesIds);
            return Application.gson.toJson(receitas);
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao buscar receitas: " + e.getMessage()));
        }
    };

    /**
     * Rota para buscar todas as receitas de um usuário específico.
     */
    public static final Route getReceitasDoUsuario = (Request req, Response res) -> {
        try {
            int usuarioId = Integer.parseInt(req.params("usuarioId"));
            List<Receita> receitas = receitaDAO.buscarPorUsuarioId(usuarioId);
            return Application.gson.toJson(receitas);
        } catch (NumberFormatException e) {
            res.status(400);
            return Application.gson.toJson(criarResposta("erro", "ID de usuário inválido"));
        } catch (SQLException e) {
            res.status(500);
            return Application.gson.toJson(criarResposta("erro", "Erro ao buscar receitas do usuário: " + e.getMessage()));
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