
import com.formdev.flatlaf.FlatLightLaf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;





public class teacherdb extends javax.swing.JFrame {
    private DefaultTableModel tableModelAll;
    private DefaultTableModel tableModelCompleted;
    private DefaultTableModel tableModelPending;
    private DefaultTableModel tableModelApproved;
    private DefaultTableModel tableModelDeclined;
    private String selectedDate = "2025-04-19";
    private DefaultTableModel tableModelMed;
   
      
   

    /**
     * Creates new form studentdb
     */
    public teacherdb() {
        initComponents();
        setSize(1200, 770);
        setLocationRelativeTo(null);
        initializeTableModels(); // Initialize the table models
        fetchTodayAppointments(); // Fetch today's appointments
        fetchPendingAppointments(); // Fetch pending appointments count
        fetchCompletedAppointments(); // Fetch completed appointments count
        loadAllAppointments(); // Load all appointments into the table
        setStatusColumnEditor();
        loadRecords();
        loadApprovedAppointments();
        loadCompletedAppointments();
        loadDeclinedAppointments();
        loadPendingAppointments();
        startServer();
        
    }
    
    
   private boolean serverStarted = false;

public void startServer() {
    if (serverStarted) return;  //prevent multiple bindings
    serverStarted = true;

    new Thread(() -> {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }).start();
}

    
    class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String appointmentRequest = in.readLine(); 
            System.out.println("Client says: " + appointmentRequest);

      

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }
}


    private void initializeTableModels() {
       
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
        
        tableModelMed = new DefaultTableModel(new Object[]{"Date", "Student ID", "Student Name", "Diagnosis","Treatment","Actions"}, 0);
        medtable.setModel(tableModelMed);
    }
    
    
    
    
    
    
    
    
 
    private void loadAllAppointments() {
        
        tableModelAll.setRowCount(0);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
            String query = "SELECT appointment_date, appointment_time, reason_for_appointment, status FROM appointments";
    
            try (PreparedStatement pst = con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                   
                    tableModelAll.addRow(new Object[]{
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("reason_for_appointment"),
                        rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading all appointments: " + e.getMessage());
        }
    }
    
    
 

    
    
    

    private void fetchTodayAppointments() {
       
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
            String query = "SELECT COUNT(*) AS total_appointments FROM appointments WHERE appointment_date = ?";
    
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, selectedDate); 

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int totalAppointments = rs.getInt("total_appointments");
                        
                        today.setText(String.valueOf(totalAppointments)); 
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }

    private void fetchPendingAppointments() {
       
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
           
            String query = "SELECT COUNT(*) AS total_pending FROM appointments WHERE status = 'pending'";
    
            try (PreparedStatement pst = con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int totalPending = rs.getInt("total_pending");
                    
                    pendingtf.setText(String.valueOf(totalPending)); 
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching pending appointments: " + e.getMessage());
        }
    }
 private void setStatusColumnEditor() {
    
    JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Pending", "Approved", "Declined", "Completed"});

 
    statusComboBox.addActionListener(e -> {
        int selectedRow = tblAllAppointments.getSelectedRow();
        if (selectedRow != -1) {
            
            String selectedStatus = (String) statusComboBox.getSelectedItem();
            
      
            tableModelAll.setValueAt(selectedStatus, selectedRow, 3);

          
            Object appointmentDate = tableModelAll.getValueAt(selectedRow, 0);
            Object appointmentTime = tableModelAll.getValueAt(selectedRow, 1);

          
            if (tblAllAppointments.getCellEditor() != null) {
                tblAllAppointments.getCellEditor().stopCellEditing();
            }

           
            updateAppointmentStatus(appointmentDate, appointmentTime, selectedStatus);

         
            updateCounts();

            switch (selectedStatus) {
                case "Completed":
                    loadCompletedAppointments();
                    break;
                case "Pending":
                    loadPendingAppointments();
                    break;
                case "Approved":
                    loadApprovedAppointments();
                    break;
                case "Declined":
                    loadDeclinedAppointments();
                    break;
            }
        }
    });


    tblAllAppointments.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusComboBox));
}

