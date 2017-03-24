package cz.muni.fi.pv168.recipeevidence.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by lenoch on 11.3.17.
 */
public class RecipeManagerImplTest extends TestCase {

    private RecipeManagerImpl manager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws SQLException {
        manager = new RecipeManagerImpl();
    }

    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------

    private Recipe sampleRecipe1(){
        Recipe recipe = new Recipe();
        recipe.setName("Svíčková");
        Set<String> ingredients = new HashSet<String>();
        ingredients.add("maso");
        ingredients.add("mrkev");
        recipe.setIngredients(ingredients);
        recipe.setProcedure("Uděláme svíčkovou");
        recipe.setDate(LocalDate.of(2015, 3, 5));
        return recipe;
    }

    private Recipe sampleRecipe2(){
        Recipe recipe = new Recipe();
        recipe.setName("Řízek");
        Set<String> ingredients = new HashSet<String>();
        ingredients.add("maso");
        ingredients.add("strouhanka");
        recipe.setIngredients(ingredients);
        recipe.setProcedure("Osmahneme řízek");
        recipe.setDate(LocalDate.of(2016, 4, 4));
        return recipe;
    }

    //--------------------------------------------------------------------------
    // Tests for RecipeManager.createRecipe(Recipe) operation
    //--------------------------------------------------------------------------

    @Test
    public void testCreateRecipe() {
        Recipe recipe = sampleRecipe1();
        manager.createRecipe(recipe);

        Long recipeId = recipe.getId();
        assertNotNull(recipeId);

        //isNotSameAs - compares pointers
        assertThat(manager.findRecipeById(recipeId))
                .isNotSameAs(recipe)
                .isEqualToComparingFieldByField(recipe);
    }

