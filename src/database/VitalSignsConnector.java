package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VitalSignsConnector {

    // ==========================================
    // CHECK IF PATIENT HAS VITAL SIGNS
    // ==========================================

    public static boolean hasVitalSigns(int patientId) {

        String sql =
                "SELECT vital_id "
                + "FROM vital_signs "
                + "WHERE patient_id = ? "
                + "LIMIT 1";

        try {

            Connection conn =
                    DBConnection.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setInt(1, patientId);

            ResultSet rs =
                    pst.executeQuery();

            boolean exists =
                    rs.next();

            rs.close();
            pst.close();
            conn.close();

            return exists;

        } catch (Exception e) {

            System.out.println(
                    "CHECK VITAL SIGNS ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // ADD VITAL SIGNS
    // ==========================================

    public static boolean addVitalSigns(
            int patientId,
            String temperature,
            String bloodPressure,
            String pulseRate,
            String respiratoryRate,
            String weight,
            String height
    ) {

        String sql =
                "INSERT INTO vital_signs "
                + "(patient_id, temperature, systolic, diastolic, "
                + "weight, height, pulse_rate, respiratory_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {

            double temp =
                    Double.parseDouble(temperature);

            String[] bp =
                    bloodPressure.split("/");

            if (bp.length != 2) {
                return false;
            }

            int systolic =
                    Integer.parseInt(bp[0].trim());

            int diastolic =
                    Integer.parseInt(bp[1].trim());

            int pulse =
                    Integer.parseInt(pulseRate);

            int respiratory =
                    Integer.parseInt(respiratoryRate);

            double weightValue =
                    Double.parseDouble(weight);

            double heightValue =
                    Double.parseDouble(height);

            pst.setInt(1, patientId);
            pst.setDouble(2, temp);
            pst.setInt(3, systolic);
            pst.setInt(4, diastolic);
            pst.setDouble(5, weightValue);
            pst.setDouble(6, heightValue);
            pst.setInt(7, pulse);
            pst.setInt(8, respiratory);

            pst.executeUpdate();

            return true;

        } catch (Exception e) {

            System.out.println(
                    "ADD VITAL SIGNS ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET TOTAL VITAL SIGNS RECORDS
    // ==========================================

    public static int getVitalSignsCount() {

        String sql =
                "SELECT COUNT(*) FROM vital_signs";

        try (
            Connection conn =
                    DBConnection.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery()
        ) {

            if (rs.next()) {

                return rs.getInt(1);
            }

        } catch (Exception e) {

            System.out.println(
                    "GET VITAL SIGNS COUNT ERROR:"
            );

            e.printStackTrace();
        }

        return 0;
    }
}