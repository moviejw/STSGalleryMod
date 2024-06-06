package stsgallery.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.events.shrines.GremlinWheelGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import stsgallery.achievements.GalleryAchievements;

import java.util.ArrayList;

import static stsgallery.STSGalleryMod.makeID;

public class AchievementsPatch {
    private static UIStrings getUIStrings(String ID)
    {
        return CardCrawlGame.languagePack.getUIString(ID);
    }

    @SpirePatch(clz = AchievementItem.class, method = "reloadImg")
    public static class ReloadImgEdit {
        @SpirePostfixPatch
        public static void patch(AchievementItem __instance) {
            if (__instance instanceof GalleryAchievements) {
                ((GalleryAchievements)__instance).currentImg = GalleryAchievements.atlas.findRegion(((GalleryAchievements)__instance).currentImg.name);
            }
        }
    }

    @SpirePatch(clz = AchievementGrid.class, method = "<ctor>")
    public static class GridEdit {
        @SpirePostfixPatch
        public static void patch (AchievementGrid instance){
            BaseMod.logger.info("Loading newAchievementsAtlas...");
            GalleryAchievements.atlas = new TextureAtlas(Gdx.files.internal("stsgallery/images/achievements/newAchievementsAtlas.atlas"));
            loadAchievement(instance, "spireHooray", "Spire Hooray", "Hooray!", "SPIRE_HOORAY", false);
            loadAchievement(instance, "bossLament", "Boss Lament", "Lament!", "BOSS_LAMENT", false);
            loadAchievement(instance, "fastGremlin", "Fast Gremlin", "Fast!", "FAST_GREMLIN", false);
            loadAchievement(instance, "bloom", "Bloom", "Bloom!", "BLOOM", false);
            loadAchievement(instance, "flameBarrier", "Flame Barrier", "Flame!", "FLAME_BARRIER", false);
            loadAchievement(instance, "tastyFruit", "Tasty Fruit", "Tasty!", "TASTY_FRUIT", false);
            loadAchievement(instance, "cruiseShip", "Cruise Ship", "Cruise!", "CRUISE_SHIP", false);
            loadAchievement(instance, "kingOfCurse", "King of Curse", "Curse!", "KING_OF_CURSE", false);
            BaseMod.logger.info("newAchievementsAtlas loaded.");
        }

        private static void loadAchievement(AchievementGrid instance, String imgName, String name, String desc, String id, boolean isHidden) {
            BaseMod.logger.info("Finding region '" + imgName + "'...");
            UIStrings strings = getUIStrings(makeID(imgName));
            TextureAtlas.AtlasRegion myAchievementImageUnlocked = GalleryAchievements.atlas.findRegion("unlocked/" + imgName);
            TextureAtlas.AtlasRegion myAchievementImageLocked = GalleryAchievements.atlas.findRegion("locked/" + imgName);
            if (myAchievementImageLocked != null && myAchievementImageUnlocked != null) {
                BaseMod.logger.info("'" + imgName + "' region found.");
                instance.items.add(new GalleryAchievements(strings.EXTRA_TEXT[0], strings.TEXT[0], id, isHidden, myAchievementImageUnlocked, myAchievementImageLocked));
            } else {
                BaseMod.logger.error("'" + imgName + "' region not found. Please check your spelling and make sure the region exists in your atlas.");
            }
        }
    }

    @SpirePatch2(clz = StatsScreen.class, method = "renderStatScreen")
    public static class OffsetEdit {
        public static Integer NEW_ROWS = 1;

        @SpireInsertPatch(rloc = 8, localvars = {"renderY"})
        public static void patch(StatsScreen __instance, @ByRef float[] renderY) {
            renderY[0] -= (float)(180 * NEW_ROWS) * Settings.scale;
        }
    }

    @SpirePatch(clz = StatsScreen.class, method = "calculateScrollBounds")
    public static class OffsetEdit2 {
        public static Integer NEW_ROWS = 1;

        @SpirePostfixPatch
        public static void patch(StatsScreen __instance) {
            float currentUpperBound = (Float) ReflectionHacks.getPrivate(__instance, StatsScreen.class, "scrollUpperBound");
            ReflectionHacks.setPrivate(__instance, StatsScreen.class, "scrollUpperBound", currentUpperBound + (float)(180 * NEW_ROWS) * Settings.scale);
        }
    }

    /*
    @SpirePatch(clz = WarPaint.class, method="onEquip")
    public static class TestEdit {
        @SpirePostfixPatch
        public static void patch(AbstractRelic __instance) {
            UnlockTracker.unlockAchievement("SPIRE_HOORAY");
        }
    }
    */

