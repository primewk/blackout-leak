package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.MovementUtils;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2708;
import net.minecraft.class_2743;
import org.apache.commons.lang3.mutable.MutableDouble;

public class LongJump extends Module {
   private static LongJump INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgDamageBoost = this.addGroup("Damage Boost");
   private final Setting<Double> jumpPower;
   private final Setting<Double> boost;
   private final Setting<Double> timer;
   private final Setting<Boolean> effects;
   private final Setting<Double> friction;
   private final Setting<Integer> chargeTicks;
   private final Setting<Double> chargeMotion;
   private final Setting<Boolean> chargeSprint;
   private final Setting<Integer> jumps;
   private final Setting<Double> boostMulti;
   private final Setting<Double> boostDiv;
   private final Setting<Double> effectMultiplier;
   private final Setting<Boolean> ncpSpeed;
   private final Setting<Double> minSpeed;
   private final Setting<Boolean> damageBoost;
   private final Setting<Boolean> stackingBoost;
   private final Setting<Boolean> directionalBoost;
   private final Setting<Double> boostFactor;
   private final Setting<Double> boostTime;
   private final Setting<Double> maxDamageBoost;
   private int phase;
   private class_243 prevMovement;
   private int ticks;
   private int jumped;
   private boolean changedTimer;
   private final TimerList<Double> boosts;
   private long prevRubberband;

   public LongJump() {
      super("Long Jump", "Jumps but long.", SubCategory.MOVEMENT, true);
      this.jumpPower = this.sgGeneral.d("Jump Power", 0.424D, 0.38D, 0.44D, 0.001D, ".");
      this.boost = this.sgGeneral.d("Boost", 1.0D, 0.0D, 3.0D, 0.01D, ".");
      this.timer = this.sgGeneral.d("Timer", 1.0D, 0.05D, 10.0D, 0.05D, ".");
      this.effects = this.sgGeneral.b("Effects", true, ".");
      this.friction = this.sgGeneral.d("Friction", 0.93D, 0.8D, 1.0D, 0.001D, ".");
      this.chargeTicks = this.sgGeneral.i("Charge Ticks", 5, 0, 20, 1, ".");
      this.chargeMotion = this.sgGeneral.d("Charge Motion", 0.05D, 0.0D, 1.0D, 0.01D, ".");
      this.chargeSprint = this.sgGeneral.b("Charge Sprint", true, ".");
      this.jumps = this.sgGeneral.i("Jumps", 1, 0, 20, 1, ".");
      this.boostMulti = this.sgGeneral.d("Boost Multi", 1.6D, 0.0D, 5.0D, 0.05D, "Multiplies movement by x when jumping.");
      this.boostDiv = this.sgGeneral.d("Boost Div", 1.6D, 0.0D, 5.0D, 0.05D, "Divides movement by x after jumping.");
      this.effectMultiplier = this.sgGeneral.d("Effect Multiplier", 1.0D, 0.0D, 2.0D, 0.02D, ".");
      this.ncpSpeed = this.sgGeneral.b("NCP Min Speed", true, ".");
      this.minSpeed = this.sgGeneral.d("Min Speed", 0.3D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return !(Boolean)this.ncpSpeed.get();
      });
      this.damageBoost = this.sgDamageBoost.b("Damage Boost", false, ".");
      this.stackingBoost = this.sgDamageBoost.b("Stacking Boost", false, ".");
      this.directionalBoost = this.sgDamageBoost.b("Directional Boost", true, ".");
      this.boostFactor = this.sgDamageBoost.d("Boost Factor", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.boostTime = this.sgDamageBoost.d("Boost Time", 0.5D, 0.0D, 2.0D, 0.02D, ".");
      this.maxDamageBoost = this.sgDamageBoost.d("Max Damage Boost", 0.5D, 0.0D, 5.0D, 0.05D, ".");
      this.phase = 0;
      this.prevMovement = new class_243(0.0D, 0.0D, 0.0D);
      this.ticks = 0;
      this.jumped = 0;
      this.changedTimer = false;
      this.boosts = new TimerList(true);
      this.prevRubberband = 0L;
      INSTANCE = this;
   }

