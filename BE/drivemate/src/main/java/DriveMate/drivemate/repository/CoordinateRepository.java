package DriveMate.drivemate.repository;

import DriveMate.drivemate.domain.Coordinate;
import DriveMate.drivemate.domain.SemiRoute;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CoordinateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void batchInsertCoordinates(List<Coordinate> coordinates, List<Long> semiRouteIds) {
        String sql = "INSERT INTO coordinate (first, second, semi_route_id) VALUES (?, ?, ?)";

        // 리스트 크기가 일치하는지 확인
        if (coordinates.size() != semiRouteIds.size()) {
            throw new IllegalArgumentException("Coordinates list size must match SemiRouteIds list size.");
        }

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    System.out.println(i);
                    Coordinate coordinate = coordinates.get(i);
                    ps.setDouble(1, coordinate.getFirst());
                    ps.setDouble(2, coordinate.getSecond());
                    ps.setLong(3, semiRouteIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return coordinates.size();
                }
            });

            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace(); // 예외 출력
        }
    }

}

