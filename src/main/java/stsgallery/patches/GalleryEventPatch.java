package stsgallery.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import stsgallery.events.GalleryEvent;

public class GalleryEventPatch {
    @SpirePatch(clz = AbstractDungeon.class, method = "generateEvent")
    public static class EventEdit {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> patch() {
            return SpireReturn.Return(new GalleryEvent());
        }
    }
}
