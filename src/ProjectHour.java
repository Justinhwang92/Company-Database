import java.math.BigDecimal;

public class ProjectHour {
	private String project;
	private BigDecimal pno;
	private double hours;

	public ProjectHour() {
		
	}

	public ProjectHour(String project, double hours) {
		this.project = project;
		this.hours = hours;
	}

	// getters
	public String getProject() {
		return this.project;
	}

	public BigDecimal getProjectPno() {
		return this.pno;
	}

	public double getHours() {
		return this.hours;
	}

	// setters
	public void setProject(String project) {
		this.project = project;
	}

	public void setProjectPno(BigDecimal pno) {
		this.pno = pno;
	}

	public void setHours(double hours) {
		this.hours = hours;
	}
}
