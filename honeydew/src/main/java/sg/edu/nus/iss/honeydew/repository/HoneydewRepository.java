package sg.edu.nus.iss.honeydew.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.honeydew.model.DinnerMember;
import sg.edu.nus.iss.honeydew.model.Member;

@Repository
public class HoneydewRepository {
    @Autowired
    @Qualifier("honeydew")
    private RedisTemplate<String, String> redisTemplate;

    private String MEMBER_DATABASE = "member_database";

    private String DINNER_DATABASE = "dinner_database";

    public void saveMember(Member member) {
        redisTemplate.opsForHash().put(MEMBER_DATABASE, member.getId(), member.toJSONObject().toString());
    }

    public void saveDinnerDetails(DinnerMember dm) {
        redisTemplate.opsForHash().put(DINNER_DATABASE, dm.getMember().getId(), dm.toJSON());
    }
}
