import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.math.BigDecimal;

public class CompanyDatabase {
	SimpleDateFormat formatDate = new SimpleDateFormat("DD-MM-YY");
	private Connection conn;
	private PreparedStatement state;
	private ResultSet resultSet;
	
	public CompanyDatabase() throws SQLException, IOException {
		
	}
	
	// Project number / work hour set
	private class PnoHourSet  {
		private BigDecimal pno;
		private BigDecimal hours;

		private PnoHourSet(BigDecimal pno, BigDecimal hours) {
			this.pno = pno;
			this.hours = hours;
		}

		private BigDecimal getPno() {
			return this.pno;
		}

		private BigDecimal getProjectHour() {
			return this.hours;
		}
	}

	// JDBC connect
	public void connect() throws SQLException, IOException {
		// register the JDBC driver
	    try {
	    	Class.forName("oracle.jdbc.driver.OracleDriver");
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	    
	    // create a connection
	    try {
	    	conn = DriverManager.getConnection("");
	    } catch (SQLException e) {
	    	System.out.println("Error: unable to load driver class!");
	    	e.printStackTrace();
	    }
	}
	
	// get project number
	public BigDecimal getPno(String pName) throws SQLException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		String sql = "SELECT pnumber "
				+ "FROM project "
				+ "WHERE pname = ?";
		BigDecimal pno = null;
		
		state = conn.prepareStatement(sql);
		
		state.clearParameters();
		state.setString(1, pName);
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			pno = resultSet.getBigDecimal(1);
		}
		
		state.close();
		resultSet.close();
		conn.close();
		
