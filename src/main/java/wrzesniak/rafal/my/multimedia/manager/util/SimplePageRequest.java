package wrzesniak.rafal.my.multimedia.manager.util;

import static org.springframework.data.domain.Sort.Direction;

public record SimplePageRequest(int page,
                                int pageSize,
                                String sortKey,
                                Direction direction) {}
