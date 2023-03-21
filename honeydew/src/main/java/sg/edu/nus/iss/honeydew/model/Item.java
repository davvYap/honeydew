package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
