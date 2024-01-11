package com.example.cloudcalc.entity;

import java.util.List;
import java.util.Objects;

public class Profile {
    private int id;
    private String name;
    private String link;
    private String lastScannedDate;
    private List<ProgramPrize> programPrizes;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLastScannedDate() {
        return lastScannedDate;
    }

    public void setLastScannedDate(String lastScannedDate) {
        this.lastScannedDate = lastScannedDate;
    }

    public List<ProgramPrize> getProgramPrizes() {
        return programPrizes;
    }

    public void setProgramPrizes(List<ProgramPrize> programPrizes) {
        this.programPrizes = programPrizes;
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
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "link='" + link + '\'' +
                ", lastScannedDate='" + lastScannedDate + '\'' +
                ", programPrizes=" + programPrizes +
                '}';
    }
}