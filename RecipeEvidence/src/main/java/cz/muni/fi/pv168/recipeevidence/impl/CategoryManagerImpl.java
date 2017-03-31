package cz.muni.fi.pv168.recipeevidence.impl;

import cz.muni.fi.pv168.recipeevidence.CategoryManager;
import cz.muni.fi.pv168.recipeevidence.common.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: create javadoc you lazy bitch
 *
 * @author Tomas Soukal
 */
public class CategoryManagerImpl implements CategoryManager {

    private static final Logger logger = Logger.getLogger(
            CategoryManagerImpl.class.getName());

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
    public Category findCategoryByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //TODO up

    @Override
    public void deleteCategory(Category category) throws ServiceFailureException {
        checkDataSource();
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() == null) {
            throw new IllegalArgumentException("category id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM Category WHERE id = ?")) {

            st.setLong(1, category.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Category " + category + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating category " + category, ex);
        }
    }

    @Override
    public void createCategory(Category category) throws ServiceFailureException {
        checkDataSource();
        validate(category);
        if (category.getId() != null) {
            throw new IllegalEntityException("grave id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Category (row,col,capacity,note) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, category.getId());
            st.setString(2, category.getCategoryName());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, category, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            category.setCategoryID(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting category into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    private void validate(Category category) throws IllegalArgumentException {
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getCategoryName() == "") {
            throw new IllegalArgumentException("category name is empty");
        }
    }

    @Override
    public Category findCategoryById(Long id) throws ServiceFailureException {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name FROM Category WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Category category = resultSetToCategory(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + category + " and " + resultSetToCategory(rs));
                }

                return category;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving category with id " + id, ex);
        }
    }

    @Override
    public void updateCategory(Category category) throws ServiceFailureException {
        checkDataSource();
        validate(category);
        if (category.getId() == null) {
            throw new IllegalArgumentException("category id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE Category SET name = ? WHERE id = ?")) {

            st.setLong(1, category.getId());
            st.setString(2, category.getCategoryName());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Category " + category + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating category " + category, ex);
        }
    }

    @Override
    public List<Category> findAllCategories() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name FROM Category");
            return executeQueryForMultipleCategories(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all graves from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<Category> executeQueryForMultipleCategories(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Category> result = new ArrayList<Category>();
        while (rs.next()) {
            result.add(resultSetToCategory(rs)); //rowToGrave
        }
        return result;
    }

    private static Category resultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryID(rs.getLong("id"));
        category.setCategoryName(rs.getString("name"));
        return category;
    }
}
