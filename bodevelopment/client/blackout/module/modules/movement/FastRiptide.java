package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class FastRiptide extends Module {
   private static FastRiptide INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Double> cooldown;
   public long prevRiptide;

   public FastRiptide() {
      super("Fast Riptide", ".", SubCategory.MOVEMENT, false);
      this.cooldown = this.sgGeneral.d("Cooldown", 0.0D, 0.0D, 1.0D, 0.01D, ".");
      this.prevRiptide = 0L;
      INSTANCE = this;
   }

   public static FastRiptide getInstance() {
      return INSTANCE;
   }
}
