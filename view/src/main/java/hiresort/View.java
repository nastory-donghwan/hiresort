package hiresort;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="View_table")
public class View {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long reservationid;
        private String region;
        private String resort;
        private Long persons;
        private Long rooms;
        private String issuestatus;
        private Long ticketnumber;
        private String reservationstatus;


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
        public String getReservationstatus() {
            return reservationstatus;
        }

        public void setReservationstatus(String reservationstatus) {
            this.reservationstatus = reservationstatus;
        }

}
