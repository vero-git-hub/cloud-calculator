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

    public Map<String, String> calculateBadgeCounts(Profile profile, ArrayList<String> receivedBadges) {
        Map<String, String> badgeCounts = new LinkedHashMap<>();
        int total = receivedBadges.size();

        List<String> skillBadges = filterSkillBadges(receivedBadges);
        int skill = skillBadges.size();
        int ignore = calculateIgnoreBadges(total, skill);

        List<String> pdfBadges = getPDFBadges(profile, skillBadges);
        int totalPDF = pdfBadges.size();
        int prizePDF = calculatePDFPrizeBadgeCount(totalPDF);
        int prizeSkill = calculateSkillPrizeBadgeCount(skill, prizePDF);

        int prizeActivity = calculateActivityBadgeCount(skill);

        List<String> typesList = dataExtractor.typeBadgeExtractedData;
        typesList = filterSkillBadges(typesList);

        int prizeTypeBadge = typesList.size();

        prizeManager.determinePrizesForBadgeCount(prizePDF, prizeSkill, prizeActivity, prizeTypeBadge);

        badgeCounts.put(Constants.TOTAL, String.valueOf(total));
        badgeCounts.put(Constants.IGNORE, String.valueOf(ignore));
        badgeCounts.put(Constants.SKILL, String.valueOf(skill));
        badgeCounts.put(Constants.PDF_TOTAL, String.valueOf(totalPDF));

        badgeCounts.put(Constants.PDF_FOR_PRIZE, String.valueOf(prizePDF));
        badgeCounts.put(Constants.SKILL_FOR_PRIZE, String.valueOf(prizeSkill));
        badgeCounts.put(Constants.SKILL_FOR_ACTIVITY, String.valueOf(prizeActivity));
        badgeCounts.put(Constants.SKILL_FOR_PL, String.valueOf(prizeTypeBadge));

        return badgeCounts;
    }

    private int calculateActivityBadgeCount(int skill) {
        int prizeActivity;

        if(skill >= 30) {
            prizeActivity = skill;
        } else {
            prizeActivity = 0;
        }

        return prizeActivity;
    }

    private int calculateSkillPrizeBadgeCount(int skill, int prizePDF) {
        int prizeSkill;

        if(prizePDF == 0) {
            prizeSkill = skill;
        } else {
            prizeSkill = skill - prizePDF;
        }

        return prizeSkill;
    }

    private int calculatePDFPrizeBadgeCount(int totalPDF) {
        int prizePDF;
        int constantForPrize = 7;

        if(totalPDF > constantForPrize) {
            prizePDF = constantForPrize;
        } else if (totalPDF < constantForPrize) {
            prizePDF = 0;
        } else {
            prizePDF = constantForPrize;
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
