import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbconnection {

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.137.1:3306/clinic";  // Replace with actual database name
        String user = "newuser";                                   // Replace with actual MySQL username
        String password = "password";                                // Replace with actual password

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}

