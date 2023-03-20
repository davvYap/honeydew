package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class DinnerMember implements Serializable {
    private Member member;
    private Dinner dinner;

    public DinnerMember() {
    }

    public DinnerMember(Member member, Dinner dinner) {
        this.member = member;
        this.dinner = dinner;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Dinner getDinner() {
        return dinner;
    }

    public void setDinner(Dinner dinner) {
        this.dinner = dinner;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("member", this.getMember().toJSON())
                .add("dinner", this.getDinner().toJSON())
                .build();
    }

}
