package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class Reach extends Module {
   private static Reach INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Double> entityReach;
   public final Setting<Double> blockReach;

   public Reach() {
      super("Reach", "Modifies interaction range for blocks and entities.", SubCategory.MISC, false);
      this.entityReach = this.sgGeneral.d("Entity Reach", 3.0D, 0.0D, 10.0D, 0.1D, ".");
      this.blockReach = this.sgGeneral.d("Block Reach", 4.5D, 0.0D, 10.0D, 0.1D, ".");
      INSTANCE = this;
   }

   public static Reach getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return String.format("E: %.1f B: %.1f", this.entityReach.get(), this.blockReach.get());
   }
}
