package rawinng.lab.batch.repo;

import org.springframework.data.repository.CrudRepository;

import rawinng.lab.batch.model.Hello;

public interface HelloRepository extends CrudRepository<Hello, Long> {
    
}
