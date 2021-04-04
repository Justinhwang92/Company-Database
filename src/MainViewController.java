import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MainViewController {
	@FXML
	private TextField userInput;

	@FXML
	Button login_button;

	private String ssn;
	private boolean valid;

	
	@FXML
	protected void initialize() {
		login_button.setOnAction(new EventHandler<ActionEvent>() {
			Boolean loggedIn;
			public void handle(ActionEvent event) {
				Parent root;
				try {
					// get ssn from user input
					ssn = userInput.getText();
					CompanyDatabase company = new CompanyDatabase();
					
					// check ssn validation
					valid = company.isManager(ssn);
					if (valid) loggedIn = true; 
					else loggedIn = false;

					// showing error message
					if(!loggedIn) {
						infoBox("Please enter correct SSN", null, "Failed");
					}
					else {
						root = FXMLLoader.load(getClass().getResource("ManagerView.fxml"));
						System.out.println(root);
						Stage stage = new Stage();
						stage.setTitle("Manager View");
						stage.setScene(new Scene(root, 800, 600));
						stage.show();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void infoBox(String msg, String text, String title) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

}
