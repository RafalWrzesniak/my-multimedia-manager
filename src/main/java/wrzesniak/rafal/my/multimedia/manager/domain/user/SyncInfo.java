package wrzesniak.rafal.my.multimedia.manager.domain.user;

import java.time.LocalDateTime;
import java.util.List;

public record SyncInfo(LocalDateTime syncTimestamp,
                       List<String> changedListIds) {
}
