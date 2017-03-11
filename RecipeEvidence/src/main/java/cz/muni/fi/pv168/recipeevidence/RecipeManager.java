package cz.muni.fi.pv168.recipeevidence;

import cz.muni.fi.pv168.recipeevidence.impl.Recipe;

import java.util.List;
import java.util.Set;

/**
 * Interface for Recipe objects
 * @author Petra Halova
 */
public interface RecipeManager {

    void createRecipe(Recipe recipe);

    void updateRecipe(Recipe recipe);

    void deleteRecipe(Recipe recipe);

    List<Recipe> findAllRecipes();

    Recipe findRecipeById(Long id);

    List<Recipe> findRecipeByName(String name);

    /**
     * @param ingredients is list of desired ingredients
     * @return list of Recipes containing all given ingredients
     */
    List<Recipe> findRecipeByIngredients(Set<String> ingredients);

}