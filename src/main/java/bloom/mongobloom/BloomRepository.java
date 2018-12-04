package bloom.mongobloom;

interface BloomRepository<T, ID> {
    T upsert(T entity);
}
