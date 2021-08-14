package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String username, String password) throws SQLException {

        String query = "SELECT  UserId, Username, Name, Surname FROM user  WHERE Username = ? AND Password =?";

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setString(1, username);
            pstatement.setString(2, password);
            try (ResultSet result = pstatement.executeQuery();) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setUserId(result.getInt("UserId"));
                    user.setUsername(result.getString("Username"));
                    user.setName(result.getString("Name"));
                    user.setSurname(result.getString("Surname"));
                    return user;
                }
            }
        }
    }
}
