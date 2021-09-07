package hiresort;

import hiresort.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ViewViewHandler {


    @Autowired
    private ViewRepository viewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_CREATE_1 (@Payload Reserved reserved) {
        try {

            if (!reserved.validate()) return;

            // view 객체 생성
            View view = new View();
            // view 객체에 이벤트의 Value 를 set 함
            view.setReservationid(reserved.getId());
            view.setRegion(reserved.getRegion());
            view.setResort(reserved.getResort());
            view.setPersons(reserved.getPersons());
            view.setRooms(reserved.getRooms());
            view.setReservationstatus(reserved.getReservationstatus());
            // view 레파지 토리에 save
            viewRepository.save(view);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenIssuedTicket_then_CREATE_2 (@Payload IssuedTicket issuedTicket) {
        try {

            if (!issuedTicket.validate()) return;

            // view 객체 생성
            View view = new View();
            // view 객체에 이벤트의 Value 를 set 함
            view.setIssuestatus(issuedTicket.getIssuestatus());
            view.setTicketnumber(issuedTicket.getTicketnumber());
            // view 레파지 토리에 save
            viewRepository.save(view);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCancelledTicket_then_UPDATE_1(@Payload CancelledTicket cancelledTicket) {
        try {
            if (!cancelledTicket.validate()) return;
                // view 객체 조회

                    List<View> viewList = viewRepository.findByReservationid(cancelledTicket.getReservationid());
                    for(View view : viewList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    view.setIssuestatus(cancelledTicket.getIssuestatus());
                    view.setTicketnumber(cancelledTicket.getId());
                // view 레파지 토리에 save
                viewRepository.save(view);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelled_then_UPDATE_2(@Payload ReservationCancelled reservationCancelled) {
        try {
            if (!reservationCancelled.validate()) return;
                // view 객체 조회

                    List<View> viewList = viewRepository.findByReservationid(reservationCancelled.getId());
                    for(View view : viewList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    view.setReservationstatus(reservationCancelled.getReservationstatus());
                // view 레파지 토리에 save
                viewRepository.save(view);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

