import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class studentdb extends javax.swing.JFrame {
        
    String url = "jdbc:mysql://192.168.137.1:3306/clinic";
        String user = "newuser";
        String password = "password";
    private String loggedInEmail;
private DefaultTableModel tableModelAll;
    private DefaultTableModel tableModelCompleted;
    private DefaultTableModel tableModelPending;
    private DefaultTableModel tableModelApproved;
    private DefaultTableModel tableModelDeclined;

    /**
     * Creates new form studentdb
     */
    public studentdb(String email) {
        this.loggedInEmail = email; 
        initComponents();
        setSize(1200, 770);
        setLocationRelativeTo(null);
        fetchStudentDetails(); 
         idtf.setEnabled(false);
         nametf.setEnabled(false);
         addresstf.setEnabled(false);
         agetf.setEnabled(false);
       bdaytf.setEnabled(false);
collegetf.setEnabled(false);
time.setSelectedIndex(-1); 
date.setDate(null); 
appointment.setSelectedIndex(-1); 
details.setText("");
initializeTableModels();
loadAppointmentsIntoTable(loggedInEmail);
loadPending(loggedInEmail);
loadCompleted(loggedInEmail);
loadApprove(loggedInEmail);
loadDecline(loggedInEmail);
 loadAppointmentsIntoTable(loggedInEmail);
/*new Thread(() -> {
        sendAppointmentRequest();
    }).start();*/



    }
    
   /*public void sendAppointmentRequest() {
    try {
        // Connect to the server's IP and port
        Socket socket = new Socket("127.0.0.1", 5000);

        // Create writer to send data
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Hello Server!");
        // Get the appointment text from the text field
      //  String appointmentText = jTextField1.getText();

        // Send the appointment text to the server
       // out.println(appointmentText);

        // Close the socket after sending
        socket.close();

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to send request: " + e.getMessage());
    }
}
*/
    
    private void initializeTableModels() {
        // Initialize the table models for each appointments table
        tableModelAll = new DefaultTableModel(new Object[]{"Date", "Time", "Reason", "Status"}, 0);
        tblAllAppointments.setModel(tableModelAll);

        tableModelCompleted = new DefaultTableModel(new Object[]{"Date", "Time", "Reason", "Status"}, 0);
        tblCompleted.setModel(tableModelCompleted);

        tableModelPending = new DefaultTableModel(new Object[]{"Date", "Time", "Reason", "Status"}, 0);
        tblPending.setModel(tableModelPending);

        tableModelApproved = new DefaultTableModel(new Object[]{"Date", "Time", "Reason", "Status"}, 0);
        tblApproved.setModel(tableModelApproved);

        tableModelDeclined = new DefaultTableModel(new Object[]{"Date", "Time", "Reason", "Status"}, 0);
        tblDecline.setModel(tableModelDeclined);
    }

 
    
    
private void fetchStudentDetails() {
    try (Connection con = DriverManager.getConnection(url, user, password)) {
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        String query = "SELECT * FROM students WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, loggedInEmail);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                   
                    nametf.setText(rs.getString("name"));
                    addresstf.setText(rs.getString("address"));
                    agetf.setText(String.valueOf(rs.getInt("age")));
                    idtf.setText(rs.getString("student_id"));
                    
                    
                    java.sql.Date sqlDate = rs.getDate("birthdate"); 
                    if (sqlDate != null) {
                        bdaytf.setDate(sqlDate); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Birthday not found for this student.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No student found with this email.");
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage());
    }
}


private void loadAppointmentsByStatus(String email, String status, JTable table) {
    String query = "SELECT appointment_date, appointment_time, reason_for_appointment, status " +
                   "FROM appointments " +
                   "WHERE status = ? AND student_id IN (SELECT student_id FROM students WHERE email = ?)";

    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Date");
    model.addColumn("Time");
    model.addColumn("Reason");
    model.addColumn("Status");

    table.setModel(model);

    
    try (Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = con.prepareStatement(query)) {

        pstmt.setString(1, status);
        pstmt.setString(2, email);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Object[] row = new Object[4];
            row[0] = rs.getDate("appointment_date");
            row[1] = rs.getTime("appointment_time");
            row[2] = rs.getString("reason_for_appointment");
            row[3] = rs.getString("status");
            model.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading appointments: " + e.getMessage());
    }
}

// Method to load all appointments
public void loadAppointmentsIntoTable(String email) {
    String query = "SELECT a.appointment_date, a.appointment_time, a.reason_for_appointment, a.status " +
                   "FROM appointments a " +
                   "JOIN students s ON a.student_id = s.student_id " +
                   "WHERE s.email = ?";

    DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Time", "Reason", "Status"}, 0);
    tblAllAppointments.setModel(model);

    try (Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = con.prepareStatement(query)) {

        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getDate("appointment_date"),
                rs.getTime("appointment_time"),
                rs.getString("reason_for_appointment"),
                rs.getString("status")
            });
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading student appointments: " + e.getMessage());
    }
}

