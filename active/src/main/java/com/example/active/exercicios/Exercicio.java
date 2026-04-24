package com.example.active.exercicios;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToMany
    @JoinTable(name = "exercise_musculoPrimario",
    joinColumns = @JoinColumn(name = "exercise_id"),
    inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private List<Musculo> musculoPrimario;

    @ManyToMany
    @JoinTable(name = "exercise_musculoSecundario",
    joinColumns = @JoinColumn(name = "exercise_id"),
    inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private  List<Musculo> musculoSecundario;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Musculo> getMusculoPrimario() {
        return musculoPrimario;
    }

    public void setMusculoPrimario(List<Musculo> musculoPrimario) {
        this.musculoPrimario = musculoPrimario;
    }

    public List<Musculo> getMusculoSecundario() {
        return musculoSecundario;
    }

    public void setMusculoSecundario(List<Musculo> musculoSecundario) {
        this.musculoSecundario = musculoSecundario;
    }
}
