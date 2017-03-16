package cz.muni.fi.pv168.recipeevidence.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TODO: create javadoc you lazy bitch
 *
 * @author Tomas Soukal
 */
public class CategoryManagerImplTest {

    private CategoryManagerImpl categoryManager;

    @Before
    public void setUp() throws Exception {
        categoryManager = new CategoryManagerImpl();
    }

    @Test
    public void createCategory() throws Exception {
        Category category = newCategory(42L, "Chocolate desserts");
        categoryManager.createCategory(category);

        Long categoryId = category.getCategoryID();
        assertNotNull(categoryId);

        Category result = categoryManager.findCategoryById(categoryId);
        assertEquals(category, result);
        assertNotSame(category, result);
        assertDeepEquals(category, result);
    }

    @Test
    public void deleteCategory() throws Exception {
        //TODO
    }

    @Test
    public void findCategoryById() throws Exception {
        assertNull(categoryManager.findCategoryById(1L));

        Category category = newCategory(42L, "Chocolate desserts");
        categoryManager.createCategory(category);
        Long categoryId = category.getCategoryID();

        Category result = categoryManager.findCategoryById(categoryId);
        assertEquals(category, result);
        assertDeepEquals(category, result);
    }

    @Test
    public void findCategoryByName() throws Exception {
        assertNull(categoryManager.findCategoryByName("Chocolate desserts"));

        Category category = newCategory(42L, "Chocolate desserts");
        categoryManager.createCategory(category);
        String categoryName = category.getCategoryName();

        Category result = categoryManager.findCategoryByName(categoryName);
        assertEquals(category, result);
        assertDeepEquals(category, result);
    }

    @Test
    public void findAllCategories() throws Exception {
        assertTrue(categoryManager.findAllCategories().isEmpty());

        Category category1 = newCategory(42L, "Chocolate desserts 1");
        Category category2 = newCategory(103L, "Chocolate desserts 2");

        categoryManager.createCategory(category1);
        categoryManager.createCategory(category2);

        List<Category> expected = Arrays.asList(category1, category2);
        List<Category> actual = categoryManager.findAllCategories();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void addNullCategory() {
        exception.expect(IllegalArgumentException.class);
        categoryManager.createCategory(null);
    }

    @Test
    public void addCategoryWithAssignedId() {
        Category category = newCategory(1L, "Chocolate dessert");
        category.setCategoryID(42L);
        try {
            categoryManager.createCategory(category);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void addCategoryWithAssignedName() {
        Category category = newCategory(1L, "Chocolate dessert");
        category.setCategoryName("Diet meals");
        try {
            categoryManager.createCategory(category);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void addCategoryOK() {
        Category category = newCategory(42L, "Chocolate dessert");
        categoryManager.createCategory(category);
        Category result = categoryManager.findCategoryByName("Chocolate dessert");
        assertNotNull(result);
        assertTrue(result.getCategoryID() == 42L);
    }

    @Test
    public void updateCategory() throws Exception {
        Category category1 = newCategory(42L, "Chocolate dessert");
        Category category2 = newCategory(103L, "Diet meals");
        categoryManager.createCategory(category1);
        categoryManager.createCategory(category2);

        Long categoryId = category1.getCategoryID();
        Category categoryResult = categoryManager.findCategoryById(categoryId);

        categoryResult.setCategoryName("Vegetable");
        categoryManager.updateCategory(categoryResult);
        categoryResult = categoryManager.findCategoryById(categoryId);
        assertEquals("Vegetable", categoryResult.getCategoryName());
        assertTrue(42L == categoryResult.getCategoryID());

        assertDeepEquals(category2, categoryManager.findCategoryById(category2.getCategoryID()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullCategory() {
        categoryManager.updateCategory(null);
    }

    @Test
    public void updateCategorySetIdNull() {
        Category category = newCategory(42L, "Chocolate dessert");
        categoryManager.updateCategory(category);

        Long categoryId = category.getCategoryID();
        category = categoryManager.findCategoryById(categoryId);

        category.setCategoryID(null);

        exception.expect(IllegalArgumentException.class);
        categoryManager.updateCategory(category);
    }

    @Test
    public void updateCategorySetIdNegative() {
        Category category = newCategory(42L, "Chocolate dessert");
        categoryManager.createCategory(category);

        Long categoryId = category.getCategoryID();
        category = categoryManager.findCategoryById(categoryId);

        category.setCategoryID(-1L);

        exception.expect(IllegalArgumentException.class);
        categoryManager.updateCategory(category);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullCategory() {
        categoryManager.deleteCategory(null);
    }

    @Test
    public void deleteCategoryWithNullId() {
        Category category = newCategory(42L, "Chocolate dessert");

        category.setCategoryID(null);

        try {
            categoryManager.deleteCategory(category);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void deleteCategoryWithNonexistentId() {
        Category category = newCategory(100L, "Chocolate dessert");

        category.setCategoryID(100L);

        try {
            categoryManager.deleteCategory(category);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    private static Category newCategory(Long id, String name) {
        Category category = new Category();
        category.setCategoryID(id);
        category.setCategoryName(name);
        return category;
    }

    private void assertDeepEquals(List<Category> expectedList, List<Category> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Category expected = expectedList.get(i);
            Category actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Category expected, Category actual) {
        assertEquals(expected.getCategoryID(), actual.getCategoryID());
        assertEquals(expected.getCategoryName(), actual.getCategoryName());
    }

    private static Comparator<Category> idComparator = new Comparator<Category>() {

        @Override
        public int compare(Category c1, Category c2) {
            return c1.getCategoryID().compareTo(c2.getCategoryID());
        }

    };
}