public void loadPending(String email) {
    
    loadAppointmentsByStatus(email, "Pending", tblPending);
}

public void loadCompleted(String email) {
    loadAppointmentsByStatus(email, "Completed", tblCompleted);
}

public void loadApprove(String email) {
    loadAppointmentsByStatus(email, "Approved", tblApproved);
}

public void loadDecline(String email) {
    loadAppointmentsByStatus(email, "Declined", tblDecline);
}










    
    
    
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        schedapt = new javax.swing.JButton();
        schedapt1 = new javax.swing.JButton();
        schedapt2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        schedapt3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        idtf = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        nametf = new javax.swing.JTextField();
        addresstf = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        agetf = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        collegetf = new javax.swing.JComboBox<>();
        bdaytf = new com.toedter.calendar.JDateChooser();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        appointment = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        details = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        idtf1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        nametf1 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        time = new javax.swing.JComboBox<>();
        date = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAllAppointments = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCompleted = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPending = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblApproved = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblDecline = new javax.swing.JTable();

        jLabel4.setText("jLabel4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 35, 102));
        jPanel2.setLayout(null);

        schedapt.setBackground(new java.awt.Color(0, 35, 102));
        schedapt.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt.setForeground(new java.awt.Color(255, 255, 255));
        schedapt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/transaction.png"))); // NOI18N
        schedapt.setText("  Transactions");
        schedapt.setBorderPainted(false);
        schedapt.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        schedapt.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        schedapt.setIconTextGap(6);
        schedapt.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedaptActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt);
        schedapt.setBounds(0, 180, 310, 50);

        schedapt1.setBackground(new java.awt.Color(0, 35, 102));
        schedapt1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt1.setForeground(new java.awt.Color(255, 255, 255));
        schedapt1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendarr.png"))); // NOI18N
        schedapt1.setText("  Book Appointment");
        schedapt1.setBorderPainted(false);
        schedapt1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        schedapt1.setIconTextGap(6);
        schedapt1.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt1ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt1);
        schedapt1.setBounds(0, 130, 310, 50);

        schedapt2.setBackground(new java.awt.Color(0, 35, 102));
        schedapt2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt2.setForeground(new java.awt.Color(255, 255, 255));
        schedapt2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cl.png"))); // NOI18N
        schedapt2.setText("  Profile");
        schedapt2.setBorderPainted(false);
        schedapt2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        schedapt2.setIconTextGap(6);
        schedapt2.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt2ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt2);
        schedapt2.setBounds(0, 80, 310, 50);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 215, 0));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/smalllogo.png"))); // NOI18N
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel2.add(jLabel6);
        jLabel6.setBounds(120, 10, 60, 70);

        schedapt3.setBackground(new java.awt.Color(0, 35, 102));
        schedapt3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt3.setForeground(new java.awt.Color(255, 215, 0));
        schedapt3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/signout.png"))); // NOI18N
        schedapt3.setText("Sign Out");
        schedapt3.setBorderPainted(false);
        schedapt3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        schedapt3.setIconTextGap(5);
        schedapt3.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt3ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt3);
        schedapt3.setBounds(0, 710, 310, 60);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 310, 770);

        jPanel4.setLayout(null);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(null);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(null);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 35, 102));
        jButton5.setText("Sign Out");
        jPanel9.add(jButton5);
        jButton5.setBounds(-300, 710, 280, 40);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 35, 102));
        jLabel22.setText("Profile");
        jPanel9.add(jLabel22);
        jLabel22.setBounds(50, 30, 390, 50);

        jLabel25.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 35, 102));
        jLabel25.setText("Name");
        jPanel9.add(jLabel25);
        jLabel25.setBounds(80, 180, 60, 30);

        jLabel26.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 35, 102));
        jLabel26.setText("Student ID");
        jPanel9.add(jLabel26);
        jLabel26.setBounds(80, 120, 210, 30);

        idtf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idtfActionPerformed(evt);
            }
        });
        jPanel9.add(idtf);
        idtf.setBounds(170, 120, 240, 30);

        jButton6.setBackground(new java.awt.Color(0, 35, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Edit Profile");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton6);
        jButton6.setBounds(660, 700, 160, 40);

        nametf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nametfActionPerformed(evt);
            }
        });
        jPanel9.add(nametf);
        nametf.setBounds(170, 180, 240, 30);

        addresstf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addresstfActionPerformed(evt);
            }
        });
        jPanel9.add(addresstf);
        addresstf.setBounds(170, 240, 240, 30);

        jLabel27.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 35, 102));
        jLabel27.setText("Address");
        jPanel9.add(jLabel27);
        jLabel27.setBounds(80, 240, 60, 30);

        jLabel28.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 35, 102));
        jLabel28.setText("Age");
        jPanel9.add(jLabel28);
        jLabel28.setBounds(480, 120, 210, 30);

        agetf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agetfActionPerformed(evt);
            }
        });
        jPanel9.add(agetf);
        agetf.setBounds(570, 120, 240, 30);

        jLabel29.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 35, 102));
        jLabel29.setText("College");
        jPanel9.add(jLabel29);
        jLabel29.setBounds(480, 240, 60, 30);

        jLabel30.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 35, 102));
        jLabel30.setText("Birthday");
        jPanel9.add(jLabel30);
        jLabel30.setBounds(480, 180, 60, 30);

        collegetf.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "College of Arts and Sciences", "College of Engineering and Architecture", "College of Industrial Technology", "College of Education" }));
        jPanel9.add(collegetf);
        collegetf.setBounds(570, 242, 240, 30);
        jPanel9.add(bdaytf);
        bdaytf.setBounds(580, 190, 230, 24);

        jPanel5.add(jPanel9);
        jPanel9.setBounds(0, -10, 890, 780);

        jPanel4.add(jPanel5);
        jPanel5.setBounds(0, 0, 890, 765);

        jTabbedPane1.addTab("tab2", jPanel4);

        jPanel6.setLayout(null);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 35, 102));
        jLabel9.setText("Additional Details (Optional)");
        jPanel7.add(jLabel9);
        jLabel9.setBounds(50, 340, 380, 50);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 35, 102));
        jLabel10.setText("Schedule Appointment");
        jPanel7.add(jLabel10);
        jLabel10.setBounds(50, 30, 390, 50);

        appointment.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        appointment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dental Consultation", "Skin Issues", "Cough and Cold", "Headache", "Injury", "Other" }));
        jPanel7.add(appointment);
        appointment.setBounds(50, 280, 760, 30);

        jLabel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 35, 102));
        jLabel12.setText("Reason for Appointment");
        jPanel7.add(jLabel12);
        jLabel12.setBounds(50, 240, 210, 40);

        details.setColumns(20);
        details.setRows(5);
        jScrollPane1.setViewportView(details);

        jPanel7.add(jScrollPane1);
        jScrollPane1.setBounds(50, 400, 760, 130);

        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 35, 102));
        jLabel13.setText("Date");
        jPanel7.add(jLabel13);
        jLabel13.setBounds(50, 160, 290, 40);

        jLabel14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 35, 102));
        jLabel14.setText("Name");
        jPanel7.add(jLabel14);
        jLabel14.setBounds(50, 90, 60, 30);

        jLabel15.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 35, 102));
        jLabel15.setText("Student ID");
        jPanel7.add(jLabel15);
        jLabel15.setBounds(330, 90, 210, 30);
        jPanel7.add(idtf1);
        idtf1.setBounds(330, 120, 240, 30);

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 35, 102));
        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton1);
        jButton1.setBounds(50, 610, 760, 40);

        jButton2.setBackground(new java.awt.Color(0, 35, 102));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Schedule Appointment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton2);
        jButton2.setBounds(50, 560, 760, 40);

        nametf1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nametf1ActionPerformed(evt);
            }
        });
        jPanel7.add(nametf1);
        nametf1.setBounds(50, 120, 250, 30);

        jLabel23.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 35, 102));
        jLabel23.setText("Time");
        jPanel7.add(jLabel23);
        jLabel23.setBounds(330, 160, 290, 40);

        time.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", " " }));
        jPanel7.add(time);
        time.setBounds(330, 200, 240, 30);
        jPanel7.add(date);
        date.setBounds(50, 200, 260, 24);

        jPanel6.add(jPanel7);
        jPanel7.setBounds(0, -10, 890, 780);

        jTabbedPane1.addTab("tab3", jPanel6);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(null);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 35, 102));
        jLabel17.setText("Transaction History");
        jPanel8.add(jLabel17);
        jLabel17.setBounds(50, 30, 390, 50);

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));

        tblAllAppointments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Reason", "Status"
            }
        ));
        jScrollPane2.setViewportView(tblAllAppointments);

        jTabbedPane2.addTab("All", jScrollPane2);

        tblCompleted.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Reason", "Status"
            }
        ));
        jScrollPane3.setViewportView(tblCompleted);

        jTabbedPane2.addTab("Completed", jScrollPane3);

        tblPending.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Reason", "Status"
            }
        ));
        jScrollPane4.setViewportView(tblPending);

        jTabbedPane2.addTab("Pending", jScrollPane4);

        tblApproved.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Reason", "Status"
            }
        ));
        jScrollPane5.setViewportView(tblApproved);

        jTabbedPane2.addTab("Approved", jScrollPane5);

        tblDecline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Reason", "Status"
            }
        ));
        jScrollPane6.setViewportView(tblDecline);

        jTabbedPane2.addTab("Decline", jScrollPane6);

        jPanel8.add(jTabbedPane2);
        jTabbedPane2.setBounds(50, 130, 790, 590);

        jPanel3.add(jPanel8);
        jPanel8.setBounds(0, -10, 890, 780);

        jTabbedPane1.addTab("tab1", jPanel3);

        jPanel1.add(jTabbedPane1);
        jTabbedPane1.setBounds(310, -30, 890, 800);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1220, 780);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void schedaptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedaptActionPerformed
        // TODO add your handling code here:
          jTabbedPane1.setSelectedIndex(2); 
          loadAppointmentsIntoTable(loggedInEmail); 

          


    


          
    }//GEN-LAST:event_schedaptActionPerformed

    private void schedapt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt1ActionPerformed
    jTabbedPane1.setSelectedIndex(1); 
    
    
    idtf1.setEnabled(false);
    nametf1.setEnabled(false);
                                         
  try (Connection con = DriverManager.getConnection(url, user, password)) {

        if (con == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        String query = "SELECT name, student_id FROM students WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, loggedInEmail); 
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                   
                    nametf1.setText(rs.getString("name"));
                     
                    idtf1.setText(rs.getString("student_id"));
                } else {
                    JOptionPane.showMessageDialog(this, "No student record found for the given email.");
                    this.dispose();
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage());
    }

          
    }//GEN-LAST:event_schedapt1ActionPerformed

    private void schedapt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt2ActionPerformed
        // TODO add your handling code here:
          jTabbedPane1.setSelectedIndex(0); 
    }//GEN-LAST:event_schedapt2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed


    String studentId = idtf1.getText();
