package wrzesniak.rafal.my.multimedia.manager.domain.user;

import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;

import java.util.List;

public record SyncInfoWrapper(SyncInfo syncInfo,
                              List<ListDto> currentLists) {}