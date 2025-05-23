package com.receitasfaceis.dao;

import com.receitasfaceis.config.DatabaseConfig;
import com.receitasfaceis.model.Ingrediente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Ingrediente
 */
public class IngredienteDAO implements DAO<Ingrediente> {

    @Override
    public Ingrediente inserir(Ingrediente ingrediente) throws SQLException {
        String sql = "INSERT INTO ingredientes (nome) VALUES (?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ingrediente.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ingrediente.setId(rs.getInt("id"));
                }
            }
            
            return ingrediente;
        }
    }

    @Override
    public boolean atualizar(Ingrediente ingrediente) throws SQLException {
        String sql = "UPDATE ingredientes SET nome = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ingrediente.getNome());
            stmt.setInt(2, ingrediente.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM ingredientes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Ingrediente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ingredientes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIngrediente(rs);
                }
            }
            
            return null;
        }
    }

    @Override
    public List<Ingrediente> listar() throws SQLException {
        String sql = "SELECT * FROM ingredientes ORDER BY nome";
        List<Ingrediente> ingredientes = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ingredientes.add(mapResultSetToIngrediente(rs));
            }
            
            return ingredientes;
        }
    }
    
    /**
     * Busca um ingrediente pelo nome.
     * 
     * @param nome Nome do ingrediente
     * @return Ingrediente encontrado ou null
     * @throws SQLException em caso de erro no banco de dados
     */
    public Ingrediente buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM ingredientes WHERE LOWER(nome) = LOWER(?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIngrediente(rs);
                }
            }
            
            return null;
        }
    }
    
    /**
     * Mapeia um ResultSet para um objeto Ingrediente.
     * 
     * @param rs ResultSet com dados do ingrediente
     * @return Objeto Ingrediente preenchido
     * @throws SQLException em caso de erro no acesso aos dados
     */
    private Ingrediente mapResultSetToIngrediente(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        
        return new Ingrediente(id, nome);
    }
} 