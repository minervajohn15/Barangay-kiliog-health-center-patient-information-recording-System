package AdminMenu3;

import StaffMenu3.*;
import Frame.*;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewAllPatient extends javax.swing.JFrame {

    //DIRI MO TAWAGON ANG DATABASE
    private java.util.ArrayList<Integer> patientIds =
            new java.util.ArrayList<>();

    public ViewAllPatient() {

        initComponents();

        setLocationRelativeTo(null);
        //---------------------------
        loadPatients();

        // DIRI ANG VIEW NGA ACTION NGA PART
        jTable1.addMouseListener(
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(
                        java.awt.event.MouseEvent e) {

                    int row =
                            jTable1.rowAtPoint(e.getPoint());

                    int column =
                            jTable1.columnAtPoint(e.getPoint());

                    if (row >= 0 && column == 4) {

                        if (row < patientIds.size()) {

                            int patientId =
                                    patientIds.get(row);

                            viewPatient(patientId);
                        }
                    }
                }
            }
        );
    }

    private void loadPatients() {

        DefaultTableModel model =
                (DefaultTableModel) jTable1.getModel();
        //I CLEAR NIYA ANG ROW
        model.setRowCount(0);

        // Clear ANG old patient IDs
        patientIds.clear();

        String sql =
                "SELECT patient_id, last_name, first_name, "
                + "middle_name, age, gender, contact "
                + "FROM patients "
                + "WHERE status = 'ACTIVE' "
                + "ORDER BY patient_id DESC";

        try (
            Connection conn =
                    DBConnection.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery()
        ) {

            while (rs.next()) {

                int patientId =
                        rs.getInt("patient_id");

                // Build UG patient name
                String lastName =
                        rs.getString("last_name");

                String firstName =
                        rs.getString("first_name");

                String middleName =
                        rs.getString("middle_name");

                String fullName =
                        lastName + ", " + firstName;

                if (middleName != null
                        && !middleName.trim().isEmpty()) {

                    fullName +=
                            " " + middleName;
                }

                patientIds.add(patientId);

                // Add patient to JTable
                model.addRow(new Object[]{

                    fullName,

                    rs.getInt("age"),

                    rs.getString("gender"),

                    rs.getString("contact"),

                    "VIEW"

                });
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "ERROR LOADING PATIENTS:\n\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


private void viewPatient(int patientId) {

    String patientSQL =
            "SELECT * FROM patients WHERE patient_id = ?";

    StringBuilder patientInfo = new StringBuilder();

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement patientStmt =
                conn.prepareStatement(patientSQL)
    ) {

        // I NIYA GET ANG PATIENT INFORMATION

        patientStmt.setInt(1, patientId);

        try (ResultSet rs = patientStmt.executeQuery()) {

            if (!rs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Patient not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            patientInfo.append("PATIENT INFORMATION\n\n");

            patientInfo.append("Patient ID: ")
                    .append(rs.getInt("patient_id"))
                    .append("\n");

            patientInfo.append("Last Name: ")
                    .append(rs.getString("last_name"))
                    .append("\n");

            patientInfo.append("First Name: ")
                    .append(rs.getString("first_name"))
                    .append("\n");

            patientInfo.append("Middle Name: ")
                    .append(rs.getString("middle_name"))
                    .append("\n");

            patientInfo.append("Suffix: ")
                    .append(rs.getString("suffix"))
                    .append("\n");

            patientInfo.append("Age: ")
                    .append(rs.getInt("age"))
                    .append("\n");

            patientInfo.append("Gender: ")
                    .append(rs.getString("gender"))
                    .append("\n");

            patientInfo.append("Birthdate: ")
                    .append(rs.getDate("birthdate"))
                    .append("\n");

            patientInfo.append("Contact Number: ")
                    .append(rs.getString("contact"))
                    .append("\n\n");

            patientInfo.append("FULL ADDRESS\n\n");

            patientInfo.append("Street: ")
                    .append(rs.getString("street"))
                    .append("\n");

            patientInfo.append("Barangay: ")
                    .append(rs.getString("barangay"))
                    .append("\n");

            patientInfo.append("Postal Code: ")
                    .append(rs.getString("postal_code"))
                    .append("\n");

            patientInfo.append("Subdivision/Village: ")
                    .append(rs.getString("subdivision"))
                    .append("\n");

            patientInfo.append("City/Municipality: ")
                    .append(rs.getString("city_municipality"))
                    .append("\n");

            patientInfo.append("Province: ")
                    .append(rs.getString("province"))
                    .append("\n\n");
        }


        // CHECK WHICH MEDICAL SERVICES HAVE DATA

        java.util.ArrayList<String> availableServices =
                new java.util.ArrayList<>();

        // CHECK CONSULTATION

        String checkConsultation =
                "SELECT COUNT(*) FROM consultations "
                + "WHERE patient_id = ?";

        try (PreparedStatement pst =
                     conn.prepareStatement(checkConsultation)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    availableServices.add("Consultation");
                }
            }
        }

        // CHECK PRENATAL CARE

        String checkPrenatal =
                "SELECT COUNT(*) FROM prenatal_care "
                + "WHERE patient_id = ?";

        try (PreparedStatement pst =
                     conn.prepareStatement(checkPrenatal)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    availableServices.add("Prenatal Care");
                }
            }
        }


        // CHECK VITAL SIGNS

        String checkVitals =
                "SELECT COUNT(*) FROM vital_signs "
                + "WHERE patient_id = ?";

        try (PreparedStatement pst =
                     conn.prepareStatement(checkVitals)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    availableServices.add("Vital Signs");
                }
            }
        }


        // CHECK IMMUNIZATION

        String checkImmunization =
                "SELECT COUNT(*) FROM immunizations "
                + "WHERE patient_id = ?";

        try (PreparedStatement pst =
                     conn.prepareStatement(checkImmunization)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    availableServices.add(
                            "Routine Child Immunization"
                    );
                }
            }
        }

        // NO MEDICAL SERVICE FOUND

        if (availableServices.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    patientInfo.toString()
                    + "MEDICAL SERVICES\n\n"
                    + "No medical service records found.",
                    "Patient Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }


        // CHOOSE MEDICAL SERVICE
        String selectedService =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Choose Medical Service:",
                        "Medical Services",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        availableServices.toArray(
                                new String[0]
                        ),
                        availableServices.get(0)
                );

        // User clicked Cancel
        if (selectedService == null) {
            return;
        }


        // CONSULTATION

        if (selectedService.equals("Consultation")) {

            patientInfo.append(
                    "========================================\n"
            );

            patientInfo.append(
                    "MEDICAL SERVICE: CONSULTATION\n"
            );

            patientInfo.append(
                    "========================================\n\n"
            );

            String sql =
                    "SELECT * FROM consultations "
                    + "WHERE patient_id = ? "
                    + "ORDER BY consultation_id DESC";

            try (PreparedStatement pst =
                         conn.prepareStatement(sql)) {

                pst.setInt(1, patientId);

                try (ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {

                        patientInfo.append(
                                "Consultation Date: "
                        )
                        .append(
                                rs.getDate(
                                        "consultation_date"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Reason: "
                        )
                        .append(
                                rs.getString("reason")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Symptoms: "
                        )
                        .append(
                                rs.getString("symptoms")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Medical History: "
                        )
                        .append(
                                rs.getString(
                                        "medical_history"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Assessment: "
                        )
                        .append(
                                rs.getString("assessment")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Treatment: "
                        )
                        .append(
                                rs.getString("treatment")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Health Advice: "
                        )
                        .append(
                                rs.getString("health_advice")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Follow-up Date: "
                        )
                        .append(
                                rs.getDate(
                                        "follow_up_date"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Remarks: "
                        )
                        .append(
                                rs.getString("remarks")
                        )
                        .append("\n\n");

                        patientInfo.append(
                                "----------------------------------------\n\n"
                        );
                    }
                }
            }
        }


        // PRENATAL CARE

        else if (selectedService.equals("Prenatal Care")) {

            patientInfo.append(
                    "========================================\n"
            );

            patientInfo.append(
                    "MEDICAL SERVICE: PRENATAL CARE\n"
            );

            patientInfo.append(
                    "========================================\n\n"
            );

            String sql =
                    "SELECT * FROM prenatal_care "
                    + "WHERE patient_id = ? "
                    + "ORDER BY prenatal_id DESC";

            try (PreparedStatement pst =
                         conn.prepareStatement(sql)) {

                pst.setInt(1, patientId);

                try (ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {

                        patientInfo.append(
                                "Date of Visit: "
                        )
                        .append(
                                rs.getDate("date_of_visit")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Gestational Age: "
                        )
                        .append(
                                rs.getString(
                                        "gestational_age"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "LMP: "
                        )
                        .append(
                                rs.getDate("lmp")
                        )
                        .append("\n");

                        patientInfo.append(
                                "EDD: "
                        )
                        .append(
                                rs.getDate("edd")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Blood Pressure: "
                        )
                        .append(
                                rs.getString(
                                        "blood_pressure"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Weight: "
                        )
                        .append(
                                rs.getBigDecimal("weight")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Fetal Heart Rate: "
                        )
                        .append(
                                rs.getInt(
                                        "fetal_heart_rate"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Pregnancy Complaints: "
                        )
                        .append(
                                rs.getString(
                                        "pregnancy_complaints"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Assessment: "
                        )
                        .append(
                                rs.getString("assessment")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Advice/Treatment: "
                        )
                        .append(
                                rs.getString(
                                        "advice_treatment"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Next Prenatal Visit: "
                        )
                        .append(
                                rs.getDate(
                                        "next_prenatal_visit"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Remarks: "
                        )
                        .append(
                                rs.getString("remarks")
                        )
                        .append("\n\n");
                    }
                }
            }
        }

        // VITAL SIGNS

        else if (selectedService.equals("Vital Signs")) {

            patientInfo.append(
                    "========================================\n"
            );

            patientInfo.append(
                    "MEDICAL SERVICE: VITAL SIGNS\n"
            );

            patientInfo.append(
                    "========================================\n\n"
            );

            String sql =
                    "SELECT * FROM vital_signs "
                    + "WHERE patient_id = ? "
                    + "ORDER BY vital_id DESC";

            try (PreparedStatement pst =
                         conn.prepareStatement(sql)) {

                pst.setInt(1, patientId);

                try (ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {

                        patientInfo.append(
                                "Temperature: "
                        )
                        .append(
                                rs.getObject(
                                        "temperature"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Systolic: "
                        )
                        .append(
                                rs.getObject(
                                        "systolic"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Diastolic: "
                        )
                        .append(
                                rs.getObject(
                                        "diastolic"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Weight: "
                        )
                        .append(
                                rs.getObject("weight")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Height: "
                        )
                        .append(
                                rs.getObject("height")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Pulse Rate: "
                        )
                        .append(
                                rs.getObject(
                                        "pulse_rate"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Respiratory Rate: "
                        )
                        .append(
                                rs.getObject(
                                        "respiratory_rate"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Oxygen Saturation: "
                        )
                        .append(
                                rs.getObject(
                                        "oxygen_saturation"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Recorded At: "
                        )
                        .append(
                                rs.getTimestamp(
                                        "recorded_at"
                                )
                        )
                        .append("\n\n");
                    }
                }
            }
        }


        // IMMUNIZATION

        else if (
                selectedService.equals(
                        "Routine Child Immunization"
                )
        ) {

            patientInfo.append(
                    "========================================\n"
            );

            patientInfo.append(
                    "MEDICAL SERVICE: ROUTINE CHILD IMMUNIZATION\n"
            );

            patientInfo.append(
                    "========================================\n\n"
            );

            String sql =
                    "SELECT * FROM immunizations "
                    + "WHERE patient_id = ? "
                    + "ORDER BY immunization_id DESC";

            try (PreparedStatement pst =
                         conn.prepareStatement(sql)) {

                pst.setInt(1, patientId);

                try (ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {

                        patientInfo.append(
                                "Parent/Guardian: "
                        )
                        .append(
                                rs.getString(
                                        "parent_guardian"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Date of Immunization: "
                        )
                        .append(
                                rs.getDate(
                                        "date_of_immunization"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Vaccine Given: "
                        )
                        .append(
                                rs.getString(
                                        "vaccine_given"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Dose: "
                        )
                        .append(
                                rs.getString("dose")
                        )
                        .append("\n");

                        patientInfo.append(
                                "Next Immunization Date: "
                        )
                        .append(
                                rs.getDate(
                                        "next_immunization_date"
                                )
                        )
                        .append("\n");

                        patientInfo.append(
                                "Remarks: "
                        )
                        .append(
                                rs.getString("remarks")
                        )
                        .append("\n\n");
                    }
                }
            }
        }

        // DISPLAY RESULT

        JTextArea textArea =
                new JTextArea(
                        patientInfo.toString()
                );

        textArea.setEditable(false);

        textArea.setFont(
                new java.awt.Font(
                        "Arial",
                        java.awt.Font.PLAIN,
                        14
                )
        );

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane =
                new JScrollPane(textArea);

        scrollPane.setPreferredSize(
                new java.awt.Dimension(650, 650)
        );

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Patient Information - "
                        + selectedService,
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "ERROR LOADING PATIENT INFORMATION:\n\n"
                        + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField6 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        panel1 = new java.awt.Panel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        panel3 = new java.awt.Panel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel17.setText("Subdivision/Village:");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel1.setBackground(new java.awt.Color(21, 90, 33));

        jButton1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(13, 65, 25));
        jButton1.setText("DONE");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ADMIN PANEL");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(22, 22, 22))
        );

        jLayeredPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 0), 2, true));

        panel3.setBackground(new java.awt.Color(21, 90, 33));

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("PATIENT REGISTRY");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(278, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(220, 220, 220))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Age", "Gender", "Contact Number", "Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
        }

        jLayeredPane1.setLayer(panel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 776, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        AdminMenu obj= new AdminMenu();
        obj.setVisible(true);
        dispose();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewAllPatient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField6;
    private java.awt.Panel panel1;
    private java.awt.Panel panel3;
    // End of variables declaration//GEN-END:variables
}
