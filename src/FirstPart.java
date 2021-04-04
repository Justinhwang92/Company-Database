import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstPart {
	private static Connection conn;
	private static PreparedStatement state;


	public static void main(String[] args) throws SQLException {
		connect();
		
		// Part1_1
		System.out.println("1) The employees who work in the Research department and print the employee’s last name and their SSN.\n");
		part1_1();
		System.out.println();
		
		// Part1_2
		System.out.println("2) The employees who work in departments located in Houston and work on the project ‘ProductZ’. "
				+ "List their last name, SSN, and the number of hours that the employee works on that project.\n");
		part1_2();
		
	}

	private static void connect() throws SQLException {
		// register the JDBC driver
	    try {
	    	Class.forName("oracle.jdbc.driver.OracleDriver");
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	    
	    // create a connection
	    try {
	    	conn = DriverManager.getConnection("jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu", "shwang27", "ushuptee");
	    } catch (SQLException e) {
	    	System.out.println("Error: unable to load driver class!");
	    	e.printStackTrace();
	    }
	}
	
	private static void part1_1() {
		String sql = "SELECT lname, ssn "
					+ "FROM employee, department "
					+ "WHERE dno = dnumber and dname = ?";
		
		try{
			state=conn.prepareStatement(sql);
			state.setString(1, "Research");
			
			ResultSet queryResult=state.executeQuery();
			System.out.println("Last Name\tSSN");
			
			while(queryResult.next()){
				String lname = queryResult.getString("lname");
				String ssn = queryResult.getString("ssn");
				
				System.out.println(lname + "\t\t" + ssn);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void part1_2() {
		String sql = "SELECT lname, ssn, SUM(hours) "
				+ "FROM employee, works_on "
				+ "WHERE dno IN (SELECT dnumber FROM dept_locations WHERE dlocation = ?) "
				+ "AND ssn = essn AND pno IN (SELECT pnumber FROM project WHERE pname = ?) "
				+ "GROUP BY lname, ssn";
		
		try{
			state = conn.prepareStatement(sql);
			state.setString(1, "Houston");
			state.setString(2, "ProductZ");
			
			ResultSet queryResult = state.executeQuery();
			System.out.println("Last Name\tSSN\t\tTotal Hours");
			
			while(queryResult.next()){
				String lname = queryResult.getString("lname");
				String ssn = queryResult.getString("ssn");
				float hours = queryResult.getFloat("SUM(hours)");
				
				System.out.println(lname + "\t\t" + ssn + "\t" + hours);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}