package bloom.mongobloom;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BloomTestBean {
    @Autowired
    public CustomizedMongoTestRepository bloomTestRepository;

    @Autowired
    public CustomizedMongoRepositoryImpl<Bloom, ObjectId> customizedMongoRepository;

    @PostConstruct
    public void bla() {
        Bloom bloom = Bloom.builder().id(new ObjectId()).name("Margarida").build();
        bloom = customizedMongoRepository.save(bloom);
        bloom = customizedMongoRepository.save(bloom.withName("Margô"));
        bloom.withName("jersey");
    }
}
