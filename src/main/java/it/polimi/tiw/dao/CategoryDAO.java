package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }


    public List<Category> findAllCategories() throws SQLException {

        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM category ORDER BY position ASC";

        try (PreparedStatement pstatement = connection.prepareStatement(query)) {

            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Category newCat = registerCategory(result);
                    categories.add(newCat);
                }
            }
        }
        return categories;
    }


    public List<Category> findTopsAndSubtrees() throws SQLException {

        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM category WHERE id NOT IN (select child FROM relations) ORDER BY position ASC";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {

            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Category newCat = registerCategory(result);
                    newCat.setTop(true);
                    categories.add(newCat);
                }
                for (Category c: categories) {
                    findSubclasses(c);
                }
            }
        }

        return categories;
    }

    public void findSubclasses(Category c) throws SQLException {

        String query = "SELECT P.id, P.name, P.position, P.num_child FROM relations S JOIN category P on P.id = S.child WHERE S.father = ? ORDER BY P.position ASC";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {

            pstatement.setInt(1, c.getId());

            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Category newCat = registerCategory(result);
                    findSubclasses(newCat);
                    c.addSubClass(newCat);
                }
            }
        }
    }


    /* Category creation:
    - Trace the father category with fid
    - Check if father's num_child >= 9
    - Calculate the new category's position index
    - Update father's num_child
    - Insert the new category in category table (name, position)
    - Insert the link in relations table
     */
    public void createCategory(String name, int fid) throws Exception {

        connection.setAutoCommit(false);

        String query = "SELECT * FROM category WHERE id = ?";
        Category father = null;

        int newNumChild = 0;
        int newPosition = 0;

        try (PreparedStatement pStatement = connection.prepareStatement(query);) {
            pStatement.setInt(1, fid);
            try (ResultSet result = pStatement.executeQuery()) {
                while (result.next()) {
                    father = registerCategory(result);
                }
            }
        }

        try {
            if(father==null) {
                throw new SQLException();
            }

            if(father.getNumChild() >= 9) {
                throw new Exception();
            }

            newNumChild = father.getNumChild()+1;
            newPosition = (father.getPosition()*10)+(newNumChild);

            updateNumChild(newNumChild,fid);
            insertNewCategory(name,newPosition);
            insertNewLink(fid,findMaxId());

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }


    }


    private Category registerCategory(ResultSet result) throws SQLException {

        Category cat = new Category();

        cat.setId(result.getInt("id"));
        cat.setName(result.getString("name"));
        cat.setNumChild(result.getInt("num_child"));
        cat.setPosition(result.getInt("position"));

        return cat;
    }


    private void updateNumChild(int numChild,int fid) throws SQLException {
        String query = "UPDATE category SET num_child = ? WHERE id = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1,numChild);
            pStatement.setInt(2,fid);
            pStatement.executeUpdate();
        }
    }

    private void insertNewCategory(String name, int position) throws SQLException {
        String query = "INSERT into category(name, position) VALUES(?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setString(1, name);
            pstatement.setInt(2, position);
            pstatement.executeUpdate();
        }
    }

    private void insertNewLink(int fid, int cid) throws SQLException {
        String query = "INSERT into relations(father, child) VALUES(?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, fid);
            pstatement.setInt(2, cid);
            pstatement.executeUpdate();
        }
    }

    private int findMaxId() throws SQLException {
        String query = "SELECT MAX(id) FROM category";
        int max = 0;

        try (PreparedStatement pStatement = connection.prepareStatement(query);) {
            try (ResultSet result = pStatement.executeQuery()) {
                while (result.next()) {
                    max = result.getInt(1);
                }
            }
        }

        return max;
    }


}
