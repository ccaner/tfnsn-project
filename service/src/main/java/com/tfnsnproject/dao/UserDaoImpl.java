package com.tfnsnproject.dao;

import com.google.common.collect.Maps;
import com.tfnsnproject.to.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public UserDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public User loadUser(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return jdbcTemplate.query("select * from users where upper(username) = upper(:username)", params,
                new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId((long)rs.getInt("id"));
                    user.setUsername(rs.getString("username").toLowerCase());
                    user.setPassword(rs.getString("password"));
                    user.setCode(rs.getString("code"));
                    user.setActive("yes".equalsIgnoreCase(rs.getString("active")));
                    return user;
                }
                return null;
            }
        });
    }

}
