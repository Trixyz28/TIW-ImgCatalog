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
        String query = "SELECT * FROM category";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {

            try (ResultSet result = pstatement.executeQuery();) {
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
        String query = "SELECT * FROM category WHERE id NOT IN (select child FROM relations)";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {

            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Category newCat = registerCategory(result);
                    newCat.setTop(true);
                    categories.add(newCat);
                }
                for (Category c: categories) {
                    findSubparts(c);
                }
            }
        }

        return categories;
    }

    public void findSubparts(Category c) throws SQLException {

        String query = "SELECT P.id, P.name, P.position, P.num_child FROM relations S JOIN category P on P.id = S.child WHERE S.father = ?";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {

            pstatement.setInt(1, c.getId());

            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Category newCat = registerCategory(result);
                    findSubparts(newCat);
                    c.addSubClass(newCat,1);
                }
            }
        }

    }

    public void createProduct(String name, String father) throws SQLException {
        String query = "INSERT into category (name) VALUES(?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setString(1, name);
            pstatement.executeUpdate();
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

}
