package erik.best.practice;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class JdbcConnectDemo {

    public static final String MYSQL_USER_NAME = "root";
    public static final String MYSQL_PASS_WORD = "201203";
    public static final String SELECT_ALL_PERSON = "SELECT * FROM t_person";

    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.

    }

    @Test
    public void test_naming_look_up() throws NamingException {
        // 这个还没有测试通过。
        Context context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/myDB");
        try (Connection conn = dataSource.getConnection(MYSQL_USER_NAME, MYSQL_PASS_WORD);
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PERSON);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                printLine(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_driver() {
        String sqlSelectAllPersons = "SELECT * FROM t_person";
        String connectionUrl = "jdbc:mysql://localhost:3306/yueyi_test?serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "201203")) {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            System.out.println(drivers);
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                System.out.println(driver.getClass());
                System.out.println(driver.getClass().getClassLoader());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_new_mode_fof_connect_mysql() {
        ClassLoader demoClassLoader = JdbcConnectDemo.class.getClassLoader();
        System.out.println("当前类的类加载器：" + demoClassLoader);
        String sqlSelectAllPersons = "SELECT * FROM t_person";
        String connectionUrl = "jdbc:mysql://localhost:3306/yueyi_test?serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "201203");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                printLine(rs);
                // do something with the extracted data...
            }
        } catch (SQLException e) {
            // handle the exception
            e.printStackTrace();
        }
    }

    public void printLine(ResultSet rs) throws SQLException {
        while (rs.next()) {
            long id = rs.getLong("ID");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            System.out.printf("%d-%s-%d\n", id, name, age);
            // do something with the extracted data...
        }
    }

    @Test
    public void test_old() {
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/yueyi_test",
                    "root", "201203");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            Statement statement;
            statement = connection.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery(
                    "select * from t_person");
            int id;
            String name;
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                name = resultSet.getString("name").trim();
                System.out.println("id : " + id
                        + " name : " + name);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}