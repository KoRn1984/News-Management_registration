package by.itacademy.matveenko.jd2.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import by.itacademy.matveenko.jd2.bean.NewUserInfo;
import by.itacademy.matveenko.jd2.dao.DaoException;
import by.itacademy.matveenko.jd2.dao.IUserDao;
import by.itacademy.matveenko.jd2.dao.connectionpool.ConnectionPool;
import by.itacademy.matveenko.jd2.dao.connectionpool.ConnectionPoolException;

public class UserDao implements IUserDao{
	private int roleId;
    private String role;

    @Override
    public boolean logination(String login, String password) throws DaoException {
        String sql = "SELECT * FROM users WHERE login=? AND password=?";
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roleId = rs.getInt("role");
                    setUserRole(connection, roleId);
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (ConnectionPoolException e) {
            throw new DaoException(e);
        }
        return false;
    }

    private void setUserRole(Connection connection, int roleId) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id=" + roleId;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql);) {
            if (rs.next()) {
                role = rs.getString("role");
            }
        }
    }

    @Override
    public String getUserRole(String login, String password) {
        if (role != null) {
            return role;
        }
        return "guest";
    }

    @Override
    public boolean registration(NewUserInfo user) throws DaoException {
        String sql = "INSERT INTO users(login, password, name, surname, email) values (?,?,?,?,?)";
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
        	ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getUserSurname());
            ps.setString(5, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            return false;
        } catch (ConnectionPoolException e) {
            throw new DaoException(e);
        }
        return true;
    }
}