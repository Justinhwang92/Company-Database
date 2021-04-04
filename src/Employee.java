import java.text.SimpleDateFormat;
import java.util.*;

public class Employee {
	private ArrayList<Dependent> dependent = new ArrayList<Dependent>();
	private ArrayList<ProjectHour> listOfAssignedProjects = new ArrayList<ProjectHour>();
	private String fname;
	private String middlename;
	private String lname;
	private String sex;
	private String address;
	private String birthdate;
	private String ssn;
	private String superSSN;
	private int salary;
	private int dno;
	private String email;

	public Employee() {
		
	}

	public Employee(String fname, String mname, String lname, String sex, String address, String ssn, String superSSN, String birthdate, int salary, int dno, String email) {
		this.fname = fname;
		this.middlename = mname;
		this.lname = lname;
		this.sex = sex;
		this.ssn = ssn;
		
		this.address = address;
		this.birthdate = birthdate;
		this.email = email;
		this.salary = salary;
		
		this.superSSN = superSSN;
		this.dno = dno;
	}
	
	// getters
	public String getFirstName() {
		return this.fname;
	}
	
	public String getMiddleName() {
		return this.middlename;
	}
	
	public String getLastName() {
		return this.lname;
	}
	
	public String getSSN() {
		return this.ssn;
	}
	
	public String getAddress() { 
		return this.address;
	}
	
	public String getBirthDate() {
		return this.birthdate;
	}
	
	public String getSex() {
		return this.sex;
	}
	
	public String getSupervisorSSN() {
		return this.superSSN;
	}
	
	public int getSalary() {
		return this.salary;
	}
	
	public int getDepartmentNum() {
		return this.dno;
	}
	
	public String getEmail() {
		return this.email;
	}

	public ArrayList<ProjectHour> getAssignedProjects() {
		return this.listOfAssignedProjects;
	}
	
	public ArrayList<Dependent> getDependentInfo() {
		return this.dependent;
	}
	
	public String getProjectName(int index) {
		ProjectHour project = listOfAssignedProjects.get(index);
		return project.getProject();
	}
	
	public double getProjectHours(int index) {
		ProjectHour project = listOfAssignedProjects.get(index);
		return project.getHours();
	}
	
	
	// setters
	public void setFirstName(String fname) {
		this.fname = fname;
	}
	
	public void setMiddleInitial(String minit) {
		this.middlename = minit;
	}
	
	public void setLastName(String lname) {
		this.lname = lname;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setBirthDate(String bdate) {
		this.birthdate = bdate;
	}
	
	public void setSSN(String ssn) {
		this.ssn = ssn;
	}
	
	public void setSupervisorSSN(String superssn) {
		this.superSSN = superssn;
	}
	
	public void setSalary(int salary) {
		this.salary = salary;
	}
	
	public void setDepartmentNumber(int dno) {
		this.dno = dno;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setAssignedProjects(ArrayList<ProjectHour> listOfProjects) {
		this.listOfAssignedProjects = listOfProjects;
	}
	
	// class for dependent
	public class Dependent {
		private String dependentName;
		private String dependentSex;
		private String dependentBirthdate;
		private String relationship;
		private java.sql.Date dateSQL;
		
		public Dependent() {
			
		}
		
		public Dependent(String name, String sex, String birthdate, String relationship) {
			this.dependentName = name;
			this.dependentSex = sex;
			this.dependentBirthdate = birthdate;
			this.relationship = relationship;
		}
		
		
		//getters
		public String getName() {
			return this.dependentName;
		}
		public String getSex() {
			return this.dependentSex;
		}
		public String getBirthDate() {
			return this.dependentBirthdate;
		}
		public String getRelationship() {
			return this.relationship;
		}
		public java.sql.Date getDOBinSQL() {
			return this.dateSQL;
		}
		
		// set birth date 
		public void setSQLBdate(String birthdate) {
			SimpleDateFormat formatDate = new SimpleDateFormat("DD-MM-YY");
			try {
				Date formatBdate = formatDate.parse(birthdate);
				this.dateSQL = (java.sql.Date) formatBdate;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setDependents(String dependentName, String sex, String birthdate, String relationship) {
		Dependent myDependent = new Dependent(dependentName, sex, birthdate, relationship);
		myDependent.setSQLBdate(birthdate);
		dependent.add(myDependent);
	}	
}
