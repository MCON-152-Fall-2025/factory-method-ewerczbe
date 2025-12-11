package com.mcon152.recipeshare;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SOUP")
public class SoupRecipe extends Recipe {

    private Integer spiceLevel;

    public SoupRecipe() {}

    public Integer getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(Integer spiceLevel) {
        this.spiceLevel = spiceLevel;
    }
}