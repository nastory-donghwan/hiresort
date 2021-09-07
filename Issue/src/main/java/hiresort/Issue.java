package hiresort;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Issue_table")
public class Issue {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long reservationid;
    private String region;
    private String resort;
    private Long persons;
    private Long rooms;
    private String phone;
    private String email;
    private String issuestatus;
    private Long ticketnumber;
    private Long paymentId;
    private String paymentstatus;

    @PostPersist
    public void onPostPersist(){
        IssuedTicket issuedTicket = new IssuedTicket();
        BeanUtils.copyProperties(this, issuedTicket);
        issuedTicket.publishAfterCommit();

    }
    public String getPaymentstatus() {
        return paymentstatus;
    }
    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }
    public Long getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    @PostRemove
    public void onPostRemove(){
        CancelledTicket cancelledTicket = new CancelledTicket();
        BeanUtils.copyProperties(this, cancelledTicket);
        cancelledTicket.publishAfterCommit();

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




}