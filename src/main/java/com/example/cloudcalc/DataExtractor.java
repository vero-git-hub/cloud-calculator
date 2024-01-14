package com.example.cloudcalc;

import com.example.cloudcalc.exception.ProfilePageStructureChangedException;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.entity.Profile;
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
    private String dataExtractorDescriptionError = "The structure of the profile page has changed or page is empty!";

    public DataExtractor() {
        LanguageManager.registerLocalizable(this);
    }

    public ArrayList<String> performScan(Profile profile) {
        typeBadgeExtractedData.clear();

        Map<String, String> extractedData = null;
        //try {

            //extractedData = scanProfileLink(profile);
            extractedData = new LinkedHashMap<>();
            extractTypeBadgesAfterDate(extractedData, "02.10.2023");

        //}
//        catch (ProfilePageStructureChangedException ex) {
//            Notification.showErrorMessage(dataExtractorError, dataExtractorTitleError);
//        }

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

    public Map<String, String> scanProfileLink(Profile profile, LocalDate profileDate) throws ProfilePageStructureChangedException {
        Map<String, String> resultMap = new LinkedHashMap<>();

        try {
            Document doc = Jsoup.connect(profile.getLink()).timeout(10 * 1000).get();

            Elements subheadElements = doc.select(".ql-title-medium.l-mts");
            Elements bodyElements = doc.select(".ql-body-medium.l-mbs");

            if (subheadElements.isEmpty() || bodyElements.isEmpty() || subheadElements.size() != bodyElements.size()) {
                throw new ProfilePageStructureChangedException(dataExtractorDescriptionError);
            }

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
