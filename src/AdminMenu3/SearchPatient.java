package AdminMenu3;

import javax.swing.JOptionPane;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SearchPatient extends javax.swing.JFrame {

    public SearchPatient() {
        initComponents();
        setLocationRelativeTo(null);

        loadPatientNames();
    }

private void loadPatientNames() {

    jComboBox1.removeAllItems();

    String sql =
            "SELECT patient_id, last_name, first_name, "
            + "middle_name, suffix "
            + "FROM patients "
            + "WHERE status = 'ACTIVE' "
            + "ORDER BY last_name, first_name";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery()
    ) {

        while (rs.next()) {

            String lastName =
                    rs.getString("last_name");

            String firstName =
                    rs.getString("first_name");

            String middleName =
                    rs.getString("middle_name");

            String suffix =
                    rs.getString("suffix");

            String fullName =
                    lastName + ", " + firstName;

            if (middleName != null
                    && !middleName.trim().isEmpty()) {

                fullName += " " + middleName;
            }

            if (suffix != null
                    && !suffix.trim().isEmpty()
                    && !suffix.equalsIgnoreCase("None")) {

                fullName += " " + suffix;
            }

            jComboBox1.addItem(fullName);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        jLabel1 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel1.setBackground(new java.awt.Color(21, 90, 33));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SEARCH PATIENT");

        btnBack.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnBack.setForeground(new java.awt.Color(13, 65, 25));
        btnBack.setText("BACK ");
        btnBack.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 413, Short.MAX_VALUE)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jLayeredPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 0), 2, true));

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Search by Name:");

        btnSearch.setBackground(new java.awt.Color(13, 65, 25));
        btnSearch.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("SEARCH PATIENT");
        btnSearch.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(13, 65, 25));
        btnClear.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("CLEAR");
        btnClear.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnSearch, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnClear, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jLayeredPane1)
                .addGap(50, 50, 50))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
            if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        AdminMenu obj =  new AdminMenu();
        obj.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed

    if (jComboBox1.getSelectedItem() == null) {

        JOptionPane.showMessageDialog(
                this,
                "Please select a patient.",
                "Search Patient",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    String selectedName =
            jComboBox1.getSelectedItem().toString();

    String sql =
            "SELECT * FROM patients "
            + "WHERE CONCAT(last_name, ', ', first_name, "
            + "IF(middle_name IS NULL OR middle_name = '', "
            + "'', CONCAT(' ', middle_name)), "
            + "IF(suffix IS NULL OR suffix = '' "
            + "OR suffix = 'None', '', CONCAT(' ', suffix))) "
            + "= ? "
            + "AND status = 'ACTIVE' "
            + "LIMIT 1";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst =
                conn.prepareStatement(sql)
    ) {

        pst.setString(1, selectedName);

        try (ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                int patientId =
                        rs.getInt("patient_id");

                String lastName =
                        rs.getString("last_name");

                String firstName =
                        rs.getString("first_name");

                String middleName =
                        rs.getString("middle_name");

                String suffix =
                        rs.getString("suffix");

                String fullName =
                        lastName + ", " + firstName;

                if (middleName != null
                        && !middleName.trim().isEmpty()) {

                    fullName += " " + middleName;
                }

                if (suffix != null
                        && !suffix.trim().isEmpty()
                        && !suffix.equalsIgnoreCase("None")) {

                    fullName += " " + suffix;
                }

                String age =
                        String.valueOf(
                                rs.getInt("age")
                        );

                String gender =
                        rs.getString("gender");

                String birthdate =
                        rs.getDate("birthdate") != null
                        ? rs.getDate("birthdate").toString()
                        : "";

                String contact =
                        rs.getString("contact");

                String street =
                        rs.getString("street");

                String barangay =
                        rs.getString("barangay");

                String postalCode =
                        rs.getString("postal_code");

                String subdivision =
                        rs.getString("subdivision");

                String city =
                        rs.getString(
                                "city_municipality"
                        );

                String province =
                        rs.getString("province");


                // DIRI I SHOW ANG PATIENT INFORMATION

                String message =
                        "PATIENT INFORMATION\n\n"

                        + "Patient ID: "
                        + patientId

                        + "\nName: "
                        + fullName

                        + "\nAge: "
                        + age

                        + "\nGender: "
                        + gender

                        + "\nBirthdate: "
                        + birthdate

                        + "\nContact Number: "
                        + contact

                        + "\n\nFULL ADDRESS\n\n"

                        + "Street: "
                        + street

                        + "\nBarangay: "
                        + barangay

                        + "\nPostal Code: "
                        + postalCode

                        + "\nSubdivision/Village: "
                        + subdivision

                        + "\nCity/Municipality: "
                        + city

                        + "\nProvince: "
                        + province;


                JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Patient Information",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }

    } catch (Exception e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "ERROR SEARCHING PATIENT:\n\n"
                + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    JOptionPane.showMessageDialog(
            this,
            "Patient not found.",
            "Search Patient",
            JOptionPane.WARNING_MESSAGE
    );


    }//GEN-LAST:event_btnSearchActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed


    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchPatient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private java.awt.Panel panel1;
    // End of variables declaration//GEN-END:variables
}
