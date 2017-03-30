package cz.muni.fi.pv168.recipeevidence.impl;

import cz.muni.fi.pv168.recipeevidence.RecipeManager;
import cz.muni.fi.pv168.recipeevidence.common.DBUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RecipeManagerImpl implements RecipeManager {

    private static final Logger logger = Logger.getLogger(RecipeManagerImpl.class.getName());

    private DataSource dataSource;
    private final Clock clock;


    public RecipeManagerImpl(Clock clock) {
        this.clock = clock;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe is null");
        }
        if (recipe.getName() == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (recipe.getIngredients() == null) {
            throw new IllegalArgumentException("Recipe without ingredients cannot exist");
        }
        if (recipe.getProcedure() == null) {
            throw new IllegalArgumentException("Recipe without procedure cannot exist");
        }
        LocalDate today = LocalDate.now(clock);
        if (recipe.getDate() != null && !recipe.getDate().equals(today)) {
            throw new IllegalArgumentException("Date of a recipe creation must be today ");
        }
    }


    //--------------------------------------------------------------------------
    // Implementing RecipeManager functions
    //--------------------------------------------------------------------------


    public void createRecipe(Recipe recipe) throws ServiceFailureException {
        checkDataSource();
        validate(recipe);
        if (recipe.getId() != null) {
            throw new IllegalEntityException("recipe id is already set");
        }
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            //TABLE RECIPE
            statement1 = connection.prepareStatement(
                    "INSERT INTO Recipe (name,procedure,date) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement1.setString(1, recipe.getName());
            statement1.setString(2, recipe.getProcedure());
            statement1.setDate(3, toSqlDate(recipe.getDate()));

            int count1 = statement1.executeUpdate();
            DBUtils.checkUpdatesCount(count1, recipe, true);
            //TABLE INGREDIENTS
            for (String element : recipe.getIngredients()) {
                statement2 = connection.prepareStatement("INSERT INTO Ingredients (id, name) VALUES (?,?)");
                statement2.setLong(1, recipe.getId());
                statement2.setString(2, element);
                int count2 = statement2.executeUpdate();
                DBUtils.checkUpdatesCount(count2, recipe, true);
            }
            Long id = DBUtils.getId(statement1.getGeneratedKeys());
            recipe.setId(id);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting grave into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement1, statement2);
        }

    }


    public void updateRecipe(Recipe recipe) {
        checkDataSource();
        validate(recipe);

        if (recipe.getId() == null) {
            throw new IllegalEntityException("recipe id is null");
        }
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement1 = connection.prepareStatement(
                    "UPDATE Recipe SET name = ?, procedure = ?, date  = ? WHERE id = ?");
            statement1.setString(1, recipe.getName());
            statement1.setString(2, recipe.getProcedure());
            statement1.setDate(3, toSqlDate(recipe.getDate()));
            statement1.setLong(4, recipe.getId());
            int count1 = statement1.executeUpdate();
            DBUtils.checkUpdatesCount(count1, recipe, false);
            for (String element : recipe.getIngredients()) {
                statement2 = connection.prepareStatement(
                        "UPDATE Ingredients SET String = ?, WHERE id = ?");
                statement2.setString(1, element);
                int count2 = statement2.executeUpdate();
                DBUtils.checkUpdatesCount(count2, recipe, false); // musi tam byt to count tolikrat?
            }

            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating recipe in the DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement1, statement2);
        }
    }


    public void deleteRecipe(Recipe recipe) {
        checkDataSource();
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is null");
        }
        if (recipe.getId() == null) {
            throw new IllegalEntityException("recipe id is null");
        }
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;

        RCDependencyManagerImpl dependency = new RCDependencyManagerImpl();
        List<Category> categories = dependency.findCategoriesForRecipe(recipe);
        for (Category element : categories) {
            dependency.deleteDependency(dependency.getDependency(recipe, element));
        }

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement1 = connection.prepareStatement(
                    "DELETE FROM Recipe WHERE id = ?");
            statement1.setLong(1, recipe.getId());
            int count1 = statement1.executeUpdate();
            DBUtils.checkUpdatesCount(count1, recipe, false);

            statement2 = connection.prepareStatement(
                    "DELETE FROM Ingredients WHERE id = ?");
            statement2.setLong(1, recipe.getId());
            int count2 = statement2.executeUpdate();
            DBUtils.checkUpdatesCount(count2, recipe, false);

            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting Recipe from the DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement1, statement2);
        }
    }


    public List<Recipe> findAllRecipes() {
        checkDataSource();
        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        try {
            connection = dataSource.getConnection();
            statement1 = connection.prepareStatement(
                    "SELECT id, name, procedure, date FROM Recipe");
            List<Recipe> recipes = executeQueryForMultipleRecipes(statement1);

            statement2 = connection.prepareStatement(
                    "SELECT name FROM Ingredients WHERE id = ?");
            for (Recipe element : recipes) {
                statement2.setLong(1, element.getId());
                element.setIngredients(findIngredients(statement2));
            }
            return recipes;

        } catch (SQLException ex) {
            String msg = "Error when getting all recipes from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, statement1);
            DBUtils.closeQuietly(connection, statement2);
        }
    }


    public Recipe findRecipeById(Long id) {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        try {
            connection = dataSource.getConnection();
            statement1 = connection.prepareStatement(
                    "SELECT name, ingredients, procedure, date FROM Recipe WHERE id = ?");
            statement1.setLong(1, id);
            statement2 = connection.prepareStatement("SELECT name FROM Ingredients WHERE id = ?");
            statement2.setLong(1, id);
            Recipe recipe = executeQueryForSingleRecipe(statement1);
            recipe.setIngredients(findIngredients(statement2));
            return recipe;
        } catch (SQLException ex) {
            String msg = "Error when getting body with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, statement1, statement2);
        }
    }


    public List<Recipe> findRecipeByName(String name) {
        checkDataSource();

        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        Connection connection = null;
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        try {
            connection = dataSource.getConnection();
            statement1 = connection.prepareStatement(
                    "SELECT id, name, procedure, date FROM Recipe WHERE name = ?");
            statement1.setString(1, name);
            List<Recipe> recipes =  executeQueryForMultipleRecipes(statement1);

            statement2 = connection.prepareStatement(
                    "SELECT name FROM Ingredients WHERE id = ?");
            for (Recipe element : recipes) {
                statement2.setLong(1, element.getId());
                element.setIngredients(findIngredients(statement2));
            }
            return recipes;

        } catch (SQLException ex) {
            String msg = "Error when getting recipe with name = " + name + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, statement1, statement2);
        }
    }

    public List<Recipe> findRecipeByIngredients(Set<String> ingredients) {
        checkDataSource();

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("No ingredients");
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            Set<Long> containingAllIngredients = new TreeSet<>();
            String[] arrayOfIngredients = ingredients.toArray(new String[ingredients.size()]);
            statement = connection.prepareStatement(
                    "SELECT id FROM Ingredients WHERE name = ?");
            statement.setString(1, arrayOfIngredients[0]);
            ResultSet rs1 = statement.executeQuery();
            while (rs1.next()) {
                containingAllIngredients.add(rs1.getLong("id"));
            }

            for(int i = 1; i< arrayOfIngredients.length; i++ ) {
                Set<String> set = new TreeSet<>();
                statement.setString(1, arrayOfIngredients[i]);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    set.add(rs.getString("name"));
                }
                containingAllIngredients.retainAll(set);
                if (containingAllIngredients.isEmpty()){
                    List<Recipe> empty = new ArrayList<>();
                    return empty;
                }
            }
            List<Recipe> recipes = new ArrayList<>();
            for(Long id: containingAllIngredients){
                recipes.add(findRecipeById(id));
            }
            return recipes;
        } catch (SQLException ex) {
            String msg = "Error when getting ingredients from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, statement);
        }
    }

    //--------------------------------------------------------------------------
    // Other helpful functions
    //--------------------------------------------------------------------------

    /**
     * Used in createRecipe and updateRecipe to transform date
     * @param localDate date
     * @return date which will be added to DB
     */
    private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    static Recipe executeQueryForSingleRecipe(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Recipe result = rowToRecipe(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more recipes with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Used in findAllRecipes and findRecipe by name
     * @param statement SQL statement
     * @return Recipes from DB which satisfy condition in SQL statement
     * @throws SQLException
     */
    static List<Recipe> executeQueryForMultipleRecipes(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        List<Recipe> result = new ArrayList<Recipe>();
        while (rs.next()) {
            result.add(rowToRecipe(rs));
        }
        return result;
    }

    /**
     * Used in executeQueryForMultiplyRecipes and
     * @param rs is set of recipes satisfying condition
     * @return object recipe with set parameters
     * @throws SQLException
     */
    static private Recipe rowToRecipe(ResultSet rs) throws SQLException {
        Recipe result = new Recipe();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setProcedure(rs.getString("procedure"));
        result.setDate(toLocalDate(rs.getDate("date")));
        return result;
    }


    public Set<String> findIngredients(PreparedStatement statement) throws SQLException{
            ResultSet rs = statement.executeQuery();
            Set<String> ingredients = new HashSet<String>();
            while (rs.next()) {
                ingredients.add(rs.getString("name"));
            }
        return ingredients;
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null: date.toLocalDate();
    }
}