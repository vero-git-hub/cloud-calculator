package com.example.cloudcalc;

import com.example.cloudcalc.exception.ProfilePageStructureChangedException;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.util.Notification;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DataExtractor implements Localizable {

    private final DateUtils dateUtils = new DateUtils();
    public List<String> typeBadgeExtractedData = new ArrayList<>();
    private String dataExtractorError = "Error";
    private String dataExtractorTitleError = "The given HTML structure was not found on the page.";
    private String dataExtractorDescriptionError = "The structure of the profile page has changed!";

    public DataExtractor() {
        LanguageManager.registerLocalizable(this);
    }

    public List<String> extractHiddenLinksFromPdf(String pdfFilePath) {
        List<String> links = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            for (PDPage page : document.getPages()) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annotation;
                        if (link.getAction() instanceof PDActionURI) {
                            PDActionURI uri = (PDActionURI) link.getAction();
                            String linkUrl = uri.getURI();
                            if (linkUrl.startsWith("https://www.cloudskillsboost.google/")) {
                                links.add(linkUrl);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return links;
    }

    public List<String> extractH1FromLinks(List<String> extractedLinks) {
        List<String> h1Contents = new ArrayList<>();

        for (String link : extractedLinks) {
            if (isURLAccessible(link)) {
                try {
                    Document doc = Jsoup.connect(link).timeout(30 * 1000).get();
                    Elements h1Elements = doc.select("h1[class=\"ql-headline-1\"]");
                    for (int i = 0; i < h1Elements.size(); i++) {
                        String str = h1Elements.get(i).text();
                        h1Contents.add(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return h1Contents;
    }

    public boolean isURLAccessible(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    public ArrayList<String> performScan(Profile profile) {
        typeBadgeExtractedData.clear();

        Map<String, String> extractedData = null;
        try {
            extractedData = scanProfileLink(profile);
            extractTypeBadgesAfterDate(extractedData, "02.10.2023");

        } catch (ProfilePageStructureChangedException ex) {
            Notification.showErrorMessage(dataExtractorError, dataExtractorTitleError);
        }

        return new ArrayList<>(extractedData.keySet());
    }

    private void extractTypeBadgesAfterDate(Map<String, String> extractedData, String dateFromTypeBadge) {
        LocalDate typeBadgeDate = dateUtils.convertProfileOrTypeBadgeStartDate(dateFromTypeBadge);

        for (Map.Entry entry: extractedData.entrySet()) {
            LocalDate valueDate = dateUtils.extractDateFromValue((String) entry.getValue());

            if(!valueDate.isBefore(typeBadgeDate)) {
                String labName = (String) entry.getKey();
                typeBadgeExtractedData.add(labName);
            }
        }
    }

    public Map<String, String> scanProfileLink(Profile profile) throws ProfilePageStructureChangedException {
        Map<String, String> resultMap = new LinkedHashMap<>();

        try {
            Document doc = Jsoup.connect(profile.getProfileLink()).timeout(10 * 1000).get();

            Elements subheadElements = doc.select(".ql-title-medium.l-mts");
            Elements bodyElements = doc.select(".ql-body-medium.l-mbs");

            if (subheadElements.isEmpty() || bodyElements.isEmpty() || subheadElements.size() != bodyElements.size()) {
                throw new ProfilePageStructureChangedException(dataExtractorDescriptionError);
            }

            LocalDate profileDate = dateUtils.convertProfileOrTypeBadgeStartDate(profile.getStartDate());

            for(int i = 0; i < subheadElements.size(); i++) {
                String key = subheadElements.get(i).text();
                String valueStr = bodyElements.get(i).text();

                LocalDate valueDate = dateUtils.extractDateFromValue(valueStr);

                if(!valueDate.isBefore(profileDate)) {
                    resultMap.put(key, valueStr);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        dataExtractorError = bundle.getString("dataExtractorError");
        dataExtractorTitleError = bundle.getString("dataExtractorTitleError");
        dataExtractorDescriptionError = bundle.getString("dataExtractorDescriptionError");
    }
}
