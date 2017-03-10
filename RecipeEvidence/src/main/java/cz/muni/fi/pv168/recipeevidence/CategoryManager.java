package cz.muni.fi.pv168.recipeevidence;

import cz.muni.fi.pv168.recipeevidence.impl.Category;

import java.util.List;

/**
 * Represents functions for handling categories
 *
 * @author Tomas Soukal
 */
public interface CategoryManager {

    /**
     * Stores new category into database.
     * @param category
     */
    void createCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Category category);

    Category findCategoryById(Long id);

    List<Category> findCategoryByName(String name);

    List<Category> findAllCategories();
}