private void updateAppointmentStatus(Object appointmentDate, Object appointmentTime, String status) {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
        String query = "UPDATE appointments SET status = ? WHERE appointment_date = ? AND appointment_time = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setObject(2, appointmentDate);
            pst.setObject(3, appointmentTime);
            pst.executeUpdate(); 

            if (status.equalsIgnoreCase("Approved")) {
                NotificationPopup.showNotification("✅ Appointment has been approved!");
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating appointment status: " + e.getMessage());
    }
}



private void fetchCompletedAppointments() {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
        
        String query = "SELECT COUNT(*) AS total_completed FROM appointments WHERE status = 'Completed'"; 

        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int totalCompleted = rs.getInt("total_completed");
                
                completed.setText(String.valueOf(totalCompleted)); 
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error fetching completed appointments: " + e.getMessage());
    }
}
private void loadCompletedAppointments() {
    tableModelCompleted.setRowCount(0); 
    loadAppointmentsByStatus("Completed", tableModelCompleted);
}

private void loadPendingAppointments() {
    tableModelPending.setRowCount(0); 
    loadAppointmentsByStatus("Pending", tableModelPending);
}

private void loadApprovedAppointments() {
    tableModelApproved.setRowCount(0);
    loadAppointmentsByStatus("Approved", tableModelApproved);
}

private void loadDeclinedAppointments() {
    tableModelDeclined.setRowCount(0); 
    loadAppointmentsByStatus("Declined", tableModelDeclined);
}
private void loadAppointmentsByStatus(String status, DefaultTableModel tableModel) {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
        String query = "SELECT appointment_date, appointment_time, reason_for_appointment, status FROM appointments WHERE status = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, status);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                   
                    tableModel.addRow(new Object[]{
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("reason_for_appointment"),
                        rs.getString("status")
                    });
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
    }
}
private void updateCounts() {
    fetchTodayAppointments(); 
    fetchPendingAppointments(); 
    fetchCompletedAppointments(); 
}



