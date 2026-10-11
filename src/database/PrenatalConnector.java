package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;

public class PrenatalConnector {

    // ==========================================
    // CHECK IF PATIENT HAS PRENATAL RECORD
    // ==========================================

    public static boolean hasPrenatal(int patientId) {

        String sql =
                "SELECT prenatal_id "
                + "FROM prenatal_care "
                + "WHERE patient_id = ? "
                + "LIMIT 1";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst =
                    conn.prepareStatement(sql)
        ) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {

                return rs.next();
            }

        } catch (Exception e) {

            System.out.println(
                    "CHECK PRENATAL ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // ADD PRENATAL RECORD
    // ==========================================

    public static boolean addPrenatal(
            int patientId,
            String dateOfVisit,
            String gestationalAge,
            String lmp,
            String edd,
            String bloodPressure,
            String weight,
            String fetalHeartRate,
            String pregnancyComplaints,
            String assessment,
            String adviceTreatment,
            String nextPrenatalVisit,
            String remarks
    ) {

        String sql =
                "INSERT INTO prenatal_care "
                + "(patient_id, date_of_visit, gestational_age, lmp, edd, "
                + "blood_pressure, weight, fetal_heart_rate, "
                + "pregnancy_complaints, assessment, advice_treatment, "
                + "next_prenatal_visit, remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection conn =
                    DBConnection.getConnection();

            if (conn == null) {
                return false;
            }

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            DateTimeFormatter formatter =
        DateTimeFormatter.ISO_LOCAL_DATE;

            // Patient ID
            pst.setInt(
                    1,
                    patientId
            );

            // Date of Visit
            pst.setDate(
                    2,
                    Date.valueOf(
                            LocalDate.parse(
                                    dateOfVisit,
                                    formatter
                            )
                    )
            );

            // Gestational Age
            pst.setString(
                    3,
                    gestationalAge
            );

            // LMP
            pst.setDate(
                    4,
                    Date.valueOf(
                            LocalDate.parse(
                                    lmp,
                                    formatter
                            )
                    )
            );

            // EDD
            pst.setDate(
                    5,
                    Date.valueOf(
                            LocalDate.parse(
                                    edd,
                                    formatter
                            )
                    )
            );

            // Blood Pressure
            pst.setString(
                    6,
                    bloodPressure
            );

            // Weight
            pst.setDouble(
                    7,
                    Double.parseDouble(weight)
            );

            // Fetal Heart Rate
            pst.setInt(
                    8,
                    Integer.parseInt(fetalHeartRate)
            );

            // Pregnancy Complaints
            pst.setString(
                    9,
                    pregnancyComplaints
            );

            // Assessment
            pst.setString(
                    10,
                    assessment
            );

            // Advice / Treatment
            pst.setString(
                    11,
                    adviceTreatment
            );

            // Next Prenatal Visit
            if (nextPrenatalVisit == null
                    || nextPrenatalVisit.trim().isEmpty()) {

                pst.setNull(
                        12,
                        java.sql.Types.DATE
                );

            } else {

                pst.setDate(
                        12,
                        Date.valueOf(
                                LocalDate.parse(
                                        nextPrenatalVisit,
                                        formatter
                                )
                        )
                );
            }

            // Remarks
            pst.setString(
                    13,
                    remarks
            );

            int result =
                    pst.executeUpdate();

            System.out.println(
                    "Prenatal INSERT result: "
                    + result
            );

            pst.close();
            conn.close();

            return result > 0;

        } catch (Exception e) {

            System.out.println(
                    "PRENATAL INSERT ERROR:"
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET TOTAL PRENATAL RECORDS
    // ==========================================

    public static int getPrenatalCount() {

        String sql =
                "SELECT COUNT(*) FROM prenatal_care";

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
                    "GET PRENATAL COUNT ERROR:"
            );

            e.printStackTrace();
        }

        return 0;
    }
}