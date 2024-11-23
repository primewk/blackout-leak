package bodevelopment.client.blackout.module.modules.client;

import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.mainmenu.ColorMainMenu;
import bodevelopment.client.blackout.randomstuff.mainmenu.MainMenuRenderer;
import bodevelopment.client.blackout.randomstuff.mainmenu.SmokeMainMenu;
import bodevelopment.client.blackout.randomstuff.mainmenu.ThemeMainMenu;

public class MainMenuSettings extends SettingsModule {
   private static MainMenuSettings INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<MainMenuSettings.MenuMode> mode;
   public final Setting<BlackOutColor> shitfuckingmenucolor;
   public final Setting<BlackOutColor> color;
   public final Setting<BlackOutColor> color2;
   public final Setting<Double> speed;
   public final Setting<Integer> blur;

   public MainMenuSettings() {
      super("Main Menu", true, false);
      this.mode = this.sgGeneral.e("Mode", MainMenuSettings.MenuMode.Smoke, ".");
      this.shitfuckingmenucolor = this.sgGeneral.c("Background Color", new BlackOutColor(125, 125, 125, 255), ".", () -> {
         return this.mode.get() == MainMenuSettings.MenuMode.Color;
      });
      this.color = this.sgGeneral.c("Color", new BlackOutColor(10, 10, 10, 255), ".", () -> {
         return this.mode.get() == MainMenuSettings.MenuMode.Smoke;
      });
      this.color2 = this.sgGeneral.c("Color 2", new BlackOutColor(125, 125, 125, 255), ".", () -> {
         return this.mode.get() == MainMenuSettings.MenuMode.Smoke;
      });
      this.speed = this.sgGeneral.d("Speed", 1.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return this.mode.get() == MainMenuSettings.MenuMode.Smoke;
      });
      this.blur = this.sgGeneral.i("Blur", 5, 0, 20, 1, ".");
      INSTANCE = this;
   }

   public static MainMenuSettings getInstance() {
      return INSTANCE;
   }

   public MainMenuRenderer getRenderer() {
      return ((MainMenuSettings.MenuMode)this.mode.get()).renderer;
   }

   public static enum MenuMode {
      Smoke(new SmokeMainMenu()),
      Color(new ColorMainMenu()),
      Theme(new ThemeMainMenu());

      private final MainMenuRenderer renderer;

      private MenuMode(MainMenuRenderer renderer) {
         this.renderer = renderer;
      }

      // $FF: synthetic method
      private static MainMenuSettings.MenuMode[] $values() {
         return new MainMenuSettings.MenuMode[]{Smoke, Color, Theme};
      }
   }
}
