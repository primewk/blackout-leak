package bodevelopment.client.blackout.module.modules.client;

import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class BlurSettings extends SettingsModule {
   private static BlurSettings INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Integer> hudBlur;
   private final Setting<Integer> threeDBlur;

   public BlurSettings() {
      super("Blur", true, false);
      this.hudBlur = this.sgGeneral.i("HUD Blur", 5, 1, 20, 1, "");
      this.threeDBlur = this.sgGeneral.i("3D Blur", 5, 1, 20, 1, "");
      INSTANCE = this;
   }

   public static BlurSettings getInstance() {
      return INSTANCE;
   }

   public int getHUDBlurStrength() {
      return (Integer)this.hudBlur.get();
   }

   public int get3DBlurStrength() {
      return (Integer)this.threeDBlur.get();
   }
}
