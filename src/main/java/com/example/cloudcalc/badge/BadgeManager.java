package com.example.cloudcalc.badge;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;

import java.util.*;

public class BadgeManager {

    private final IgnoredBadgeManager ignoredBadgeManager;
    private final DataExtractor dataExtractor;
    private final PrizeManager prizeManager;
    private final int COUNT_FOR_PDF_PRIZE = 7;

    public BadgeManager(DataExtractor dataExtractor, PrizeManager prizeManager, UICallbacks uiCallbacks) {
        this.dataExtractor = dataExtractor;
        this.prizeManager = prizeManager;
        this.ignoredBadgeManager = new IgnoredBadgeManager(uiCallbacks);
    }

    public PrizeManager getPrizeManager() {
        return prizeManager;
    }

    /**
     * Delete ignore from received badges
     * @return received badges without ignore
     */
    public List<String> filterSkillBadges(List<String> receivedBadges) {
        List<String> ignoreBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
        if (!ignoreBadges.isEmpty()) {
            receivedBadges.removeAll(ignoreBadges);
        }
        return receivedBadges;
    }

    public BadgeCounts calculateBadgeCounts(Profile profile, ArrayList<String> receivedBadges) {
        BadgeCounts badgeCounts = new BadgeCounts();
        int total = receivedBadges.size();

        List<String> skillBadges = filterSkillBadges(receivedBadges);
        int skill = skillBadges.size();
        int ignore = calculateIgnoreBadges(total, skill);

        List<String> pdfBadges = getPDFBadges(profile, skillBadges);
        int totalPDF = pdfBadges.size();
        int prizePDF = calculatePDFPrizeBadgeCount(totalPDF);

        int prizeSkill = calculatePrizeNoPDF(skill, prizePDF);

        int prizeActivity = skill;

        List<String> typesList = dataExtractor.typeBadgeExtractedData;
        typesList = filterSkillBadges(typesList);

        int prizeTypeBadge = typesList.size();

        prizeManager.determinePrizesForBadgeCount(prizePDF, prizeSkill, prizeActivity, prizeTypeBadge);

        badgeCounts.setTotal(total);
        badgeCounts.setIgnore(ignore);
        badgeCounts.setSkill(skill);
        badgeCounts.setTotalPDF(totalPDF);
        badgeCounts.setPrizePDF(prizePDF);
        badgeCounts.setPrizeSkill(prizeSkill);
        badgeCounts.setPrizeActivity(prizeActivity);
        badgeCounts.setPrizePL(prizeTypeBadge);

        return badgeCounts;
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

    private int calculateIgnoreBadges(int total, int skill) {
        return total - skill;
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
