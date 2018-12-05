package bloom.mongobloom;

interface CustomizedMongoRepository<T, ID> {
    <S extends T> S save(S entity);
}
