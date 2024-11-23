package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.Objects;
import net.minecraft.class_3486;
import net.minecraft.class_744;

public class FastSwim extends Module {
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   private final SettingGroup sgVertical = this.addGroup("Vertical");
   private final Setting<Double> waterTouching;
   private final Setting<Double> waterSubmerged;
   private final Setting<Double> waterDiving;
   private final Setting<Double> lavaTouching;
   private final Setting<Double> lavaSubmerged;
   private Setting<Boolean> stillVertical;
   private Setting<Boolean> modifyVertical;
   private Setting<Double> waterUp;
   private Setting<Double> waterDown;
   private Setting<Double> lavaUp;
   private Setting<Double> lavaDown;

   public FastSwim() {
      super("Fast Swim", "Swims faster guh", SubCategory.MOVEMENT, true);
      this.waterTouching = this.sgSpeed.d("Water Touching", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.waterSubmerged = this.sgSpeed.d("Water Submerged", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.waterDiving = this.sgSpeed.d("Water Diving", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.lavaTouching = this.sgSpeed.d("Lava Touching", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.lavaSubmerged = this.sgSpeed.d("Lava Submerged", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.stillVertical = this.sgVertical.b("Still Vertical", true, ".");
      this.modifyVertical = this.sgVertical.b("Modify Vertical", false, ".");
      SettingGroup var10001 = this.sgVertical;
      Setting var10008 = this.modifyVertical;
      Objects.requireNonNull(var10008);
      this.waterUp = var10001.d("Water Up", 0.5D, 0.0D, 2.0D, 0.02D, ".", var10008::get);
      var10001 = this.sgVertical;
      var10008 = this.modifyVertical;
      Objects.requireNonNull(var10008);
      this.waterDown = var10001.d("Water Down", 0.5D, 0.0D, 2.0D, 0.02D, ".", var10008::get);
      var10001 = this.sgVertical;
      var10008 = this.modifyVertical;
      Objects.requireNonNull(var10008);
      this.lavaUp = var10001.d("Lava Up", 0.5D, 0.0D, 2.0D, 0.02D, ".", var10008::get);
      var10001 = this.sgVertical;
      var10008 = this.modifyVertical;
      Objects.requireNonNull(var10008);
      this.lavaDown = var10001.d("Lava Down", 0.5D, 0.0D, 2.0D, 0.02D, ".", var10008::get);
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      boolean touchingWater = BlackOut.mc.field_1724.method_5799();
      boolean diving = BlackOut.mc.field_1724.method_20232() && touchingWater;
      double targetSpeed;
      if (diving) {
         targetSpeed = (Double)this.waterDiving.get();
      } else {
         targetSpeed = this.getSpeed(touchingWater);
      }

      if (!(targetSpeed <= 0.0D)) {
         if (!diving && (Boolean)this.modifyVertical.get() && BlackOut.mc.field_1724.field_3913.field_3904 ^ BlackOut.mc.field_1724.field_3913.field_3903) {
            event.setY(this, this.getVertical(touchingWater && !BlackOut.mc.field_1724.method_5771()));
         } else if (this.canBeStill(diving)) {
            event.setY(this, 0.0D);
         }

         if (Managers.ROTATION.move) {
            double yaw = Math.toRadians((double)(Managers.ROTATION.moveYaw + 90.0F));
            double cos = Math.cos(yaw) * targetSpeed;
            double sin = Math.sin(yaw) * targetSpeed;
            if (!diving) {
               event.setXZ(this, cos, sin);
            } else {
               double hz = this.horizontalMulti(BlackOut.mc.field_1724.field_3913);
               double v = this.verticalMulti(BlackOut.mc.field_1724.field_3913);
               event.set(this, hz * cos, v * targetSpeed, hz * sin);
            }
         }

      }
   }

   private double getVertical(boolean water) {
      return BlackOut.mc.field_1724.field_3913.field_3904 ? (Double)(water ? this.waterUp : this.lavaUp).get() : -(Double)(water ? this.waterDown : this.lavaDown).get();
   }

   private double horizontalMulti(class_744 i) {
      if (i.method_3128().method_35587() == 0.0F) {
         return 0.0D;
      } else {
         return i.field_3904 ^ i.field_3903 ? 0.707106781D : 1.0D;
      }
   }

   private double verticalMulti(class_744 i) {
      if (i.field_3904 == i.field_3903) {
         return 0.0D;
      } else {
         double sus = i.method_3128().method_35587() == 0.0F ? 1.0D : 0.707106781D;
         return i.field_3904 ? sus : -sus;
      }
   }

   private boolean canBeStill(boolean diving) {
      if (!(Boolean)this.stillVertical.get()) {
         return false;
      } else {
         class_744 i = BlackOut.mc.field_1724.field_3913;
         if (diving) {
            return !i.field_3904 && !i.field_3903 && i.method_3128().method_35587() == 0.0F;
         } else {
            return !i.field_3904 && !i.field_3903;
         }
      }
   }

   private double getSpeed(boolean touchingWater) {
      boolean submergedWater = BlackOut.mc.field_1724.method_5869();
      boolean submergedLava = BlackOut.mc.field_1724.method_5777(class_3486.field_15518);
      boolean touchingLava = BlackOut.mc.field_1724.method_5771();
      if (submergedWater && submergedLava) {
         return Math.min((Double)this.waterSubmerged.get(), (Double)this.lavaSubmerged.get());
      } else if (submergedWater) {
         return (Double)this.waterSubmerged.get();
      } else if (submergedLava) {
         return (Double)this.lavaSubmerged.get();
      } else if (touchingWater && touchingLava) {
         return Math.min((Double)this.waterTouching.get(), (Double)this.lavaTouching.get());
      } else if (touchingWater) {
         return (Double)this.waterTouching.get();
      } else {
         return touchingLava ? (Double)this.lavaTouching.get() : -1.0D;
      }
   }
}
