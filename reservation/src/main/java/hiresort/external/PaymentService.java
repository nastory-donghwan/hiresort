package hiresort.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

// @FeignClient(name="payment", url="http://localhost:8082")
@FeignClient(name="payment", url="${api.url.payment}")
public interface PaymentService {
    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void paid(@RequestBody Payment payment);
    // public void Pay(@PathVariable Long id);
    // public void payCancel(Long paymentid);

    // public void Pay(Payment payment);

}

