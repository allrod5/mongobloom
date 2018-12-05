package bloom.mongobloom;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.experimental.Wither;

@Wither
@Builder
@Document(collection = "bloom")
public class Bloom implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id private final ObjectId id;
    private final String name;
}
