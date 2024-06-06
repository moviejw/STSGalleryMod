package stsgallery.events;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stsgallery.cardmods.*;
import stsgallery.cards.*;

import java.util.*;

import static stsgallery.STSGalleryMod.makeID;
import static stsgallery.STSGalleryMod.modID;

public class GalleryEvent extends AbstractImageEvent {
    public static final String ID = "GalleryEvent";

    private static final EventStrings general = CardCrawlGame.languagePack.getEventString(makeID("General"));
    private static final String NAME = general.NAME;
    private static final String[] GENERAL_DESCRIPTIONS = general.DESCRIPTIONS;
    private static final String[] GENERAL_OPTIONS = general.OPTIONS;

    private static final EventStrings normal = CardCrawlGame.languagePack.getEventString(makeID("Normal"));
    private static final String[] NORMAL_DESCRIPTIONS = normal.DESCRIPTIONS;
    private static final String[] NORMAL_OPTIONS = normal.OPTIONS;

    private static final EventStrings discussion = CardCrawlGame.languagePack.getEventString(makeID("Discussion"));
    private static final String[] DISCUSSION_DESCRIPTIONS = discussion.DESCRIPTIONS;
    private static final String[] DISCUSSION_OPTIONS = discussion.OPTIONS;

    private CurScreen screen;
    private int firstArticle;
    private int secondArticle;
    private int maxArticle = 7;
    private AbstractCard discussionCard;
    private String discussionName;

    private boolean firstPressed;
    private boolean secondPressed;
    private boolean discussionPressed;
    private boolean pickCard = false;


    public static final Logger logger = LogManager.getLogger(modID);

    public GalleryEvent() {

        super(NAME, GENERAL_DESCRIPTIONS[0], "stsgallery/images/events/gallery.jpg");

        discussionPressed = false;
        discussionCard = getRandomNonBasicCard();
        discussionName = DISCUSSION_OPTIONS[0] + AbstractDungeon.player.getCharacterString().NAMES[0] + " - " + discussionCard.name;
        this.screen = CurScreen.MAINPAGE;
        firstArticle = AbstractDungeon.eventRng.random(0, maxArticle - 1);
        secondArticle = firstArticle;
        while (firstArticle == secondArticle) {
            secondArticle = AbstractDungeon.eventRng.random(0, maxArticle - 1);
        }
        firstPressed = false;
        secondPressed = false;


        this.imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
        this.imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
        if (!discussionPressed) this.imageEventText.setDialogOption(discussionName);
        this.imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
    }

