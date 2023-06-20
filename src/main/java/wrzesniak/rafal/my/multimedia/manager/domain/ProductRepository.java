package wrzesniak.rafal.my.multimedia.manager.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ProductRepository<PRODUCT> {

    Optional<PRODUCT> findById(long id);

    List<PRODUCT> findProductsInContentList(long contentListId, Pageable pageRequest);

    void deleteById(long id);

    List<PRODUCT> findAll(Specification<PRODUCT> specification);

}
