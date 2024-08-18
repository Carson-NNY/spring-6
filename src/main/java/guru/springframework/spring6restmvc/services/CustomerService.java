package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Carson
 * @Version
 */
public interface CustomerService {

    Optional<CustomerDTO> getCustomerById(UUID id);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO saveNewCustomer(CustomerDTO customer);

    void patchCustomerById(UUID id, CustomerDTO customer);
}
