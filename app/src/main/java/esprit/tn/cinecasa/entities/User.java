package esprit.tn.cinecasa.entities;

import java.util.Date;

/**
 * Created by Yessine on 11/14/2017.
 */

public class User {

    private int id;
    private String uid;
    private String name;
    private String email;
    private String password;
    private String created_at;
    private Date updated_at;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public User(String name, String email, String password, String created_at, Date updated_at) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public User(int id, String name, String email, String password, String created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public User(String uid, String name, String email, String password, String created_at) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.email = password;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
