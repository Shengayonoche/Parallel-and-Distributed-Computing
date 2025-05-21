
import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class main extends javax.swing.JFrame {

    
    public main() {
        initComponents();
       // setLocationRelativeTo(null);
        setSize(1200,770);
        setLocationRelativeTo(null);
        role();
    }
    
    public void role(){
    ButtonGroup rb=new ButtonGroup();
    rb.add(studentrb);
    rb.add(staffrb);
    
    if(studentrb.isSelected()){
    staffrb.setSelected(false);
    }else{
    studentrb.setSelected(false);
    }
    
    
    }
    
    public void sendAppointmentRequest(String email, String password, String role) {
    try {
        Socket socket = new Socket("92.168.137.1", 5000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send whatever data you want - for example, email, password, role
        out.println(email);
        out.println(password);
        out.println(role);

        socket.close();
    } catch (IOException e) {
        e.printStackTrace();
        // You can also show error on the UI if needed using SwingUtilities.invokeLater
    }
}

   
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        staffrb = new javax.swing.JRadioButton();
        studentrb = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        emailtf = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        passwordtf = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 770));
        setUndecorated(true);
        setSize(new java.awt.Dimension(1200, 770));
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(0, 35, 102));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        staffrb.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        staffrb.setForeground(new java.awt.Color(83, 100, 134));
        staffrb.setText(" staff");
        staffrb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staffrbActionPerformed(evt);
            }
        });
        jPanel2.add(staffrb);
        staffrb.setBounds(250, 290, 160, 27);

        studentrb.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        studentrb.setForeground(new java.awt.Color(83, 100, 134));
        studentrb.setText(" student");
        studentrb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentrbActionPerformed(evt);
            }
        });
        jPanel2.add(studentrb);
        studentrb.setBounds(90, 290, 160, 27);

        jLabel4.setBackground(new java.awt.Color(0, 35, 102));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 35, 102));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Password");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(0, 420, 260, 40);

        jLabel5.setBackground(new java.awt.Color(0, 35, 102));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 35, 102));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Log In");
        jPanel2.add(jLabel5);
        jLabel5.setBounds(0, 140, 600, 40);

        jLabel8.setBackground(new java.awt.Color(0, 35, 102));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 35, 102));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Role");
        jPanel2.add(jLabel8);
        jLabel8.setBounds(0, 250, 220, 40);

        jLabel9.setBackground(new java.awt.Color(0, 35, 102));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 35, 102));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Email");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(0, 320, 220, 40);

        emailtf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailtfActionPerformed(evt);
            }
        });
        jPanel2.add(emailtf);
        emailtf.setBounds(90, 370, 400, 40);

        jButton1.setBackground(new java.awt.Color(0, 35, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Sign In");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        jButton1.setBounds(250, 520, 150, 40);

        passwordtf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordtfActionPerformed(evt);
            }
        });
        jPanel2.add(passwordtf);
        passwordtf.setBounds(90, 460, 400, 40);

        jLabel12.setBackground(new java.awt.Color(0, 35, 102));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(60, 84, 128));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Enter credentials to sign in");
        jPanel2.add(jLabel12);
        jLabel12.setBounds(0, 180, 600, 40);

        jButton2.setBackground(new java.awt.Color(0, 35, 102));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Register");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);
        jButton2.setBounds(250, 580, 150, 40);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(600, 0, 600, 770);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/isatu.png"))); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(800, 800));
        jLabel2.setMinimumSize(new java.awt.Dimension(800, 800));
        jPanel1.add(jLabel2);
        jLabel2.setBounds(0, 220, 600, 340);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 215, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Iloilo Science and Technology University");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(0, 550, 600, 40);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(" Clinic Appointment System");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(40, 580, 510, 40);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1220, 780);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void staffrbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staffrbActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_staffrbActionPerformed

    private void studentrbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentrbActionPerformed
        // TODO add your handling code here:       
    }//GEN-LAST:event_studentrbActionPerformed

    private void emailtfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailtfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailtfActionPerformed

    private void passwordtfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordtfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordtfActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

/*
   String email = emailtf.getText().trim();
String password = passwordtf.getText().trim();
String role = studentrb.isSelected() ? "student" : staffrb.isSelected() ? "staff" : "";

if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Please fill all fields and select a role.");
    return;
}

if (role.equals("student")) {
    if (!email.matches("^[a-zA-Z0-9._%+-]+\\.[a-zA-Z0-9._%+-]+@students\\.isatu\\.edu\\.ph$")) {
        JOptionPane.showMessageDialog(this, "Student email must be in the format studentname.lastname@students.isatu.edu.ph");
        return;
    }
    if (!password.matches("^\\d{4}-\\d{4}-[A-Za-z]$")) {
        JOptionPane.showMessageDialog(this, "Password must be in the format: YYYY-1234-A");
        return;
    }
}

if (role.equals("staff")) {
    if (!email.matches("^[a-zA-Z0-9._%+-]+@isatu\\.edu\\.ph$")) {
        JOptionPane.showMessageDialog(this, "Staff email must be in the format name@isatu.edu.ph");
        return;
    }
    
}

try {
    
    if (role.equals("student")) {
        new studentdb(email).setVisible(true); 
    } else if (role.equals("staff")) {
        new teacherdb().setVisible(true);
    }

    this.dispose();
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
}*/

String email = emailtf.getText().trim();
String password = passwordtf.getText().trim();
String role = studentrb.isSelected() ? "student" : staffrb.isSelected() ? "staff" : "";

if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Please fill all fields and select a role.");
    return;
}

if (role.equals("student")) {
    if (!email.matches("^[a-zA-Z0-9._%+-]+\\.[a-zA-Z0-9._%+-]+@students\\.isatu\\.edu\\.ph$")) {
        JOptionPane.showMessageDialog(this, "Student email must be in the format studentname.lastname@students.isatu.edu.ph");
        return;
    }
    if (!password.matches("^\\d{4}-\\d{4}-[A-Za-z]$")) {
        JOptionPane.showMessageDialog(this, "Password must be in the format: YYYY-1234-A");
        return;
    }
}

if (role.equals("staff")) {
    if (!email.matches("^[a-zA-Z0-9._%+-]+@isatu\\.edu\\.ph$")) {
        JOptionPane.showMessageDialog(this, "Staff email must be in the format name@isatu.edu.ph");
        return;
    }
}

jButton1.setEnabled(false);

new Thread(() -> {
    try {
        if (role.equals("staff")) {
    SwingUtilities.invokeLater(() -> {
        teacherdb serverWindow = new teacherdb();
        serverWindow.setVisible(true);
        serverWindow.startServer();  // Starts server ONCE
        this.dispose();
    });
    }else if (role.equals("student")) {
            // Student acts as client
            sendAppointmentRequest(email, password, role);

            SwingUtilities.invokeLater(() -> {
                new studentdb(email).setVisible(true);
                this.dispose();
            });
        }
    } catch (Exception e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage());
            jButton1.setEnabled(true);
        });
    }
}).start();



    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        FlatLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField emailtf;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField passwordtf;
    private javax.swing.JRadioButton staffrb;
    private javax.swing.JRadioButton studentrb;
    // End of variables declaration//GEN-END:variables
}
