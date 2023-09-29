package com.example.cloudcalc;

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
import java.time.LocalDate;
import java.util.*;

public class DataExtractor {

    private final DateUtils dateUtils = new DateUtils();

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
            try {
                Document doc = Jsoup.connect(link).timeout(10 * 1000).get();
                Elements h1Elements = doc.select("h1[class=\"ql-headline-1\"]");
                for (int i = 0; i < h1Elements.size(); i++) {
                    String str = h1Elements.get(i).text();
                    h1Contents.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return h1Contents;
    }

    public ArrayList<String> performScan(Profile profile) {
        Map<String, String> extractedData = scanProfileLink(profile);
        return new ArrayList<>(extractedData.keySet());
    }

    public Map<String, String> scanProfileLink(Profile profile) {
        Map<String, String> resultMap = new LinkedHashMap<>();

        try {
            Document doc = Jsoup.connect(profile.getProfileLink()).timeout(10 * 1000).get();

            Elements subheadElements = doc.select(".ql-subhead-1.l-mts");
            Elements bodyElements = doc.select(".ql-body-2.l-mbs");

            LocalDate profileDate = dateUtils.convertProfileStartDate(profile.getStartDate());

            if(subheadElements.size() == bodyElements.size()) {
                for(int i = 0; i < subheadElements.size(); i++) {
                    String key = subheadElements.get(i).text();
                    String valueStr = bodyElements.get(i).text();

                    LocalDate valueDate = dateUtils.extractDateFromValue(valueStr);

                    if(!valueDate.isBefore(profileDate)) {
                        resultMap.put(key, valueStr);
                    }
                }
            } else {
                System.out.println("Number of subhead and body elements do not match!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
