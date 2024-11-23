package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class AntiHunger extends Module {
   private static AntiHunger INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> sprint;
   public final Setting<Boolean> moving;

   public AntiHunger() {
      super("Anti Hunger", "Prevents losing hunger while travelling.", SubCategory.MISC, true);
      this.sprint = this.sgGeneral.b("Sprint", true, "Doesn't send sprint packets.");
      this.moving = this.sgGeneral.b("Moving", true, "Sets you off ground to not use hunger.");
      INSTANCE = this;
   }

   public static AntiHunger getInstance() {
      return INSTANCE;
   }
}
