package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class TargetStrafe extends Module {
   private static TargetStrafe INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> preferredDist;
   private final Setting<Double> approach;
   private final Setting<Boolean> auraTarget;
   private final Setting<Double> range;
   private Double bestYaw;
   private double closest;
   private boolean valid;
   private boolean right;
   private int sinceCollide;
   private class_1657 target;

   public TargetStrafe() {
      super("Target Strafe", "Spins around enemies.", SubCategory.MOVEMENT, true);
      this.preferredDist = this.sgGeneral.d("Preferred Dist", 1.0D, 0.0D, 6.0D, 0.1D, "");
      this.approach = this.sgGeneral.d("Approach", 1.0D, 0.0D, 1.0D, 0.01D, "");
      this.auraTarget = this.sgGeneral.b("Aura Target", true, ".");
      this.range = this.sgGeneral.d("Range", 4.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return !(Boolean)this.auraTarget.get();
      });
      this.right = false;
      this.sinceCollide = 0;
      INSTANCE = this;
   }

   public static TargetStrafe getInstance() {
      return INSTANCE;
   }

   public void onMove(class_243 movement) {
      ++this.sinceCollide;
      this.target = this.getTarget();
      if (this.target != null) {
         double speed = movement.method_37267();
         if (!(speed <= 0.0D)) {
            double yaw = this.getYaw(speed);
            if (this.valid) {
               double x = Math.cos(yaw);
               double z = Math.sin(yaw);
               ((IVec3d)movement).blackout_Client$setXZ(x * speed, z * speed);
            }
         }
      }
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1724.field_5976 && this.sinceCollide > 10) {
         this.sinceCollide = 0;
         this.right = !this.right;
      }

   }

   private double getYaw(double movement) {
      if (!Aura.getInstance().enabled) {
         this.valid = false;
         return 0.0D;
      } else {
         this.closest = 10000.0D;
         this.bestYaw = null;
         this.calc(this.right, movement);
         if (this.bestYaw == null) {
            this.valid = false;
            return 0.0D;
         } else {
            this.valid = true;
            return this.bestYaw;
         }
      }
   }

   private void calc(boolean right, double movement) {
      double distance = BlackOut.mc.field_1724.method_19538().method_1020(this.target.method_19538()).method_37267();

      for(double delta = -1.0D; delta <= 1.0D; delta += 0.01D) {
         double d = distance + delta * movement * (Double)this.approach.get();
         double diff = Math.abs(d - (Double)this.preferredDist.get());
         if (!(diff >= this.closest)) {
            Double yaw = this.doTheMathing(movement, d, distance, right);
            if (yaw != null) {
               class_243 vec = (new class_243(d * Math.cos(yaw), 0.0D, d * Math.sin(yaw))).method_1019(BlackOut.mc.field_1724.method_19538());
               double width = 0.3D;
               double height = 1.8D;
               class_238 box = new class_238(vec.method_10216() - width, vec.method_10214(), vec.method_10215() - width, vec.method_10216() + width, vec.method_10214() + height, vec.method_10215() + width);
               if (!OLEPOSSUtils.inLava(box) && !this.wouldFall(box, this.target.method_23318())) {
                  this.closest = diff;
                  this.bestYaw = yaw;
               }
            }
         }
      }

   }

   private Double doTheMathing(double movement, double preferred, double distance, boolean reversed) {
      double val = (preferred * preferred - distance * distance - movement * movement) / (-2.0D * distance * movement);
      double angle = Math.acos(val);
      return Double.isNaN(angle) ? null : Math.toRadians(RotationUtils.getYaw((class_1297)this.target)) + Math.abs(angle) * (double)(reversed ? 1 : -1) + 1.5707963705062866D;
   }

   private boolean wouldFall(class_238 box, double y) {
      double diff = Math.min(BlackOut.mc.field_1724.method_23318() - y, 0.0D);
      return !OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_1012(0.0D, diff - 2.5D, 0.0D));
   }

   private class_1657 getTarget() {
      if ((Boolean)this.auraTarget.get()) {
         return Aura.targetedPlayer;
      } else {
         class_1657 closest = null;
         double closestDist = 0.0D;
         Iterator var4 = BlackOut.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            double dist;
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return closest;
                        }

                        player = (class_1657)var4.next();
                     } while(player == BlackOut.mc.field_1724);
                  } while(Managers.FRIENDS.isFriend(player));

                  dist = (double)BlackOut.mc.field_1724.method_5739(player);
               } while(dist > (Double)this.range.get());
            } while(closest != null && dist > closestDist);

            closest = player;
            closestDist = dist;
         }
      }
   }
}
