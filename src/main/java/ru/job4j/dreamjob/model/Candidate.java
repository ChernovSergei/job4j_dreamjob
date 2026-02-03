package ru.job4j.dreamjob.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Candidate {
    private Integer id;
    private String name;
    private String description;
    private LocalDate createdDate;

    public Candidate(Integer id, String name, String description, LocalDate createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Candidate candidate = (Candidate) object;
        return Objects.equals(id, candidate.id) && Objects.equals(name, candidate.name) && Objects.equals(description, candidate.description) && Objects.equals(createdDate, candidate.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, createdDate);
    }
}
