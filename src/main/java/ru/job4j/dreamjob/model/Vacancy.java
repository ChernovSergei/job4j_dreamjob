package ru.job4j.dreamjob.model;

import java.util.Objects;

public class Vacancy {
    private int id;
    private String title;

    public Vacancy(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Vacancy vacancy = (Vacancy) object;
        return id == vacancy.id && Objects.equals(title, vacancy.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
