package bloom.mongobloom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

@Repository
class BloomRepositoryBean<T, ID> implements BloomRepository<T, ID> {

    private static final String ID_FIELD_NAME = "_id";
    private static final String MAYBE_EMIT_EVENT_METHOD_NAME = "maybeEmitEvent";
    private final MongoTemplate mongoTemplate;
    private final MappingMongoConverter mappingMongoConverter;

    @Autowired
    public BloomRepositoryBean(
            MongoTemplate mongoTemplate,
            MappingMongoConverter mappingMongoConverter) {
        this.mongoTemplate = mongoTemplate;
        this.mappingMongoConverter = mappingMongoConverter;
    }

    @Override
    public T upsert(T entity) {
        final Query query = new Query();
        query.addCriteria(Criteria.where(ID_FIELD_NAME).is(Extractor.getEntityId(entity)));
        Update update = new Update();

        Document document = mapDocument(entity);

        for (String key : document.keySet()) {
            update.set(key, document.get(key));
        }

        FindAndModifyOptions options = FindAndModifyOptions
                .options()
                .upsert(true)
                .remove(false)
                .returnNew(false);

        T oldEntity = (T) mongoTemplate.findAndModify(query, update, options, entity.getClass());

        emitAfterUpsertEvent(entity, document, oldEntity);

        return entity;
    }

    private @NonNull Document mapDocument(T entity) {
        Document document = (Document) mappingMongoConverter.convertToMongoType(entity);
        if (document == null) {
            throw new RuntimeException("Error mapping entity");
        }
        return document;
    }

    private void emitAfterUpsertEvent(T entity, Document document, T oldEntity) {
        Method maybeEmitEvent;
        try {
            maybeEmitEvent = mongoTemplate.getClass().getDeclaredMethod(MAYBE_EMIT_EVENT_METHOD_NAME);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        ReflectionUtils.makeAccessible(maybeEmitEvent);
        try {
            String collectionName
                    = entity.getClass().getSimpleName().substring(0, 1).toLowerCase()
                    + entity.getClass().getSimpleName().substring(1);
            if (entity.getClass().isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
                org.springframework.data.mongodb.core.mapping.Document documentAnnotation
                        = entity.getClass().getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
                collectionName = documentAnnotation.collection();
            }
            maybeEmitEvent.invoke(mongoTemplate, new AfterUpsertEvent<>(entity, oldEntity, document, collectionName));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
