package com.receitasfaceis.model;

/**
 * Classe que representa um ingrediente no sistema.
 */
public class Ingrediente {
    private int id;
    private String nome;
    
    // Para relacionamento muitos-para-muitos com receitas
    private double quantidade;
    private String observacao;

    // Construtores
    public Ingrediente() {
    }

    public Ingrediente(String nome) {
        this.nome = nome;
    }

    public Ingrediente(int id, String nome) {
        this.id = id;
        this.nome = nome;
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

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "Ingrediente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", quantidade=" + quantidade +
                (observacao != null ? ", observacao='" + observacao + '\'' : "") +
                '}';
    }
} 