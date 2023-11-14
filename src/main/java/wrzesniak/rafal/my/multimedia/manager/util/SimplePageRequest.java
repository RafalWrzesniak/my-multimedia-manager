package wrzesniak.rafal.my.multimedia.manager.util;

public record SimplePageRequest(int page,
                                int pageSize,
                                String sortKey,
                                String direction) {}
