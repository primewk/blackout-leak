package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;

public class Step extends Module {
   private static Step INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Step.StepMode> stepMode;
   public final Setting<Double> height;
   public final Setting<Boolean> slow;
   public final Setting<Boolean> useTimer;
   public final Setting<Double> timer;
   public final Setting<Double> cooldown;
   public boolean shouldResetTimer;
   public int stepProgress;
   public int sinceStep;
   public double[] offsets;
   public double lastSlow;
   public long lastStep;
   public class_243 prevMovement;

   public Step() {
      super("Step", "Makes you sprint", SubCategory.MOVEMENT, true);
      this.stepMode = this.sgGeneral.e("Step Mode", Step.StepMode.NCP, ".");
      this.height = this.sgGeneral.d("Height", 2.0D, 0.0D, 4.0D, 0.05D, ".");
      this.slow = this.sgGeneral.b("Slow", false, "Moves up slowly.", () -> {
         return this.stepMode.get() != Step.StepMode.Vanilla;
      });
      this.useTimer = this.sgGeneral.b("Use Timer", false, "Uses timer when stepping.", () -> {
         return (Boolean)this.slow.get() && this.stepMode.get() != Step.StepMode.Vanilla;
      });
      this.timer = this.sgGeneral.d("Timer", 2.0D, 0.0D, 10.0D, 0.1D, "Packet multiplier.", () -> {
         return (Boolean)this.slow.get() && this.stepMode.get() != Step.StepMode.Vanilla && (Boolean)this.useTimer.get();
      });
      this.cooldown = this.sgGeneral.d("Cooldown", 0.0D, 0.0D, 1.0D, 0.01D, "Time between steps.", () -> {
         return this.stepMode.get() != Step.StepMode.Vanilla;
      });
      this.shouldResetTimer = false;
      this.stepProgress = -1;
      this.sinceStep = 0;
      this.offsets = null;
      this.lastSlow = 0.0D;
      this.lastStep = 0L;
      this.prevMovement = class_243.field_1353;
      INSTANCE = this;
   }

   public static Step getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.shouldResetTimer = false;
      this.stepProgress = -1;
      this.offsets = null;
      this.lastSlow = 0.0D;
   }

   public void onDisable() {
      if (this.shouldResetTimer) {
         Timer.reset();
      }

      this.shouldResetTimer = false;
   }

   public String getInfo() {
      return ((Step.StepMode)this.stepMode.get()).name();
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.sinceStep;
   }

   public boolean isActive() {
      return this.enabled || HoleSnap.getInstance().shouldStep() || TickShift.getInstance().shouldStep();
   }

   public boolean cooldownCheck() {
      return (double)(System.currentTimeMillis() - this.lastStep) > (Double)this.cooldown.get() * 1000.0D;
   }

   public void start(double step) {
      this.offsets = this.getOffsets(step);
      if (this.offsets != null) {
         this.lastSlow = step - this.sum();
      }

   }

   private double[] getOffsets(double step) {
      switch((Step.StepMode)this.stepMode.get()) {
      case NCP:
         if (step > 2.019D) {
            return new double[]{0.425D, 0.396D, -0.122D, -0.1D, 0.423D, 0.35D, 0.28D, 0.217D, 0.15D, -0.1D};
         }

         if (step > 1.5D) {
            return new double[]{0.42D, 0.36D, -0.15D, -0.12D, 0.39D, 0.31D, 0.24D, -0.02D};
         }

         if (step > 1.015D) {
            return new double[]{0.42D, 0.3332D, 0.2568D, 0.083D, -0.078D};
         }

         if (step > 0.6D) {
            return new double[]{0.42D * step, 0.3332D * step};
         }
         break;
      case UpdatedNCP:
         if (step > 1.1661D) {
            return new double[]{0.42D, 0.3332D, 0.2478D, 0.1651D};
         }

         if (step > 1.015D) {
            return new double[]{0.42D, 0.3332D, 0.2478D};
         }

         if (step > 0.6D) {
            return new double[]{0.42D * step, 0.3332D * step};
         }
      }

      return null;
   }

   public boolean canStrafeJump(class_243 movement) {
      if (!this.enabled) {
         return true;
      } else if (this.sinceStep <= 1) {
         return false;
      } else {
         class_238 box = BlackOut.mc.field_1724.method_5829();
         List<class_265> list = BlackOut.mc.field_1687.method_20743(BlackOut.mc.field_1724, box.method_18804(movement));
         class_243 vec3d = movement.method_1027() == 0.0D ? movement : class_1297.method_20736(BlackOut.mc.field_1724, movement, box, BlackOut.mc.field_1687, list);
         boolean collidedX = movement.field_1352 != vec3d.field_1352;
         boolean collidedZ = movement.field_1350 != vec3d.field_1350;
         boolean collidedHorizontally = collidedX || collidedZ;
         if (!collidedHorizontally) {
            return true;
         } else {
            class_243 stepMovement = class_1297.method_20736(BlackOut.mc.field_1724, new class_243(movement.field_1352, 0.6D, movement.field_1350), box, BlackOut.mc.field_1687, list);
            class_243 stepMovementUp = class_1297.method_20736(BlackOut.mc.field_1724, new class_243(0.0D, 0.6D, 0.0D), box.method_1012(movement.field_1352, 0.0D, movement.field_1350), BlackOut.mc.field_1687, list);
            if (stepMovementUp.field_1351 < (Double)this.height.get()) {
               class_243 vec3d4 = class_1297.method_20736(BlackOut.mc.field_1724, new class_243(movement.field_1352, 0.0D, movement.field_1350), box.method_997(stepMovementUp), BlackOut.mc.field_1687, list).method_1019(stepMovementUp);
               if (vec3d4.method_37268() > stepMovement.method_37268()) {
                  stepMovement = vec3d4;
               }
            }

            return stepMovement.method_37268() <= vec3d.method_37268();
         }
      }
   }

   private double sum() {
      double v = 0.0D;
      double[] var3 = this.offsets;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double d = var3[var5];
         v += d;
      }

      return v;
   }

   public static enum StepMode {
      Vanilla,
      NCP,
      UpdatedNCP;

      // $FF: synthetic method
      private static Step.StepMode[] $values() {
         return new Step.StepMode[]{Vanilla, NCP, UpdatedNCP};
      }
   }
}
