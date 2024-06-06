package stsgallery.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConfusionPower;

import java.util.ArrayList;
import java.util.List;

import static stsgallery.STSGalleryMod.makeID;

public class TrashWithoutSneckoMod extends GalleryModifier {
    public static String ID = makeID("TrashWithoutSneckoMod");

    @Override
    public AbstractCardModifier makeCopy() {
        return new TrashWithoutSneckoMod();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    public String modifyName(String cardName, AbstractCard card) {
        return CardCrawlGame.languagePack.getCardStrings(makeID("TrashWithoutSnecko")).NAME + " " + cardName;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        ArrayList<TooltipInfo> tooltips = new ArrayList<>();
        tooltips.add(new TooltipInfo(CardCrawlGame.languagePack.getCardStrings(makeID("TrashWithoutSnecko")).NAME, CardCrawlGame.languagePack.getCardStrings(makeID("TrashWithoutSnecko")).DESCRIPTION));
        return tooltips;
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractPlayer p = AbstractDungeon.player;
        addToBot(new ApplyPowerAction(p, p, new ConfusionPower(p)));
        addToBot(new DrawCardAction(1));
    }
}