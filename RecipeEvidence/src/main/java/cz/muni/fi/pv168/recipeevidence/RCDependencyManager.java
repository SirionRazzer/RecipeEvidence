package cz.muni.fi.pv168.recipeevidence;

import cz.muni.fi.pv168.recipeevidence.impl.Category;
import cz.muni.fi.pv168.recipeevidence.impl.RCDependency;
import cz.muni.fi.pv168.recipeevidence.impl.Recipe;
import cz.muni.fi.pv168.recipeevidence.impl.ServiceFailureException;
import org.apache.derby.catalog.Dependable;

import java.util.List;

/**
 * Interface for RCDependency objects
 *
 * @author Petra Halova
 */
public interface RCDependencyManager {

    /**
     * Returns dependency for given recipe and category
     * @param recipe wanted
     * @param category wanted
     * @throws ServiceFailureException
     */
    RCDependency getDependency(Recipe recipe, Category category) throws ServiceFailureException;

    /**
     * Insert recipe intro category
     * @param recipe is recipe to be stored into the category
     * @param category is target
     * @throws ServiceFailureException
     */
    void insertRecipeIntoCategory(Recipe recipe, Category category) throws ServiceFailureException;

    /**
     * Store new dependency into database
     * @param dependency is a new dependency to be stored into database
     * @throws ServiceFailureException
     */
    void createDependency(RCDependency dependency) throws ServiceFailureException;

    /**
    * Delete given dependency from database
    * @param dependency is dependency to be deleted from database
    * @throws ServiceFailureException
    */
    void deleteDependency(RCDependency dependency) throws ServiceFailureException;

    /**
     * Get all dependencies currently stored in database
     * @return all found dependencies
     * @throws ServiceFailureException
     */
    List<RCDependency> findAllDependencies() throws ServiceFailureException;

    /**
     * Get all recipes from the fiven category
     * @param category is category with recipes
     * @return all recipes currently stored in the category
     * @throws ServiceFailureException
     */
    List<Recipe> findRecipesInCategory(Category category) throws ServiceFailureException;

    /**
     * Get all categories containing the given recipe
     * @param recipe is a recipe stored in categories
     * @return all categories with this recipe
     * @throws ServiceFailureException
     */
    List<Category> findCategoriesForRecipe(Recipe recipe) throws ServiceFailureException;
}