    @SpirePatch2(clz = WarPaint.class, method="onEquip")
    public static class SpireHoorayEdit {
        @SpireInsertPatch(rloc = 7, localvars = {"upgradableCards"})
        public static void patch(AbstractRelic __instance, ArrayList<AbstractCard> upgradableCards) {
            if (!upgradableCards.isEmpty()) {
                if (upgradableCards.size() != 1) {
                    AbstractCard c1 = upgradableCards.get(0);
                    AbstractCard c2 = upgradableCards.get(1);
                    AbstractCard.CardTags strike = AbstractCard.CardTags.STARTER_STRIKE;
                    AbstractCard.CardTags defend = AbstractCard.CardTags.STARTER_DEFEND;
                    boolean strikeCheck = !c1.hasTag(strike) && !c2.hasTag(strike);
                    boolean defendCheck = !c1.hasTag(defend) && !c2.hasTag(defend);
                    boolean starterCheck = c1.rarity == AbstractCard.CardRarity.BASIC && c2.rarity == AbstractCard.CardRarity.BASIC;
                    if (strikeCheck && defendCheck && starterCheck) {
                        UnlockTracker.unlockAchievement("SPIRE_HOORAY");
                    }
                }
            }
            //UnlockTracker.unlockAchievement("SPIRE_HOORAY");
        }
    }

    @SpirePatch2(clz = ShiningLight.class, method="upgradeCards")
    public static class SpireHoorayEdit2 {
        @SpireInsertPatch(rloc = 13, localvars = {"upgradableCards"})
        public static void patch(AbstractImageEvent __instance, ArrayList<AbstractCard> upgradableCards) {
            if (!upgradableCards.isEmpty()) {
                if (upgradableCards.size() != 1) {
                    AbstractCard c1 = upgradableCards.get(0);
                    AbstractCard c2 = upgradableCards.get(1);
                    AbstractCard.CardTags strike = AbstractCard.CardTags.STARTER_STRIKE;
                    AbstractCard.CardTags defend = AbstractCard.CardTags.STARTER_DEFEND;
                    boolean strikeCheck = !c1.hasTag(strike) && !c2.hasTag(strike);
                    boolean defendCheck = !c1.hasTag(defend) && !c2.hasTag(defend);
                    boolean starterCheck = c1.rarity == AbstractCard.CardRarity.BASIC && c2.rarity == AbstractCard.CardRarity.BASIC;
                    if (strikeCheck && defendCheck && starterCheck) {
                        UnlockTracker.unlockAchievement("SPIRE_HOORAY");
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = NeowsLament.class, method="atBattleStart")
    public static class BossLamentEdit {
        @SpireInsertPatch(rloc = 11)
        public static void patch(AbstractRelic __instance) {
            for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                if (m.type.equals(AbstractMonster.EnemyType.BOSS)) {
                    UnlockTracker.unlockAchievement("BOSS_LAMENT");
                }
            }
        }
    }

    @SpirePatch2(clz = GremlinWheelGame.class, method="applyResult")
    public static class FastGremlinEdit {
        @SpireInsertPatch(rloc = 40)
        public static void patch(AbstractImageEvent __instance, float ___hpLossPercent) {
            AbstractPlayer p = AbstractDungeon.player;
            int current = p.currentHealth;
            int damage = (int)((float)p.maxHealth * ___hpLossPercent) - ((p.hasRelic("TungstenRod")) ? 1 : 0);
            if (current <= damage) {
                UnlockTracker.unlockAchievement("FAST_GREMLIN");
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method="damage")
    public static class BloomEdit {
        @SpireInsertPatch(rloc = 126)
        public static void patch(AbstractPlayer __instance) {
            if (__instance.hasRelic("Mark of the Bloom")) {
                boolean fairy = __instance.hasPotion("FairyPotion");
                boolean lizard = __instance.hasRelic("Lizard Tail") && ((LizardTail)__instance.getRelic("Lizard Tail")).counter == -1;
                if (fairy || lizard) {
                    UnlockTracker.unlockAchievement("BLOOM");
                }
            }
        }
    }

    @SpirePatch2(clz = Byrd.class, method="die")
    public static class FlameBarrierEdit {
        @SpirePostfixPatch
        public static void patch(AbstractMonster __instance) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDead && !m.isDying) return;
            }
            if (GameActionManager.turn <= 1) {
                UnlockTracker.unlockAchievement("FLAME_BARRIER");
            }
        }
    }

    @SpirePatch2(clz = AbstractRelic.class, method="obtain")
    public static class RelicEdit {
        @SpirePostfixPatch
        public static void patch(AbstractRelic __instance) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p.hasRelic("Strawberry") && p.hasRelic("Pear") && p.hasRelic("Mango")) {
                UnlockTracker.unlockAchievement("TASTY_FRUIT");
            }
            if (p.hasRelic("Anchor") && p.hasRelic("HornCleat") && p.hasRelic("CaptainsWheel")) {
                UnlockTracker.unlockAchievement("CRUISE_SHIP");
            }
            if (p.hasRelic("Omamori") && p.hasRelic("Darkstone Periapt") && p.hasRelic("Du-Vu Doll")) {
                UnlockTracker.unlockAchievement("KING_OF_CURSE");
            }
        }
    }
}
