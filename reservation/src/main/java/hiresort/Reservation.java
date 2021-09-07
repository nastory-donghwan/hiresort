package hiresort;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Date date;
    private Long persons;
    private Long rooms;
    private String region;
    private String resort;
    private String needs;
    private String reservationstatus;
    private Long issuedid;
    private String issuedstatus;
    private Long paymentid;
    private String paymenttype;
    private Long cost;

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        // reserved.publishAfterCommit();

        // reserved.setReservationstatus("Reservation Completed");

        hiresort.external.Payment payment = new hiresort.external.Payment();
        // mappings goes here

        payment.setReservationid(this.getId());
        payment.setPaymenttype(this.getPaymenttype());
        payment.setPaymentstatus("Not Pay");
        payment.setRegion(this.getRegion());
        payment.setResort(this.getResort());
        payment.setCost(this.getCost());
        payment.setPersons(this.getPersons());
        payment.setRooms(this.getRooms());       

        // ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        //     .Pay(this.getPaymentid());

        ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        .paid(payment);

        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
   
        // reserved.publishAfterCommit();
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.


    }

    @PostRemove
    public void onPostRemove(){
        ReservationCancelled reservationCancelled = new ReservationCancelled();
        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.setReservationstatus("Reservation Cancelled");
        reservationCancelled.publishAfterCommit();

        // hiresort.external.Payment payment = new hiresort.external.Payment();
        // ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        //     .paycancel(this.getPaymentid());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Long getRooms() {
        return rooms;
    }

    public void setRooms(Long rooms) {
        this.rooms = rooms;
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
    public String getReservationstatus() {
        return reservationstatus;
    }

    public void setReservationstatus(String reservationstatus) {
        this.reservationstatus = reservationstatus;
    }

    public String getIssuedstatus() {
        return issuedstatus;
    }
    public void setIssuedstatus(String issuedstatus) {
        this.issuedstatus = issuedstatus;
    }
    public Long getIssuedid() {
        return issuedid;
    }
    public void setIssuedid(Long issuedid) {
        this.issuedid = issuedid;
    }

    public long getCost() {
        return cost;
    }


    public void setCost(long cost) {
        this.cost = cost;
    }


    public String getPaymenttype() {
        return paymenttype;
    }


    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public Long getPaymentid() {
        return paymentid;
    }


    public void setPaymentid(Long paymentid) {
        this.paymentid = paymentid;
    }
}