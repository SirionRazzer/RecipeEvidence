package cz.muni.fi.pv168.recipeevidence;

import cz.muni.fi.pv168.recipeevidence.impl.Category;
import cz.muni.fi.pv168.recipeevidence.impl.RCDependency;
import cz.muni.fi.pv168.recipeevidence.impl.Recipe;

import java.util.List;

/**
 * Interface for RCDependency objects
 * @author Petra Halova
 */
public interface RCDependencyManager {

    void deleteDependency(RCDependency dependency);

    void updateDependency(RCDependency dependency);

    void insertRecipeIntoCategory(Recipe recipe, Category category);

    RCDependency getDependencyById(Long id);

    List<RCDependency> findAllDependencies();

    List<Recipe> findRecipesInCategory(Category category);

    List<Category> findCategoriesForRecipe(Recipe recipe);

}