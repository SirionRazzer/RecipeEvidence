package cz.muni.fi.pv168.recipeevidence.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.assertj.core.api.Assertions.*;

/**
 * TODO: create javadoc you lazy bitch
 *
 * @author Tomas Soukal
 */
public class RCDependencyManagerImplTest {

    private RCDependencyManagerImpl manager;

    @Before
    public void setUp() throws Exception {
        manager = new RCDependencyManagerImpl();

    }

    private RCDependencyBuilder sampleDependency() {
        return new RCDependencyBuilder()
                .id(null)
                .category(null)
                .recipe(null);
    }

    @Test
    public void createDependency() {
        RCDependency dependency = sampleDependency().build();
        manager.createDependency(dependency);

        Long dependencyId = dependency.getId();
        assertThat(manager.getDependencyById(dependencyId))
                .isNotSameAs(dependency)
                .isEqualToComparingFieldByField(dependency);
    }

    @Test
    public void findAllDependencies() throws Exception {

        assertThat(manager.findAllDependencies().isEmpty());

        RCDependency dependency1 = sampleDependency().build();

        manager.createDependency(dependency1);

        assertThat(manager.findAllDependencies())
                .usingFieldByFieldElementComparator()
                .containsOnly(dependency1);
    }



    //TODO all code below

    @Test
    public void deleteDependency() throws Exception {

    }

    @Test
    public void updateDependency() throws Exception {

    }

    @Test
    public void insertRecipeIntoCategory() throws Exception {

    }

    @Test
    public void getDependencyById() throws Exception {

    }

    @Test
    public void findRecipesInCategory() throws Exception {

    }

    @Test
    public void findCategoriesForRecipe() throws Exception {

    }

}