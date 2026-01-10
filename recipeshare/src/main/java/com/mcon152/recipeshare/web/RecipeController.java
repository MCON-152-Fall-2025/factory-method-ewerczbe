package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.service.RecipeFactory;
import com.mcon152.recipeshare.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody RecipeRequest recipeRequest) {
        logger.info("POST /api/recipes – create recipe request received");
        logger.debug("Incoming recipe summary: title={}, type={}", recipeRequest.getTitle(), recipeRequest.getType());

        try {
            Recipe toSave = RecipeFactory.createFromRequest(recipeRequest);
            Recipe saved = recipeService.addRecipe(toSave);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            logger.info("Created recipe with id={}", saved.getId());
            return ResponseEntity.created(location).body(saved);

        } catch (Exception e) {
            logger.error("Error occurred while adding recipe: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        logger.info("GET /api/recipes – fetch all recipes");

        List<Recipe> recipes = recipeService.getAllRecipes();
        logger.info("Returned {} recipes", recipes.size());

        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable long id) {
        logger.info("GET /api/recipes/{} – fetch recipe", id);

        return recipeService.getRecipeById(id)
                .map(recipe -> {
                    logger.info("Found recipe id={}", id);
                    return ResponseEntity.ok(recipe);
                })
                .orElseGet(() -> {
                    logger.warn("Recipe with id={} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        logger.info("DELETE /api/recipes/{} – delete request", id);

        try {
            boolean deleted = recipeService.deleteRecipe(id);

            if (deleted) {
                logger.info("Deleted recipe id={}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Attempted to delete non-existing recipe id={}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Unexpected error deleting recipe id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable long id, @RequestBody RecipeRequest updatedRequest) {
        logger.info("PUT /api/recipes/{} – full update request", id);
        logger.debug("Update summary: title={}, type={}", updatedRequest.getTitle(), updatedRequest.getType());

        MDC.put("recipeName", updatedRequest.getTitle());

        try {
            Recipe updatedRecipe = RecipeFactory.createFromRequest(updatedRequest);

            return recipeService.updateRecipe(id, updatedRecipe)
                    .map(recipe -> {
                        logger.info("Updated recipe id={}", id);
                        return ResponseEntity.ok(recipe);
                    })
                    .orElseGet(() -> {
                        logger.warn("Recipe id={} not found for update", id);
                        return ResponseEntity.notFound().build();
                    });

        } catch (Exception e) {
            logger.error("Error updating recipe id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> patchRecipe(@PathVariable long id, @RequestBody RecipeRequest partialRequest) {
        logger.info("PATCH /api/recipes/{} – partial update request", id);
        logger.debug("Patch summary: title={}, type={}", partialRequest.getTitle(), partialRequest.getType());

        MDC.put("recipeName", partialRequest.getTitle());

        try {
            Recipe partialRecipe = RecipeFactory.createFromRequest(partialRequest);

            return recipeService.patchRecipe(id, partialRecipe)
                    .map(recipe -> {
                        logger.info("Patched recipe id={}", id);
                        return ResponseEntity.ok(recipe);
                    })
                    .orElseGet(() -> {
                        logger.warn("Recipe id={} not found for patch", id);
                        return ResponseEntity.notFound().build();
                    });

        } catch (Exception e) {
            logger.error("Error patching recipe id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } finally {
            MDC.clear();
        }
    }
}