package com.example.cloudcalc.profile;

import java.util.List;
import java.util.Objects;

public class Profile {
    private String name;
    private String startDate;
    private String profileLink;
    private String pdfFilePath;
    private List<String> pdfLinks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public List<String> getPdfLinks() {
        return pdfLinks;
    }

    public void setPdfLinks(List<String> pdfLinks) {
        this.pdfLinks = pdfLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(name, profile.name) &&
                Objects.equals(startDate, profile.startDate) &&
                Objects.equals(profileLink, profile.profileLink) &&
                Objects.equals(pdfFilePath, profile.pdfFilePath) &&
                Objects.equals(pdfLinks, profile.pdfLinks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate, profileLink, pdfFilePath, pdfLinks);
    }

}
