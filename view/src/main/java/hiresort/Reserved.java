package hiresort;

import java.util.Date;

public class Reserved extends AbstractEvent {

    private Long id;
    private Long rooms;
    private String resortname;
    private String region;
    private String resort;
    private String needs;
    private Date date;
    private Long persons;
    private String reservationstatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getRooms() {
        return rooms;
    }

    public void setRooms(Long rooms) {
        this.rooms = rooms;
    }
    public String getResortname() {
        return resortname;
    }

    public void setResortname(String resortname) {
        this.resortname = resortname;
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
    public String getNeeds() {
        return needs;
    }

    public void setNeeds(String needs) {
        this.needs = needs;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Long getPersons() {
        return persons;
    }

    public void setPersons(Long persons) {
        this.persons = persons;
    }
    public String getReservationstatus() {
        return reservationstatus;
    }

    public void setReservationstatus(String reservationstatus) {
        this.reservationstatus = reservationstatus;
    }
}