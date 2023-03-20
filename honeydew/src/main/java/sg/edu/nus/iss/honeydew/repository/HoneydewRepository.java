package sg.edu.nus.iss.honeydew.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HoneydewRepository {
    @Autowired
    private RedisTemplate redisTemplate;
}
