package hiresort;

import hiresort.config.kafka.KafkaProcessor;
// import com.fasterxml.jackson.databind.DeserializationFeature;
// import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{
    @Autowired IssueRepository issueRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Createticket(@Payload Paid paid){

        if(!paid.validate()) return; {

            System.out.println("\n\n##### listener Createticket : " + paid.toJson() + "\n\n");

            // Sample Logic //
            Issue issue = new Issue();
            // if("END".equals(paid.getPaymentstatus())) {
                issue.setPaymentId(paid.getId());
                issue.setReservationid(paid.getReservationid());
                issue.setRegion(paid.getRegion());
                issue.setResort(paid.getResort());
                issue.setPersons(paid.getPersons());
                issue.setRooms(paid.getRooms());
                issue.setIssuestatus("Ticket Issued");

                issueRepository.save(issue);
            // }
        }
    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCancelled_Cancelticket(@Payload PayCancelled payCancelled){

        if(!payCancelled.validate()) return; {

        System.out.println("\n\n##### listener Cancelticket : " + payCancelled.toJson() + "\n\n");
        List<Issue> issueList = issueRepository.findByPaymentId(payCancelled.getId());

        if(issueList.size()>0) {
            for(Issue issue : issueList) {
                if(issue.getPaymentId().equals(payCancelled.getId())){
                    // issueRepository.deleteById(issue.getId());
                    issue.setIssuestatus("Cancled Issue");
                    issueRepository.save(issue);
                }
            }
        }
    }
        // // Sample Logic //
        // Issue issued = new Issue();
        // issued.setReservationid(payCancelled.getReservationid());
        // issued.setRegion(payCancelled.getRegion());
        // issued.setResort(payCancelled.getResort());
        // issued.setPersons(payCancelled.getPersons());
        // issued.setRooms(payCancelled.getRooms());
        // issued.setIssuestatus("Ticket Cancelled");
        // issueRepository.save(issued);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
