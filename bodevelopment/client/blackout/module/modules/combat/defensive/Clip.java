package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.movement.PacketFly;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.util.MovementUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_2828.class_2829;

public class Clip extends Module {
   private static Clip INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgBounds = this.addGroup("Bounds");
   private final Setting<Clip.OffsetMode> offset;
   private final Setting<Integer> movementDelay;
   private final Setting<Double> movement;
   private final Setting<Boolean> pauseMove;
   private final Setting<Integer> stillTicks;
   public final Setting<Boolean> stopRotation;
   private final Setting<Boolean> bounds;
   private final Setting<Boolean> spamBounds;
   private final Setting<PacketFly.BoundsMode> boundsMode;
   private final Setting<Boolean> setXZ;
   public final Setting<Integer> xzBound;
   private final Setting<Boolean> setY;
   public final Setting<Integer> yBound;
   private double cornerX;
   private double cornerZ;
   public boolean shouldCancel;
   private int ticksStill;
   private int timer;
   private boolean shouldBounds;
   public int noRotateTime;

   public Clip() {
      super("Clip", "Moves you inside a corner to protect from damage.", SubCategory.DEFENSIVE, true);
      this.offset = this.sgGeneral.e("Offset", Clip.OffsetMode.NODamage, ".");
      this.movementDelay = this.sgGeneral.i("Movement Delay", 10, 0, 20, 1, "How many ticks to wait betweeen movements.");
      this.movement = this.sgGeneral.d("Movement", 0.06D, 0.0D, 0.1D, 0.001D, "How many blocks to move eact time.");
      this.pauseMove = this.sgGeneral.b("Pause Move", true, ".");
      this.stillTicks = this.sgGeneral.i("Still Ticks", 5, 0, 50, 1, ".");
      this.stopRotation = this.sgGeneral.b("Stop Rotation", true, ".");
      this.bounds = this.sgBounds.b("Bounds", true, ".");
      this.spamBounds = this.sgBounds.b("Spam Bounds", false, ".");
      this.boundsMode = this.sgBounds.e("Bounds Mode", PacketFly.BoundsMode.Add, "Spoofs on ground.");
      this.setXZ = this.sgBounds.b("Set XZ", false, "Doesn't move horizontally and vertically in the same packet.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Set;
      });
      this.xzBound = this.sgBounds.i("XZ Bound", 0, -1337, 1337, 1, "Bounds offset horizontally.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Add || (Boolean)this.setXZ.get();
      });
      this.setY = this.sgBounds.b("Set Y", true, "Doesn't move horizontally and vertically in the same packet.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Set;
      });
      this.yBound = this.sgBounds.i("Y Bound", -87, -1337, 1337, 1, "Bounds offset vertically.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Add || (Boolean)this.setY.get();
      });
      this.cornerX = 0.0D;
      this.cornerZ = 0.0D;
      this.shouldCancel = false;
      this.ticksStill = 0;
      this.timer = 0;
      this.shouldBounds = false;
      this.noRotateTime = 0;
      INSTANCE = this;
   }

   public static Clip getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.timer = 0;
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      this.shouldCancel = false;
      --this.noRotateTime;
      double currentX = BlackOut.mc.field_1724.method_23317() - (double)BlackOut.mc.field_1724.method_31477();
      double currentZ = BlackOut.mc.field_1724.method_23321() - (double)BlackOut.mc.field_1724.method_31479();
      if (!(Boolean)this.pauseMove.get() || BlackOut.mc.field_1724.field_3913.field_3905 == 0.0F && BlackOut.mc.field_1724.field_3913.field_3907 == 0.0F) {
         if (!this.findCorner(currentX, currentZ, new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479()))) {
            double targetX = class_3532.method_16436(this.cornerX, 0.3D, 0.7D);
            double targetZ = class_3532.method_16436(this.cornerZ, 0.3D, 0.7D);
            double centerX = currentX - 0.5D;
            double centerZ = currentZ - 0.5D;
            if (Math.abs(centerX) >= 0.19999D && Math.signum(centerX) == Math.signum(targetX - 0.5D) && Math.abs(centerZ) >= 0.19999D && Math.signum(centerZ) == Math.signum(targetZ - 0.5D)) {
               this.wallMove(event, targetX, targetZ, currentX, currentZ);
            } else {
               this.outMove(event, targetX, targetZ, currentX, currentZ);
            }

         }
      }
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (this.shouldBounds) {
         this.shouldBounds = false;
         if ((Boolean)this.bounds.get()) {
            this.sendBounds();
         }
      }

   }

   private void wallMove(MoveEvent.Pre event, double targetX, double targetZ, double currentX, double currentZ) {
      if (this.ticksStill-- > 0) {
         this.noRotateTime = 5;
         event.setXZ(this, 0.0D, 0.0D);
      } else {
         double depth = ((Clip.OffsetMode)this.offset.get()).depth;
         targetX -= Math.signum(0.5D - targetX) * depth;
         targetZ -= Math.signum(0.5D - targetZ) * depth;
         double yaw = class_3532.method_15338(Math.toDegrees(Math.atan2(targetZ - currentZ, targetX - currentX)) - 90.0D);
         double x = Math.cos(Math.toRadians(yaw + 90.0D));
         double z = Math.sin(Math.toRadians(yaw + 90.0D));
         double dx = currentX - targetX;
         double dz = currentZ - targetZ;
         double dist = Math.min(Math.sqrt(dx * dx + dz * dz), (Double)this.movement.get());
         double mx = dist * x;
         double mz = dist * z;
         if (dist > 0.001D) {
            this.noRotateTime = 5;
            if (--this.timer < 0) {
               this.timer = (Integer)this.movementDelay.get();
               double ox = (double)BlackOut.mc.field_1724.method_31477() + currentX + mx;
               double oz = (double)BlackOut.mc.field_1724.method_31479() + currentZ + mz;
               BlackOut.mc.field_1724.method_23327(ox, BlackOut.mc.field_1724.method_23318(), oz);
               this.sendPos(ox, oz, true);
               event.setXZ(this, 0.0D, 0.0D);
               this.shouldBounds = true;
               this.shouldCancel = true;
            } else {
               this.shouldBounds = (Boolean)this.spamBounds.get();
            }
         } else {
            event.setXZ(this, 0.0D, 0.0D);
         }

      }
   }

   private void outMove(MoveEvent.Pre event, double targetX, double targetZ, double currentX, double currentZ) {
      this.ticksStill = (Integer)this.stillTicks.get();
      double yaw = class_3532.method_15338(Math.toDegrees(Math.atan2(targetZ - currentZ, targetX - currentX)) - 90.0D);
      double x = Math.cos(Math.toRadians(yaw + 90.0D));
      double z = Math.sin(Math.toRadians(yaw + 90.0D));
      double dx = currentX - targetX;
      double dz = currentZ - targetZ;
      double dist = Math.min(Math.sqrt(dx * dx + dz * dz), MovementUtils.getSpeed(0.2873D));
      event.setXZ(this, dist * x, dist * z);
   }

   private void sendBounds() {
      class_243 bounds = this.getBounds();
      this.sendPacket(new class_2829(bounds.field_1352, bounds.field_1351, bounds.field_1350, Managers.PACKET.isOnGround()));
   }

   private class_243 getBounds() {
      double yaw = Math.random() * 3.141592653589793D * 2.0D;
      double x = 0.0D;
      double y = 0.0D;
      double z = 0.0D;
      switch((PacketFly.BoundsMode)this.boundsMode.get()) {
      case Add:
         x = BlackOut.mc.field_1724.method_23317() + Math.cos(yaw) * (double)(Integer)this.xzBound.get();
         y = BlackOut.mc.field_1724.method_23318() + (double)(Integer)this.yBound.get();
         z = BlackOut.mc.field_1724.method_23321() + Math.sin(yaw) * (double)(Integer)this.xzBound.get();
         break;
      case Set:
         if ((Boolean)this.setXZ.get()) {
            x = Math.cos(yaw) * (double)(Integer)this.xzBound.get();
            z = Math.sin(yaw) * (double)(Integer)this.xzBound.get();
         } else {
            x = BlackOut.mc.field_1724.method_23317();
            z = BlackOut.mc.field_1724.method_23321();
         }

         if ((Boolean)this.setY.get()) {
            y = (double)(Integer)this.yBound.get();
         } else {
            y = BlackOut.mc.field_1724.method_23318();
         }
      }

      return new class_243(x, y, z);
   }

   private void sendPos(double x, double z, boolean onGround) {
      this.sendPacket(new class_2829(x, BlackOut.mc.field_1724.method_23318(), z, onGround));
   }

   private boolean findCorner(double x, double z, class_2338 pos) {
      boolean minX = !OLEPOSSUtils.replaceable(pos.method_10093(class_2350.field_11039));
      boolean minZ = !OLEPOSSUtils.replaceable(pos.method_10093(class_2350.field_11043));
      boolean maxX = !OLEPOSSUtils.replaceable(pos.method_10093(class_2350.field_11034));
      boolean maxZ = !OLEPOSSUtils.replaceable(pos.method_10093(class_2350.field_11035));
      List<Pair<Double, Double>> corners = new ArrayList();
      if (minX) {
         if (minZ) {
            corners.add(new Pair(0.0D, 0.0D));
         }

         if (maxZ) {
            corners.add(new Pair(0.0D, 1.0D));
         }
      }

      if (maxX) {
         if (minZ) {
            corners.add(new Pair(1.0D, 0.0D));
         }

         if (maxZ) {
            corners.add(new Pair(1.0D, 1.0D));
         }
      }

      if (corners.isEmpty()) {
         return true;
      } else {
         double distC = 1000.0D;
         Iterator var13 = corners.iterator();

         while(var13.hasNext()) {
            Pair<Double, Double> pair = (Pair)var13.next();
            double dx = x - (Double)pair.method_15442();
            double dz = z - (Double)pair.method_15441();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < distC) {
               distC = dist;
               this.set((Double)pair.method_15442(), (Double)pair.method_15441());
            }
         }

         return false;
      }
   }

   private void set(double x, double z) {
      this.cornerX = x;
      this.cornerZ = z;
   }

   public static enum OffsetMode {
      Damage(0.0624D),
      NODamage(0.059D),
      Semi(0.03D),
      NewVer(1.0E-8D);

      private final double depth;

      private OffsetMode(double depth) {
         this.depth = depth;
      }

      // $FF: synthetic method
      private static Clip.OffsetMode[] $values() {
         return new Clip.OffsetMode[]{Damage, NODamage, Semi, NewVer};
      }
   }

   public static enum Mode {
      Move,
      Manual;

      // $FF: synthetic method
      private static Clip.Mode[] $values() {
         return new Clip.Mode[]{Move, Manual};
      }
   }
}
