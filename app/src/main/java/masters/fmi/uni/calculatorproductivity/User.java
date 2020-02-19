package masters.fmi.uni.calculatorproductivity;

import java.io.Serializable;

public class User implements Serializable {
    int id;
    String name;
    String lastname;
    String emaill;
    int role_id;
    int supervisor_id;
    boolean activated;
    String password;

    public User (String name, String lastname, String email, String password)
    {
        this.name=name;
        this.lastname=lastname;
        this.emaill=email;
        this.password=password;
    }
    public User (String name, String lastname)
    {
        this.name=name;
        this.lastname=lastname;
    }

    public User(int id, String name, String lastname)
    {
        this.id=id;
        this.name=name;
        this.lastname=lastname;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmaill() {
        return emaill;
    }

    public void setEmaill(String emaill) {
        this.emaill = emaill;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getSupervisor_id() {
        return supervisor_id;
    }

    public void setSupervisor_id(int supervisor_id) {
        this.supervisor_id = supervisor_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
