package database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateConnector {

    public static boolean saveUpdate(
            int patientId,
            String diagnosis,
            String prescription,
            String adminNotes) {

        String sql =
                "INSERT INTO patient_updates "
                + "(patient_id, diagnosis, prescription, admin_notes) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {

            pst.setInt(1, patientId);
            pst.setString(2, diagnosis);
            pst.setString(3, prescription);
            pst.setString(4, adminNotes);

            int result = pst.executeUpdate();

            return result > 0;

        } catch (Exception e) {

            System.out.println("UPDATE CONNECTOR ERROR:");
            e.printStackTrace();

            return false;
        }
    }
}