package cz.muni.fi.pv168.recipeevidence.impl;

import cz.muni.fi.pv168.recipeevidence.RCDependencyManager;

import java.util.List;


public class RCDependencyManagerImpl implements RCDependencyManager {

    public RCDependency getDependencyById(Long id) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createDependency(RCDependency dependency) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteDependency(RCDependency dependency) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<RCDependency> findAllDependencies() throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Recipe> findRecipesInCategory(Category category) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Category> findCategoriesForRecipe(Recipe recipe) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}