Date selectedDate = date.getDate(); 
String appointmentType = (String) appointment.getSelectedItem();
String appointmentTimeStr = (String) time.getSelectedItem(); 
String message = details.getText(); 


if (studentId.isEmpty() || selectedDate == null || appointmentTimeStr == null || appointmentType == null) {
    JOptionPane.showMessageDialog(this, "Please complete all required fields.");
    return;
}

java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());


Time sqlTime = null;
try {
    SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a");
    java.util.Date timeDate = sdf12.parse(appointmentTimeStr);
    sqlTime = new Time(timeDate.getTime());
} catch (ParseException e) {
    JOptionPane.showMessageDialog(this, "Invalid time format.");
    return;
}

try (Connection con = DriverManager.getConnection(url, user, password)) {

    String insertQuery = "INSERT INTO appointments (student_id, appointment_date, appointment_time, reason_for_appointment, additional_details, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, NOW())";

    try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
        pst.setString(1, studentId);
        pst.setDate(2, sqlDate);
        pst.setTime(3, sqlTime);
        pst.setString(4, appointmentType);
        pst.setString(5, message.isEmpty() ? null : message); 

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
           
            
            new booked().setVisible(true);
            
          time.setSelectedIndex(-1); 
date.setDate(null); 
appointment.setSelectedIndex(-1); 
details.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Failed to schedule appointment.");
        }
    }
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
}

