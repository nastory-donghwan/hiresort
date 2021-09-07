package hiresort;

import hiresort.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_Paycancel(@Payload ReservationCancelled reservationCancelled){

        // if(!reservationCancelled.validate()) return;

        // System.out.println("\n\n##### listener Paycancel : " + reservationCancelled.toJson() + "\n\n");

        // // Sample Logic //
        // Payment payment = new Payment();
        // payment.setReservationid(reservationCancelled.getId());
        // // payment.setPaymenttype(reservationCancelled.getPaymenttype());
        // payment.setPaymentstatus("Pay Cancelled");
        // payment.setRegion(reservationCancelled.getRegion());
        // payment.setResort(reservationCancelled.getResort());
        // // payment.setCost(reservationCancelled.getCost());
        // payment.setPersons(reservationCancelled.getPersons());
        // payment.setRooms(reservationCancelled.getRooms());       
        // paymentRepository.save(payment);
        if(reservationCancelled.validate())
        {
            System.out.println("\n\n##### listener PayCancel : " + reservationCancelled.toJson() + "\n\n");
            List<Payment> paymanetsList = paymentRepository.findByreservationid(reservationCancelled.getId());
            if(paymanetsList.size()>0) {
                for(Payment payment : paymanetsList) {
                    if(payment.getReservationid().equals(reservationCancelled.getId())){
                        System.out.println("##### OrderId :: "+ payment.getId() 
                                                      +" ... "+ payment.getResort()+" is Cancelled");
                        payment.setPaymenttype("ORDER CANCEL");
                        paymentRepository.save(payment);
                    }
                }
            }
        }


    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
