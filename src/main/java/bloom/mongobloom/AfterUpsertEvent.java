package bloom.mongobloom;

import java.util.Objects;
import java.util.function.Function;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

import lombok.Getter;

public class AfterUpsertEvent<E> extends MongoMappingEvent<E> {

    @Getter private final E oldEntity;
    @Getter private final E newEntity;

    /**
     * Creates new {@link AfterUpsertEvent}.
     *
     * @param source         must not be {@literal null}.
     * @param document       can be {@literal null}.
     * @param collectionName can be {@literal null}.
     */
    public AfterUpsertEvent(E source, E oldEntity, Document document, String collectionName) {
        super(source, document, collectionName);
        this.oldEntity = oldEntity;
        this.newEntity = source;
    }

    public boolean hasDiff(Function<E, ?> function) {
        return Objects.equals(function.apply(oldEntity), function.apply(newEntity));
    }
}
