package db.migration;

import java.sql.Connection;
import java.sql.Statement;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

/**
 * @author kawasima
 */
public class V3__CreateSignature implements JdbcMigration {
    @Override
    public void migrate(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE signature (" +
                    "signature_id INTEGER DEFAULT 1 AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(30) NOT NULL," +
                    "signature_comment CLOB," +
                    "signature_user_id INTEGER," +
                    "campaign_id INTEGER NOT NULL," +
                    "FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)" +
                    ")"
            );
        }
    }
}
