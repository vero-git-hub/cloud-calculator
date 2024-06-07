package com.example.cloudcalc;

import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.exception.ProfilePageStructureChangedException;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.util.Notification;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DataExtractor implements Localizable {

    private final DateUtils dateUtils = new DateUtils();
    private String dataExtractorError = "Error";
    private String dataExtractorTitleError = "The given HTML structure was not found on the page.";
    private String dataExtractorDescriptionError = "The structure of the profile page has changed or page is empty!";

    public DataExtractor() {
        LanguageManager.registerLocalizable(this);
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
            throw new ProfilePageStructureChangedException("Error scanning profile.");
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