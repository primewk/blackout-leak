package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.MovementUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;

public class PacketFly extends Module {
   private static PacketFly INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgFly = this.addGroup("Fly");
   private final SettingGroup sgPhase = this.addGroup("Phase");
   private final Setting<Boolean> onGroundSpoof;
   private final Setting<Boolean> onGround;
   private final Setting<PacketFly.BoundsMode> boundsMode;
   private final Setting<Boolean> setXZ;
   public final Setting<Integer> xzBound;
   private final Setting<Boolean> setY;
   public final Setting<Integer> yBound;
   private final Setting<Boolean> strictVertical;
   private final Setting<Boolean> syncPacket;
   private final Setting<Boolean> noClip;
   private final Setting<Boolean> resync;
   private final Setting<Double> packets;
   private final Setting<Double> flySpeed;
   private final Setting<Boolean> fastVertical;
   private final Setting<Double> downSpeed;
   private final Setting<Double> upSpeed;
   private final Setting<Boolean> flyEffects;
   private final Setting<Boolean> flyRotate;
   private final Setting<Boolean> stillFlyRotate;
   private final Setting<Boolean> antiKick;
   private final Setting<Double> antiKickAmount;
   private final Setting<Integer> antiKickDelay;
   private final Setting<Double> phasePackets;
   private final Setting<Double> phaseSpeed;
   private final Setting<Boolean> phaseFastVertical;
   private final Setting<Double> phaseDownSpeed;
   private final Setting<Double> phaseUpSpeed;
   private final Setting<Boolean> phaseEffects;
   private final Setting<Boolean> phaseRotate;
   private final Setting<Boolean> stillPhaseRotate;
   private final Setting<Boolean> sneakPhase;
   private int ticks;
   private int sent;
   private int rur;
   private double packetsToSend;
   private final Random random;
   private String info;
   private final List<class_2828> validPackets;
   private class_243 offset;
   public boolean moving;
   private boolean sneaked;

