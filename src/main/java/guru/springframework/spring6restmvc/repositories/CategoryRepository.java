package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
}
