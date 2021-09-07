package hiresort;

public class CancelledTicket extends AbstractEvent {

    private Long id;
    private Long reservationid;
    private String region;
    private String resort;
    private Long persons;
    private Long rooms;
    private String phonenumber;
    private String emailnumber;
    private String issuestatus;
    private Long ticketnumber;
    private String phone;
    private String email;

    public CancelledTicket(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationid() {
        return reservationid;
    }

    public void setReservationid(Long reservationid) {
        this.reservationid = reservationid;
    }
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    public String getResort() {
        return resort;
    }

    public void setResort(String resort) {
        this.resort = resort;
    }
    public Long getPersons() {
        return persons;
    }

    public void setPersons(Long persons) {
        this.persons = persons;
    }
    public Long getRooms() {
        return rooms;
    }

    public void setRooms(Long rooms) {
        this.rooms = rooms;
    }
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getEmailnumber() {
        return emailnumber;
    }

    public void setEmailnumber(String emailnumber) {
        this.emailnumber = emailnumber;
    }
    public String getIssuestatus() {
        return issuestatus;
    }

    public void setIssuestatus(String issuestatus) {
        this.issuestatus = issuestatus;
    }
    public Long getTicketnumber() {
        return ticketnumber;
    }

    public void setTicketnumber(Long ticketnumber) {
        this.ticketnumber = ticketnumber;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
