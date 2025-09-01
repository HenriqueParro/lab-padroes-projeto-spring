package one.digitalinnovation.gof.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
