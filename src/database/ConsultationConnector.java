package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;

public class ConsultationConnector {
    public static int getConsultationCount() {

    String sql = "SELECT COUNT(*) FROM consultations";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery()
    ) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        System.out.println("GET CONSULTATION COUNT ERROR:");
        e.printStackTrace();
    }

    return 0;
}
        public static boolean hasConsultation(int patientId) {

        String sql =
                "SELECT consultation_id "
                + "FROM consultations "
                + "WHERE patient_id = ? "
                + "LIMIT 1";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {

            pst.setInt(1, patientId);

            var rs = pst.executeQuery();

            boolean exists = rs.next();

            rs.close();

            return exists;

        } catch (Exception e) {

            System.out.println(
                    "CHECK CONSULTATION ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }

    public static boolean addConsultation(
        int patientId,
        String consultationDate,
        String reason,
        String symptoms,
        String medicalHistory,
        String assessment,
        String treatment,
        String healthAdvice,
        String followUpDate,
        String remarks
) {

    String sql = "INSERT INTO consultations "
            + "(patient_id, consultation_date, reason, symptoms, "
            + "medical_history, assessment, treatment, health_advice, "
            + "follow_up_date, remarks) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql)
    ) {

        pst.setInt(1, patientId);

        // Consultation date: yyyy-MM-dd
        LocalDate consultationDateValue =
                LocalDate.parse(consultationDate);

        pst.setDate(
                2,
                Date.valueOf(consultationDateValue)
        );

        pst.setString(3, reason);
        pst.setString(4, symptoms);
        pst.setString(5, medicalHistory);
        pst.setString(6, assessment);
        pst.setString(7, treatment);
        pst.setString(8, healthAdvice);

        // Follow-up date is optional
        if (followUpDate == null
                || followUpDate.trim().isEmpty()) {

            pst.setNull(9, java.sql.Types.DATE);

        } else {

            LocalDate followUpDateValue =
                    LocalDate.parse(followUpDate);

            pst.setDate(
                    9,
                    Date.valueOf(followUpDateValue)
            );
        }

        pst.setString(10, remarks);

        int result = pst.executeUpdate();

        System.out.println(
                "Consultation INSERT result: " + result
        );

        return result > 0;

    } catch (Exception e) {

        System.out.println("CONSULTATION INSERT ERROR:");
        e.printStackTrace();

        return false;
    }
}
}