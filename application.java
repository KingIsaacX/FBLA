public class application {
    
    private account person;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String education;
    private String experience;

    private String references;
    
    public application(account person, String firstName, String lastName, String phoneNumber, String email, String education, String experience, String references){
        this.person = person;
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.education = education;
        this.experience = experience;
        this.references = references;
    }



}
