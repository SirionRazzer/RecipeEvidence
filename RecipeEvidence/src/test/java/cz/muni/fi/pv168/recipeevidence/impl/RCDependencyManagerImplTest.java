package cz.muni.fi.pv168.recipeevidence.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import static org.assertj.core.api.Assertions.*;

/**
 * TODO: create javadoc you lazy bitch
 *
 * @author Tomas Soukal
 */
public class RCDependencyManagerImplTest {

    private RCDependencyManagerImpl manager;

    @Rule
    // attribute annotated with @Rule annotation must be public :-(
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test(expected = IllegalArgumentException.class)
    public void createNullDependency() {
        manager.createDependency(null);
    }

    @Test
    public void createDependencyWithExistingId() {
        RCDependency dependency = sampleDependency().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.createDependency(dependency);
    }

    @Test
    public void createDependencyWithNullRecipe() {
        RCDependency dependency = sampleDependency().recipe(null).build();
        manager.createDependency(dependency);

        assertThat(manager.getDependencyById(dependency.getId()))
                .isNotNull()
                .isEqualToComparingFieldByField(dependency);
    }

    //TODO vse nize, od radku 242 v GraveManagerImplTest.java

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