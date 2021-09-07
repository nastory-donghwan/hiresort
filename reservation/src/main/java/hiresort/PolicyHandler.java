package hiresort;

import hiresort.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverIssuedTicket_UpdateStatus(@Payload IssuedTicket issuedTicket){

        if(!issuedTicket.validate()) return;

        System.out.println("\n\n##### listener UpdateStatus : " + issuedTicket.toJson() + "\n\n");

        // Sample Logic //
        java.util.Optional<Reservation> optionalReservation = reservationRepository.findById(issuedTicket.getReservationid());
        Reservation reservation = optionalReservation.get();
        reservation.setIssuedid(issuedTicket.getId());
        reservation.setIssuedstatus(issuedTicket.getIssuestatus());

        reservationRepository.save(reservation);
    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
