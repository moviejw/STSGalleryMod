package stsgallery.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.SearingBlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static stsgallery.STSGalleryMod.makeID;
import static stsgallery.STSGalleryMod.modID;

public class UpgradePatch {

    public static final Logger logger = LogManager.getLogger(modID);

    @SpirePatch(clz = AbstractCard.class, method = "canUpgrade")
    public static class UpgradeEdit {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> patch(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, makeID("NoNeedUpgradeMod"))) {
                return SpireReturn.Return(false);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = SearingBlow.class, method = "canUpgrade")
    public static class Upgrade2Edit {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> patch(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, makeID("NoNeedUpgradeMod"))) return SpireReturn.Return(false);
            else return SpireReturn.Continue();
        }
    }
}
