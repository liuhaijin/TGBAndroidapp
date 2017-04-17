package com.tgb.model;

import java.util.Date;

public class Notice {
    private Integer idNotice;

    private Integer idUser;

    private String content;

    private Date releaseTime;

    private String publisher;

    public Integer getIdNotice() {
        return idNotice;
    }

    public void setIdNotice(Integer idNotice) {
        this.idNotice = idNotice;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher == null ? null : publisher.trim();
    }

    @Override
    public String toString() {
        return "Notice{" +
                "idNotice=" + idNotice +
                ", idUser=" + idUser +
                ", content='" + content + '\'' +
                ", releaseTime=" + releaseTime +
                ", publisher='" + publisher + '\'' +
                '}';
    }
}