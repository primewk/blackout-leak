package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.MovementPrediction;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Objects;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;

public class PhaseWalk extends Module {
   private static PhaseWalk INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> stopRotation;
   private final Setting<Integer> preTicks;
   private final Setting<Integer> postTicks;
   private final Setting<Boolean> syncPacket;
   private final Setting<Boolean> phasedCheck;
   private final Setting<Boolean> onGroundCheck;
   private final Setting<Boolean> descend;
   private final Setting<Boolean> ascend;
   private final Setting<Boolean> stopWait;
   public final Setting<Boolean> useTimer;
   public final Setting<Double> timer;
   private final Setting<Boolean> resync;
   private boolean waitingForPhase;
   private int sincePhase;
   private int sinceRotation;
   private boolean ignore;
   private boolean setTimer;
   private boolean descending;
   private boolean ascending;
   private double ascendY;
   private int ascendProgress;
   private int timerLeft;
   private boolean prevActive;
   private boolean rubberbanded;

   public PhaseWalk() {
      super("Phase Walk", ".", SubCategory.MOVEMENT, true);
      this.stopRotation = this.sgGeneral.b("Stop Rotation", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.stopRotation;
      Objects.requireNonNull(var10008);
      this.preTicks = var10001.i("Pre Ticks", 1, 0, 20, 1, ".", var10008::get);
      var10001 = this.sgGeneral;
      var10008 = this.stopRotation;
      Objects.requireNonNull(var10008);
      this.postTicks = var10001.i("Post Ticks", 1, 0, 20, 1, ".", var10008::get);
      this.syncPacket = this.sgGeneral.b("Sync Packet", true, ".");
      this.phasedCheck = this.sgGeneral.b("Phased Check", true, ".");
      this.onGroundCheck = this.sgGeneral.b("On Ground Check", true, ".");
      this.descend = this.sgGeneral.b("Descend", true, ".");
      this.ascend = this.sgGeneral.b("Ascend", true, ".");
      this.stopWait = this.sgGeneral.b("Stop Wait", true, ".");
      this.useTimer = this.sgGeneral.b("Use Timer", false, "Uses timer when stepping.");
      var10001 = this.sgGeneral;
      var10008 = this.useTimer;
      Objects.requireNonNull(var10008);
      this.timer = var10001.d("Timer", 1.5D, 0.0D, 10.0D, 0.1D, "Packet multiplier.", var10008::get);
      this.resync = this.sgGeneral.b("Resync", true, ".");
      this.waitingForPhase = false;
      this.sincePhase = 0;
      this.sinceRotation = 0;
      this.ignore = false;
      this.setTimer = false;
      this.descending = false;
      this.ascending = false;
      this.ascendY = 0.0D;
      this.ascendProgress = 0;
      this.timerLeft = 0;
      this.prevActive = false;
      this.rubberbanded = false;
      INSTANCE = this;
   }

   public static PhaseWalk getInstance() {
      return INSTANCE;
   }

   public void onDisable() {
      if (this.setTimer) {
         this.setTimer = false;
         Timer.reset();
      }

      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.prevActive && (Boolean)this.resync.get()) {
         this.timerLeft = 0;
         this.sincePhase = 0;
         class_243 pos = Managers.PACKET.pos;
         this.sendPacket(new class_2830(pos.field_1352, pos.field_1351 + 1.0D, pos.field_1350, Managers.ROTATION.prevYaw + 5.0F, Managers.ROTATION.prevPitch, false));
      }

   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         if (packet.method_36172() && !this.ignore) {
            this.sinceRotation = 0;
         }
      }

   }

   @Event
   public void onReceive(PacketEvent.Received event) {
      this.descending = false;
      this.ascending = false;
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      this.move(event);
      if ((Boolean)this.useTimer.get() && --this.timerLeft >= 0) {
         this.setTimer = true;
         Timer.set(((Double)this.timer.get()).floatValue());
      } else if (this.setTimer) {
         this.setTimer = false;
         Timer.reset();
      }

   }

   private void move(MoveEvent.Pre event) {
      ++this.sincePhase;
      ++this.sinceRotation;
      if (!PacketFly.getInstance().enabled) {
         this.waitingForPhase = false;
         boolean phasing = OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_1011(0.0625D));
         if (!phasing) {
            if (this.prevActive && this.rubberbanded && (Boolean)this.resync.get()) {
               this.timerLeft = 0;
               this.sincePhase = 0;
               class_243 pos = Managers.PACKET.pos;
               this.sendPacket(new class_2830(pos.field_1352, pos.field_1351 + 1.0D, pos.field_1350, Managers.ROTATION.prevYaw + 5.0F, Managers.ROTATION.prevPitch, false));
            }

            this.rubberbanded = false;
         }

         this.prevActive = phasing;
         if (!this.preMovement(event)) {
            if ((Boolean)this.stopRotation.get()) {
               this.waitingForPhase = true;
               if (this.sinceRotation < (Integer)this.preTicks.get()) {
                  if ((Boolean)this.stopWait.get()) {
                     BlackOut.mc.field_1724.method_18799(new class_243(0.0D, BlackOut.mc.field_1724.method_18798().field_1351, 0.0D));
                     event.setXZ(this, 0.0D, 0.0D);
                  }

                  return;
               }

               this.waitingForPhase = false;
               this.sincePhase = 0;
            }

            this.handleMovement(event);
         }
      }
   }

   private boolean preMovement(MoveEvent.Pre event) {
      this.handleInput();
      if (this.descending) {
         return this.preDescend(event);
      } else {
         return this.ascending ? this.preAscend(event) : this.preXZ(event);
      }
   }

   private void handleMovement(MoveEvent.Pre event) {
      if (this.descending) {
         this.handleDescend(event);
      } else if (this.ascending) {
         this.handleAscend(event);
      } else {
         this.handleXZ(event);
      }

   }

   private void handleInput() {
      if (!this.descending && !this.ascending) {
         if (this.canVertical()) {
            if (BlackOut.mc.field_1690.field_1832.method_1434() && (Boolean)this.descend.get()) {
               this.descending = true;
            } else if (BlackOut.mc.field_1690.field_1903.method_1434() && (Boolean)this.ascend.get()) {
               this.ascending = true;
               this.ascendProgress = 0;
               this.ascendY = BlackOut.mc.field_1724.method_23318();
            }

         }
      }
   }

   private boolean preDescend(MoveEvent.Pre event) {
      return false;
   }

   private boolean preAscend(MoveEvent.Pre event) {
      return false;
   }

   private boolean canVertical() {
      return this.phased() && this.isOnGround();
   }

   private boolean preXZ(MoveEvent.Pre event) {
      class_243 vec = MovementPrediction.predict(BlackOut.mc.field_1724);
      class_243 vec2 = BlackOut.mc.field_1724.method_19538().method_1019(event.movement);
      double d = vec.method_1020(vec2).method_37267();
      double d2 = vec.method_1020(BlackOut.mc.field_1724.method_19538()).method_37267();
      if ((Boolean)this.phasedCheck.get() && !this.phased()) {
         return true;
      } else if ((Boolean)this.onGroundCheck.get() && !this.isOnGround()) {
         return true;
      } else {
         return d <= 0.01D || d2 >= 0.05D;
      }
   }

   private void handleDescend(MoveEvent.Pre event) {
      class_243 vec3 = BlackOut.mc.field_1724.method_19538().method_1031(0.0D, -0.0253D, 0.0D);
      this.sendBounds(vec3);
      BlackOut.mc.field_1724.method_33574(vec3);
      event.set(this, 0.0D, 0.0D, 0.0D);
      this.descending = false;
   }

   private void handleAscend(MoveEvent.Pre event) {
      double offset = Math.min((double)this.ascendProgress * 0.06D, 1.0D);
      class_243 vec = BlackOut.mc.field_1724.method_19538().method_38499(class_2351.field_11052, this.ascendY + offset);
      this.sendBounds(vec);
      BlackOut.mc.field_1724.method_33574(vec);
      event.set(this, 0.0D, 0.0D, 0.0D);
      ++this.ascendProgress;
      if (offset >= 1.0D) {
         this.ascending = false;
      }

   }

   private void handleXZ(MoveEvent.Pre event) {
      double ratio = event.movement.method_37267() / 0.06D;
      double x = event.movement.field_1352 / ratio;
      double z = event.movement.field_1350 / ratio;
      class_243 vec3 = BlackOut.mc.field_1724.method_19538().method_1031(x, event.movement.field_1351, z).method_38499(class_2351.field_11052, BlackOut.mc.field_1724.method_23318());
      this.sendBounds(vec3);
      BlackOut.mc.field_1724.method_33574(vec3);
      event.setXZ(this, 0.0D, 0.0D);
   }

   private boolean phased() {
      return OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_35580(0.07D, 0.1D, 0.07D));
   }

   private boolean isOnGround() {
      return BlackOut.mc.field_1724.method_24828();
   }

   private void sendBounds(class_243 to) {
      this.rubberbanded = true;
      this.timerLeft = 5;
      Managers.PACKET.sendInstantly(new class_2829(to.field_1352, to.field_1351, to.field_1350, Managers.PACKET.isOnGround()));
      Managers.PACKET.sendInstantly(new class_2829(to.field_1352, to.field_1351 - 87.0D, to.field_1350, Managers.PACKET.isOnGround()));
      Managers.PACKET.sendInstantly(Managers.PACKET.incrementedPacket(to));
      this.ignore = true;
      if ((Boolean)this.syncPacket.get()) {
         Managers.PACKET.sendPositionSync(to, Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch);
      }

      this.ignore = false;
   }

   public boolean shouldStopRotation() {
      if (!(Boolean)this.stopRotation.get()) {
         return false;
      } else if (this.waitingForPhase) {
         return true;
      } else {
         return this.sincePhase <= (Integer)this.postTicks.get();
      }
   }
}
