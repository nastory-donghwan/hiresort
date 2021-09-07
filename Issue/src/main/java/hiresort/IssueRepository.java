package hiresort;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="issues", path="issues")
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long>{
    
    List<Issue> findByPaymentId(Long PaymentId);

}
