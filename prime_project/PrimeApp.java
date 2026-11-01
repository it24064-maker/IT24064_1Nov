import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class PrimeApp {

    public static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    public static int findNthPrime(int n) {
        int count = 0;
        int num = 1;
        while (count < n) {
            num++;
            if (isPrime(num)) {
                count++;
            }
        }
        return num;
    }

    public static void main(String[] args) {
        String url = "jdbc:sqlite:test.db";
        String tableName = "roksana_prime";

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the value of n (which prime number you want): ");
        int n = scanner.nextInt();

        int nthPrime = findNthPrime(n);
        System.out.println("The " + n + "-th prime number is: " + nthPrime);

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    Statement stmt = conn.createStatement();

                    String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "n_position INTEGER NOT NULL, "
                            + "prime_value INTEGER NOT NULL"
                            + ")";
                    stmt.execute(createTableSQL);

                    String insertSQL = "INSERT INTO " + tableName + " (n_position, prime_value) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                        pstmt.setInt(1, n);
                        pstmt.setInt(2, nthPrime);
                        pstmt.executeUpdate();
                        System.out.println("Successfully inserted into table '" + tableName + "'!");
                    }

                    System.out.println("\n--- Stored Records in Database ---");
                    String selectSQL = "SELECT * FROM " + tableName;
                    ResultSet rs = stmt.executeQuery(selectSQL);

                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") 
                                + " | Position (n): " + rs.getInt("n_position") 
                                + " | Prime Number: " + rs.getInt("prime_value"));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}