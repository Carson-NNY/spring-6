package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Carson
 * @Version
 */

//JpaRepository 比 CrudRepository 多一些additional methods. eg: flush()
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
