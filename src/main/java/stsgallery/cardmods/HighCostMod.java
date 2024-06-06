package stsgallery.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;
import java.util.List;

import static stsgallery.STSGalleryMod.makeID;

public class HighCostMod extends GalleryModifier {
    public static String ID = makeID("HighCostMod");
    public static boolean energyGained = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new HighCostMod();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    public String modifyName(String cardName, AbstractCard card) {
        return CardCrawlGame.languagePack.getCardStrings(makeID("HighCost")).NAME + " " + cardName;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        ArrayList<TooltipInfo> tooltips = new ArrayList<>();
        tooltips.add(new TooltipInfo(CardCrawlGame.languagePack.getCardStrings(makeID("HighCost")).NAME, CardCrawlGame.languagePack.getCardStrings(makeID("HighCost")).DESCRIPTION));
        return tooltips;
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!energyGained) {
            addToBot(new GainEnergyAction(1));
            energyGained = true;
        }
    }

    public boolean onBattleStart(AbstractCard card) {
        energyGained = false;
        return false;
    }
}