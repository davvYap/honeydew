package sg.edu.nus.iss.honeydew.repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        // redisTemplate.opsForValue().set(member.getName(),
        // member.toJSON().toString());
        redisTemplate.opsForHash().put(MEMBER_DATABASE, member.getId(),
                member.toJSON().toString());
    }

    public void saveDinnerDetails(DinnerMember dm) {
        redisTemplate.opsForHash().put(DINNER_DATABASE, dm.getMember().getId(), dm.toJSON().toString());
    }

    public List<Member> getAllMembers() throws IOException {
        List<Object> hashMapList = redisTemplate.opsForHash().values(MEMBER_DATABASE);
        List<Member> members = new LinkedList<>();
        for (Object object : hashMapList) {
            // IMPORTANT
            String jsonMember = (String) object;
            Member newMember = Member.createFromJSON(jsonMember);
            members.add(newMember);
        }
        return members;
    }

    public Member getMemberById(String id) {
        return (Member) redisTemplate.opsForHash().get(MEMBER_DATABASE, id);
    }

}