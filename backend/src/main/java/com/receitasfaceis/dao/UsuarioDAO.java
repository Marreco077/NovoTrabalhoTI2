package com.receitasfaceis.dao;

import com.receitasfaceis.config.DatabaseConfig;
import com.receitasfaceis.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Usuario
 */
public class UsuarioDAO implements DAO<Usuario> {

    @Override
    public Usuario inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, data_criacao) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            // Hash da senha
            String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
            stmt.setString(3, senhaHash);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario.setId(rs.getInt("id"));
            }
            
            return usuario;
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, ultimo_acesso = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            
            if (usuario.getUltimoAcesso() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(usuario.getUltimoAcesso()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            
            stmt.setInt(4, usuario.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }
    
    /**
     * Atualiza a senha de um usuário.
     * 
     * @param id ID do usuário
     * @param novaSenha Nova senha (será criptografada)
     * @return true se a operação foi bem-sucedida
     * @throws SQLException em caso de erro no banco de dados
     */
    public boolean atualizarSenha(int id, String novaSenha) throws SQLException {
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Hash da nova senha
            String senhaHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            stmt.setString(1, senhaHash);
            stmt.setInt(2, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            
            return null;
        }
    }
    
    /**
     * Busca um usuário pelo email.
     * 
     * @param email Email do usuário a ser buscado
     * @return Usuário encontrado ou null se não existir
     * @throws SQLException em caso de erro no banco de dados
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            
            return null;
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
            return usuarios;
        }
    }
    
    /**
     * Autentica um usuário pelo email e senha.
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário (não criptografada)
     * @return Usuário autenticado ou null se credenciais inválidas
     * @throws SQLException em caso de erro no banco de dados
     */
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashSenha = rs.getString("senha");
                
                // Verifica se a senha fornecida corresponde ao hash armazenado
                if (BCrypt.checkpw(senha, hashSenha)) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    
                    // Atualiza o último acesso
                    atualizarUltimoAcesso(usuario.getId());
                    
                    return usuario;
                }
            }
            
            return null;
        }
    }
    
    /**
     * Atualiza o timestamp de último acesso do usuário.
     * 
     * @param id ID do usuário
     * @throws SQLException em caso de erro no banco de dados
     */
    private void atualizarUltimoAcesso(int id) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_acesso = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mapeia um ResultSet para um objeto Usuario.
     * 
     * @param rs ResultSet com dados do usuário
     * @return Objeto Usuario preenchido
     * @throws SQLException em caso de erro no acesso aos dados
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        
        Timestamp dataCriacaoTs = rs.getTimestamp("data_criacao");
        LocalDateTime dataCriacao = dataCriacaoTs != null ? 
                dataCriacaoTs.toLocalDateTime() : null;
                
        Timestamp ultimoAcessoTs = rs.getTimestamp("ultimo_acesso");
        LocalDateTime ultimoAcesso = ultimoAcessoTs != null ? 
                ultimoAcessoTs.toLocalDateTime() : null;
                
        return new Usuario(id, nome, email, senha, dataCriacao, ultimoAcesso);
    }
} 