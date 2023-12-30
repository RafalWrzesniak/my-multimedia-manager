package wrzesniak.rafal.my.multimedia.manager.domain.dynamodb;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DynamoDbClientGeneric<TYPE> {

    private final DynamoDbTable<TYPE> table;

    public DynamoDbClientGeneric(DynamoDbEnhancedClient enhancedClient, Class<TYPE> typeClass, String tableName) {
         table = enhancedClient.table(tableName, TableSchema.fromBean(typeClass));
    }

    public Optional<TYPE> getItemById(String partitionKey) {
        return Optional.ofNullable(table.getItem(Key.builder()
                .partitionValue(partitionKey)
                .build()));
    }

    public Optional<TYPE> getItemById(String partitionKey, String sortKey) {
        return Optional.ofNullable(table.getItem(Key.builder()
                .partitionValue(partitionKey)
                .sortValue(sortKey)
                .build()));
    }

    public List<TYPE> findObjectsByPartitionKey(String partitionKey) {
        return table.query(QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(partitionKey)
                    .build()))
                .items()
                .stream()
                .toList();
    }

    public void saveItem(TYPE item) {
        log.info("Saving item: {}", item);
        table.putItem(item);
    }

    public void updateItem(TYPE item) {
        log.info("Updating item: {}", item);
        table.updateItem(item);
    }

    public void removeItem(String partitionKey, String sortKey) {
        log.info("Removing item with id: {}: {}", partitionKey, sortKey);
        getItemById(partitionKey, sortKey).ifPresent(table::deleteItem);
    }

    public List<TYPE> scan() {
        return table.scan().items().stream().toList();
    }
}
