package cz.muni.fi.pv168.recipeevidence;

import cz.muni.fi.pv168.recipeevidence.impl.Category;
import cz.muni.fi.pv168.recipeevidence.impl.RCDependency;
import cz.muni.fi.pv168.recipeevidence.impl.Recipe;
import cz.muni.fi.pv168.recipeevidence.impl.ServiceFailureException;

import java.util.List;

/**
 * Interface for RCDependency objects
 *
 * @author Petra Halova
 */
public interface RCDependencyManager {

    void createDependency(RCDependency dependency) throws ServiceFailureException;

    void deleteDependency(RCDependency dependency) throws ServiceFailureException;

    void updateDependency(RCDependency dependency) throws ServiceFailureException;

    void insertRecipeIntoCategory(Recipe recipe, Category category) throws ServiceFailureException;

    RCDependency getDependencyById(Long id) throws ServiceFailureException;

    List<RCDependency> findAllDependencies() throws ServiceFailureException;

    List<Recipe> findRecipesInCategory(Category category) throws ServiceFailureException;

    List<Category> findCategoriesForRecipe(Recipe recipe) throws ServiceFailureException;

}