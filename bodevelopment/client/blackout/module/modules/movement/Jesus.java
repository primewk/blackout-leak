package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.MovementUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_1294;
import net.minecraft.class_2246;
import net.minecraft.class_2708;
import net.minecraft.class_3486;

public class Jesus extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Jesus.Mode> mode;
   private final Setting<Boolean> toggle;
   private final Setting<Double> bob;
   private final Setting<Double> waterSpeed;
   private boolean inWater;
   private boolean isSlowed;

   public Jesus() {
      super("Jesus", "Walks on water", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", Jesus.Mode.NCP, ".", () -> {
         return true;
      });
      this.toggle = this.sgGeneral.b("Anti Rubberband", true, "Tries to prevent extra rubberbanding", () -> {
         return this.mode.get() == Jesus.Mode.NCP_Fast;
      });
      this.bob = this.sgGeneral.d("Bob force", 0.005D, 0.0D, 1.0D, 0.005D, "How much to bob", () -> {
         return this.mode.get() == Jesus.Mode.NCP_Fast;
      });
      this.waterSpeed = this.sgGeneral.d("Water speed", 1.175D, 0.0D, 2.0D, 0.005D, "0.265 is generally better", () -> {
         return this.mode.get() == Jesus.Mode.NCP_Fast;
      });
      this.inWater = false;
      this.isSlowed = false;
   }

   public void onEnable() {
      this.inWater = false;
   }

   public String getInfo() {
      return ((Jesus.Mode)this.mode.get()).name();
   }

   @Event
   public void onRecieve(PacketEvent.Receive.Pre event) {
      if (event.packet instanceof class_2708) {
         this.isSlowed = true;
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1687.method_8320(BlackOut.mc.field_1724.method_24515().method_10074()).method_26204() != class_2246.field_10382 && BlackOut.mc.field_1687.method_8320(BlackOut.mc.field_1724.method_24515()).method_26204() != class_2246.field_10382) {
            this.inWater = false;
         } else {
            if (!this.inWater) {
               this.isSlowed = false;
            }

            this.inWater = true;
         }

         if (BlackOut.mc.field_1690.field_1832.method_1434() || BlackOut.mc.field_1690.field_1903.method_1434()) {
            return;
         }

         switch((Jesus.Mode)this.mode.get()) {
         case NCP:
            this.tickNCP(event);
            break;
         case NCP_Fast:
            this.tickFast(event);
            break;
         case Matrix:
            this.tickMatrix(event);
         }
      }

   }

   private void tickNCP(MoveEvent.Pre event) {
      double height = OLEPOSSUtils.fluidHeight(BlackOut.mc.field_1724.method_5829(), class_3486.field_15517);
      if (!(height <= 0.0D)) {
         if (BlackOut.mc.field_1724.field_5976) {
            event.setY(this, 0.1D);
         } else {
            event.setY(this, height < 0.05D ? -1.0E-4D : Math.min(height, 0.1D));
         }

         double yaw = Math.toRadians((double)(Managers.ROTATION.moveYaw + 90.0F));
         double speed = MovementUtils.getSpeed(0.2873D);
         if (Managers.ROTATION.move) {
            event.setXZ(this, Math.cos(yaw) * speed, Math.sin(yaw) * speed);
         }

      }
   }

   private void tickFast(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724.method_5799() && !BlackOut.mc.field_1724.method_5869() || BlackOut.mc.field_1724.method_5771() && !BlackOut.mc.field_1724.method_5777(class_3486.field_15518)) {
         ((IVec3d)BlackOut.mc.field_1724.method_18798()).blackout_Client$setY((Double)this.bob.get());
         if ((Boolean)this.toggle.get() && (!BlackOut.mc.field_1724.method_5771() || BlackOut.mc.field_1724.method_5777(class_3486.field_15518)) && !this.isSlowed) {
            double motion = MovementUtils.getSpeed((Double)this.waterSpeed.get());
            if (BlackOut.mc.field_1724.method_6059(class_1294.field_5904)) {
               motion *= 1.2D + (double)BlackOut.mc.field_1724.method_6112(class_1294.field_5904).method_5578() * 0.2D;
            }

            if (BlackOut.mc.field_1724.method_6059(class_1294.field_5909)) {
               motion /= 1.2D + (double)BlackOut.mc.field_1724.method_6112(class_1294.field_5909).method_5578() * 0.2D;
            }

            double x = Math.cos(Math.toRadians((double)(Managers.ROTATION.moveYaw + 90.0F)));
            double z = Math.sin(Math.toRadians((double)(Managers.ROTATION.moveYaw + 90.0F)));
            if (Managers.ROTATION.move) {
               event.setXZ(this, motion * x, motion * z);
            } else {
               event.setXZ(this, 0.0D, 0.0D);
            }
         }

      }
   }

   private void tickMatrix(MoveEvent.Pre event) {
      double height = OLEPOSSUtils.fluidHeight(BlackOut.mc.field_1724.method_5829(), class_3486.field_15517);
      if (height > 0.0D && height <= 1.0D) {
         event.setY(this, 0.13D);
      }

   }

   public static enum Mode {
      NCP,
      NCP_Fast,
      Matrix;

      // $FF: synthetic method
      private static Jesus.Mode[] $values() {
         return new Jesus.Mode[]{NCP, NCP_Fast, Matrix};
      }
   }
}