loadAppointmentsIntoTable(loggedInEmail); 



    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
                 idtf.setEnabled(true);
         nametf.setEnabled(true);
         addresstf.setEnabled(true);
         agetf.setEnabled(true);
       bdaytf.setEnabled(true);
collegetf.setEnabled(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void schedapt3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt3ActionPerformed
        // TODO add your handling code here:
        new signout().setVisible(true);
    }//GEN-LAST:event_schedapt3ActionPerformed

    private void idtfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idtfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idtfActionPerformed

    private void nametfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nametfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nametfActionPerformed

    private void addresstfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addresstfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addresstfActionPerformed

    private void agetfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agetfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_agetfActionPerformed

    private void nametf1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nametf1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nametf1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        time.setSelectedIndex(-1); // For JComboBox (dropdown)
date.setDate(null); // For JDateChooser
appointment.setSelectedIndex(-1); // For JComboBox (dropdown)
details.setText("");
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
            java.util.logging.Logger.getLogger(studentdb.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(studentdb.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(studentdb.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(studentdb.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(String email) {
                new studentdb(email).setVisible(true);
            }

            @Override
            public void run() {
               try {
        // Connect to the server's IP and port
        Socket socket = new Socket("192.168.137.1", 5000);

        // Create writer to send data
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Hello Server!");
        // Get the appointment text from the text field
      //  String appointmentText = jTextField1.getText();

        // Send the appointment text to the server
       // out.println(appointmentText);

        // Close the socket after sending
        socket.close();

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,"Failed to send request: " + e.getMessage());
    }
            }
        });
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addresstf;
    private javax.swing.JTextField agetf;
    private javax.swing.JComboBox<String> appointment;
    private com.toedter.calendar.JDateChooser bdaytf;
    private javax.swing.JComboBox<String> collegetf;
    private com.toedter.calendar.JDateChooser date;
    private javax.swing.JTextArea details;
    private javax.swing.JTextField idtf;
    private javax.swing.JTextField idtf1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField nametf;
    private javax.swing.JTextField nametf1;
    private javax.swing.JButton schedapt;
    private javax.swing.JButton schedapt1;
    private javax.swing.JButton schedapt2;
    private javax.swing.JButton schedapt3;
    private javax.swing.JTable tblAllAppointments;
    private javax.swing.JTable tblApproved;
    private javax.swing.JTable tblCompleted;
    private javax.swing.JTable tblDecline;
    private javax.swing.JTable tblPending;
    private javax.swing.JComboBox<String> time;
    // End of variables declaration//GEN-END:variables
}
