package DriveMate.drivemate.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CoordinateRepository {
    private final DataSource dataSource;

    @Autowired
    public CoordinateRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void batchInsertCoordinate(JsonNode coordinatesArray){
        String sql = "INSERT INTO coordinate (first, second) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            if (coordinatesArray.isArray()) {
                for (JsonNode coordinateNode : coordinatesArray) {
                    double first = coordinateNode.get(1).asDouble();
                    double second = coordinateNode.get(0).asDouble();
                    pstmt.setDouble(1, first);
                    pstmt.setDouble(2, second);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
