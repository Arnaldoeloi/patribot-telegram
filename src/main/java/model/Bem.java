package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

public class Bem  {
    private Integer id ;
    private String nome, descricao;
    private Localizacao localizacao;
    private Categoria categoria;


    public Bem(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;

    }
    public Bem(String nome, String descricao, Localizacao localizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;

    }
    public Bem(String nome, String descricao, Localizacao localizacao, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.categoria = categoria;

    }

    public Bem(Integer id, String nome, String descricao, Localizacao localizacao, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "\n" +
                "CODIGO = " + id + '\n' +
                "NOME = " + nome + '\n' +
                "DESCRIÇÃO = " + descricao + '\n' +
                "LOCALIZACAO = " + localizacao + '\n' +
                "CATEGORIA = " + localizacao + '\n' +
                '\n';
    }
}
