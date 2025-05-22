package com.receitasfaceis.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface genérica para os DAOs do sistema.
 * @param <T> Tipo da entidade
 */
public interface DAO<T> {
    
    /**
     * Insere uma entidade no banco de dados.
     * @param obj Entidade a ser inserida
     * @return Entidade com ID gerado
     * @throws SQLException em caso de erro no banco de dados
     */
    T inserir(T obj) throws SQLException;
    
    /**
     * Atualiza uma entidade no banco de dados.
     * @param obj Entidade a ser atualizada
     * @return true se a atualização foi bem-sucedida
     * @throws SQLException em caso de erro no banco de dados
     */
    boolean atualizar(T obj) throws SQLException;
    
    /**
     * Remove uma entidade do banco de dados pelo ID.
     * @param id ID da entidade a ser removida
     * @return true se a remoção foi bem-sucedida
     * @throws SQLException em caso de erro no banco de dados
     */
    boolean remover(int id) throws SQLException;
    
    /**
     * Busca uma entidade pelo ID.
     * @param id ID da entidade a ser buscada
     * @return Entidade encontrada ou null se não existir
     * @throws SQLException em caso de erro no banco de dados
     */
    T buscarPorId(int id) throws SQLException;
    
    /**
     * Lista todas as entidades.
     * @return Lista de entidades
     * @throws SQLException em caso de erro no banco de dados
     */
    List<T> listar() throws SQLException;
} 