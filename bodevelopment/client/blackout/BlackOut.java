package bodevelopment.client.blackout;

import bodevelopment.client.blackout.addon.AddonLoader;
import bodevelopment.client.blackout.event.EventBus;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.RegistryNames;
import bodevelopment.client.blackout.rendering.font.CustomFontRenderer;
import bodevelopment.client.blackout.util.ClassUtils;
import bodevelopment.client.blackout.util.EnchantmentNames;
import bodevelopment.client.blackout.util.FileUtils;
import java.awt.Color;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_310;

public class BlackOut implements ClientModInitializer {
   public static final String NAME = "BlackOut";
   public static final String VERSION = "2.0.0";
   public static final BlackOut.Type TYPE;
   public static final Color TYPECOLOR;
   public static final class_310 mc;
   public static final EventBus EVENT_BUS;
   public static final CustomFontRenderer FONT;
   public static final CustomFontRenderer BOLD_FONT;

   public void onInitializeClient() {
      EnchantmentNames.init();
      ClassUtils.init();
      FileUtils.init();
      AddonLoader.load();
      Managers.init();
      Managers.CONFIG.readConfigs();
      Managers.CLICK_GUI.CLICK_GUI.initGui();
      RegistryNames.init();
   }

   static {
      TYPE = BlackOut.Type.Beta;
      TYPECOLOR = TYPE.getColor();
      mc = class_310.method_1551();
      EVENT_BUS = new EventBus();
      FONT = new CustomFontRenderer("ubuntu");
      BOLD_FONT = new CustomFontRenderer("ubuntu-bold");
   }

   public static enum Type {
      Dev(new Color(0, 175, 0, 255)),
      Beta(new Color(150, 150, 255, 255)),
      Release(new Color(255, 0, 0, 255));

      private final Color color;

      private Type(Color color) {
         this.color = color;
      }

      public Color getColor() {
         return this.color;
      }

      public boolean isDevBuild() {
         return this == Dev;
      }

      // $FF: synthetic method
      private static BlackOut.Type[] $values() {
         return new BlackOut.Type[]{Dev, Beta, Release};
      }
   }
}
