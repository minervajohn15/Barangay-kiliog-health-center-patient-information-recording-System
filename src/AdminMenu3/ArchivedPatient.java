package AdminMenu3;

import Frame.*;
import database.DBConnection;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ArchivedPatient extends javax.swing.JFrame {
    //DIRI ANG MYSQL DATABASE
    private java.util.ArrayList<Integer> patientIds =
            new java.util.ArrayList<>();

public ArchivedPatient() {

    initComponents();

    setLocationRelativeTo(null);

    loadArchivedPatients();

    jTable1.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {

            int row = jTable1.rowAtPoint(e.getPoint());
            int column = jTable1.columnAtPoint(e.getPoint());

            if (row < 0 || row >= patientIds.size()) {
                return;
            }

            int patientId = patientIds.get(row);

            String patientName =
                    jTable1.getValueAt(row, 0).toString();
            //-----------------------------------
            if (column == 2) {

                permanentlyDeletePatient(
                        patientId,
                        patientName,
                        row
                );

            }
            //DIRI ANG STORE
            else if (column == 3) {

                restorePatient(
                        patientId,
                        patientName,
                        row
                );
            }
        }
    });
}

    //DIRI MO  LOAD ARCHIVED ANG PATIENTS FROM MYSQL
private void loadArchivedPatients() {

    DefaultTableModel model =
            (DefaultTableModel) jTable1.getModel();

    model.setRowCount(0);
    patientIds.clear();

    String sql =
            "SELECT patient_id, last_name, first_name, "
            + "middle_name, suffix, created_at "
            + "FROM patients "
            + "WHERE status = 'ARCHIVED' "
            + "ORDER BY patient_id DESC";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst =
                conn.prepareStatement(sql);
        ResultSet rs =
                pst.executeQuery()
    ) {

        while (rs.next()) {

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

            patientIds.add(patientId);

            String recordDate = "";

            if (rs.getTimestamp("created_at") != null) {

                recordDate =
                        rs.getTimestamp("created_at")
                          .toLocalDateTime()
                          .toLocalDate()
                          .toString();
            }

            model.addRow(new Object[]{
                fullName,
                recordDate,
                "DELETE PERMANENTLY",
                "RESTORE"
            });
        }

    } catch (Exception e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "ERROR LOADING ARCHIVED PATIENTS:\n\n"
                + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

private void restorePatient(
        int patientId,
        String patientName,
        int tableRow) {

    int confirm =
            JOptionPane.showConfirmDialog(
                    this,

                    "Are you sure you want to RESTORE:\n\n"
                    + patientName
                    + "\n\n"
                    + "The patient will be moved back to the ACTIVE registry.",

                    "Restore Patient",

                    JOptionPane.YES_NO_OPTION,

                    JOptionPane.QUESTION_MESSAGE
            );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    String sql =
            "UPDATE patients "
            + "SET status = 'ACTIVE' "
            + "WHERE patient_id = ? "
            + "AND status = 'ARCHIVED'";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst =
                conn.prepareStatement(sql)
    ) {

        pst.setInt(1, patientId);

        int result =
                pst.executeUpdate();

        if (result > 0) {

            DefaultTableModel model =
                    (DefaultTableModel) jTable1.getModel();

            // Remove the patient from Archived Patients table
            model.removeRow(tableRow);

            // Remove the ID from the ArrayList
            patientIds.remove(tableRow);

            JOptionPane.showMessageDialog(
                    this,

                    "Patient successfully restored.\n\n"
                    + patientName
                    + "\n\n"
                    + "The patient is now ACTIVE again.",

                    "Patient Restored",

                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,

                    "Patient could not be restored.\n\n"
                    + "The patient may no longer be archived.",

                    "Restore Error",

                    JOptionPane.ERROR_MESSAGE
            );

            loadArchivedPatients();
        }

    } catch (Exception e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,

                "ERROR RESTORING PATIENT:\n\n"
                + e.getMessage(),

                "Database Error",

                JOptionPane.ERROR_MESSAGE
        );
    }
}

    // PERMANENT DELETE ANG PATIENT SA MYSQL
    private void permanentlyDeletePatient(
            int patientId,
            String patientName,
            int tableRow) {

        // FIRST I CONFIRM NIYA DAAN
        int firstConfirm =
                JOptionPane.showConfirmDialog(
                        this,

                        "WARNING!\n\n"
                        + "You are about to permanently delete:\n\n"
                        + patientName
                        + "\n\n"
                        + "This action cannot be undone.\n"
                        + "The patient's archived record will be completely removed.",

                        "Permanent Delete Confirmation",

                        JOptionPane.YES_NO_OPTION,

                        JOptionPane.WARNING_MESSAGE
                );

        if (firstConfirm != JOptionPane.YES_OPTION) {
            return;
        }

        // SECOND NGA I CONFIRM
        String input =
                JOptionPane.showInputDialog(
                        this,

                        "To permanently delete this patient,\n"
                        + "type exactly:\n\n"
                        + "DELETE\n\n"
                        + "Patient: " + patientName,

                        "Final Confirmation",

                        JOptionPane.WARNING_MESSAGE
                );

        if (input == null) {
            return;
        }

        if (!input.trim().equals("DELETE")) {

            JOptionPane.showMessageDialog(
                    this,

                    "Permanent deletion cancelled.\n\n"
                    + "You must type DELETE exactly.",

                    "Deletion Cancelled",

                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        // DIRI ANG DELETE  SA MYSQL
        String sql =
                "DELETE FROM patients "
                + "WHERE patient_id = ? "
                + "AND status = 'ARCHIVED'";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst =
                    conn.prepareStatement(sql)
        ) {

            pst.setInt(1, patientId);

            int result =
                    pst.executeUpdate();

            if (result > 0) {
                //----------------------------
                DefaultTableModel model =
                        (DefaultTableModel) jTable1.getModel();

                model.removeRow(tableRow);
                //------------------------------------
                patientIds.remove(tableRow);

                JOptionPane.showMessageDialog(
                        this,

                        "Patient permanently deleted.\n\n"
                        + patientName
                        + "\n\n"
                        + "The record can no longer be recovered.",

                        "Permanent Delete",

                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,

                        "Patient could not be deleted.\n\n"
                        + "The record may no longer be archived.",

                        "Delete Error",

                        JOptionPane.ERROR_MESSAGE
                );
                //------------------------------------
                loadArchivedPatients();
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,

                    "ERROR DELETING ARCHIVED PATIENT:\n\n"
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
        jLabel2 = new javax.swing.JLabel();
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

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLayeredPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 0), 2, true));

        panel3.setBackground(new java.awt.Color(21, 90, 33));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ARCHIVED PATIENT REGISTRY ");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(127, 127, 127))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Record Date", "Delete Permamently", "Restore"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLayeredPane1.setLayer(panel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 848, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
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
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ArchivedPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArchivedPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArchivedPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArchivedPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArchivedPatient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField6;
    private java.awt.Panel panel1;
    private java.awt.Panel panel3;
    // End of variables declaration//GEN-END:variables
}
