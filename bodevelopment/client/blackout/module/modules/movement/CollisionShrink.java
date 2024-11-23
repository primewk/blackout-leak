package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_238;

public class CollisionShrink extends Module {
   private static CollisionShrink INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Integer> shrinkAmount;

   public CollisionShrink() {
      super("Collision Shrink", "Shrinks your bounding box to phase inside walls.", SubCategory.MOVEMENT, false);
      this.shrinkAmount = this.sgGeneral.i("Shrink Amount", 1, 1, 10, 1, ".");
      INSTANCE = this;
   }

   public static CollisionShrink getInstance() {
      return INSTANCE;
   }

   public class_238 getBox(class_238 normal) {
      double amount = 0.0625D * Math.pow(10.0D, (double)(Integer)this.shrinkAmount.get()) / 1.0E10D;
      return normal.method_35580(amount, 0.0D, amount);
   }
}
