package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.MovementUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_243;
import net.minecraft.class_3486;
import net.minecraft.class_3532;

public class Speed extends Module {
   private static Speed INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgPause = this.addGroup("Pause");
   public final Setting<Speed.SpeedMode> mode;
   private final Setting<Boolean> strict;
   private final Setting<Boolean> ncpSpeed;
   private final Setting<Double> speed;
   private final Setting<Integer> accelerationTicks;
   public final Setting<Double> vanillaSpeed;
   private final Setting<Double> speedMulti;
   private final Setting<Boolean> useTimer;
   private final Setting<Double> stepBoost;
   private final Setting<Double> boostDecay;
   private final Setting<Double> stepBoostCooldown;
   private final Setting<Boolean> instantStop;
   private final Setting<Boolean> pauseSneak;
   private final Setting<Boolean> pauseElytra;
   private final Setting<Boolean> pauseFly;
   public final Setting<Speed.LiquidMode> pauseWater;
   public final Setting<Speed.LiquidMode> pauseLava;
   private class_243 prevMovement;
   private double velocity;
   private double yaw;
   private boolean setTimer;
   private double boost;
   private long prevBoost;

   public Speed() {
      super("Speed", ".", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", Speed.SpeedMode.Instant, ".");
      this.strict = this.sgGeneral.b("Strict", true, ".", () -> {
         return this.mode.get() == Speed.SpeedMode.NCPOld;
      });
      this.ncpSpeed = this.sgGeneral.b("NCP Speed", true, "Uses instant mode when you arent pressing jump key.", () -> {
         return this.mode.get() != Speed.SpeedMode.NCPOld && this.mode.get() != Speed.SpeedMode.Verus && this.mode.get() != Speed.SpeedMode.Vulcan;
      });
      this.speed = this.sgGeneral.d("Speed", 0.3D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.mode.get() != Speed.SpeedMode.NCPOld && this.mode.get() != Speed.SpeedMode.Verus && this.mode.get() != Speed.SpeedMode.Vulcan && !(Boolean)this.ncpSpeed.get();
      });
      this.accelerationTicks = this.sgGeneral.i("Acceleration", 3, 0, 10, 1, ".", () -> {
         return this.mode.get() == Speed.SpeedMode.Instant;
      });
      this.vanillaSpeed = this.sgGeneral.d("Vanilla Speed", 1.0D, 1.0D, 2.0D, 0.01D, ".", () -> {
         return this.mode.get() == Speed.SpeedMode.Vanilla;
      });
      this.speedMulti = this.sgGeneral.d("Speed Multi", 1.3D, 0.0D, 2.0D, 0.01D, ".", () -> {
         return this.mode.get() == Speed.SpeedMode.NCPOld;
      });
      this.useTimer = this.sgGeneral.b("Use Timer", true, ".", () -> {
         return this.mode.get() != Speed.SpeedMode.Verus;
      });
      this.stepBoost = this.sgGeneral.d("Step Boost", 0.0D, 0.0D, 0.5D, 0.01D, ".");
      this.boostDecay = this.sgGeneral.d("Boost Decay", 0.5D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return (Double)this.stepBoost.get() > 0.0D;
      });
      this.stepBoostCooldown = this.sgGeneral.d("Step Boost Cooldown", 0.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return (Double)this.stepBoost.get() > 0.0D;
      });
      this.instantStop = this.sgGeneral.b("Instant Stop", false, ".");
      this.pauseSneak = this.sgPause.b("PauseSneak", true, ".");
      this.pauseElytra = this.sgPause.b("PauseElytra", true, ".");
      this.pauseFly = this.sgPause.b("FlySneak", true, ".");
      this.pauseWater = this.sgPause.e("Pause Water", Speed.LiquidMode.Touching, ".", () -> {
         return true;
      });
      this.pauseLava = this.sgPause.e("Pause Lava", Speed.LiquidMode.Touching, ".", () -> {
         return true;
      });
      this.prevMovement = class_243.field_1353;
      this.velocity = 0.0D;
      this.yaw = 0.0D;
      this.setTimer = false;
      this.boost = 0.0D;
      this.prevBoost = 0L;
      INSTANCE = this;
   }

   public static Speed getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      if (BlackOut.mc.field_1724 != null) {
         this.velocity = this.prevMovement.method_37267();
         this.yaw = class_3532.method_15338(Math.toDegrees(Math.atan2(this.prevMovement.field_1350, this.prevMovement.field_1352)));
      }
   }

   public void onDisable() {
      Timer.reset();
   }

   public String getInfo() {
      return ((Speed.SpeedMode)this.mode.get()).name();
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null || BlackOut.mc.field_1687 != null) {
         this.prevMovement = BlackOut.mc.field_1724.method_19538().method_1023(BlackOut.mc.field_1724.field_6014, BlackOut.mc.field_1724.field_6036, BlackOut.mc.field_1724.field_5969);
         if (this.enabled && BlackOut.mc.field_1724.method_24828() && (this.mode.get() == Speed.SpeedMode.Vulcan || this.mode.get() == Speed.SpeedMode.Verus)) {
            BlackOut.mc.field_1724.method_6043();
         }

      }
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled && !this.isPaused() && this.mode.get() != Speed.SpeedMode.Vanilla) {
         if (this.mode.get() != Speed.SpeedMode.Vulcan || BlackOut.mc.field_1724.method_24828()) {
            this.yaw = (double)(Managers.ROTATION.moveYaw + 90.0F);
         }

         this.boost *= (Double)this.boostDecay.get();
         if (this.prevMovement.field_1351 > 0.6D && BlackOut.mc.field_1724.method_24828() && (double)(System.currentTimeMillis() - this.prevBoost) > (Double)this.stepBoostCooldown.get() * 1000.0D) {
            this.boost = MovementUtils.getSpeed((Double)this.stepBoost.get());
            this.prevBoost = System.currentTimeMillis();
         }

         if (Managers.ROTATION.move) {
            if ((Boolean)this.useTimer.get()) {
               Timer.set(this.getTimer());
               this.setTimer = true;
            }

            this.velocity = this.getNewVelocity();
            this.setMovement(event, Math.toRadians(this.yaw));
         } else {
            if (this.setTimer) {
               Timer.reset();
               this.setTimer = false;
            }

            if ((Boolean)this.instantStop.get()) {
               event.setXZ(this, 0.0D, 0.0D);
            }
         }

      }
   }

   private float getTimer() {
      float var10000;
      switch((Speed.SpeedMode)this.mode.get()) {
      case NCPOld:
      case Instant:
      case Vanilla:
         var10000 = 1.088F;
         break;
      case Verus:
         var10000 = 1.0F;
         break;
      case Vulcan:
         var10000 = 1.0420911F;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private void setMovement(MoveEvent.Pre event, double yaw) {
      switch((Speed.SpeedMode)this.mode.get()) {
      case NCPOld:
         if (BlackOut.mc.field_1724.method_24828()) {
            event.setY(this, 0.3995D);
            this.velocity = MovementUtils.getSpeed(0.2873D * (Double)this.speedMulti.get());
         }
      case Instant:
      case Vanilla:
      case Verus:
      case Vulcan:
      default:
         event.setXZ(this, (this.velocity + this.boost) * Math.cos(yaw), (this.velocity + this.boost) * Math.sin(yaw));
      }
   }

   private double getNewVelocity() {
      double var10000;
      switch((Speed.SpeedMode)this.mode.get()) {
      case NCPOld:
         var10000 = BlackOut.mc.field_1724.method_24828() ? 0.2873D * (Double)this.speedMulti.get() : this.prevVelocity() * 0.9691111111D;
         break;
      case Instant:
         var10000 = OLEPOSSUtils.approach(this.prevMovement.method_37267(), MovementUtils.getSpeed(this.getSpeed()), MovementUtils.getSpeed(this.getSpeed()) / (double)(Integer)this.accelerationTicks.get());
         break;
      case Vanilla:
         var10000 = 0.0D;
         break;
      case Verus:
         var10000 = BlackOut.mc.field_1724.method_24828() ? 0.55D : 0.349D;
         break;
      case Vulcan:
         var10000 = 0.2872D;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private double prevVelocity() {
      return (Boolean)this.strict.get() ? this.prevMovement.method_37267() : this.velocity;
   }

   private double getSpeed() {
      return (Boolean)this.ncpSpeed.get() ? 0.2873D : (Double)this.speed.get();
   }

   private boolean isPaused() {
      if ((Boolean)this.pauseSneak.get() && BlackOut.mc.field_1724.method_5715()) {
         return true;
      } else if ((Boolean)this.pauseElytra.get() && BlackOut.mc.field_1724.method_6128()) {
         return true;
      } else if ((Boolean)this.pauseFly.get() && BlackOut.mc.field_1724.method_31549().field_7479) {
         return true;
      } else {
         boolean var10000;
         label87: {
            switch((Speed.LiquidMode)this.pauseWater.get()) {
            case Touching:
               if (BlackOut.mc.field_1724.method_5799()) {
                  break label87;
               }
            case Disabled:
               break;
            case Submerged:
               if (BlackOut.mc.field_1724.method_5777(class_3486.field_15517)) {
                  break label87;
               }
               break;
            case Both:
               if (BlackOut.mc.field_1724.method_5799() || BlackOut.mc.field_1724.method_5777(class_3486.field_15517)) {
                  break label87;
               }
               break;
            default:
               throw new IncompatibleClassChangeError();
            }

            switch((Speed.LiquidMode)this.pauseLava.get()) {
            case Touching:
               if (BlackOut.mc.field_1724.method_5771()) {
                  break label87;
               }
            case Disabled:
               break;
            case Submerged:
               if (BlackOut.mc.field_1724.method_5777(class_3486.field_15518)) {
                  break label87;
               }
               break;
            case Both:
               if (BlackOut.mc.field_1724.method_5771() || BlackOut.mc.field_1724.method_5777(class_3486.field_15518)) {
                  break label87;
               }
               break;
            default:
               throw new IncompatibleClassChangeError();
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public static enum SpeedMode {
      NCPOld,
      Instant,
      Vanilla,
      Verus,
      Vulcan;

      // $FF: synthetic method
      private static Speed.SpeedMode[] $values() {
         return new Speed.SpeedMode[]{NCPOld, Instant, Vanilla, Verus, Vulcan};
      }
   }

   public static enum LiquidMode {
      Disabled,
      Submerged,
      Touching,
      Both;

      // $FF: synthetic method
      private static Speed.LiquidMode[] $values() {
         return new Speed.LiquidMode[]{Disabled, Submerged, Touching, Both};
      }
   }
}
