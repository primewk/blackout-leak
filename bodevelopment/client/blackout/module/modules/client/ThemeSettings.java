package bodevelopment.client.blackout.module.modules.client;

import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.theme.Theme;
import java.util.ArrayList;
import java.util.List;

public class ThemeSettings extends SettingsModule {
   private static ThemeSettings INSTANCE;
   public static final List<Theme> themes = new ArrayList();
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Theme> theme;
   public final Setting<Integer> alpha;
   public final Setting<Integer> lowAlpha;

   public ThemeSettings() {
      super("Theme", true, false);
      this.theme = this.sgGeneral.e("Theme", Theme.BLACKOUT, ".");
      this.alpha = this.sgGeneral.i("Color Alpha", 175, 0, 255, 1, "Should be used on backgrounds");
      this.lowAlpha = this.sgGeneral.i("Low Alpha", 50, 0, 255, 1, "Should be used on other transparent things");
      INSTANCE = this;
   }

   public static ThemeSettings getInstance() {
      return INSTANCE;
   }

   public Theme getTheme() {
      return (Theme)this.theme.get();
   }

   public List<Theme> getThemes() {
      return themes;
   }

   public int getMain(int alpha) {
      return this.getTheme().mainWithAlpha(alpha);
   }

   public int getSecond(int alpha) {
      return this.getTheme().secondaryWithAlpha(alpha);
   }

   public int getMain() {
      return this.getTheme().getMain();
   }

   public int getSecond() {
      return this.getTheme().getSecondary();
   }

   public int alpha() {
      return (Integer)this.alpha.get();
   }

   public int lowAlpha() {
      return (Integer)this.lowAlpha.get();
   }

   public int getThemeAlpha() {
      return (Integer)this.alpha.get();
   }
}
