package com.receitasfaceis.model;

import java.time.LocalDateTime;
import com.google.gson.annotations.Expose;

/**
 * Classe que representa um usuário no sistema.
 */
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    
    // Utilizamos @Expose(serialize = false) para evitar problemas de serialização com Gson
    @Expose(serialize = false)
    private LocalDateTime dataCriacao;
    
    @Expose(serialize = false)
    private LocalDateTime ultimoAcesso;

    // Construtores
    public Usuario() {
    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = LocalDateTime.now();
    }

    public Usuario(int id, String nome, String email, String senha, LocalDateTime dataCriacao, LocalDateTime ultimoAcesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
        this.ultimoAcesso = ultimoAcesso;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", ultimoAcesso=" + ultimoAcesso +
                '}';
    }
} 