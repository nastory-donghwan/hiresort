package hiresort;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewRepository extends CrudRepository<View, Long> {

// 두줄 중복이라 오류
//    List<View> findByReservationid(Long reservationid);
    List<View> findByReservationid(Long reservationid);

}