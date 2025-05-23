package com.receitasfaceis.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável pela configuração e conexão com o banco de dados PostgreSQL.
 */
public class DatabaseConfig {
    // Configurações de conexão
    private static final String URL = "jdbc:postgresql://localhost:5432/receitasfaceis";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123"; // Substitua pela sua senha do PostgreSQL
    
    private static Connection connection = null;

    /**
     * Obtém uma conexão com o banco de dados.
     * 
     * @return Connection - conexão com o banco de dados
     * @throws SQLException em caso de erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver PostgreSQL não encontrado");
                throw new SQLException("Driver PostgreSQL não encontrado", e);
            }
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados se estiver aberta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
} 