    public void update() {
        super.update();
        boolean up = false;
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c1 = discussionCard.makeStatEquivalentCopy();
            AbstractCard c2 = discussionCard.makeStatEquivalentCopy();
            AbstractDungeon.player.masterDeck.removeCard(discussionCard);
            if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("NeedUpgrade"))) {
                c1.upgrade();
            }
            if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("NoNeedUpgrade"))) {
                c1 = CardLibrary.getCard(c1.cardID);
            }
            CardModifierManager.addModifier(c1, cardToMod(((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0))));
            CardModifierManager.addModifier(c2, cardToMod(((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0))));
            if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("WorseThanCurse"))) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new AscendersBane(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
            else if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("Bad"))) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c2, (float)Settings.WIDTH * 0.6F, (float)Settings.HEIGHT / 2.0F));
            }
            else if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("GoodForOtherChar"))) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new PrismaticShard());
            }
            else if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("NoNeedUpgrade"))) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float)Settings.WIDTH / 0.2F, (float)Settings.HEIGHT / 2.0F));
                ArrayList<AbstractCard> upgradableCards = new ArrayList();
                AbstractCard c;
                for (AbstractCard abstractCard : AbstractDungeon.player.masterDeck.group) {
                    c = abstractCard;
                    if (c.canUpgrade()) {
                        upgradableCards.add(c);
                    }
                }
                Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
                if (!upgradableCards.isEmpty()) {
                    c = (AbstractCard)upgradableCards.get(0);
                    c.upgrade();
                    AbstractDungeon.player.bottledCardUpgradeCheck(c);
                    AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                }
            }
            else if (Objects.equals(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID, makeID("NeedUpgrade"))) {
                ArrayList<AbstractCard> upgradedCards = new ArrayList();
                AbstractCard c;
                for (AbstractCard abstractCard : AbstractDungeon.player.masterDeck.group) {
                    c = abstractCard;
                    if (!c.canUpgrade()) {
                        upgradedCards.add(c);
                    }
                }
                Collections.shuffle(upgradedCards, new Random(AbstractDungeon.miscRng.randomLong()));
                if (!upgradedCards.isEmpty()) {
                    c = (AbstractCard)upgradedCards.get(0);
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(CardLibrary.getCard(c.cardID), (float)Settings.WIDTH * 0.6F, (float)Settings.HEIGHT / 2.0F));
                }
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
            }
            else {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case MAINPAGE:
                switch (buttonPressed) {
                    case 0:
                        imageEventText.loadImage("stsgallery/images/events/" + firstArticle +".jpg");
                        imageEventText.updateBodyText(NORMAL_DESCRIPTIONS[firstArticle]);
                        imageEventText.clearAllDialogs();
                        if (!firstPressed) {
                            imageEventText.setDialogOption(GENERAL_OPTIONS[1]);
                            imageEventText.setDialogOption(GENERAL_OPTIONS[2]);
                        }
                        imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                        screen = CurScreen.ARTICLE_FIRST;
                        break;
                    case 1:
                        imageEventText.loadImage("stsgallery/images/events/" + secondArticle +".jpg");
                        imageEventText.updateBodyText(NORMAL_DESCRIPTIONS[secondArticle]);
                        imageEventText.clearAllDialogs();
                        if (!secondPressed) {
                            imageEventText.setDialogOption(GENERAL_OPTIONS[1]);
                            imageEventText.setDialogOption(GENERAL_OPTIONS[2]);
                        }
                        imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                        screen = CurScreen.ARTICLE_SECOND;
                        break;
                    case 2:
                        if (!discussionPressed) {
                            discussionPressed = true;
                            imageEventText.loadImage("images/1024Portraits/" + discussionCard.assetUrl + ".png");
                            imageEventText.updateBodyText(DISCUSSION_DESCRIPTIONS[0]);
                            imageEventText.clearAllDialogs();
                            imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                            screen = CurScreen.ARTICLE_DISCUSSION;
                            break;
                        }
                        else {
                            imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[1]);
                            imageEventText.clearAllDialogs();
                            imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                            screen = CurScreen.LEAVE;
                            break;
                        }
                    case 3:
                    default:
                        imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[1]);
                        imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                        screen = CurScreen.LEAVE;
                        break;
                }
                break;
            case ARTICLE_FIRST:
                if (!firstPressed) {
                    switch (buttonPressed) {
                        case 0:
                            AbstractDungeon.player.heal(1);
                            imageEventText.clearAllDialogs();
                            firstPressed = true;
                            imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                            break;
                        case 1:
                            AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, 1));
                            imageEventText.clearAllDialogs();
                            firstPressed = true;
                            imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                            break;
                        case 2:
                            imageEventText.loadImage("stsgallery/images/events/gallery.jpg");
                            imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[0]);
                            imageEventText.clearAllDialogs();
                            imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
                            imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
                            if (!discussionPressed) {
                                imageEventText.setDialogOption(discussionName);
                            }
                            imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                            screen = CurScreen.MAINPAGE;
                    }
                }
                else {
                    imageEventText.loadImage("stsgallery/images/events/gallery.jpg");
                    imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[0]);
                    imageEventText.clearAllDialogs();
                    imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
                    imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
                    if (!discussionPressed) {
                        imageEventText.setDialogOption(discussionName);
                    }
                    imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                    screen = CurScreen.MAINPAGE;
                }
                break;
            case ARTICLE_SECOND:
                if (!secondPressed) {
                    switch (buttonPressed) {
                        case 0:
                            AbstractDungeon.player.heal(1);
                            imageEventText.clearAllDialogs();
                            secondPressed = true;
                            imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                            break;
                        case 1:
                            AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, 1));
                            imageEventText.clearAllDialogs();
                            secondPressed = true;
                            imageEventText.setDialogOption(GENERAL_OPTIONS[3]);
                            break;
                        case 2:
                            imageEventText.loadImage("stsgallery/images/events/gallery.jpg");
                            imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[0]);
                            imageEventText.clearAllDialogs();
                            imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
                            imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
                            if (!discussionPressed) {
                                imageEventText.setDialogOption(discussionName);
                            }
                            imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                            screen = CurScreen.MAINPAGE;
                    }
                }
                else {
                    imageEventText.loadImage("stsgallery/images/events/gallery.jpg");
                    imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[0]);
                    imageEventText.clearAllDialogs();
                    imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
                    imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
                    if (!discussionPressed) {
                        imageEventText.setDialogOption(discussionName);
                    }
                    imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                    screen = CurScreen.MAINPAGE;
                }
                break;
            case ARTICLE_DISCUSSION:
                this.pickCard = true;
                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                group.addToBottom(new Bad());
                group.addToBottom(new Frontload());
                group.addToBottom(new GoodForOtherChar());
                group.addToBottom(new NeedUpgrade());
                group.addToBottom(new NoNeedUpgrade());
                group.addToBottom(new Planning());
                group.addToBottom(new TrashWithoutPyramid());
                group.addToBottom(new TrashWithoutSnecko());
                group.addToBottom(new HighCost());
                group.addToBottom(new WorseThanCurse());
                AbstractDungeon.gridSelectScreen.open(group, 1, DISCUSSION_DESCRIPTIONS[2], false);
                screen = CurScreen.DISCUSSION;

                break;
            case DISCUSSION:
                imageEventText.loadImage("stsgallery/images/events/gallery.jpg");
                imageEventText.updateBodyText(GENERAL_DESCRIPTIONS[0]);
                imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(NORMAL_OPTIONS[firstArticle]);
                imageEventText.setDialogOption(NORMAL_OPTIONS[secondArticle]);
                if (!discussionPressed) {
                    imageEventText.setDialogOption(discussionName);
                }
                imageEventText.setDialogOption(GENERAL_OPTIONS[0]);
                screen = CurScreen.MAINPAGE;
                break;
            case LEAVE:
            default:
                openMap();
                break;
        }
    }

    private AbstractCard getRandomNonBasicCard() {
        ArrayList<AbstractCard> list = new ArrayList();
        ArrayList<AbstractCard> list2 = new ArrayList();
        Iterator var2 = AbstractDungeon.player.masterDeck.group.iterator();



        while(var2.hasNext()) {
            AbstractCard c = (AbstractCard)var2.next();
            boolean hasMod = false;
            for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
                if (mod instanceof GalleryModifier) {
                    hasMod = true;
                }
            }
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type != AbstractCard.CardType.CURSE && !hasMod && c.color == AbstractDungeon.player.getCardColor()) {
                list.add(c);
            }
            else if (c.type != AbstractCard.CardType.CURSE) {
                list2.add(c);
            }
        }

        if (list.isEmpty()) {
            Collections.shuffle(list2, new Random(AbstractDungeon.miscRng.randomLong()));
            discussionPressed = true;
            return (AbstractCard)list2.get(0);
        } else {
            Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
            return (AbstractCard)list.get(0);
        }
    }

    private AbstractCardModifier cardToMod(AbstractCard c) {
        if (c == null) return null;
        switch (c.cardID) {
            case "stsgallery:Bad":
                return new BadMod();
            case "stsgallery:TrashWithoutPyramid":
                return new TrashWithoutPyramidMod();
            case "stsgallery:WorseThanCurse":
                return new WorseThanCurseMod();
            case "stsgallery:TrashWithoutSnecko":
                return new TrashWithoutSneckoMod();
            case "stsgallery:Planning":
                return new PlanningMod();
            case "stsgallery:GoodForOtherChar":
                return new GoodForOtherCharMod();
            case "stsgallery:Frontload":
                return new FrontloadMod();
            case "stsgallery:NeedUpgrade":
                return new NeedUpgradeMod();
            case "stsgallery:HighCost":
                return new HighCostMod();
            case "stsgallery:NoNeedUpgrade":
                return new NoNeedUpgradeMod();
        }
        return null;
    }

    private static enum CurScreen {
        MAINPAGE,
        ARTICLE_FIRST,
        ARTICLE_SECOND,
        ARTICLE_DISCUSSION,
        DISCUSSION,
        LEAVE;

        private CurScreen() {

        }
    }
}
