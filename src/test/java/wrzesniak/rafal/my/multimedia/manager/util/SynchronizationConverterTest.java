package wrzesniak.rafal.my.multimedia.manager.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.user.SyncInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SynchronizationConverterTest {

    private final SynchronizationConverter synchronizationConverter = new SynchronizationConverter();
    LocalDateTime SYNC_DATE_TIME = LocalDateTime.of(2024, 7, 5, 16, 0);

    @Test
    void shouldConvertFromListToAttributeValue() {
        // given
        SyncInfo syncInfo1 = new SyncInfo(SYNC_DATE_TIME, List.of("id1", "id2"));
        SyncInfo syncInfo2 = new SyncInfo(SYNC_DATE_TIME.plusDays(1), List.of("id2", "id3"));
        AttributeValue expected = AttributeValue.builder()
                .l(
                        AttributeValue.builder()
                                .m(Map.of(SYNC_DATE_TIME.toString(), AttributeValue.fromSs(List.of("id1", "id2"))))
                                .build(),
                        AttributeValue.builder()
                                .m(Map.of(SYNC_DATE_TIME.plusDays(1).toString(), AttributeValue.fromSs(List.of("id2", "id3"))))
                                .build()
                        )
                .build();

        // when
        AttributeValue attributeValue = synchronizationConverter.transformFrom(List.of(syncInfo1, syncInfo2));

        // then
        System.out.println(expected);
        assertEquals(expected, attributeValue);
    }

    @Test
    void shouldConvertFromAttributeValueToList() {
        // given
        SyncInfo syncInfo1 = new SyncInfo(SYNC_DATE_TIME, List.of("id1", "id2"));
        SyncInfo syncInfo2 = new SyncInfo(SYNC_DATE_TIME.plusDays(1), List.of("id2", "id3"));
        AttributeValue given = AttributeValue.builder()
                .l(
                        AttributeValue.builder()
                                .m(Map.of(SYNC_DATE_TIME.toString(), AttributeValue.fromSs(List.of("id1", "id2"))))
                                .build(),
                        AttributeValue.builder()
                                .m(Map.of(SYNC_DATE_TIME.plusDays(1).toString(), AttributeValue.fromSs(List.of("id2", "id3"))))
                                .build()
                )
                .build();

        // when
        List<SyncInfo> syncInfos = synchronizationConverter.transformTo(given);

        // then
        assertThat("List equality without order", syncInfos, containsInAnyOrder(List.of(syncInfo1, syncInfo2).toArray()));
    }
}