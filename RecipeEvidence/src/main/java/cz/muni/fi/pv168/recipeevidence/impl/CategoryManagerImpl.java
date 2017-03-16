package cz.muni.fi.pv168.recipeevidence.impl;

import cz.muni.fi.pv168.recipeevidence.CategoryManager;

import java.util.List;

/**
 * TODO: create javadoc you lazy bitch
 *
 * @author Tomas Soukal
 */
public class CategoryManagerImpl implements CategoryManager {

    @Override
    public void createCategory(Category category) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateCategory(Category category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteCategory(Category category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Category findCategoryById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Category findCategoryByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Category> findAllCategories() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
