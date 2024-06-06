package stsgallery.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheEnding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static stsgallery.STSGalleryMod.makeID;

public class FrontloadMod extends GalleryModifier {
    public static String ID = makeID("FrontloadMod");

    @Override
    public AbstractCardModifier makeCopy() {
        return new FrontloadMod();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    public String modifyName(String cardName, AbstractCard card) {
        return CardCrawlGame.languagePack.getCardStrings(makeID("Frontload")).NAME + " " + cardName;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        ArrayList<TooltipInfo> tooltips = new ArrayList<>();
        tooltips.add(new TooltipInfo(CardCrawlGame.languagePack.getCardStrings(makeID("Frontload")).NAME, CardCrawlGame.languagePack.getCardStrings(makeID("Frontload")).DESCRIPTION));
        return tooltips;
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        if (Exordium.ID.equals(AbstractDungeon.id)) {
            if (card.cost > 0) card.updateCost(-1);
            card.isCostModified = true;
            card.exhaust = true;
        }
        return false;
    }
}