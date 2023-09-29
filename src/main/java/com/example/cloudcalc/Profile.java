package com.example.cloudcalc;

import java.util.List;

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
}