   public static LongJump getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.phase = 0;
      this.jumped = 0;
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Double)this.timer.get() != 1.0D && this.enabled) {
            Timer.set(((Double)this.timer.get()).floatValue());
            this.changedTimer = true;
         }

         this.prevMovement = BlackOut.mc.field_1724.method_19538().method_1023(BlackOut.mc.field_1724.field_6014, BlackOut.mc.field_1724.field_6036, BlackOut.mc.field_1724.field_5969);
         if (BlackOut.mc.field_1724.method_5799()) {
            this.disable(this.getDisplayName() + " disabled, touching water");
         }

      }
   }

   @Event
   public void onPacket(PacketEvent.Receive.Pre event) {
      if (event.packet instanceof class_2708) {
         if (this.enabled) {
            this.disable(this.getDisplayName() + " was disabled to prevent rubberbanding", 4, Notifications.Type.Alert);
         }

         this.prevRubberband = System.currentTimeMillis();
         this.boosts.clear();
      } else if ((Boolean)this.damageBoost.get()) {
         if (System.currentTimeMillis() - this.prevRubberband >= 1000L) {
            class_243 vel = null;
            class_2596 var4 = event.packet;
            if (var4 instanceof class_2743) {
               class_2743 packet = (class_2743)var4;
               if (BlackOut.mc.field_1724 == null || BlackOut.mc.field_1724.method_5628() != packet.method_11818()) {
                  return;
               }

               vel = (new class_243((double)((float)packet.method_11815() / 8000.0F), (double)((float)packet.method_11816() / 8000.0F), (double)((float)packet.method_11819() / 8000.0F))).method_1020(BlackOut.mc.field_1724.method_18798());
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

   public void onDisable() {
      if (this.changedTimer) {
         Timer.reset();
         this.changedTimer = false;
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (this.enabled && BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.phase == 0 && BlackOut.mc.field_1724.method_24828()) {
            this.phase = 1;
            this.ticks = (Integer)this.chargeTicks.get();
         }

         if (this.phase == 1) {
            if (--this.ticks < 0) {
               this.phase = 2;
            } else {
               event.setXZ(this, MovementUtils.xMovement((Double)this.chargeMotion.get(), (double)BlackOut.mc.field_1724.method_36454()), MovementUtils.zMovement((Double)this.chargeMotion.get(), (double)BlackOut.mc.field_1724.method_36454()));
               if ((Boolean)this.chargeSprint.get()) {
                  BlackOut.mc.field_1724.method_5728(true);
               }
            }
         }

         if (this.phase >= 2) {
            double velocity;
            if (this.phase == 2) {
               velocity = (Boolean)this.effects.get() ? MovementUtils.getSpeed((Double)this.boost.get(), (Double)this.effectMultiplier.get()) : (Double)this.boost.get();
               velocity += this.getBoost();
               velocity *= (Double)this.boostMulti.get();
               event.set(this, MovementUtils.xMovement(velocity, (double)BlackOut.mc.field_1724.method_36454()), (double)((Double)this.jumpPower.get()).floatValue(), MovementUtils.zMovement(velocity, (double)BlackOut.mc.field_1724.method_36454()));
               ++this.jumped;
               this.phase = 3;
            } else {
               if (BlackOut.mc.field_1724.method_24828()) {
                  if (this.jumped >= (Integer)this.jumps.get() && (Integer)this.jumps.get() != 0) {
                     this.disable(this.getDisplayName() + " was disabled due to landing");
                  } else {
                     this.phase = 0;
                  }
               }

               velocity = this.prevMovement.method_37267() * (Double)this.friction.get();
               if (this.phase == 3) {
                  velocity /= (Double)this.boostDiv.get();
                  this.phase = 4;
               }

               velocity = Math.max(velocity, this.getMinSpeed());
               double yaw = Math.toRadians((double)(BlackOut.mc.field_1724.method_36454() + 90.0F));
               event.setXZ(this, velocity * Math.cos(yaw), velocity * Math.sin(yaw));
            }
         }
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

   private double getMinSpeed() {
      return (Boolean)this.ncpSpeed.get() ? 0.2873D : (Double)this.minSpeed.get();
   }
}
