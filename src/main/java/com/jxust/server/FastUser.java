package com.jxust.server;


import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class FastUser implements Serializable {

    private String id;
    private String name;
    private Date birthday;
    private Integer status;
    private String motto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        if(!(obj instanceof FastUser)) return false;

        FastUser user = (FastUser) obj;
        return Objects.equals(user.getId(),id)&&Objects.equals(user.getName(),name)
                &&Objects.equals(user.getBirthday(),birthday)&&Objects.equals(user.getMotto(),motto)
                &&Objects.equals(user.getStatus(),status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,name,birthday,status,motto);
    }
}
