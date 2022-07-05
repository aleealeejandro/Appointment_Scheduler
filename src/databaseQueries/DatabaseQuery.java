package databaseQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Alexander Padilla
 */
public class DatabaseQuery {
    private static PreparedStatement preparedStatement;

    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        preparedStatement = conn.prepareStatement(sqlStatement);
    }

    public static PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

}