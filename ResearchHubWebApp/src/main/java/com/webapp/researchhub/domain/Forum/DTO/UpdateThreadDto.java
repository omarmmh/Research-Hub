package com.webapp.researchhub.domain.Forum.DTO;

public class UpdateThreadDto {
    private String title;
    private String body;

    public UpdateThreadDto(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "UpdateThreadDto{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
