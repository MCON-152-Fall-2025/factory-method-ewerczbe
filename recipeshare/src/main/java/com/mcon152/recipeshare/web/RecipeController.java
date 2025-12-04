package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final List<Recipe> recipes = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public RecipeController() {}

    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        recipe.setId(counter.incrementAndGet());
        recipes.add(recipe);
        return recipe;
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable long id) {
        return recipes.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    @DeleteMapping("/{id}")
    public boolean deleteRecipe(@PathVariable long id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId() == id) {
                recipes.remove(i);
                return true;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable long id, @RequestBody Recipe updatedRecipe) {
        for (Recipe recipe : recipes) {
            if (recipe.getId() == id) {
                recipe.setTitle(updatedRecipe.getTitle());
                recipe.setDescription(updatedRecipe.getDescription());
                recipe.setIngredients(updatedRecipe.getIngredients());
                recipe.setInstructions(updatedRecipe.getInstructions());
                return recipe;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    @PatchMapping("/{id}")
    public Recipe patchRecipe(@PathVariable long id, @RequestBody Recipe partialRecipe) {
        for (Recipe recipe : recipes) {
            if (recipe.getId() == id) {

                if (partialRecipe.getTitle() != null)
                    recipe.setTitle(partialRecipe.getTitle());

                if (partialRecipe.getDescription() != null)
                    recipe.setDescription(partialRecipe.getDescription());

                if (partialRecipe.getIngredients() != null)
                    recipe.setIngredients(partialRecipe.getIngredients());

                if (partialRecipe.getInstructions() != null)
                    recipe.setInstructions(partialRecipe.getInstructions());

                return recipe;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    public void clear() {
        recipes.clear();
        counter.set(0);

    }
}