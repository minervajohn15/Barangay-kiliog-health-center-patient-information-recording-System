package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;

public class ImmunizationConnector {

    // ==========================================
    // CHECK IF PATIENT HAS IMMUNIZATION
    // ==========================================

    public static boolean hasImmunization(int patientId) {

        String sql =
                "SELECT immunization_id "
                + "FROM immunizations "
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
                    "CHECK IMMUNIZATION ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // ADD IMMUNIZATION
    // ==========================================

    public static boolean addImmunization(
            int patientId,
            String parentGuardian,
            String dateOfImmunization,
            String vaccineGiven,
            String dose,
            String nextImmunizationDate,
            String remarks
    ) {

        String sql =
                "INSERT INTO immunizations "
                + "(patient_id, parent_guardian, date_of_immunization, "
                + "vaccine_given, dose, next_immunization_date, remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy");

            LocalDate immunizationDate =
                    LocalDate.parse(
                            dateOfImmunization,
                            formatter
                    );

            pst.setInt(1, patientId);

            pst.setString(
                    2,
                    parentGuardian
            );

            pst.setDate(
                    3,
                    Date.valueOf(immunizationDate)
            );

            pst.setString(
                    4,
                    vaccineGiven
            );

            pst.setString(
                    5,
                    dose
            );

            if (nextImmunizationDate == null
                    || nextImmunizationDate.trim().isEmpty()) {

                pst.setNull(
                        6,
                        java.sql.Types.DATE
                );

            } else {

                LocalDate nextDate =
                        LocalDate.parse(
                                nextImmunizationDate,
                                formatter
                        );

                pst.setDate(
                        6,
                        Date.valueOf(nextDate)
                );
            }

            pst.setString(
                    7,
                    remarks
            );

            pst.executeUpdate();

            return true;

        } catch (Exception e) {

            System.out.println(
                    "ADD IMMUNIZATION ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET TOTAL IMMUNIZATION RECORDS
    // ==========================================

    public static int getImmunizationCount() {

        String sql =
                "SELECT COUNT(*) FROM immunizations";

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
                    "GET IMMUNIZATION COUNT ERROR:"
            );

            e.printStackTrace();
        }

        return 0;
    }
}