   public PacketFly() {
      super("PacketFly", "Flies using packets", SubCategory.MOVEMENT, true);
      this.onGroundSpoof = this.sgGeneral.b("On Ground Spoof", false, "Spoofs on ground.");
      this.onGround = this.sgGeneral.b("On Ground", false, "Should we tell the server that you are on ground.");
      this.boundsMode = this.sgGeneral.e("Bounds Mode", PacketFly.BoundsMode.Add, "Spoofs on ground.");
      this.setXZ = this.sgGeneral.b("Set XZ", false, "Doesn't move horizontally and vertically in the same packet.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Set;
      });
      this.xzBound = this.sgGeneral.i("XZ Bound", 0, -1337, 1337, 1, "Bounds offset horizontally.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Add || (Boolean)this.setXZ.get();
      });
      this.setY = this.sgGeneral.b("Set Y", true, "Doesn't move horizontally and vertically in the same packet.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Set;
      });
      this.yBound = this.sgGeneral.i("Y Bound", -87, -1337, 1337, 1, "Bounds offset vertically.", () -> {
         return this.boundsMode.get() == PacketFly.BoundsMode.Add || (Boolean)this.setY.get();
      });
      this.strictVertical = this.sgGeneral.b("Strict Vertical", false, "Doesn't move horizontally and vertically in the same packet.");
      this.syncPacket = this.sgGeneral.b("Sync Packet", false, ".");
      this.noClip = this.sgGeneral.b("No Clip", true, ".");
      this.resync = this.sgGeneral.b("Resync", true, ".");
      this.packets = this.sgFly.d("Fly Packets", 1.0D, 0.0D, 10.0D, 0.1D, "How many packets to send every movement tick.");
      this.flySpeed = this.sgFly.d("Fly Speed", 0.2873D, 0.0D, 1.0D, 0.001D, "Distance to travel each packet.");
      this.fastVertical = this.sgFly.b("Fast Vertical Fly", false, "Sends multiple packets every movement tick while going up.");
      this.downSpeed = this.sgFly.d("Fly Down Speed", 0.062D, 0.0D, 10.0D, 0.01D, "How fast to fly down.");
      this.upSpeed = this.sgFly.d("Fly Up Speed", 0.062D, 0.0D, 10.0D, 0.01D, "How fast to fly up.");
      this.flyEffects = this.sgFly.b("Fly Effects", true, ".");
      this.flyRotate = this.sgFly.b("Fly Rotate", true, "Allows rotating while phasing.");
      SettingGroup var10001 = this.sgFly;
      Setting var10005 = this.flyRotate;
      Objects.requireNonNull(var10005);
      this.stillFlyRotate = var10001.b("Still Fly Rotate", true, ".", var10005::get);
      this.antiKick = this.sgFly.b("Anti-Kick", false, "Slowly falls down.");
      var10001 = this.sgFly;
      Setting var10008 = this.antiKick;
      Objects.requireNonNull(var10008);
      this.antiKickAmount = var10001.d("Anti-Kick Multiplier", 1.0D, 0.0D, 10.0D, 1.0D, "Fall speed multiplier for antikick (0.04 blocks * multiplier).", var10008::get);
      var10001 = this.sgFly;
      var10008 = this.antiKick;
      Objects.requireNonNull(var10008);
      this.antiKickDelay = var10001.i("Anti-Kick Delay", 10, 1, 100, 1, "Tick delay between moving anti kick packets.", var10008::get);
      this.phasePackets = this.sgPhase.d("Phase Packets", 1.0D, 0.0D, 10.0D, 0.1D, "How many packets to send every movement tick.");
      this.phaseSpeed = this.sgPhase.d("Phase Speed", 0.062D, 0.0D, 1.0D, 0.001D, "Distance to travel each packet.");
      this.phaseFastVertical = this.sgPhase.b("Fast Vertical Phase", false, "Sends multiple packets every movement tick while going up.");
      this.phaseDownSpeed = this.sgPhase.d("Phase Down Speed", 0.062D, 0.0D, 10.0D, 0.01D, "How fast to phase down.");
      this.phaseUpSpeed = this.sgPhase.d("Phase Up Speed", 0.062D, 0.0D, 10.0D, 0.01D, "How fast to phase up.");
      this.phaseEffects = this.sgPhase.b("Phase Effects", false, ".");
      this.phaseRotate = this.sgPhase.b("Phase Rotate", true, "Allows rotating while phasing.");
      var10001 = this.sgPhase;
      var10005 = this.phaseRotate;
      Objects.requireNonNull(var10005);
      this.stillPhaseRotate = var10001.b("Still Phase Rotate", true, ".", var10005::get);
      this.sneakPhase = this.sgPhase.b("Sneak Phase", true, ".");
      this.ticks = 0;
      this.sent = 0;
      this.rur = 0;
      this.packetsToSend = 0.0D;
      this.random = new Random();
      this.info = null;
      this.validPackets = new ArrayList();
      this.offset = class_243.field_1353;
      this.moving = false;
      this.sneaked = false;
      INSTANCE = this;
   }

   public static PacketFly getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.ticks = 0;
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onTick(TickEvent.Pre e) {
      if (this.enabled) {
         --this.ticks;
         ++this.rur;
         if (this.rur % 20 == 0) {
            this.info = "Packets: " + this.sent;
            this.sent = 0;
         }

      }
   }

   public void onDisable() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && (Boolean)this.resync.get()) {
         class_243 pos = Managers.PACKET.pos;
         this.sendPacket(new class_2830(pos.field_1352, pos.field_1351 + 1.0D, pos.field_1350, Managers.ROTATION.prevYaw + 5.0F, Managers.ROTATION.prevPitch, false));
      }

   }

   @Event
   public void onMove(MoveEvent.Pre e) {
      if (this.enabled && BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         boolean phasing = this.isPhasing();
         boolean semiPhasing = this.isSemiPhase();
         if ((Boolean)this.noClip.get()) {
            BlackOut.mc.field_1724.field_5960 = true;
         }

         this.packetsToSend += this.packets(semiPhasing);
         boolean shouldAntiKick = (Boolean)this.antiKick.get() && this.ticks <= 0 && !phasing && !this.onGround();
         double yaw = this.getYaw();
         double motion = this.getSpeed(semiPhasing);
         double x = 0.0D;
         double y = 0.0D;
         double z = 0.0D;
         if (this.jumping()) {
            y = (Double)(semiPhasing ? this.phaseUpSpeed : this.upSpeed).get();
         } else if (this.sneaking()) {
            y = -(Double)(semiPhasing ? this.phaseDownSpeed : this.downSpeed).get();
         }

         if (this.jumping()) {
            y = semiPhasing ? (Double)this.phaseUpSpeed.get() : (Double)this.upSpeed.get();
         } else if (this.sneaking()) {
            y = semiPhasing ? -(Double)this.phaseDownSpeed.get() : -(Double)this.downSpeed.get();
         }

         if (y != 0.0D && (Boolean)this.strictVertical.get()) {
            this.moving = false;
         }

         if (this.moving) {
            x = Math.cos(Math.toRadians(yaw + 90.0D)) * motion;
            z = Math.sin(Math.toRadians(yaw + 90.0D)) * motion;
         } else {
            if (semiPhasing && !(Boolean)this.phaseFastVertical.get()) {
               this.packetsToSend = Math.min(this.packetsToSend, 1.0D);
            }

            if (!semiPhasing && !(Boolean)this.fastVertical.get()) {
               this.packetsToSend = Math.min(this.packetsToSend, 1.0D);
            }
         }

         this.offset = new class_243(0.0D, 0.0D, 0.0D);
         boolean antiKickSent = false;

         while(this.packetsToSend > 0.0D) {
            double yOffset;
            if (shouldAntiKick && y >= 0.0D && !antiKickSent) {
               this.ticks = (Integer)this.antiKickDelay.get();
               yOffset = (Double)this.antiKickAmount.get() * -0.04D;
               antiKickSent = true;
            } else {
               yOffset = y;
            }

            this.offset = this.offset.method_1031(x, yOffset, z);
            this.send(this.offset.method_1019(BlackOut.mc.field_1724.method_19538()), this.getBounds(), this.getOnGround(), semiPhasing);
            --this.packetsToSend;
            if (x == 0.0D && z == 0.0D && y == 0.0D) {
               break;
            }
         }

         this.doPhase();
         e.set(this, this.offset.field_1352, this.offset.field_1351, this.offset.field_1350);
         this.packetsToSend = Math.min(this.packetsToSend, 1.0D);
      }
   }

   private double getSpeed(boolean phasing) {
      Setting<Double> speed = phasing ? this.phaseSpeed : this.flySpeed;
      Setting<Boolean> effects = phasing ? this.phaseEffects : this.flyEffects;
      return (Boolean)effects.get() ? MovementUtils.getSpeed((Double)speed.get()) : (Double)speed.get();
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      if (this.enabled) {
         if (event.packet instanceof class_2828) {
            if (!this.validPackets.contains((class_2828)event.packet)) {
               event.setCancelled(true);
            } else {
               ++this.sent;
            }
         } else {
            ++this.sent;
         }

      }
   }

   private void doPhase() {
      if ((Boolean)this.sneakPhase.get()) {
         if (!this.jumping()) {
            if (this.sneaked) {
               this.sneaked = false;
               this.endSneak();
            }

         } else {
            class_238 standBox = this.boxFor(class_4050.field_18076, BlackOut.mc.field_1724.method_19538()).method_35580(0.0625D, 0.0625D, 0.0625D).method_989(0.0D, this.offset.field_1351 * 2.0D, 0.0D);
            class_238 movedBox = this.boxFor(class_4050.field_18076, BlackOut.mc.field_1724.method_19538()).method_35580(0.0625D, 0.0D, 0.0625D).method_989(0.0D, this.offset.field_1351 * 3.0D, 0.0D);
            boolean standIn = this.in(standBox);
            boolean movedIn = this.in(movedBox);
            if (this.sneaking()) {
               if (standIn) {
                  this.endSneak();
               }
            } else if (movedIn) {
               this.startSneak();
            }

         }
      }
   }

   private class_238 boxFor(class_4050 pose, class_243 vec3d) {
      return ((class_4048)class_1657.field_18134.getOrDefault(pose, class_1657.field_18135)).method_30757(vec3d);
   }

   private boolean in(class_238 box) {
      return OLEPOSSUtils.inside(BlackOut.mc.field_1724, box);
   }

   private void startSneak() {
      this.sneaked = true;
      BlackOut.mc.field_1724.method_5660(true);
      BlackOut.mc.field_1690.field_1832.method_23481(true);
   }

   private void endSneak() {
      BlackOut.mc.field_1724.method_5660(false);
      BlackOut.mc.field_1690.field_1832.method_23481(false);
   }

   public String getInfo() {
      return this.info;
   }

   private boolean onGround() {
      return BlackOut.mc.field_1724.method_24828() || (double)BlackOut.mc.field_1724.method_31478() - BlackOut.mc.field_1724.method_23318() == 0.0D && OLEPOSSUtils.collidable(BlackOut.mc.field_1724.method_24515().method_10074());
   }

   private double packets(boolean semiPhasing) {
      return (Double)(semiPhasing ? this.phasePackets : this.packets).get();
   }

   private class_243 getBounds() {
      double yaw = this.random.nextDouble(0.0D, 6.283185307179586D);
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

   private boolean getOnGround() {
      return (Boolean)this.onGroundSpoof.get() ? (Boolean)this.onGround.get() : BlackOut.mc.field_1724.method_24828();
   }

   private boolean isPhasing() {
      return OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_35580(0.0625D, 0.0D, 0.0625D));
   }

   private boolean isSemiPhase() {
      return OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_1009(0.01D, 0.0D, 0.01D));
   }

   private boolean jumping() {
      return BlackOut.mc.field_1690.field_1903.method_1434();
   }

   private boolean sneaking() {
      return BlackOut.mc.field_1690.field_1832.method_1434();
   }

   private void send(class_243 pos, class_243 bounds, boolean onGround, boolean phasing) {
      class_2828 normal = this.getPacket(pos, onGround, phasing);
      class_2829 bound = new class_2829(bounds.field_1352, bounds.field_1351, bounds.field_1350, onGround);
      this.validPackets.add(normal);
      this.sendPacket(normal);
      this.validPackets.add(bound);
      this.sendPacket(bound);
      this.sendPacket(Managers.PACKET.incrementedPacket(pos));
      if ((Boolean)this.syncPacket.get()) {
         Managers.PACKET.sendPositionSync(pos, Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch);
      }

   }

   private class_2828 getPacket(class_243 pos, boolean onGround, boolean phasing) {
      if (!this.shouldRotate(phasing ? this.phaseRotate : this.flyRotate, phasing ? this.stillPhaseRotate : this.stillFlyRotate)) {
         return this.onlyMove(pos, onGround);
      } else {
         Managers.ROTATION.updateNext();
         if (!Managers.ROTATION.rotated()) {
            return this.onlyMove(pos, onGround);
         } else {
            float yaw = Managers.ROTATION.nextYaw;
            float pitch = Managers.ROTATION.nextPitch;
            return new class_2830(pos.field_1352, pos.field_1351, pos.field_1350, yaw, pitch, onGround);
         }
      }
   }

   private boolean shouldRotate(Setting<Boolean> rot, Setting<Boolean> still) {
      if (!(Boolean)rot.get()) {
         return false;
      } else {
         return !(Boolean)still.get() || this.offset.method_1033() < 0.01D;
      }
   }

   private class_2829 onlyMove(class_243 vec3d, boolean ong) {
      return new class_2829(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350, ong);
   }

   private double getYaw() {
      double f = (double)BlackOut.mc.field_1724.field_3913.field_3905;
      double s = (double)BlackOut.mc.field_1724.field_3913.field_3907;
      double yaw = (double)BlackOut.mc.field_1724.method_36454();
      if (f > 0.0D) {
         this.moving = true;
         yaw += s > 0.0D ? -45.0D : (s < 0.0D ? 45.0D : 0.0D);
      } else if (f < 0.0D) {
         this.moving = true;
         yaw += s > 0.0D ? -135.0D : (s < 0.0D ? 135.0D : 180.0D);
      } else {
         this.moving = s != 0.0D;
         yaw += s > 0.0D ? -90.0D : (s < 0.0D ? 90.0D : 0.0D);
      }

      return yaw;
   }

   public static enum BoundsMode {
      Add,
      Set;

      // $FF: synthetic method
      private static PacketFly.BoundsMode[] $values() {
         return new PacketFly.BoundsMode[]{Add, Set};
      }
   }
}
