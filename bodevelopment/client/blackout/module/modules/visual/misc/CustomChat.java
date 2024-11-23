package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import java.util.Objects;

public class CustomChat extends Module {
   private static CustomChat INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final TextColorMultiSetting textColor;
   public final Setting<Boolean> blur;
   public final Setting<Boolean> background;
   public final Setting<Boolean> shadow;
   public final Setting<BlackOutColor> bgColor;
   public final Setting<BlackOutColor> shadowColor;

   public CustomChat() {
      super("Custom Chat", "Modifies Chat.", SubCategory.MISC_VISUAL, true);
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.background = this.sgGeneral.b("Background", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.background;
      Objects.requireNonNull(var10005);
      this.shadow = var10001.b("Shadow", true, ".", var10005::get);
      var10001 = this.sgGeneral;
      BlackOutColor var10003 = new BlackOutColor(0, 0, 0, 50);
      var10005 = this.background;
      Objects.requireNonNull(var10005);
      this.bgColor = var10001.c("Background Color", var10003, ".", var10005::get);
      this.shadowColor = this.sgGeneral.c("Shadow Color", new BlackOutColor(0, 0, 0, 100), ".", () -> {
         return (Boolean)this.background.get() && (Boolean)this.shadow.get();
      });
      INSTANCE = this;
   }

   public static CustomChat getInstance() {
      return INSTANCE;
   }
}
