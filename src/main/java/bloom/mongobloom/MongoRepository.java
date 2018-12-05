package bloom.mongobloom;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoRepository<T, ID>
        extends org.springframework.data.mongodb.repository.MongoRepository<T, ID>, CustomizedMongoRepository<T, ID> {

}
