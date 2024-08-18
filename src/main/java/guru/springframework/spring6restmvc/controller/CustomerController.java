package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Carson
 * @Version
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/customer")
@RestController  // serialize the body into JSON
public class CustomerController {

    private final CustomerService customerService;

    @RequestMapping(method= RequestMethod.GET)
    public List<CustomerDTO> listAllCustomers(){
        return customerService.getAllCustomers();
    }

    @RequestMapping(value="{customerId}", method = RequestMethod.GET)
    public Optional<CustomerDTO> getCustomerById(@PathVariable("customerId") UUID id){
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public ResponseEntity handlePost(@RequestBody CustomerDTO customer){
        CustomerDTO savedCustomer =  customerService.saveNewCustomer(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("location", "api/vi/customer"+savedCustomer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PatchMapping("{customerId}")
    public ResponseEntity updateCustomerPatchById(@PathVariable("customerId") UUID id, @RequestBody CustomerDTO customer){
        customerService.patchCustomerById(id,customer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
