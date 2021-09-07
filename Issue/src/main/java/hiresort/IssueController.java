package hiresort;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class IssueController {
    //  @Autowired IssueRepository issueRepository;

    //  @RequestMapping(value = "/issues/cancelticket",
    //     method = RequestMethod.DELETE,
    //     produces = "application/json;charset=UTF-8")
    // public void cancelticket(HttpServletRequest request, HttpServletResponse response)
    //     throws Exception {
    //         String issue_Id = request.getParameter("id");
    //         java.util.Optional<Issue> optionalIssue = issueRepository.findById(Long.valueOf(issue_Id);
    //         Issue issue = optionalIssue.get();

    //         issueRepository.delete(issue);
        // }

 }