    @Test
    public void createRecipeWithNullName() {
        Recipe recipe = sampleRecipe1();
        recipe.setName(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.createRecipe(recipe);
    }

    @Test
    public void createRecipeWithNullIngredients() {
        Recipe recipe = sampleRecipe1();
        recipe.setIngredients(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.createRecipe(recipe);
    }

    @Test
    public void createRecipeWithExistingId() {
        Recipe recipe = sampleRecipe1();
        recipe.setId(1L);

        expectedException.expect(IllegalArgumentException.class);
        manager.createRecipe(recipe);
    }


    //--------------------------------------------------------------------------
    // Tests for RecipeManager.deleteRecipe(Recipe) operation
    //--------------------------------------------------------------------------

    @Test
    public void deleteRecipe() {

        Recipe recipe1 = sampleRecipe1();
        Recipe recipe2 = sampleRecipe2();
        manager.createRecipe(recipe1);
        manager.createRecipe(recipe2);

        assertThat(manager.findRecipeById(recipe1.getId())).isNotNull();
        assertThat(manager.findRecipeById(recipe2.getId())).isNotNull();

        manager.deleteRecipe(recipe1);

        assertThat(manager.findRecipeById(recipe1.getId())).isNull();
        assertThat(manager.findRecipeById(recipe2.getId())).isNotNull();
    }

    // Test of delete operation with invalid parameter

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullRecipe() {
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteRecipe(null);
    }

    @Test
    public void deleteRecipeWithNullId() {
        Recipe recipe = sampleRecipe1();
        recipe.setId(null);
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteRecipe(recipe);
    }

    @Test
    public void deleteRecipeWithNonExistingId() {
        Recipe recipe = sampleRecipe1();
        recipe.setId(1L);
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteRecipe(recipe);
    }


    //--------------------------------------------------------------------------
    // Tests for RecipeManager.updateRecipe(Recipe) operation
    //--------------------------------------------------------------------------

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void testUpdateRecipe(Operation<Recipe> updateOperation) {
        Recipe recipeForUpdate = sampleRecipe1();
        Recipe anotherRecipe = sampleRecipe2();
        manager.createRecipe(recipeForUpdate);
        manager.createRecipe(anotherRecipe);

        updateOperation.callOn(recipeForUpdate);

        manager.updateRecipe(recipeForUpdate);
        assertThat(manager.findRecipeById(recipeForUpdate.getId()))
                .isEqualToComparingFieldByField(recipeForUpdate);
        // Check if updates didn't affected other records
        assertThat(manager.findRecipeById(anotherRecipe.getId()))
                .isEqualToComparingFieldByField(anotherRecipe);
    }

    @Test
    public void updateName() {
        testUpdateRecipe((recipe) -> recipe.setName("New name"));
    }

    @Test
    public void updateIngredients() {
        Set<String> updateIngredients = new HashSet<>();
        updateIngredients.add("Brambory");
        updateIngredients.add("Mouka");
        testUpdateRecipe((recipe) -> recipe.setIngredients(updateIngredients));
    }

    @Test
    public void updateProcedure() {
        testUpdateRecipe((recipe) -> recipe.setProcedure("Změna receptury"));
    }

    @Test
    public void updateDate() {
        testUpdateRecipe((recipe) -> recipe.setDate(LocalDate.of(2017, 3, 23)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullRecipe() {
        manager.updateRecipe(null);
    }

    @Test
    public void updateRecipeWithNullId() {
        Recipe recipe = sampleRecipe1();
        recipe.setId(null);
        expectedException.expect(IllegalEntityException.class);
        manager.updateRecipe(recipe);
    }

    @Test
    public void updateNonExistingRecipe() {
        Recipe recipe = sampleRecipe1();
        recipe.setId(1L);
        expectedException.expect(IllegalEntityException.class);
        manager.updateRecipe(recipe);
    }

    @Test
    public void updateRecipeWithNullName() {
        Recipe recipe = sampleRecipe1();
        manager.createRecipe(recipe);
        recipe.setName(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateRecipe(recipe);
    }

    @Test
    public void updateRecipeWithNullIngredients() {
        Recipe recipe = sampleRecipe1();
        manager.createRecipe(recipe);
        recipe.setIngredients(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateRecipe(recipe);
    }

    //--------------------------------------------------------------------------
    // Tests for find operations
    //--------------------------------------------------------------------------

    public void testFindAllRecipes() throws Exception {
        assertThat(manager.findAllRecipes()).isEmpty();

        Recipe recipe1 = sampleRecipe1();
        Recipe recipe2 = sampleRecipe2();

        manager.createRecipe(recipe1);
        manager.createRecipe(recipe2);

        assertThat(manager.findAllRecipes())
                .usingFieldByFieldElementComparator()
                .containsOnly(sampleRecipe1(),sampleRecipe2());
    }

    public void testFindRecipeByName() throws Exception {
        assertThat(manager.findRecipeByName("Nic")).isEmpty();

        Recipe recipeSameName1 = sampleRecipe1();
        Recipe recipeDifferentName = sampleRecipe2();
        Recipe recipeSameName2 = sampleRecipe2();
        recipeSameName2.setName("Falešná svíčková");
        manager.createRecipe(recipeSameName1);
        manager.createRecipe(recipeSameName2);
        manager.createRecipe(recipeDifferentName);

        assertThat(manager.findRecipeByName(recipeSameName1.getName()))
                .usingFieldByFieldElementComparator()
                .containsOnly(recipeSameName1,recipeSameName2);
    }

    public void testFindRecipeByNullName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        manager.findRecipeByName(null);
    }

    public void testFindRecipeByIngredients() throws Exception {
        Set<String> nonExistingIngredients = new HashSet<>();
        nonExistingIngredients.add("Nic");
        assertThat(manager.findRecipeByIngredients(nonExistingIngredients)).isEmpty();

        Recipe recipeWithDesiredIngredients1 = sampleRecipe1();
        Recipe recipeWithDesiredIngredients2 = sampleRecipe1();
        Recipe recipeWithDifferentIngredients = sampleRecipe2();
        Set<String> moreIngredientsThanDesired = recipeWithDesiredIngredients1.getIngredients();
        moreIngredientsThanDesired.add("petržel");
        recipeWithDesiredIngredients2.setIngredients(moreIngredientsThanDesired);

        manager.createRecipe(recipeWithDesiredIngredients1);
        manager.createRecipe(recipeWithDesiredIngredients2);
        manager.createRecipe(recipeWithDifferentIngredients);

        assertThat(manager.findRecipeByIngredients(recipeWithDesiredIngredients1.getIngredients()))
                .usingFieldByFieldElementComparator()
                .containsOnly(recipeWithDesiredIngredients1, recipeWithDesiredIngredients2);
    }

    public void testFindRecipeByNullIngredients() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        manager.findRecipeByIngredients(null);
    }
}