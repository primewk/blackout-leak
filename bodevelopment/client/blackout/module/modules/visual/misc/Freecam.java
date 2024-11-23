package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_243;
import net.minecraft.class_743;

public class Freecam extends Module {
   private static Freecam INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Freecam.Mode> mode;
   private final Setting<Double> speedH;
   private final Setting<Double> speedV;
   private float moveYaw;
   private float vertical;
   private boolean move;
   public final class_243 velocity;
   public class_243 pos;

   public Freecam() {
      super("Freecam", "Allows your camera to move without you moving.", SubCategory.MISC_VISUAL, true);
      this.mode = this.sgGeneral.e("Mode", Freecam.Mode.Normal, ".");
      this.speedH = this.sgGeneral.d("Horizontal Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".");
      this.speedV = this.sgGeneral.d("Vertical Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".");
      this.velocity = new class_243(0.0D, 0.0D, 0.0D);
      this.pos = class_243.field_1353;
      INSTANCE = this;
   }

   public static Freecam getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(TickEvent.Pre event) {
      if (BlackOut.mc.field_1687 == null || BlackOut.mc.field_1724 == null) {
         this.disable();
      }

   }

   public void resetInput(class_743 input) {
      input.field_3910 = false;
      input.field_3909 = false;
      input.field_3908 = false;
      input.field_3906 = false;
      input.field_3905 = 0.0F;
      input.field_3907 = 0.0F;
      input.field_3904 = false;
      input.field_3903 = false;
   }

   public class_243 getPos(float yaw, float pitch) {
      this.inputYaw(yaw);
      class_243 movement;
      double rad;
      double x;
      double y;
      double z;
      switch((Freecam.Mode)this.mode.get()) {
      case Normal:
         rad = Math.toRadians((double)(this.moveYaw + 90.0F));
         x = 0.0D;
         y = (double)this.vertical * (Double)this.speedV.get();
         z = 0.0D;
         if (this.move) {
            x = Math.cos(rad) * (Double)this.speedH.get();
            z = Math.sin(rad) * (Double)this.speedH.get();
         }

         movement = new class_243(x, y, z);
         break;
      case Smooth:
         rad = Math.toRadians((double)(this.moveYaw + 90.0F));
         x = 0.0D;
         y = (double)this.vertical;
         z = 0.0D;
         if (this.move) {
            x = Math.cos(rad);
            z = Math.sin(rad);
         }

         x = this.smoothen(this.velocity.field_1352, x);
         y = this.smoothen(this.velocity.field_1351, y);
         z = this.smoothen(this.velocity.field_1350, z);
         ((IVec3d)this.velocity).blackout_Client$set(x, y, z);
         movement = this.velocity.method_18805((Double)this.speedH.get(), (Double)this.speedV.get(), (Double)this.speedH.get());
         break;
      default:
         return this.pos;
      }

      return this.pos = this.pos.method_1019(movement.method_1021((double)BlackOut.mc.method_1534()));
   }

   private double smoothen(double from, double to) {
      return (from + to * (double)BlackOut.mc.method_1534() / 4.0D) * (double)(1.0F - BlackOut.mc.method_1534() / 4.0F);
   }

   private void inputYaw(float yaw) {
      this.moveYaw = yaw;
      float forward = this.getMovementMultiplier(BlackOut.mc.field_1690.field_1894.method_1434(), BlackOut.mc.field_1690.field_1881.method_1434());
      float strafing = this.getMovementMultiplier(BlackOut.mc.field_1690.field_1913.method_1434(), BlackOut.mc.field_1690.field_1849.method_1434());
      if (forward > 0.0F) {
         this.move = true;
         this.moveYaw += strafing > 0.0F ? -45.0F : (strafing < 0.0F ? 45.0F : 0.0F);
      } else if (forward < 0.0F) {
         this.move = true;
         this.moveYaw += strafing > 0.0F ? -135.0F : (strafing < 0.0F ? 135.0F : 180.0F);
      } else {
         this.move = strafing != 0.0F;
         this.moveYaw += strafing > 0.0F ? -90.0F : (strafing < 0.0F ? 90.0F : 0.0F);
      }

      this.vertical = this.getMovementMultiplier(BlackOut.mc.field_1690.field_1903.method_1434(), BlackOut.mc.field_1690.field_1832.method_1434());
   }

   private float getMovementMultiplier(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }

   public static enum Mode {
      Normal,
      Smooth;

      // $FF: synthetic method
      private static Freecam.Mode[] $values() {
         return new Freecam.Mode[]{Normal, Smooth};
      }
   }
}