private void loadRecords() {
  
    tableModelMed.setRowCount(0);

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "root", "Hnah@3.1415926")) {
        String query = "SELECT record_date, student_id, student_name, diagnosis, treatment FROM medical_records";

        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModelMed.addRow(new Object[]{
                    rs.getDate("record_date"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("diagnosis"),
                    rs.getString("treatment")
                });
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading medical records: " + e.getMessage());
    }
}











    // Other methods and components initialization...

   
       
    

    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        schedapt1 = new javax.swing.JButton();
        schedapt2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        schedapt3 = new javax.swing.JButton();
        schedapt4 = new javax.swing.JButton();
        schedapt5 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        completed = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        pendingtf = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        today = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
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
        jLabel32 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        searchbar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        medtable = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 35, 102));
        jPanel2.setLayout(null);

        schedapt1.setBackground(new java.awt.Color(0, 35, 102));
        schedapt1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt1.setForeground(new java.awt.Color(255, 255, 255));
        schedapt1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendarr.png"))); // NOI18N
        schedapt1.setText("Appointment Queue");
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
        schedapt2.setText("Dashboard");
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
        schedapt3.setForeground(new java.awt.Color(255, 255, 255));
        schedapt3.setText("Sign Out");
        schedapt3.setBorderPainted(false);
        schedapt3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedapt3.setIconTextGap(6);
        schedapt3.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt3ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt3);
        schedapt3.setBounds(0, 720, 310, 50);

        schedapt4.setBackground(new java.awt.Color(0, 35, 102));
        schedapt4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt4.setForeground(new java.awt.Color(255, 255, 255));
        schedapt4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendarr.png"))); // NOI18N
        schedapt4.setText("Medical Records");
        schedapt4.setBorderPainted(false);
        schedapt4.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        schedapt4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        schedapt4.setIconTextGap(6);
        schedapt4.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt4ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt4);
        schedapt4.setBounds(0, 180, 310, 50);

        schedapt5.setBackground(new java.awt.Color(0, 35, 102));
        schedapt5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        schedapt5.setForeground(new java.awt.Color(255, 255, 255));
        schedapt5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendarr.png"))); // NOI18N
        schedapt5.setText("Appointment Queue");
        schedapt5.setBorderPainted(false);
        schedapt5.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        schedapt5.setIconTextGap(6);
        schedapt5.setMargin(new java.awt.Insets(2, 30, 3, 14));
        schedapt5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedapt5ActionPerformed(evt);
            }
        });
        jPanel2.add(schedapt5);
        schedapt5.setBounds(0, 130, 310, 50);

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
        jLabel22.setText("Staff Dashboard");
        jPanel9.add(jLabel22);
        jLabel22.setBounds(50, 30, 390, 50);

        jPanel11.setBackground(new java.awt.Color(255, 253, 231));
        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 2, true));
        jPanel11.setLayout(null);

        jLabel1.setBackground(new java.awt.Color(0, 35, 102));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 35, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Completed");
        jPanel11.add(jLabel1);
        jLabel1.setBounds(0, 0, 220, 40);

        completed.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        completed.setForeground(new java.awt.Color(255, 215, 0));
        completed.setText("0");
        jPanel11.add(completed);
        completed.setBounds(90, 60, 90, 40);

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(102, 102, 102));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("total completed appointments");
        jPanel11.add(jLabel31);
        jLabel31.setBounds(20, 120, 180, 30);

        jPanel9.add(jPanel11);
        jPanel11.setBounds(560, 100, 220, 180);

        jPanel13.setBackground(new java.awt.Color(255, 253, 231));
        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 2, true));
        jPanel13.setLayout(null);

        jLabel3.setBackground(new java.awt.Color(0, 35, 102));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 35, 102));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Pending Approval");
        jPanel13.add(jLabel3);
        jLabel3.setBounds(0, 0, 220, 50);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 35, 102), 1, true));
        jPanel14.setLayout(null);

        jLabel7.setBackground(new java.awt.Color(0, 35, 102));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Today's Appointment");
        jPanel14.add(jLabel7);
        jLabel7.setBounds(10, 6, 140, 20);

        jLabel8.setText("jLabel2");
        jPanel14.add(jLabel8);
        jLabel8.setBounds(30, 60, 90, 40);

        jPanel13.add(jPanel14);
        jPanel14.setBounds(270, 100, 180, 180);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 35, 102), 1, true));
        jPanel15.setLayout(null);

        jLabel11.setBackground(new java.awt.Color(0, 35, 102));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Today's Appointment");
        jPanel15.add(jLabel11);
        jLabel11.setBounds(10, 6, 140, 20);

        jLabel16.setText("jLabel2");
        jPanel15.add(jLabel16);
        jLabel16.setBounds(30, 60, 90, 40);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 35, 102), 1, true));
        jPanel16.setLayout(null);

        jLabel19.setBackground(new java.awt.Color(0, 35, 102));
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Today's Appointment");
        jPanel16.add(jLabel19);
        jLabel19.setBounds(10, 6, 140, 20);

        jLabel26.setText("jLabel2");
        jPanel16.add(jLabel26);
        jLabel26.setBounds(30, 60, 90, 40);

        jPanel15.add(jPanel16);
        jPanel16.setBounds(270, 100, 180, 180);

        jPanel13.add(jPanel15);
        jPanel15.setBounds(270, 100, 180, 180);

        pendingtf.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        pendingtf.setForeground(new java.awt.Color(255, 215, 0));
        pendingtf.setText("0");
        jPanel13.add(pendingtf);
        pendingtf.setBounds(90, 60, 90, 40);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("waiting for approval");
        jPanel13.add(jLabel5);
        jLabel5.setBounds(20, 120, 180, 30);

        jPanel9.add(jPanel13);
        jPanel13.setBounds(310, 100, 220, 180);

        jPanel17.setBackground(new java.awt.Color(255, 253, 231));
        jPanel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 2, true));
        jPanel17.setLayout(null);

        jLabel27.setBackground(new java.awt.Color(0, 35, 102));
        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 35, 102));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Today's Appointment");
        jPanel17.add(jLabel27);
        jLabel27.setBounds(0, 0, 220, 50);

        today.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        today.setForeground(new java.awt.Color(255, 215, 0));
        today.setText("0");
        jPanel17.add(today);
        today.setBounds(90, 60, 90, 40);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Scheduled for today");
        jPanel17.add(jLabel2);
        jLabel2.setBounds(20, 120, 180, 30);

        jPanel9.add(jPanel17);
        jPanel17.setBounds(60, 100, 220, 180);

        jPanel5.add(jPanel9);
        jPanel9.setBounds(0, -10, 890, 780);

        jPanel4.add(jPanel5);
        jPanel5.setBounds(0, 0, 890, 765);

        jTabbedPane1.addTab("tab2", jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);

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

        jTabbedPane2.addTab("Declined", jScrollPane6);

        jPanel6.add(jTabbedPane2);
        jTabbedPane2.setBounds(50, 130, 790, 590);

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 35, 102));
        jLabel32.setText("Appointment Queue");
        jPanel6.add(jLabel32);
        jLabel32.setBounds(50, 30, 390, 30);

        jButton1.setText("refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton1);
        jButton1.setBounds(731, 60, 70, 24);

        jTabbedPane1.addTab("tab3", jPanel6);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(null);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 35, 102));
        jLabel17.setText("Medical Records");
        jPanel8.add(jLabel17);
        jLabel17.setBounds(50, 30, 390, 50);

        searchbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbarActionPerformed(evt);
            }
        });
        searchbar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchbarKeyReleased(evt);
            }
        });
        jPanel8.add(searchbar);
        searchbar.setBounds(50, 100, 790, 24);

        medtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Student ID", "Student Name", "Diagnosis", "Treatment", "Actions"
            }
        ));
        jScrollPane1.setViewportView(medtable);

        jPanel8.add(jScrollPane1);
        jScrollPane1.setBounds(50, 170, 790, 550);

        jPanel3.add(jPanel8);
        jPanel8.setBounds(0, -10, 890, 780);

        jTabbedPane1.addTab("tab1", jPanel3);

        jPanel10.setLayout(null);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 35, 102));
        jLabel18.setText("Reports");
        jPanel12.add(jLabel18);
        jLabel18.setBounds(50, 30, 390, 50);

        jLabel24.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel24.setText("Name");
        jPanel12.add(jLabel24);
        jLabel24.setBounds(50, 90, 60, 30);

        jLabel25.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel25.setText("Student ID");
        jPanel12.add(jLabel25);
        jLabel25.setBounds(330, 90, 210, 30);

        jPanel10.add(jPanel12);
        jPanel12.setBounds(0, -10, 890, 780);

        jTabbedPane1.addTab("tab4", jPanel10);

        jPanel1.add(jTabbedPane1);
        jTabbedPane1.setBounds(310, -30, 890, 800);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1220, 780);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void schedapt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt1ActionPerformed
        // TODO add your handling code here:
          jTabbedPane1.setSelectedIndex(1); 
    }//GEN-LAST:event_schedapt1ActionPerformed

    private void schedapt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt2ActionPerformed
        // TODO add your handling code here:
          jTabbedPane1.setSelectedIndex(0); 
    }//GEN-LAST:event_schedapt2ActionPerformed

    private void schedapt3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt3ActionPerformed
        // TODO add your handling code here:
        new signout().setVisible(true);
    }//GEN-LAST:event_schedapt3ActionPerformed

    private void schedapt4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt4ActionPerformed
        // TODO add your handling code here:
          jTabbedPane1.setSelectedIndex(2); 
    }//GEN-LAST:event_schedapt4ActionPerformed

    private void schedapt5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedapt5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_schedapt5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchbarActionPerformed

    private void searchbarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchbarKeyReleased
        // TODO add your handling code here:
        
     DefaultTableModel obj=(DefaultTableModel) medtable.getModel(); 
        TableRowSorter<DefaultTableModel> model1=new TableRowSorter<>(obj);
        medtable.setRowSorter(model1);
        model1.setRowFilter(RowFilter.regexFilter(searchbar.getText()));
    }//GEN-LAST:event_searchbarKeyReleased

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
            public void run() {
                new teacherdb().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel completed;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
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
    private javax.swing.JTable medtable;
    private javax.swing.JLabel pendingtf;
    private javax.swing.JButton schedapt1;
    private javax.swing.JButton schedapt2;
    private javax.swing.JButton schedapt3;
    private javax.swing.JButton schedapt4;
    private javax.swing.JButton schedapt5;
    private javax.swing.JTextField searchbar;
    private javax.swing.JTable tblAllAppointments;
    private javax.swing.JTable tblApproved;
    private javax.swing.JTable tblCompleted;
    private javax.swing.JTable tblDecline;
    private javax.swing.JTable tblPending;
    private javax.swing.JLabel today;
    // End of variables declaration//GEN-END:variables
}
