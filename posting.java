import java.util.Queue;

public class posting {
    private String companyName;
    private String jobTitle;

    private String jobDescription;

    private String skills;
    private String startingSalary;
    private String location;
    private Queue<application> applicants;
    //this is the object for our postings
    public posting(String companyName, String jobTitle, String jobDescription, String skills, String startingSalary, String location){
        
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.skills = skills;
        this.startingSalary = startingSalary;
        this.location = location;

    }

    public void addApplication(application data){
        applicants.add(data);
    }

    public application getApplicant(){
        return applicants.poll();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getStartingSalary() {
        return startingSalary;
    }

    public void setStartingSalary(String startingSalary) {
        this.startingSalary = startingSalary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
