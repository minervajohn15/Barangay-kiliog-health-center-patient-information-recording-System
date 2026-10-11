package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



public class PatientConnector {
    public static ResultSet getAllPatients() throws Exception {

    String sql = "SELECT * FROM patients ORDER BY patient_id DESC";

    Connection conn = DBConnection.getConnection();

    if (conn == null) {
        throw new Exception("Database connection failed.");
    }

    PreparedStatement pst = conn.prepareStatement(sql);

    return pst.executeQuery();
}

    public static int savePatient(
            String lastName,
            String firstName,
            String middleName,
            String suffix,
            int age,
            String gender,
            String birthdate,
            String contact,
            String street,
            String barangay,
            String postalCode,
            String subdivision,
            String city,
            String province
    ) {

        String sql =
                "INSERT INTO patients "
                + "(last_name, first_name, middle_name, suffix, age, gender, "
                + "birthdate, contact, street, barangay, postal_code, "
                + "subdivision, city_municipality, province) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection conn = DBConnection.getConnection();

            if (conn == null) {
                System.out.println("Connection is NULL.");
                return -1;
            }

            PreparedStatement pst = conn.prepareStatement(
                    sql,
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );

            pst.setString(1, lastName);
            pst.setString(2, firstName);
            pst.setString(3, middleName);
            pst.setString(4, suffix);
            pst.setInt(5, age);
            pst.setString(6, gender);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy");

            LocalDate birthDate = LocalDate.parse(birthdate);

            pst.setDate(
                    7,
                    Date.valueOf(birthDate)
            );

            pst.setString(8, contact);
            pst.setString(9, street);
            pst.setString(10, barangay);
            pst.setString(11, postalCode);
            pst.setString(12, subdivision);
            pst.setString(13, city);
            pst.setString(14, province);

            int result = pst.executeUpdate();

            System.out.println(
                    "Patient INSERT result: " + result
            );

            ResultSet rs = pst.getGeneratedKeys();

            if (rs.next()) {

                int patientId = rs.getInt(1);

                System.out.println(
                        "Patient saved! ID = " + patientId
                );

                rs.close();
                pst.close();
                conn.close();

                return patientId;
            }

            rs.close();
            pst.close();
            conn.close();

            System.out.println(
                    "No generated patient ID."
            );

        } catch (Exception e) {

            System.out.println(
                    "PATIENT INSERT ERROR:"
            );

            e.printStackTrace();
        }

        return -1;
    }

public static ResultSet getPatientById(int patientId) throws Exception {

    String sql = "SELECT * FROM patients WHERE patient_id = ?";

    Connection conn = DBConnection.getConnection();

    if (conn == null) {
        throw new Exception("Database connection failed.");
    }

    PreparedStatement pst = conn.prepareStatement(sql);

    pst.setInt(1, patientId);

    return pst.executeQuery();
}
}