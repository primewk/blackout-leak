package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.HoleType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.Hole;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import java.util.Objects;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2708;

public class HoleSnap extends Module {
   private static HoleSnap INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   private final SettingGroup sgHole = this.addGroup("Hole");
   private final Setting<Boolean> jump;
   private final Setting<Integer> jumpCooldown;
   private final Setting<Boolean> step;
   private final Setting<Boolean> fastFall;
   private final Setting<Double> range;
   private final Setting<Double> downRange;
   private final Setting<Integer> maxCollisions;
   private final Setting<Integer> maxRubberbands;
   private final Setting<Double> speed;
   private final Setting<Boolean> boost;
   private final Setting<Double> boostSpeed;
   private final Setting<Integer> boostTicks;
   private final Setting<Double> timer;
   private final Setting<Boolean> singleTarget;
   private final Setting<Integer> depth;
   private final Setting<Boolean> singleHoles;
   private final Setting<Boolean> doubleHoles;
   private final Setting<Boolean> quadHoles;
   private Hole singleHole;
   private int collisions;
   private int rubberbands;
   private int ticks;
   private int boostLeft;

   public HoleSnap() {
      super("Hole Snap", "For the times when you cant even press W.", SubCategory.MOVEMENT, true);
      this.jump = this.sgGeneral.b("Jump", false, "Jumps to the hole (very useful).");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.jump;
      Objects.requireNonNull(var10008);
      this.jumpCooldown = var10001.i("Jump Cooldown", 5, 0, 20, 1, "Ticks between jumps.", var10008::get);
      this.step = this.sgGeneral.b("Use Step", false, ".");
      this.fastFall = this.sgGeneral.b("Use Fast Fall", false, ".");
      this.range = this.sgGeneral.d("Range", 3.0D, 0.0D, 5.0D, 0.1D, "Horizontal range for finding holes.");
      this.downRange = this.sgGeneral.d("Down Range", 3.0D, 0.0D, 5.0D, 0.1D, "Vertical range for finding holes.");
      this.maxCollisions = this.sgGeneral.i("Max Collisions", 15, 0, 100, 1, "Disabled after this many collisions. 0 = doesn't disable.");
      this.maxRubberbands = this.sgGeneral.i("Max Rubberbands", 1, 0, 100, 1, "Disabled after this many rubberbands. 0 = doesn't disable.");
      this.speed = this.sgSpeed.d("Speed", 0.2873D, 0.0D, 1.0D, 0.01D, "Movement speed.");
      this.boost = this.sgSpeed.b("Boost", false, "Increases movement speed for a few ticks.");
      var10001 = this.sgSpeed;
      var10008 = this.boost;
      Objects.requireNonNull(var10008);
      this.boostSpeed = var10001.d("Boost Speed", 0.5D, 0.0D, 1.0D, 0.01D, "Movement speed while boosted.", var10008::get);
      var10001 = this.sgSpeed;
      var10008 = this.boost;
      Objects.requireNonNull(var10008);
      this.boostTicks = var10001.i("Boost Ticks", 3, 0, 10, 1, "Stops boosting after this many ticks.", var10008::get);
      this.timer = this.sgSpeed.d("Timer", 1.0D, 1.0D, 10.0D, 0.04D, "Sends packets faster.");
      this.singleTarget = this.sgHole.b("Single Target", false, "Only chooses target hole once.");
      this.depth = this.sgHole.i("Hole Depth", 3, 1, 5, 1, "How deep a hole has to be.");
      this.singleHoles = this.sgHole.b("Single Holes", true, "Targets 1x1 holes.");
      this.doubleHoles = this.sgHole.b("Double Holes", true, "Targets 1x2 holes.");
      this.quadHoles = this.sgHole.b("Quad Holes", true, "Targets 2x2 holes.");
      this.boostLeft = 0;
      INSTANCE = this;
   }

