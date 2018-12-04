package bloom.mongobloom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

class Extractor {
    static @NonNull Field getIdField(Object entity) {
        List<Field> idFields = new ArrayList<>();

        ReflectionUtils.FieldCallback findIdField = field -> {
            if (field.isAnnotationPresent(Id.class)) {
                idFields.add(field);
            }
        };

        ReflectionUtils.doWithFields(entity.getClass(), findIdField);
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("Entity must have an @Id-annotated field");
        }

        return idFields.get(0);
    }

    static Object getFieldValue(Field field, Object entity) {
        ReflectionUtils.makeAccessible(field);
        Object idFieldValue;
        try {
            idFieldValue = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return idFieldValue;
    }

    static Object getEntityId(Object entity) {
        Field idField = getIdField(entity);
        return getFieldValue(idField, entity);
    }
}
