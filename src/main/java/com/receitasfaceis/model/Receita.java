package com.receitasfaceis.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa uma receita no sistema.
 */
public class Receita {
    private int id;
    private String titulo;
    private String descricao;
    private String modoPreparo;
    private int tempoPreparo; // em minutos
    private int porcoes;
    private String dificuldade;
    private String imagemUrl;
    private int usuarioId;
    private int categoriaId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Relacionamentos
    private Usuario autor;
    private Categoria categoria;
    private List<Ingrediente> ingredientes;
    
    // Construtores
    public Receita() {
        this.ingredientes = new ArrayList<>();
    }

    public Receita(String titulo, String descricao, String modoPreparo, int tempoPreparo, int porcoes, 
                  String dificuldade, int usuarioId, int categoriaId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.tempoPreparo = tempoPreparo;
        this.porcoes = porcoes;
        this.dificuldade = dificuldade;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.ingredientes = new ArrayList<>();
    }

    public Receita(int id, String titulo, String descricao, String modoPreparo, int tempoPreparo, int porcoes, 
                  String dificuldade, String imagemUrl, int usuarioId, int categoriaId, 
                  LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.tempoPreparo = tempoPreparo;
        this.porcoes = porcoes;
        this.dificuldade = dificuldade;
        this.imagemUrl = imagemUrl;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.ingredientes = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }

    public int getTempoPreparo() {
        return tempoPreparo;
    }

    public void setTempoPreparo(int tempoPreparo) {
        this.tempoPreparo = tempoPreparo;
    }

    public int getPorcoes() {
        return porcoes;
    }

    public void setPorcoes(int porcoes) {
        this.porcoes = porcoes;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
        if (autor != null) {
            this.usuarioId = autor.getId();
        }
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            this.categoriaId = categoria.getId();
        }
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }
    
    public void adicionarIngrediente(Ingrediente ingrediente) {
        if (this.ingredientes == null) {
            this.ingredientes = new ArrayList<>();
        }
        this.ingredientes.add(ingrediente);
    }
    
    public void removerIngrediente(Ingrediente ingrediente) {
        if (this.ingredientes != null) {
            this.ingredientes.removeIf(i -> i.getId() == ingrediente.getId());
        }
    }

    @Override
    public String toString() {
        return "Receita{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tempoPreparo=" + tempoPreparo +
                ", porcoes=" + porcoes +
                ", dificuldade='" + dificuldade + '\'' +
                ", usuarioId=" + usuarioId +
                ", categoriaId=" + categoriaId +
                ", dataAtualizacao=" + dataAtualizacao +
                ", ingredientes=" + (ingredientes != null ? ingredientes.size() : 0) +
                '}';
    }
} 