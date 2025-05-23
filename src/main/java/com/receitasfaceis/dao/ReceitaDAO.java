package com.receitasfaceis.dao;

import com.receitasfaceis.config.DatabaseConfig;
import com.receitasfaceis.model.Receita;
import com.receitasfaceis.model.Ingrediente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Receita
 */
public class ReceitaDAO implements DAO<Receita> {

    @Override
    public Receita inserir(Receita receita) throws SQLException {
        String sql = "INSERT INTO receitas (titulo, descricao, modo_preparo, tempo_preparo, porcoes, " +
                    "dificuldade, imagem_url, usuario_id, categoria_id, data_criacao, data_atualizacao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, receita.getTitulo());
            stmt.setString(2, receita.getDescricao());
            stmt.setString(3, receita.getModoPreparo());
            stmt.setInt(4, receita.getTempoPreparo());
            stmt.setInt(5, receita.getPorcoes());
            stmt.setString(6, receita.getDificuldade());
            stmt.setString(7, receita.getImagemUrl());
            stmt.setInt(8, receita.getUsuarioId());
            
            if (receita.getCategoriaId() > 0) {
                stmt.setInt(9, receita.getCategoriaId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            
            LocalDateTime agora = LocalDateTime.now();
            stmt.setTimestamp(10, Timestamp.valueOf(agora));
            stmt.setTimestamp(11, Timestamp.valueOf(agora));
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                receita.setId(rs.getInt("id"));
            }
            
            // Inserir ingredientes, se houver
            if (receita.getIngredientes() != null && !receita.getIngredientes().isEmpty()) {
                inserirIngredientesReceita(conn, receita);
            }
            
            conn.commit();
            return receita;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean atualizar(Receita receita) throws SQLException {
        String sql = "UPDATE receitas SET titulo = ?, descricao = ?, modo_preparo = ?, " +
                    "tempo_preparo = ?, porcoes = ?, dificuldade = ?, imagem_url = ?, " +
                    "categoria_id = ?, data_atualizacao = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, receita.getTitulo());
            stmt.setString(2, receita.getDescricao());
            stmt.setString(3, receita.getModoPreparo());
            stmt.setInt(4, receita.getTempoPreparo());
            stmt.setInt(5, receita.getPorcoes());
            stmt.setString(6, receita.getDificuldade());
            stmt.setString(7, receita.getImagemUrl());
            
            if (receita.getCategoriaId() > 0) {
                stmt.setInt(8, receita.getCategoriaId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(10, receita.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            // Atualizar ingredientes
            if (receita.getIngredientes() != null) {
                // Remover ingredientes existentes
                removerIngredientesReceita(conn, receita.getId());
                
                // Inserir novos ingredientes
                if (!receita.getIngredientes().isEmpty()) {
                    inserirIngredientesReceita(conn, receita);
                }
            }
            
            conn.commit();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM receitas WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Receita buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM receitas WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Receita receita = mapResultSetToReceita(rs);
                    
                    // Carregar ingredientes
                    carregarIngredientes(conn, receita);
                    
                    return receita;
                }
            }
            
            return null;
        }
    }

    @Override
    public List<Receita> listar() throws SQLException {
        String sql = "SELECT * FROM receitas ORDER BY data_atualizacao DESC";
        List<Receita> receitas = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Receita receita = mapResultSetToReceita(rs);
                receitas.add(receita);
            }
            
            return receitas;
        }
    }
    
    /**
     * Busca receitas por ingredientes disponíveis.
     * 
     * @param ingredientesIds Lista de IDs de ingredientes
     * @return Lista de receitas
     * @throws SQLException em caso de erro no banco de dados
     */
    public List<Receita> buscarPorIngredientes(List<Integer> ingredientesIds) throws SQLException {
        if (ingredientesIds == null || ingredientesIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.*, COUNT(ri.ingrediente_id) AS ingredientes_encontrados ");
        sql.append("FROM receitas r ");
        sql.append("JOIN receitas_ingredientes ri ON r.id = ri.receita_id ");
        sql.append("WHERE ri.ingrediente_id IN (");
        
        // Adiciona placeholders para cada ID
        for (int i = 0; i < ingredientesIds.size(); i++) {
            sql.append("?");
            if (i < ingredientesIds.size() - 1) {
                sql.append(",");
            }
        }
        
        sql.append(") ");
        sql.append("GROUP BY r.id ");
        sql.append("ORDER BY ingredientes_encontrados DESC");
        
        List<Receita> receitas = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Define os valores dos IDs nos placeholders
            for (int i = 0; i < ingredientesIds.size(); i++) {
                stmt.setInt(i + 1, ingredientesIds.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Receita receita = mapResultSetToReceita(rs);
                    receitas.add(receita);
                }
            }
        }
        
        return receitas;
    }
    
