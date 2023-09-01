package com.webapp.researchhub.domain.Forum.DTO;

public class ThreadCommentDto {

    Long threadId;
    String body;
    String usersName;

    public ThreadCommentDto(){}

    public ThreadCommentDto(Long threadId, String body, String usersName) {
        this.threadId = threadId;
        this.body = body;
        this.usersName = usersName;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    @Override
    public String toString() {
        return "ThreadCommentDto{" +
                "threadId=" + threadId +
                ", body='" + body + '\'' +
                ", usersName='" + usersName + '\'' +
                '}';
    }
}
