package com.webapp.researchhub.domain;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProfilePic {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String type;
    @OneToOne
    private MyUser user;
    @Lob
    @Column(name = "FileData", length = 10000000)
    private byte[] data;


    public ProfilePic(String contentType, byte[] bytes, MyUser user) {}
    public ProfilePic(String originalFilename,String type, byte[] bytes, MyUser user) {
        this.name = originalFilename;
        this.type = type;
        this.data = bytes;
        this.user = user;
    }

    public ProfilePic() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public MyUser getUser() {return user;}
    public void setUser(MyUser user) {this.user = user;}

    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
}