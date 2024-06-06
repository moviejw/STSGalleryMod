package stsgallery.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;
import java.util.List;

import static stsgallery.STSGalleryMod.makeID;

public class GoodForOtherCharMod extends GalleryModifier {
    public static String ID = makeID("GoodForOtherCharMod");

    @Override
    public AbstractCardModifier makeCopy() {
        return new GoodForOtherCharMod();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    public String modifyName(String cardName, AbstractCard card) {
        return CardCrawlGame.languagePack.getCardStrings(makeID("GoodForOtherChar")).NAME + " " + cardName;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        ArrayList<TooltipInfo> tooltips = new ArrayList<>();
        tooltips.add(new TooltipInfo(CardCrawlGame.languagePack.getCardStrings(makeID("GoodForOtherChar")).NAME, CardCrawlGame.languagePack.getCardStrings(makeID("GoodForOtherChar")).DESCRIPTION));
        return tooltips;
    }
}