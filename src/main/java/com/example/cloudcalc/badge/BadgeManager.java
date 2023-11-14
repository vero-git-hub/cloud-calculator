package com.example.cloudcalc.badge;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.badge.BadgeCounts;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BadgeManager {

    private final DataExtractor dataExtractor;
    private final PrizeController prizeController;
    private final ProfileModel profileModel;
    private final FileOperationManager fileOperationManager;
    private final int COUNT_FOR_PDF_PRIZE = 7;
    int countIgnoreBadge;
    int countArcadeBadge;

    public BadgeManager(ServiceFacade serviceFacade) {
        this.dataExtractor = serviceFacade.getDataExtractor();
        this.prizeController = serviceFacade.getPrizeController();
        this.fileOperationManager = serviceFacade.getFileOperationManager();
        this.profileModel = serviceFacade.getProfileController().getProfileModel();
    }

    public PrizeController getPrizeManager() {
        return prizeController;
    }

    public BadgeCounts calculateBadgeCounts(Profile profile, ArrayList<String> receivedBadges) {
        BadgeCounts badgeCounts = new BadgeCounts();
        int total = receivedBadges.size();

        List<String> skillBadges = filterSkillBadges(receivedBadges, false);
        int skill = skillBadges.size();

        List<String> pdfBadges = getPDFBadges(profile, skillBadges);
        int totalPDF = pdfBadges.size();
        int prizePDF = calculatePDFPrizeBadgeCount(totalPDF);

        int prizeSkill = calculatePrizeNoPDF(skill, prizePDF);

        int prizeActivity = skill;

        List<String> typesList = dataExtractor.typeBadgeExtractedData;
        typesList = filterSkillBadges(typesList, true);

        int prizeTypeBadge = typesList.size();

        prizeController.determinePrizesForBadgeCount(prizePDF, prizeSkill, prizeActivity, prizeTypeBadge);

        countArcadeBadge = countArcadeBadge + (skill / 3);

        badgeCounts.setTotal(total);
        badgeCounts.setIgnore(countIgnoreBadge);
        badgeCounts.setArcade(countArcadeBadge);
        badgeCounts.setSkill(skill);
        badgeCounts.setPdf(totalPDF);
        badgeCounts.setPrizePDF(prizePDF);
        badgeCounts.setPrizeSkill(prizeSkill);
        badgeCounts.setPrizeActivity(prizeActivity);
        badgeCounts.setPrizePL(prizeTypeBadge);

        return badgeCounts;
    }

    /**
     * Delete ignore & arcade from received badges
     * @return received skills badges
     */
    public List<String> filterSkillBadges(List<String> receivedBadges, boolean isType) {
        List<String> ignoreBadges = fileOperationManager.loadBadgesFromFile(FileName.IGNORE_FILE);
        List<String> matchedIgnoreBadges = new ArrayList<>();
        List<String> arcadeBadges = fileOperationManager.loadBadgesFromFile(FileName.ARCADE_FILE);
        List<String> matchedArcadeBadges = new ArrayList<>();

        for (String badge : receivedBadges) {
            if (ignoreBadges.contains(badge)) {
                matchedIgnoreBadges.add(badge);
            }
        }

        receivedBadges.removeAll(matchedIgnoreBadges);

        for (String badge : receivedBadges) {
            if (arcadeBadges.contains(badge)) {
                matchedArcadeBadges.add(badge);
            }
        }

        receivedBadges.removeAll(matchedArcadeBadges);

        if(!isType) {
            countIgnoreBadge = matchedIgnoreBadges.size();
            countArcadeBadge = matchedArcadeBadges.size();
        }

        return receivedBadges;
    }

    /**
     * prizePDF maybe has only 2 values: 7 or less
     * @param skill skill badges without ignore
     * @param prizePDF pdf badges
     * @return badges without pdf
     */
    private int calculatePrizeNoPDF(int skill, int prizePDF) {
        int prizeNoPDF = 0;

        if(prizePDF == 7) {
            prizeNoPDF = skill  - prizePDF;
        } else if (prizePDF < 7) {
            prizeNoPDF = skill;
        }
        return prizeNoPDF;
    }

    private int calculatePDFPrizeBadgeCount(int totalPDF) {
        int prizePDF;

        if(totalPDF >= COUNT_FOR_PDF_PRIZE) {
            prizePDF = COUNT_FOR_PDF_PRIZE;
        } else {
            prizePDF = totalPDF;
        }

        return prizePDF;
    }

    /**
     * Matching pdf and filtered badges
     * @return PDF badges from received badges
     */
    private List<String> getPDFBadges(Profile profile, List<String> skillBadges) throws IllegalArgumentException {
        if (profile == null) {
            throw new IllegalArgumentException("Profile should not be null");
        }

        if (skillBadges == null) {
            throw new IllegalArgumentException("Skill badges list should not be null");
        }

        List<String> pdfBadgesFromProfile = profile.getPdfLinks();

        if (pdfBadgesFromProfile == null) {
            throw new IllegalArgumentException("Profile's PDF links should not be null");
        }

        Set<String> intersection = new HashSet<>(skillBadges);
        intersection.retainAll(pdfBadgesFromProfile);

        return new ArrayList<>(intersection);
    }

}
