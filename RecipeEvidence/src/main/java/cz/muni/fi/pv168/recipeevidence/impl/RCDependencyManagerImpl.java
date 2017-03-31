package cz.muni.fi.pv168.recipeevidence.impl;

import cz.muni.fi.pv168.recipeevidence.CategoryManager;
import cz.muni.fi.pv168.recipeevidence.RCDependencyManager;

import cz.muni.fi.pv168.recipeevidence.common.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This class represents manager for handling dependencies
 *
 * @author Tomas Soukal
 */
//TODO remove recipe
public class RCDependencyManagerImpl implements RCDependencyManager {

    private static final Logger logger = Logger.getLogger(
            CategoryManager.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public List<RCDependency> findAllDependencies() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id FROM Rr_dependency");
            return executeQueryForMultipleRCDependencies(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all rcdependencies from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<RCDependency> executeQueryForMultipleRCDependencies(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<RCDependency> result = new ArrayList<RCDependency>();
        while (rs.next()) {
            result.add(resultSetToRCDependency(rs)); //rowToGrave
        }
        return result;
    }

    private static RCDependency resultSetToRCDependency(ResultSet rs) throws SQLException {
        RCDependency dependency = new RCDependency();
        dependency.setId(rs.getLong("id"));
        return dependency;
    }

    public RCDependency getDependency(Recipe recipe, Category category) throws ServiceFailureException {
        checkDataSource();
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is null");
        }
        if (recipe.getId() == null) {
            throw new IllegalArgumentException("recipe id is null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() == null) {
            throw new IllegalArgumentException("category id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT recipe_id,category_id FROM Rc_dependency WHERE Recipe.id = ?, Category.id = ?");
            st.setLong(1, recipe.getId());
            st.setLong(2, category.getId());
            return RCDependencyManagerImpl.executeQueryForMultipleRCDependencies(st).get(0);
        } catch (SQLException ex) {
            String msg = "Error in category " + category + "or recipe" + recipe;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }


    @Override
    public void createDependency(RCDependency dependency) throws ServiceFailureException {
        checkDataSource();
        if (dependency.getId() != null || dependency.getCategory() == null
                || dependency.getRecipe() == null) {
            throw new IllegalArgumentException("Invalid dependency");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Rc_dependency (recipe_id, category_id) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, dependency.getRecipe().getId());
            st.setLong(2, dependency.getCategory().getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, dependency, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            dependency.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting grave into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }

    }

    @Override
    public void deleteDependency(RCDependency dependency) throws ServiceFailureException {
        checkDataSource();
        if (dependency == null) {
            throw new IllegalArgumentException("dependency is null");
        }
        if (dependency.getId() == null) {
            throw new IllegalEntityException("dependency id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Rc_dependency WHERE Recipe.id = ?, Category.id = ?");
            st.setLong(1, dependency.getRecipe().getId());
            st.setLong(2, dependency.getCategory().getId());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, dependency, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting grave from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    //TODO below

    @Override
    public void insertRecipeIntoCategory(Recipe recipe, Category category) throws ServiceFailureException {
        checkDataSource();
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is null");
        }
        if (recipe.getId() == null) {
            throw new IllegalArgumentException("recipe id is null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() == null) {
            throw new IllegalArgumentException("category id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                "UPDATE Rc_dependency SET graveId = NULL WHERE id = ? AND graveId = ?"); //TODO
            st.setLong(1, recipe.getId());
            st.setLong(2, category.getId());
            int count = st.executeUpdate();
            //DBUtils.checkUpdatesCount(count, body, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Recipe> findRecipesInCategory(Category category) throws ServiceFailureException {
        checkDataSource();
        if (category == null) {
            throw new IllegalArgumentException("Cat is null");
        }
        if (category.getId() == null) {
            throw new IllegalEntityException("Cat id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Recipe_id FROM Rc_dependency WHERE Category_id = ?"); //TODO
            st.setLong(1, category.getId());
            return RCDependencyManagerImpl.executeQueryForMultipleRCDependencies2(st);
        } catch (SQLException ex) {
            String msg = "Error " + category;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<Recipe> executeQueryForMultipleRCDependencies2(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Recipe> result = new ArrayList<Recipe>();
        while (rs.next()) {
            result.add(resultSetToRecipe(rs)); //rowToGrave
        }
        return result;
    }

    private static Recipe resultSetToRecipe(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(rs.getLong("id"));
        return recipe;
    }


    @Override
    public List<Category> findCategoriesForRecipe(Recipe recipe) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}