		return pno;
	}
	
	// get department number
	public int getDnum(String pname) throws SQLException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();			
		}
		
		String sql = "SELECT dnum "
				+ "FROM project "
				+ "WHERE pname = ?";
		int dno = 0;
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		state.setString(1, pname);
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			dno = resultSet.getInt(1);
		}
		
		resultSet.close();
		state.close();
		conn.close();

		return dno;
	}
	
	// get the project names
	public ArrayList<String> getProjectList() throws SQLException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		String sql = "SELECT pname "
				+ "FROM project";
		String project = "";
		
		ArrayList<String> projects = new ArrayList<String>();
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			project = resultSet.getString(1);
			projects.add(project);
		}
		
		state.close();
		resultSet.close();
		conn.close();
		
		return projects;
	}
	
	// get department numbers
	public ArrayList<Integer> getDnumList() throws SQLException, IOException{
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<Integer> departmentNumbers = new ArrayList<Integer>();
		String sql = "SELECT dnumber "
				+ "FROM department";
	
		state = conn.prepareStatement(sql);
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			departmentNumbers.add(resultSet.getInt(1));
		}
		
		conn.close();
		state.close();
		resultSet.close();
		
		return departmentNumbers;
	}

	// get employee information
	public Employee getEmployee(String ssn) throws SQLException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		Employee employee = new Employee();
		String sql = "SELECT fname, minit, lname, ssn, bdate, address, sex, salary, superssn, dno, email "
				+ "FROM employee "
				+ "WHERE ssn = ?";
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		state.setString(1, ssn);
		
		resultSet = state.executeQuery();
		while(resultSet.next()) {
			employee.setFirstName(resultSet.getString(1));
			employee.setMiddleInitial(resultSet.getString(2));
			employee.setLastName(resultSet.getString(3));
			employee.setSSN(resultSet.getString(4));
			employee.setBirthDate(resultSet.getString(5));
			employee.setAddress(resultSet.getString(6));
			employee.setSex(resultSet.getString(7));
			employee.setSalary(resultSet.getInt(8));
			employee.setSupervisorSSN(resultSet.getString(9));
			employee.setDepartmentNumber(resultSet.getInt(10));
			employee.setEmail(resultSet.getString(11));
		}
		
		resultSet.close();
		state.close();
		conn.close();

		return employee;
	}
	
	// get dependent information 
	public void getDependents(Employee employee) throws SQLException { 
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		String sql = "SELECT dependent_name, sex, bdate, relationship"
				+ "FROM dependent "
				+ "WHERE essn = ?";
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		state.setString(1, employee.getSSN());
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			employee.setDependents(resultSet.getString(1), 
					resultSet.getString(2), 
					resultSet.getDate(3).toString(), 
					resultSet.getString(4));
		}
		
		state.close();
		resultSet.close();
		conn.close();
	}

	// get project name, number, hours for this employee
	public ArrayList<ProjectHour> getEmployeeProjects(Employee employee) throws SQLException, IOException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<ProjectHour> list = new ArrayList<ProjectHour>();
		String sqpPno = "SELECT pno,hours "
				+ "FROM works_on "
				+ "WHERE essn = ?";
		
		String sqlPname = "SELECT pname "
				+ "FROM project "
				+ "WHERE pnumber = ?";
		
		state = conn.prepareStatement(sqpPno);
		state.clearParameters();
		state.setString(1, employee.getSSN());
		resultSet = state.executeQuery();
		
		while(resultSet.next()) {
			ProjectHour myProjectHour = new ProjectHour();
			myProjectHour.setProjectPno(resultSet.getBigDecimal(1));
			myProjectHour.setHours(resultSet.getBigDecimal(2).doubleValue());
			list.add(myProjectHour);
		}
		state.close();
		
		state = conn.prepareStatement(sqlPname);
		for(int i = 0; i < list.size(); i++) {
			state.clearParameters();
			state.setBigDecimal(1, list.get(i).getProjectPno());
			resultSet = state.executeQuery();
			while(resultSet.next()) {
				list.get(i).setProject(resultSet.getString(1));
			}
		}
		
		state.close();
		resultSet.close();
		conn.close();
		
		return list;
	}
	
	// remove employee from works on table 
	void removeEmployee(Employee employeeToRemove, CompanyDatabase company) throws SQLException, IOException {
		String sql = "DELETE FROM works_on WHERE essn = ?";
		try {
			connect();
			if(company.getEmployeeProjects(employeeToRemove).isEmpty()) {
				infoBox("invalid ssn!", null, "failed");
			}
			else {
				state = conn.prepareStatement(sql);
				state.clearParameters();
				state.setString(1, employeeToRemove.getSSN());
				resultSet = state.executeQuery();
				
				resultSet.close();
				state.close();
				conn.close();
				infoBox("This employee is removed!", null, "Succeed");
			}
		}
		catch(IOException e) {
			e.printStackTrace();			
		}
	}

	// remove project from project table
	public void removeProj(String projectName) throws SQLException, IOException {
		String sql = "DELETE FROM project(pname) values(?)";
		connect();
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		state.setString(1,projectName);
		
		state.executeUpdate();		

		state.close();
		conn.close();
	}

	// add new employee 
	public void insertEmployee(Employee employee) throws IOException {
		BigDecimal salary = new BigDecimal(employee.getSalary());
		BigDecimal deptNum = new BigDecimal(employee.getDepartmentNum());
		java.sql.Date date = null;
		try {
			java.util.Date javaDate = formatDate.parse(employee.getBirthDate());
			date = new java.sql.Date(javaDate.getTime());
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		String sql = "INSERT INTO employee values(?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connect();
			
			state = conn.prepareStatement(sql);
			state.clearParameters();
			
			state.setString(1, employee.getFirstName());
			state.setString(2, employee.getMiddleName());
			state.setString(3, employee.getLastName());
			state.setString(4, employee.getSSN());
			state.setDate(5, date);
			state.setString(6, employee.getAddress());
			state.setString(7, employee.getSex());
			state.setBigDecimal(8, salary);
			state.setString(9, employee.getSupervisorSSN());
			state.setBigDecimal(10, deptNum);
			state.setString(11, employee.getEmail());
			
			state.executeUpdate();
			
			conn.close();
			state.close();
			resultSet.close();
		}
		catch(SQLException e) {
			System.out.println(e.getStackTrace());
		}
	}

	// add new works on
	public void insertWorks_On(Employee employee) throws SQLException, IOException {
		ArrayList<PnoHourSet> projects = new ArrayList<PnoHourSet>();
		String pName = null;
		BigDecimal pno;
		BigDecimal hour;
		String sql = "INSERT INTO works_on(essn,pno,hours) values(?,?,?)";

		for(int i = 0; i < employee.getAssignedProjects().size();i++) {
			pName = employee.getProjectName(i);
			pno = getPno(pName);
			hour = new BigDecimal(employee.getProjectHours(i));
			PnoHourSet myProjectHour = new PnoHourSet(pno, hour);
			projects.add(myProjectHour);
		}

		connect();
		for(int i = 0; i < projects.size(); i++) {
			state = conn.prepareStatement(sql);
			state.clearParameters();
			
			state.setString(1,employee.getSSN());
			state.setBigDecimal(2, projects.get(i).getPno());
			state.setBigDecimal(3, projects.get(i).getProjectHour());
			
			state.executeUpdate();
		}
		
		state.close();
		conn.close();
	}
	
	// add new dependent
	public void insertDependent(Employee employee) throws SQLException, IOException {
		ArrayList<Employee.Dependent> dependents;
		Employee.Dependent dependent;
		String sql = "INSERT INTO dependent(essn,dependent_name,sex,bdate,relationship) values(?,?,?,?,?)";

		connect();

		dependents = employee.getDependentInfo();

		for(int i = 0; i < dependents.size(); i++) {
			dependent = dependents.get(i);
			state = conn.prepareStatement(sql);
			state.clearParameters();
			
			state.setString(1, employee.getSSN());
			state.setString(2, dependent.getName());
			state.setString(3, dependent.getSex());
			state.setDate(4, dependent.getDOBinSQL());
			state.setString(5,dependent.getRelationship());
			
			state.executeUpdate();
		}
		
		state.close();
		conn.close();
	}
	
	// check given ssn matches the one of the mangers' 
	public boolean isManager(String ssn) throws SQLException {
		try {
			connect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		String sql = "SELECT dname "
				+ "FROM department "
				+ "WHERE mgrssn = ?";
		String mgrSSN = "";
		
		state = conn.prepareStatement(sql);
		state.clearParameters();
		state.setString(1,ssn);
		resultSet = state.executeQuery();
		
		while(resultSet.next()) mgrSSN = resultSet.getString(1);
		
		conn.close();
		resultSet.close();
		state.close();
		
		if(mgrSSN.length() == 0 || mgrSSN.isEmpty()) return false;
		else return true;
	}

	public static void infoBox(String msg, String text, String title) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
