package hiresort;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="payments", path="payments")
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long>{

    List<Payment> findByreservationid(Long reservationid);
    void deleteByreservationid(Long reservationid);

}
