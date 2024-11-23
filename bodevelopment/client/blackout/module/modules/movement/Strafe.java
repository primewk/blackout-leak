package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.ChatUtils;
import bodevelopment.client.blackout.util.FileUtils;
import bodevelopment.client.blackout.util.MovementUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2708;
import net.minecraft.class_2743;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import org.apache.commons.lang3.mutable.MutableDouble;

public class Strafe extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgFastFall = this.addGroup("Fast Fall");
   private final SettingGroup sgTimer = this.addGroup("Timer");
   private final SettingGroup sgLimit = this.addGroup("Speed Limits");
   private final SettingGroup sgFriction = this.addGroup("Friction");
   private final SettingGroup sgDamageBoost = this.addGroup("Damage Boost");
   private final SettingGroup sgPause = this.addGroup("Pause");
   private final Setting<Boolean> useOffsets;
   private final Setting<Boolean> offsetEffects;
   private final Setting<Boolean> strictCollisions;
   private final Setting<Boolean> onlyOG;
   private final Setting<Double> jumpPower;
   private final Setting<Double> boost;
   private final Setting<Boolean> resetBoost;
   private final Setting<Double> boostMulti;
   private final Setting<Double> boostDiv;
   private final Setting<Double> effectMultiplier;
   private final Setting<Boolean> instantStop;
   private final Setting<Boolean> fastFall;
   private final Setting<Integer> jumpTicks;
   private final Setting<Integer> fallSpeed;
   private final Setting<Boolean> spoofOG;
   private final Setting<Boolean> stopFall;
   private final Setting<Boolean> useTimer;
   private final Setting<Boolean> ncpTimer;
   private final Setting<Double> timer;
   private final Setting<Boolean> endNcpTimer;
   private final Setting<Double> endTimer;
   private final Setting<Integer> timerStartProgress;
   private final Setting<Integer> timerEndProgress;
   private final Setting<Boolean> ncpSpeed;
   private final Setting<Double> minSpeed;
   private final Setting<Double> maxSpeed;
   private final Setting<Boolean> effectMaxSpeed;
   private final Setting<Double> maxDamageBoost;
   private final Setting<Boolean> vanillaFriction;
   private final Setting<Double> startFriction;
   private final Setting<Double> endFriction;
   private final Setting<Integer> startProgress;
   private final Setting<Integer> endProgress;
   private final Setting<Boolean> damageBoost;
   private final Setting<Boolean> addBoost;
   private final Setting<Boolean> stackingBoost;
   private final Setting<Boolean> directionalBoost;
   private final Setting<Double> boostFactor;
   private final Setting<Double> boostTime;
   private final Setting<Integer> latencyTicks;
   private final Setting<Boolean> pauseSneak;
   private final Setting<Boolean> pauseElytra;
   private final Setting<Boolean> pauseFly;
   public final Setting<Speed.LiquidMode> pauseWater;
   public final Setting<Speed.LiquidMode> pauseLava;
   private double velocity;
   private double yaw;
   private int og;
   private class_243 prevMovement;
   private boolean vanillaBoosted;
   private final List<Strafe.Offset> offsets;
   private long prevRubberband;
   private boolean waiting;
   private boolean setTimer;
   private final TimerList<Double> boosts;
   private double boostAmount;
   private final List<class_243> velocities;

   public Strafe() {
      super("Strafe", "Automatically jumps and moves fast.", SubCategory.MOVEMENT, true);
      this.useOffsets = this.sgGeneral.b("Use Offsets", false, "Uses offsets from blackout/strafe-offsets.txt");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.useOffsets;
      Objects.requireNonNull(var10005);
      this.offsetEffects = var10001.b("Offset Effects", true, "Uses offsets from blackout/strafe-offsets.txt", var10005::get);
      this.strictCollisions = this.sgGeneral.b("Strict Collisions", true, ".");
      this.onlyOG = this.sgGeneral.b("Only On Ground", false, ".");
      this.jumpPower = this.sgGeneral.d("Jump Power", 0.42D, 0.0D, 1.0D, 0.01D, ".");
      this.boost = this.sgGeneral.d("Boost", 0.36D, 0.0D, 1.0D, 0.01D, ".");
      this.resetBoost = this.sgGeneral.b("Reset Boost", true, ".");
      this.boostMulti = this.sgGeneral.d("Boost Multi", 1.6D, 0.0D, 5.0D, 0.05D, "Multiplies movement by x when jumping.");
      this.boostDiv = this.sgGeneral.d("Boost Div", 1.6D, 0.0D, 5.0D, 0.05D, "Divides movement by x after jumping.");
      this.effectMultiplier = this.sgGeneral.d("Effect Multiplier", 1.0D, 0.0D, 2.0D, 0.02D, ".");
      this.instantStop = this.sgGeneral.b("Instant Stop", false, ".");
      this.fastFall = this.sgFastFall.b("Fast Fall", false, ".");
      this.jumpTicks = this.sgFastFall.i("Jump Ticks", 3, 0, 20, 1, "Ticks off ground before fast falling.");
      this.fallSpeed = this.sgFastFall.i("Fall Speed", 0, 0, 10, 1, ".");
      this.spoofOG = this.sgFastFall.b("Spoof On Ground", false, ".");
      this.stopFall = this.sgFastFall.b("Stop Fall", false, ".");
      this.useTimer = this.sgTimer.b("Use Timer", false, ".");
      var10001 = this.sgTimer;
      var10005 = this.useTimer;
      Objects.requireNonNull(var10005);
      this.ncpTimer = var10001.b("Start NCP Timer", true, ".", var10005::get);
      this.timer = this.sgTimer.d("Start Timer", 1.08D, 0.5D, 2.0D, 0.01D, ".", () -> {
         return (Boolean)this.useTimer.get() && !(Boolean)this.ncpTimer.get();
      });
      var10001 = this.sgTimer;
      var10005 = this.useTimer;
      Objects.requireNonNull(var10005);
      this.endNcpTimer = var10001.b("End NCP Timer", true, ".", var10005::get);
      this.endTimer = this.sgTimer.d("End Timer", 1.08D, 0.5D, 2.0D, 0.01D, ".", () -> {
         return (Boolean)this.useTimer.get() && !(Boolean)this.endNcpTimer.get();
      });
      this.timerStartProgress = this.sgTimer.i("Timer Start Progress", 0, 0, 40, 1, ".");
      this.timerEndProgress = this.sgTimer.i("Timer End Progress", 10, 0, 40, 1, ".");
      this.ncpSpeed = this.sgLimit.b("NCP Min Speed", true, ".");
      this.minSpeed = this.sgLimit.d("Min Speed", 0.3D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return !(Boolean)this.ncpSpeed.get();
      });
      this.maxSpeed = this.sgLimit.d("Max Speed", 0.0D, 0.0D, 5.0D, 0.05D, ".");
      this.effectMaxSpeed = this.sgLimit.b("Effect Max Speed", true, "", () -> {
         return (Double)this.maxSpeed.get() > 0.0D;
      });
      this.maxDamageBoost = this.sgLimit.d("Max Damage Boost", 0.5D, 0.0D, 5.0D, 0.05D, ".");
      this.vanillaFriction = this.sgFriction.b("Vanilla Friction", true, ".");
      this.startFriction = this.sgFriction.d("Start Friction", 0.98D, 0.9D, 1.0D, 0.001D, ".", () -> {
         return !(Boolean)this.vanillaFriction.get();
      });
      this.endFriction = this.sgFriction.d("End Friction", 0.98D, 0.9D, 1.0D, 0.001D, ".", () -> {
         return !(Boolean)this.vanillaFriction.get();
      });
      this.startProgress = this.sgFriction.i("Start Progress", 0, 0, 40, 1, ".", () -> {
         return !(Boolean)this.vanillaFriction.get();
      });
      this.endProgress = this.sgFriction.i("End Progress", 10, 0, 40, 1, ".", () -> {
         return !(Boolean)this.vanillaFriction.get();
      });
      this.damageBoost = this.sgDamageBoost.b("Damage Boost", false, ".");
      this.addBoost = this.sgDamageBoost.b("Add Boost", false, ".");
      this.stackingBoost = this.sgDamageBoost.b("Stacking Boost", false, ".");
      this.directionalBoost = this.sgDamageBoost.b("Directional Boost", true, ".");
      this.boostFactor = this.sgDamageBoost.d("Boost Factor", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.boostTime = this.sgDamageBoost.d("Boost Time", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.latencyTicks = this.sgDamageBoost.i("Latency Ticks", 0, 0, 10, 1, ".");
      this.pauseSneak = this.sgPause.b("Pause Sneak", true, ".");
      this.pauseElytra = this.sgPause.b("Pause Elytra", true, ".");
      this.pauseFly = this.sgPause.b("Pause Fly", true, ".");
      this.pauseWater = this.sgPause.e("Pause Water", Speed.LiquidMode.Touching, ".", () -> {
         return true;
      });
      this.pauseLava = this.sgPause.e("Pause Lava", Speed.LiquidMode.Touching, ".", () -> {
         return true;
      });
      this.velocity = 0.0D;
      this.yaw = 0.0D;
      this.og = 0;
      this.prevMovement = class_243.field_1353;
      this.vanillaBoosted = false;
      this.offsets = new ArrayList();
      this.prevRubberband = 0L;
      this.waiting = false;
      this.setTimer = false;
      this.boosts = new TimerList(true);
      this.boostAmount = 0.0D;
      this.velocities = new ArrayList();
   }

   public void onEnable() {
      this.offsets.clear();
      this.velocity = this.prevMovement.method_37267();
      if ((Boolean)this.useOffsets.get()) {
         File file = FileUtils.getFile("strafe-offsets.txt");
         this.waiting = true;

         try {
            if (file.exists()) {
               FileUtils.readString(file).lines().forEach((line) -> {
                  String[] c = line.split(", ");
                  this.offsets.add(new Strafe.Offset(Float.parseFloat(c[0]), Float.parseFloat(c[1]), c[0].equals("-") ? null : Boolean.parseBoolean(c[2])));
               });
            }
         } catch (Exception var3) {
            ChatUtils.addMessage("error in reading strafe-offsets.txt");
         }

      }
   }

   public void onDisable() {
      if ((Boolean)this.useTimer.get()) {
         Timer.reset();
      }

   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null || BlackOut.mc.field_1687 != null) {
         this.prevMovement = BlackOut.mc.field_1724.method_19538().method_1023(BlackOut.mc.field_1724.field_6014, BlackOut.mc.field_1724.field_6036, BlackOut.mc.field_1724.field_5969);
         this.velocities.add(0, this.prevMovement);
         OLEPOSSUtils.limitList(this.velocities, 10);
         if ((Boolean)this.strictCollisions.get()) {
            this.velocity = this.prevMovement.method_37267() - this.boostAmount;
         }

      }
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled && !this.isPaused()) {
         if ((Boolean)this.strictCollisions.get() && BlackOut.mc.field_1724.field_5976) {
            this.boosts.clear();
         }

         if (!(Boolean)this.onlyOG.get() || BlackOut.mc.field_1724.method_24828()) {
            this.yaw = (double)(Managers.ROTATION.moveYaw + 90.0F);
         }

         if (Managers.ROTATION.move) {
            if ((Boolean)this.useTimer.get()) {
               Timer.set(this.getTimer());
               this.setTimer = true;
            }

            this.move(event);
         } else {
            if (this.setTimer) {
               Timer.reset();
               this.setTimer = false;
               this.boosts.clear();
            }

            if ((Boolean)this.instantStop.get()) {
               event.setXZ(this, 0.0D, 0.0D);
            }
         }

      } else {
         this.boosts.clear();
      }
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      if ((Boolean)this.damageBoost.get() && this.enabled) {
         if (event.packet instanceof class_2708) {
            this.velocity = 0.0D;
            this.prevRubberband = System.currentTimeMillis();
            this.boosts.clear();
         } else if (System.currentTimeMillis() - this.prevRubberband >= 1000L) {
            class_243 vel = null;
            class_2596 var4 = event.packet;
            if (var4 instanceof class_2743) {
               class_2743 packet = (class_2743)var4;
               if (BlackOut.mc.field_1724 == null || BlackOut.mc.field_1724.method_5628() != packet.method_11818()) {
                  return;
               }

               vel = (new class_243((double)((float)packet.method_11815() / 8000.0F), (double)((float)packet.method_11816() / 8000.0F), (double)((float)packet.method_11819() / 8000.0F))).method_1020(this.getVelocity());
            }

            var4 = event.packet;
            if (var4 instanceof class_2664) {
               class_2664 packet = (class_2664)var4;
               vel = new class_243((double)packet.method_11472(), (double)packet.method_11473(), (double)packet.method_11474());
            }

            if (vel != null) {
               double x = this.prevMovement.field_1352 / this.prevMovement.method_37267();
               double z = this.prevMovement.field_1350 / this.prevMovement.method_37267();
               double boost;
               if ((Boolean)this.directionalBoost.get()) {
                  double velX = Math.max(vel.field_1352 * x, 0.0D);
                  double velZ = Math.max(vel.field_1350 * z, 0.0D);
                  boost = Math.sqrt(velX * velX + velZ * velZ);
               } else {
                  boost = vel.method_1033();
               }

               this.boosts.add(this.limitBoost(boost * (Double)this.boostFactor.get()), (Double)this.boostTime.get());
            }

         }
      }
   }

   private class_243 getVelocity() {
      return !this.velocities.isEmpty() && (Integer)this.latencyTicks.get() != 0 ? (class_243)this.velocities.get(Math.min(this.velocities.size(), (Integer)this.latencyTicks.get()) - 1) : BlackOut.mc.field_1724.method_18798();
   }

   private void move(MoveEvent.Pre event) {
      this.updateVelocity();
      ++this.og;
      if (BlackOut.mc.field_1724.method_24828()) {
         this.waiting = false;
      }

      if (!this.updateOffsets(event)) {
         this.updateJump(event);
         if (!this.updateFastFall(event)) {
            this.setXZ(event);
         }

      }
   }

   private void updateVelocity() {
      this.velocity = Math.max(this.velocity * this.getFriction(), this.getMinSpeed());
      this.velocity = this.limitMax(this.velocity);
   }

   private boolean updateOffsets(MoveEvent.Pre event) {
      if (!this.waiting && (Boolean)this.useOffsets.get() && this.og < this.offsets.size()) {
         if (BlackOut.mc.field_1724.method_24828()) {
            this.og = 0;
         }

         Strafe.Offset offset = (Strafe.Offset)this.offsets.get(this.og);
         if (offset.og != null) {
            Managers.PACKET.spoofOG(offset.og);
         }

         this.velocity = (double)offset.xz;
         if ((Boolean)this.offsetEffects.get()) {
            this.velocity = MovementUtils.getSpeed(this.velocity, (Double)this.effectMultiplier.get());
         }

         double rad = Math.toRadians(this.yaw);
         double x = Math.cos(rad) * this.velocity;
         double z = Math.sin(rad) * this.velocity;
         event.set(this, x, (double)offset.y, z);
         return true;
      } else {
         return false;
      }
   }

   private boolean updateFastFall(MoveEvent.Pre event) {
      if (!(Boolean)this.fastFall.get()) {
         return false;
      } else {
         if (this.og == (Integer)this.jumpTicks.get() - 1 && (Boolean)this.spoofOG.get()) {
            Managers.PACKET.spoofOG(true);
         }

         if (this.og != (Integer)this.jumpTicks.get()) {
            return false;
         } else {
            event.setY(this, (double)(-(Integer)this.fallSpeed.get()));
            if ((Boolean)this.stopFall.get()) {
               event.setXZ(this, 0.0D, 0.0D);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void updateJump(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724.method_24828() && Step.getInstance().canStrafeJump(this.stepVelocity(event.movement.field_1351))) {
         event.setY(this, (double)((Double)this.jumpPower.get()).floatValue());
         this.og = 0;
         this.vanillaBoosted = true;
         this.velocity = this.getNewJumpVelocity();
      } else if (this.og == 1 && this.vanillaBoosted) {
         this.vanillaBoosted = false;
         this.velocity /= (Double)this.boostDiv.get();
      }

   }

   private class_243 stepVelocity(double y) {
      double movement;
      if ((Boolean)this.addBoost.get()) {
         movement = this.velocity + this.boostAmount;
      } else {
         movement = Math.max(this.velocity, this.boostAmount);
      }

      double x = Math.cos(Math.toRadians(this.yaw)) * movement;
      double z = Math.sin(Math.toRadians(this.yaw)) * movement;
      return new class_243(x, y, z);
   }

   private double getNewJumpVelocity() {
      double newVel = MovementUtils.getSpeed((Double)this.boost.get(), (Double)this.effectMultiplier.get());
      if (!(Boolean)this.resetBoost.get() && !(newVel > this.velocity)) {
         this.vanillaBoosted = false;
         return this.velocity;
      } else {
         return newVel * (Double)this.boostMulti.get();
      }
   }

   private void setXZ(MoveEvent.Pre event) {
      this.boostAmount = this.getBoost();
      Step step = Step.getInstance();
      if (!step.enabled || step.sinceStep > 1) {
         double movement;
         if ((Boolean)this.addBoost.get()) {
            movement = this.velocity + this.boostAmount;
         } else {
            movement = Math.max(this.velocity, this.boostAmount);
         }

         double x = Math.cos(Math.toRadians(this.yaw)) * movement;
         double z = Math.sin(Math.toRadians(this.yaw)) * movement;
         event.setXZ(this, x, z);
      }
   }

   private double getBoost() {
      MutableDouble mutableDouble = new MutableDouble(0.0D);
      this.boosts.forEach((timer) -> {
         double newVal;
         if ((Boolean)this.stackingBoost.get()) {
            newVal = this.limitBoost(mutableDouble.getValue() + (Double)timer.value);
         } else {
            newVal = Math.max(this.limitBoost((Double)timer.value), mutableDouble.getValue());
         }

         mutableDouble.setValue(newVal);
      });
      return mutableDouble.getValue();
   }

   private double limitBoost(double boost) {
      return (Double)this.maxDamageBoost.get() <= 0.0D ? boost : Math.min(boost, (Double)this.maxDamageBoost.get());
   }

   private double limitMax(double speed) {
      return (Double)this.maxSpeed.get() > 0.0D ? Math.min(speed, (Boolean)this.effectMaxSpeed.get() ? MovementUtils.getSpeed((Double)this.maxSpeed.get()) : (Double)this.maxSpeed.get()) : speed;
   }

   private double getMinSpeed() {
      double speed = (Boolean)this.ncpSpeed.get() ? 0.2873D : (Double)this.minSpeed.get();
      return MovementUtils.getSpeed(speed, (Double)this.effectMultiplier.get());
   }

   private float getTimer() {
      float start = (Boolean)this.ncpTimer.get() ? 1.088F : ((Double)this.timer.get()).floatValue();
      float end = (Boolean)this.endNcpTimer.get() ? 1.088F : ((Double)this.endTimer.get()).floatValue();
      return class_3532.method_37166(start, end, class_3532.method_37960((float)this.og, (float)(Integer)this.timerStartProgress.get(), (float)(Integer)this.timerEndProgress.get()));
   }

   private double getFriction() {
      if ((Boolean)this.vanillaFriction.get()) {
         return this.getVanillaFriction();
      } else {
         double l = OLEPOSSUtils.safeDivide((double)(this.og - (Integer)this.startProgress.get()), (double)((Integer)this.endProgress.get() - (Integer)this.startProgress.get()));
         return class_3532.method_15390((Double)this.startFriction.get(), (Double)this.endFriction.get(), l);
      }
   }

   private double getVanillaFriction() {
      class_2338 blockPos = BlackOut.mc.field_1724.method_23314();
      double slipperiness = (double)BlackOut.mc.field_1687.method_8320(blockPos).method_26204().method_9499();
      return BlackOut.mc.field_1724.method_24828() ? 0.2160000205039978D / (slipperiness * slipperiness * slipperiness) : 0.98D;
   }

   private boolean isPaused() {
      if ((Boolean)this.pauseSneak.get() && BlackOut.mc.field_1724.method_5715()) {
         return true;
      } else if ((Boolean)this.pauseElytra.get() && BlackOut.mc.field_1724.method_6128()) {
         return true;
      } else if ((Boolean)this.pauseFly.get() && BlackOut.mc.field_1724.method_31549().field_7479) {
         return true;
      } else {
         switch((Speed.LiquidMode)this.pauseWater.get()) {
         case Touching:
            if (BlackOut.mc.field_1724.method_5799()) {
               return true;
            }
            break;
         case Submerged:
            if (BlackOut.mc.field_1724.method_5777(class_3486.field_15517)) {
               return true;
            }
            break;
         case Both:
            if (BlackOut.mc.field_1724.method_5799() || BlackOut.mc.field_1724.method_5777(class_3486.field_15517)) {
               return true;
            }
         }

         boolean var10000;
         switch((Speed.LiquidMode)this.pauseLava.get()) {
         case Touching:
            var10000 = BlackOut.mc.field_1724.method_5771();
            break;
         case Submerged:
            var10000 = BlackOut.mc.field_1724.method_5777(class_3486.field_15518);
            break;
         case Both:
            var10000 = BlackOut.mc.field_1724.method_5771() || BlackOut.mc.field_1724.method_5777(class_3486.field_15518);
            break;
         default:
            var10000 = false;
         }

         return var10000;
      }
   }

   private static record Offset(float xz, float y, Boolean og) {
      private Offset(float xz, float y, Boolean og) {
         this.xz = xz;
         this.y = y;
         this.og = og;
      }

      public float xz() {
         return this.xz;
      }

      public float y() {
         return this.y;
      }

      public Boolean og() {
         return this.og;
      }
   }
}
