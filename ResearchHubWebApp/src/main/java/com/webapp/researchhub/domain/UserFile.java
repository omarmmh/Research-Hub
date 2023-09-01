package com.webapp.researchhub.domain;


import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class UserFile {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    private String type;

    @CreationTimestamp
    private Date dateUploaded;
    @ManyToOne
    private MyUser user;

    private String title;

    @Value("${arrayOfStrings}")
    private List<String> authors = new ArrayList<>();

    private String description;

    @Lob
    @Column(name = "FileData", length = 10000000)
    private byte[] data;


    public UserFile(String originalFilename,String type, byte[] bytes,  MyUser user) {
        this.name = originalFilename;
        this.type = type;
        this.data = bytes;
        this.user = user;
    }

    public UserFile() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getDateUploaded() {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(dateUploaded);
        return date;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
