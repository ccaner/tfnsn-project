package com.tfnsnproject.dao;

import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.Photo;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class CheckinDaoImpl implements CheckinDao {

    public static final String UPDATE_SQL =
            "UPDATE latlong " +
            "SET " +
//                "userid = :userid " +
//                "created = :created " +
//                "pos = :pos " +
//                "comment = :comment " +
//                "mapic = :mapic " +
//                "geocode = :geocode " +
                "upload = :upload, " +
                "upload_small = :upload_small " +
//                "time = :time " +
//                "p_name = :p_name " +
//                "vid = :vid " +
            "WHERE id = :id";

    private SimpleJdbcInsert insertCheckin;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public CheckinDaoImpl(DataSource dataSource) {
        this.insertCheckin =
                new SimpleJdbcInsert(dataSource)
                        .withTableName("latlong")
                        .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void create(Checkin checkin) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userid", checkin.getAccountId());
        parameters.put("source", "API");
        parameters.put("created", checkin.getCheckinTime());
        parameters.put("pos", checkin.getLatitude() + "," + checkin.getLongitude());
        parameters.put("comment", checkin.getComment());
//        parameters.put("mapic", checkin.getLastName());
//        parameters.put("geocode", checkin.getLastName());
        parameters.put("p_name", checkin.getPlaceName());
//        parameters.put("upload", checkin.getLastName());
//        parameters.put("upload_small", checkin.getLastName());
//        parameters.put("vid", checkin.getLastName());
        Number newId = insertCheckin.executeAndReturnKey(parameters);
        checkin.setId(newId.longValue());
    }

    @Override
    public void updatePhoto(Checkin checkin, Photo photo) {
        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("userid", checkin.getAccountId());
//        parameters.put("created", checkin.getCheckinTime());
//        parameters.put("pos", checkin.getLatitude() + "," + checkin.getLongitude());
//        parameters.put("comment", checkin.getComment());
//        parameters.put("mapic", checkin.getLastName());
//        parameters.put("geocode", checkin.getLastName());
//        parameters.put("p_name", checkin.getLastName());
        parameters.put("id", checkin.getId());
        parameters.put("upload", photo.getOriginalPath());
        parameters.put("upload_small", photo.getThumbnailPath());
//        parameters.put("vid", checkin.getLastName());
        jdbcTemplate.update(UPDATE_SQL, parameters);

    }
}
