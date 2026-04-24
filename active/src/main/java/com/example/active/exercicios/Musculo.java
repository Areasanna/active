package com.example.active.exercicios;

import jakarta.persistence.*;

@Entity
public class Musculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(name = "nome_en")
    private String nomeEn;

    public Musculo(){

    }
    public Musculo(Long id, String nome, String nomeEn) {
        this.id = id;
        this.nome = nome;
        this.nomeEn = nomeEn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeEn() {
        return nomeEn;
    }

    public void setNomeEn(String nomeEn) {
        this.nomeEn = nomeEn;
    }
}