    /**
     * Mapeia um ResultSet para um objeto Receita.
     * 
     * @param rs ResultSet com dados da receita
     * @return Objeto Receita preenchido
     * @throws SQLException em caso de erro no acesso aos dados
     */
    private Receita mapResultSetToReceita(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String descricao = rs.getString("descricao");
        String modoPreparo = rs.getString("modo_preparo");
        int tempoPreparo = rs.getInt("tempo_preparo");
        int porcoes = rs.getInt("porcoes");
        String dificuldade = rs.getString("dificuldade");
        String imagemUrl = rs.getString("imagem_url");
        int usuarioId = rs.getInt("usuario_id");
        int categoriaId = rs.getInt("categoria_id");
        
        Timestamp dataCriacaoTs = rs.getTimestamp("data_criacao");
        LocalDateTime dataCriacao = dataCriacaoTs != null ? 
                dataCriacaoTs.toLocalDateTime() : null;
                
        Timestamp dataAtualizacaoTs = rs.getTimestamp("data_atualizacao");
        LocalDateTime dataAtualizacao = dataAtualizacaoTs != null ? 
                dataAtualizacaoTs.toLocalDateTime() : null;
                
        return new Receita(id, titulo, descricao, modoPreparo, tempoPreparo, porcoes,
                          dificuldade, imagemUrl, usuarioId, categoriaId, dataCriacao, dataAtualizacao);
    }
    
    /**
     * Carrega os ingredientes de uma receita.
     * 
     * @param conn Conexão com o banco de dados
     * @param receita Receita a ter os ingredientes carregados
     * @throws SQLException em caso de erro no banco de dados
     */
    private void carregarIngredientes(Connection conn, Receita receita) throws SQLException {
        String sql = "SELECT i.*, ri.quantidade, ri.observacao FROM ingredientes i " +
                    "JOIN receitas_ingredientes ri ON i.id = ri.ingrediente_id " +
                    "WHERE ri.receita_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receita.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    double quantidade = rs.getDouble("quantidade");
                    String observacao = rs.getString("observacao");
                    
                    Ingrediente ingrediente = new Ingrediente(id, nome);
                    ingrediente.setQuantidade(quantidade);
                    ingrediente.setObservacao(observacao);
                    
                    receita.adicionarIngrediente(ingrediente);
                }
            }
        }
    }
    
    /**
     * Insere os ingredientes de uma receita no banco de dados.
     * 
     * @param conn Conexão com o banco de dados
     * @param receita Receita com ingredientes a serem inseridos
     * @throws SQLException em caso de erro no banco de dados
     */
    private void inserirIngredientesReceita(Connection conn, Receita receita) throws SQLException {
        String sql = "INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade, observacao) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Ingrediente ingrediente : receita.getIngredientes()) {
                stmt.setInt(1, receita.getId());
                stmt.setInt(2, ingrediente.getId());
                stmt.setDouble(3, ingrediente.getQuantidade());
                stmt.setString(4, ingrediente.getObservacao());
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    /**
     * Remove todos os ingredientes de uma receita.
     * 
     * @param conn Conexão com o banco de dados
     * @param receitaId ID da receita
     * @throws SQLException em caso de erro no banco de dados
     */
    private void removerIngredientesReceita(Connection conn, int receitaId) throws SQLException {
        String sql = "DELETE FROM receitas_ingredientes WHERE receita_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receitaId);
            stmt.executeUpdate();
        }
    }
} 