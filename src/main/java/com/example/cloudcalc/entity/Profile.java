package com.example.cloudcalc.entity;

import java.util.List;
import java.util.Objects;

public class Profile {
    private String name;
    private String link;
    private List<String> prizes;
    private String lastScannedDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<String> prizes) {
        this.prizes = prizes;
    }

    public String getLastScannedDate() {
        return lastScannedDate;
    }

    public void setLastScannedDate(String lastScannedDate) {
        this.lastScannedDate = lastScannedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(name, profile.name) &&
                Objects.equals(link, profile.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", prizes=" + prizes +
                '}';
    }
}