   public static HoleSnap getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.singleHole = this.findHole();
      this.rubberbands = 0;
      this.ticks = 0;
      this.boostLeft = (Boolean)this.boost.get() ? (Integer)this.boostTicks.get() : 0;
   }

   public void onDisable() {
      Timer.reset();
   }

   @Event
   public void onPacket(PacketEvent.Receive.Pre event) {
      if (event.packet instanceof class_2708 && (Integer)this.maxRubberbands.get() > 0 && ++this.rubberbands >= (Integer)this.maxRubberbands.get() && (Integer)this.maxRubberbands.get() > 0) {
         this.disable(this.getDisplayName() + " disabled, rubberbanded " + this.rubberbands + " times", 2, Notifications.Type.Alert);
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && event.xzValue <= 9) {
         Hole hole = (Boolean)this.singleTarget.get() ? this.singleHole : this.findHole();
         if (hole != null && !this.singleBlocked()) {
            Timer.set(((Double)this.timer.get()).floatValue());
            double yaw = Math.cos(Math.toRadians((double)(this.getAngle(hole.middle) + 90.0F)));
            double pit = Math.sin(Math.toRadians((double)(this.getAngle(hole.middle) + 90.0F)));
            if (BlackOut.mc.field_1724.method_23317() == hole.middle.field_1352 && BlackOut.mc.field_1724.method_23321() == hole.middle.field_1350) {
               if (BlackOut.mc.field_1724.method_23318() <= hole.middle.field_1351) {
                  this.disable(this.getDisplayName() + " disabled, in hole");
                  ((IVec3d)event.movement).blackout_Client$setXZ(0.0D, 0.0D);
               } else if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(0.0D, -0.05D, 0.0D))) {
                  this.disable(this.getDisplayName() + " hole unreachable, disabling", 2, Notifications.Type.Alert);
               } else {
                  event.setXZ(this, 0.0D, 0.0D);
               }
            } else {
               double x = this.getSpeed() * yaw;
               double dX = hole.middle.field_1352 - BlackOut.mc.field_1724.method_23317();
               double z = this.getSpeed() * pit;
               double dZ = hole.middle.field_1350 - BlackOut.mc.field_1724.method_23321();
               if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(x, 0.0D, z))) {
                  ++this.collisions;
                  if (this.collisions >= (Integer)this.maxCollisions.get() && (Integer)this.maxCollisions.get() > 0) {
                     this.disable(this.getDisplayName() + " disabled, collided " + this.collisions + " times", 2, Notifications.Type.Alert);
                  }
               } else {
                  this.collisions = 0;
               }

               if (this.ticks > 0) {
                  --this.ticks;
               } else if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(0.0D, -0.05D, 0.0D)) && (Boolean)this.jump.get()) {
                  this.ticks = (Integer)this.jumpCooldown.get();
                  event.setY(this, 0.42D);
               }

               --this.boostLeft;
               event.setXZ(this, Math.abs(x) < Math.abs(dX) ? x : dX, Math.abs(z) < Math.abs(dZ) ? z : dZ);
            }
         } else {
            this.disable("No hole was found disabling " + this.getDisplayName(), 2, Notifications.Type.Alert);
         }
      }

   }

   private boolean singleBlocked() {
      if (!(Boolean)this.singleTarget.get()) {
         return false;
      } else {
         class_2338[] var1 = this.singleHole.positions;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            class_2338 pos = var1[var3];
            if (OLEPOSSUtils.collidable(pos)) {
               return true;
            }
         }

         return false;
      }
   }

   private Hole findHole() {
      Hole closest = null;
      int r = (int)Math.ceil((Double)this.range.get());

      for(int x = -r; x <= r; ++x) {
         for(int y = (int)(-Math.ceil((Double)this.downRange.get())); y < 1; ++y) {
            for(int z = -r; z <= r; ++z) {
               class_2338 pos = OLEPOSSUtils.roundedPos().method_10069(x, y, z);
               Hole hole = HoleUtils.getHole(pos, (Boolean)this.singleHoles.get(), (Boolean)this.doubleHoles.get(), (Boolean)this.quadHoles.get(), (Integer)this.depth.get(), true);
               if (hole.type != HoleType.NotHole) {
                  if (y == 0 && this.inHole(hole)) {
                     return hole;
                  }

                  if (closest == null || hole.middle.method_1020(BlackOut.mc.field_1724.method_19538()).method_37268() < closest.middle.method_1020(BlackOut.mc.field_1724.method_19538()).method_37268()) {
                     closest = hole;
                  }
               }
            }
         }
      }

      return closest;
   }

   private boolean inHole(Hole hole) {
      class_2338[] var2 = hole.positions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2338 pos = var2[var4];
         if (BlackOut.mc.field_1724.method_24515().equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private float getAngle(class_243 pos) {
      return (float)RotationUtils.getYaw(pos);
   }

   private double getSpeed() {
      return this.boostLeft > 0 ? (Double)this.boostSpeed.get() : (Double)this.speed.get();
   }

   public boolean shouldStep() {
      return this.enabled && (Boolean)this.step.get();
   }

   public boolean shouldFastFall() {
      return this.enabled && (Boolean)this.fastFall.get();
   }
}
