import java.util.Queue;

public class posting {
    private String companyName;
    private String jobTitle;

    private String jobDescription;

    private String skills;
    private String startingSalary;
    private String location;
    private Queue<application> applicants;

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

}
