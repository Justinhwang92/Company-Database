import javafx.event.*;
import javafx.fxml.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ManagerViewController {
	ProjectHour projectHour;
	ProjectHour projectSet;
	CompanyDatabase company;
	Employee employee;
	
	// information for employee
	@FXML
	private TextField fname_input;
	@FXML
	private TextField middleName_input;
	@FXML
	private TextField lname_input;
	@FXML
	private TextField sex_input;
	@FXML
	private TextField ssn_input;
	@FXML
	private TextField address_input;
	@FXML
	private TextField birthdate_input;
	@FXML
	private TextField email_input;
	@FXML
	private TextField salary_input;
	@FXML
	private TextField superssn_input;
	@FXML
	private ComboBox<String> departmentLists;
	// buttons for employee
	@FXML
	private Button scanButton;
	@FXML
	private Button addEmployeeButton;
	@FXML
	private Button removeEmployeeButton;
	
	// information for dependent
	@FXML
	private CheckBox checkBox_yes;
	@FXML
	private CheckBox checkBox_no;
	@FXML
	private TextField dependent_fname_input;
	@FXML
	private TextField dependent_bdate_input;
	@FXML
	private TextField dependent_sex_input;
	@FXML
	private TextField dependent_relationship_input;
	// button for dependent
	@FXML
	private Button addDependentButton;
	
	// information for project
	@FXML
	private ComboBox<String> projectList;
	@FXML
	private TextField workHour_input;
	// buttons for project
	@FXML
	private Button assign_project_button;
	@FXML
	private Button delete_project_button;
	
	// boolean values for checking validation
	boolean employValidation = false;
	boolean dependentValidation = false;
	
	int dependentCounter = 0; // counter for how many dependents this employee has
	private double sumHours = 0; // value for checking whether it exceed the 40 max hours

	private ObservableList<String> listOfProjects = FXCollections.observableArrayList();
	private ObservableList<Integer> department_Numbers = FXCollections.observableArrayList();
	
	private ArrayList<Integer> dumList;
	private ArrayList<String> projectLists;
	private ArrayList<ProjectHour> selectedProj = new ArrayList<ProjectHour>();

	@FXML
	protected void initialize() throws IOException {
		int listSize = 0;
		try {
			employee = new Employee();
			company = new CompanyDatabase();
			dumList = company.getDnumList();
			
			for (int i = 0; i < dumList.size(); i++) {
				department_Numbers.add(dumList.get(i));
			}
			for (int i = 0; i < department_Numbers.size(); i++) {
				departmentLists.getItems().add(department_Numbers.get(i).toString());
			}
			projectLists = company.getProjectList();
			assert projectLists.size() > 0;
			listSize = projectLists.size();
			for (int i = 0; i < listSize; i++) {
				listOfProjects.add(projectLists.get(i));
			}
			projectList.getItems().addAll(listOfProjects);

		} catch (SQLException se) {
			System.out.println("ERROR: Could not connect to the Database");
			System.exit(1);
		}
		
		// initialize buttons
		addEmployeeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				addEmployee();
			}
		});
		assign_project_button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				addProject();
			}
		});
		addDependentButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				addDependent();
			}
		});
		
		
		// Extra credit (scan employee)
		scanButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				scanEmployee(); 
			}
		});
		
		// Extra credit (delete employee)
		// When deleting a project, the employees will also be deleted, 
		// and check for constraint violations, and explain why a project may not be deleted
		removeEmployeeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				removeEmployee();
			}
		});
		
		// Extra credit (delete project)
		delete_project_button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				deleteProject();
			}
		});
		
		checkBox_yes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> oldValue, Boolean myOldValue, Boolean myNewValue) {
				System.out.println(myNewValue);
				if (!myNewValue) {
					dependent_fname_input.setEditable(false);
					dependent_sex_input.setEditable(false);
					dependent_bdate_input.setEditable(false);
					dependent_relationship_input.setEditable(false);
				} else {
					dependent_fname_input.setEditable(true);
					dependent_sex_input.setEditable(true);
					dependent_bdate_input.setEditable(true);
					dependent_relationship_input.setEditable(true);
				}
			}
		});

		
		checkBox_no.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> oldValue, Boolean myOldValue, Boolean myNewValue) {
				System.out.println(myNewValue);
				if (!myNewValue) {
					dependent_fname_input.setEditable(true);
					dependent_sex_input.setEditable(true);
					dependent_bdate_input.setEditable(true);
					dependent_relationship_input.setEditable(true);

					dependent_fname_input.setVisible(true);
					dependent_sex_input.setVisible(true);
					dependent_bdate_input.setVisible(true);
					dependent_relationship_input.setVisible(true);
				} else {
					dependent_fname_input.setEditable(false);
					dependent_sex_input.setEditable(false);
					dependent_bdate_input.setEditable(false);
					dependent_relationship_input.setEditable(false);

					dependent_fname_input.setVisible(false);
					dependent_sex_input.setVisible(false);
					dependent_bdate_input.setVisible(false);
					dependent_relationship_input.setVisible(false);
				}
			}
		});
	}

	@FXML
	protected void handleYesBox() {
		if (checkBox_yes.isSelected()) {
			checkBox_no.setSelected(false);
		} 
	}

	@FXML
	protected void handleNoBox() {
		if (checkBox_no.isSelected()) {
			checkBox_yes.setSelected(false);
		} 
	}

	// Extra credit (scan employee)
	// Perform a scan of the database to verify that no employees violate the above rules
	@FXML
	protected void scanEmployee() {
		try {
			if (fname_input.getText().isEmpty()) {
				infoBox("Please enter First name", null, "Failed");
				employValidation = false;
			}
			else if (middleName_input.getText().isEmpty()) {
				infoBox("Please enter Middle initial", null, "Failed");
				employValidation = false;
			}
			else if (lname_input.getText().isEmpty()) {
				infoBox("Please enter Last name", null, "Failed");
				employValidation = false;
			}

			else if (ssn_input.getText().isEmpty()) {
				infoBox("Please enter employee's SSN", null, "Failed");
				employValidation = false;
			}

			else if (ssn_input.getText().length() != 9) {
				infoBox("Invalid SSN, please enter employee's 9 digit SSN without dashes(-)", null, "Failed");
				employValidation = false;
			}

			else if (birthdate_input.getText().isEmpty()) {
				infoBox("Please enter Employee's birth date", null, "Failed");
				employValidation = false;
			}
			else if (birthdate_input.getText().length() != 8) {
				infoBox("Invalid Birthdate format, please endter dd-mm-yy", null, "Failed");
				employValidation = false;

			}
			else if (address_input.getText().isEmpty()) {
				infoBox("Please enter the address", null, "Failed");
				employValidation = false;
			}
			String sexInput = sex_input.getText();

			if (sexInput.isEmpty()) {
				infoBox("Please enter Employee's sex", null, "Failed");
				employValidation = false;
			}
			else if (!sexInput.equalsIgnoreCase("M") && !sexInput.equalsIgnoreCase("F")){
				infoBox("Invalid sex format, please M for male or F for female", null, "Failed");
				employValidation = false;
			}

			else if (salary_input.getText().isEmpty()) {
				infoBox("Please enter the salary", null, "Failed");
				employValidation = false;
			}
			else if (superssn_input.getText().isEmpty()) {
				infoBox("Please enter the manager's SSN", null, "Failed");
				employValidation = false;
			}
			else if (superssn_input.getText().length() != 9) {
				infoBox("Invalid SSN, please enter manager's 9 digit SSN without dashes(-)", null, "Failed");
				employValidation = false;
			}
			
			// Extra credit (ask for the department)
			// When inserting a new employee, ask for the department to which they will be assigned,
			// and the project(s) to which they are assigned;
			else if (departmentLists.getSelectionModel().getSelectedItem() == null) {
				infoBox("Please select the department number", null, "Failed");
				employValidation = false;
			}

			else if (email_input.getText().isEmpty()) {
				infoBox("Please enter the email address", null, "Failed");
				employValidation = false;
			}
			
			// Extra credit (2)
			// An employee must work on at least one project controlled by his/her department.
			else if (selectedProj.size() == 0) {
				infoBox("Please assign at least 1 project for this employee", null, "Failed");
				employValidation = false;
			}

			else if (workHour_input.getText().isEmpty()) {
				infoBox("Please enter the work hours", null, "Failed");
				employValidation = false;
			}

			else if (!checkBox_yes.isSelected() && !checkBox_no.isSelected() || checkBox_yes.isSelected() && checkBox_no.isSelected()) {
				infoBox("Please check dependent box (yes or no)", null, "Failed");
				employValidation = false;
			}

			else if (checkBox_yes.isSelected() && dependentCounter == 0) {
				infoBox("Please enter the dependent information correctly", null, "Failed");
				employValidation = false;
			}

			else { 
				employValidation = true;
				infoBox("Everything is good!", null, "Succeed");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Add employee
	@FXML
	protected void addEmployee(){
		try {
			if(!employValidation) {
				infoBox("Please hit the \"Validation\" button to check the above information is correct", null, "Failed");
			}
			else {
				employee.setFirstName(fname_input.getText());
				employee.setMiddleInitial(middleName_input.getText());
				employee.setLastName(lname_input.getText());
				employee.setSex(sex_input.getText());

				employee.setSSN(ssn_input.getText());
				employee.setAddress(address_input.getText());
				employee.setBirthDate(birthdate_input.getText());
				employee.setEmail(email_input.getText());

				employee.setSalary(Integer.parseInt(salary_input.getText()));
				employee.setSupervisorSSN(superssn_input.getText());
				employee.setDepartmentNumber(Integer.parseInt(departmentLists.getSelectionModel().getSelectedItem()));
				
				if (employee.getDependentInfo().size() > 0) company.insertDependent(employee); 
					
				employee.setAssignedProjects(selectedProj);

				company.insertEmployee(employee);
				company.insertWorks_On(employee);
				
				printReport(ssn_input.getText());
				
				infoBox("This employee is added!", null, "Succeed");
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Extra credit (delete employee)
	// When removing an employee from a project, check if any constraints are violated, 
	// and if so reject the update and explain why it was rejected.
	@FXML
	protected void removeEmployee() {
		if(ssn_input.getText().isEmpty()) {
			infoBox("Please enter the SSN for the employee you want to remove", null, "Failed");
		}
		try {
			Employee employeeToRemove = new Employee();
			employeeToRemove = company.getEmployee(ssn_input.getText());
			company.removeEmployee(employeeToRemove,company);
		}
		catch (Exception e) {
			e.printStackTrace();
			infoBox("Constraint violation!", null, "Failed");
		}
	}
	
	// Extra credit (violation check)
	// When removing an employee from a project, check if any constraints 
	// are violated, and if so reject the update and explain why it was rejected.
	@FXML
	protected void deleteProject() {
		String pName;
		try {
			pName = projectList.getSelectionModel().getSelectedItem();

			if(pName == null){
				infoBox("Please choose the project name that you want to remove", null, "Failed");
			}
			else {
				company.removeProj(pName);
			}
		}
		catch (Exception e) {
			infoBox("Refferential constraint violation!, please check the other tables which are using pnumber", null, "Failed");
		}
	}
	
	// Add dependent
	@FXML
	protected void addDependent() {
		try {
			String sex = dependent_sex_input.getText();
			
			if(!checkBox_yes.isSelected() && !checkBox_no.isSelected()) {
				infoBox("Please select the yes or no box in dependent section", null, "Failed");
				dependentValidation = false;
			}
			else if(checkBox_yes.isSelected() && dependent_bdate_input.getText().length() != 8 ) {
				infoBox("Invalid Birthdate format, please endter dd-mm-yy", null, "Failed");
				dependentValidation = false;
			}
			else if (checkBox_yes.isSelected() && sex.isEmpty()) {
				infoBox("Please enter the dependent's sex", null, "Failed");
				dependentValidation = false;
			}
			else if (checkBox_yes.isSelected() && (!sex.equalsIgnoreCase("M") && !sex.equalsIgnoreCase("F"))) {
				infoBox("Invalid sex format, please M for male or F for female", null, "Failed");
				dependentValidation = false;
			}
			else if(checkBox_yes.isSelected() && dependent_relationship_input.getText().isEmpty()) {
				infoBox("Please enter the dependent's relation", null, "Failed");
				dependentValidation = false;
			}
			else if(checkBox_yes.isSelected() && dependent_fname_input.getText().isEmpty()) {
				infoBox("Please enter the dependent's first name", null, "Failed");
				dependentValidation = false;
			}

			else if(checkBox_no.isSelected()) {
				infoBox("Please check \"Yes\" if this employee havs dependent", null, "Failed");
				dependentValidation = false;
			}
			else {
				// dependent is added
				dependentValidation = true;
				dependentCounter++;
				// set this dependent to this employee
				employee.setDependents(dependent_fname_input.getText(), dependent_sex_input.getText(), dependent_bdate_input.getText(), dependent_relationship_input.getText());
				
				infoBox("This dependent is added!", null, "Succeed");
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			// un-check the check boxes 
			checkBox_no.setSelected(false);
			checkBox_yes.setSelected(false);
		}
	}
	
	// Add project
	@FXML
	protected void addProject() {
		projectHour = new ProjectHour();
		
		sumHours = 0;
		boolean valid = true;

		try {
			projectHour.setHours(Double.parseDouble(workHour_input.getText()));
			projectHour.setProject(projectList.getSelectionModel().getSelectedItem().toString());

			if(ssn_input.getText().isEmpty()) {
				selectedProj.clear();
				infoBox("Please enter the all inforamtion correctly", null, "Failed");
				valid = false;
			}
			else if(ssn_input.getText().length() != 9) {
				selectedProj.clear();
				infoBox("Invalid SSN, please enter employee's 9 digit SSN without dashes(-)", null, "Failed");
				valid = false;
			}
			
			// Extra credit
			// An employee may not work on more than two projects managed by his/her department.
			selectedProj.add(projectHour);
			if(noTwoProjects(selectedProj,Integer.parseInt(departmentLists.getSelectionModel().getSelectedItem()))) {
				selectedProj.clear();
				infoBox("one department assigned too many projects", null, "Failed");
				valid = false;
			}

			if(isMaxHour(selectedProj)) {
				selectedProj.clear();
				infoBox("work hour must be less than 40 hours", null, "Failed");
				valid = false;
			}

			if(findDuplicateProject(selectedProj) > 0) {
				int i = findDuplicateProject(selectedProj);
				selectedProj.remove(i);
				infoBox("assign duplicate projects", null, "Failed");
				valid = false;
			}
			else {
				if(valid)
				infoBox("This project is added!", null, "Succeed");
				projectList.getSelectionModel().clearSelection();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Checking maximum hour
	private boolean isMaxHour(ArrayList<ProjectHour> proj) {
		for (int i = 0; i < proj.size(); i++) {
			projectSet = proj.get(i);
			sumHours += projectSet.getHours();
			if (sumHours > 40) {
				return true;
			}
		}
		return false;
	}

	// Check duplicate project
	private int findDuplicateProject(ArrayList<ProjectHour> proj) {
		ProjectHour pName = new ProjectHour();
		int i;
		for (i = 0; i < proj.size(); i++) {
			pName = proj.get(i);
			for (int j = ++i; j < proj.size(); j++) {
				projectSet = proj.get(j);
				if (pName.getProject().equals(projectSet.getProject())) {
					return j;
				}
			}
		}
		return 0;
	}
	
	// printing the result
	private void printReport(String ssn) {
		String report = "REPORT\n";
		Employee newEmp;
		Employee.Dependent newDep;
		ArrayList<ProjectHour> projHour;

		try {
			newEmp = company.getEmployee(ssn);
			report = report + "Here is the information about new employee\n";
			
			report = report + "First name: " + newEmp.getFirstName() + "\n";
			report = report + "Middle name: " + newEmp.getMiddleName() + "\n";
			report = report + "Last name: " + newEmp.getLastName() + "\n";
			report = report + "Sex: " + newEmp.getSex() + "\n";
			report = report + "SSN: " + newEmp.getSSN() + "\n";
			report = report + "Address: " + newEmp.getAddress() + "\n";
			report = report + "Birthdate: " + newEmp.getBirthDate()+ "\n";
			report = report + "Email: " + newEmp.getEmail() + "\n";
			report = report + "Salary: " + newEmp.getSalary() + "\n";
			report = report + "Manager's SSN: " + newEmp.getSupervisorSSN() + "\n";
			report = report + "Department: " + newEmp.getDepartmentNum() + "\n";

			projHour = company.getEmployeeProjects(newEmp);
			for (int i = 0; i < projHour.size(); i++) {
				report = report + "Assigned Project Name: " + projHour.get(i).getProject() + "\n";
				report = report + "Project Hour: " + projHour.get(i).getHours() + "\n";
			}
			
			report = report + "\nDependent Information\n";
			company.getDependents(newEmp);
			if (newEmp.getDependentInfo().size() > 0) {
				for (int i = 0; i < newEmp.getDependentInfo().size(); i++) {
					newDep = newEmp.getDependentInfo().get(i);
					
					report = report + "Name: " + newDep.getName() + "\n";
					report = report + "Sex: " + newDep.getSex() + "\n";
					report = report + "Birthdate: " + newDep.getBirthDate() + "\n";
					report = report + "Relationship: " + newDep.getRelationship() + "\n";
				}
			}
			System.out.println(report);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	 
	// Extra credit (1)
	// An employee may not work on more than two projects managed by his/her department.
	private Boolean noTwoProjects(ArrayList<ProjectHour> proj, int dno) {
		int temp = 0;
		int c = 0;
		for (int i = 0; i < proj.size(); i++) {
			try {
				temp = company.getDnum(proj.get(i).getProject());
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			if (temp == dno) c++;
			if (c > 2) return true;
		}
		return false;
	}
	
	public static void infoBox(String msg, String text, String title) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
