package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.settings.RotationSettings;
import bodevelopment.client.blackout.module.modules.movement.ElytraFly;
import bodevelopment.client.blackout.module.modules.movement.PacketFly;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.SharedFeatures;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2828;
import net.minecraft.class_3532;

public class RotationManager extends Manager {
   public long prevRotation = 0L;
   public float nextYaw = 0.0F;
   public float nextPitch = 0.0F;
   public long timeYaw = 0L;
   public long timePitch = 0L;
   public RotationManager.RotatePhase rotatingYaw;
   public RotationManager.RotatePhase rotatingPitch;
   public float prevYaw;
   public float prevPitch;
   public float renderYaw;
   public float renderPitch;
   public float prevRenderYaw;
   public float prevRenderPitch;
   public double priorityYaw;
   public double priorityPitch;
   public String keyYaw;
   public String keyPitch;
   public float moveLookYaw;
   public float moveYaw;
   public float moveOffset;
   public boolean move;
   public double packetsLeft;
   public final List<FloatFloatPair> rotationHistory;
   public final List<FloatFloatPair> tickRotationHistory;

   public RotationManager() {
      this.rotatingYaw = RotationManager.RotatePhase.Inactive;
      this.rotatingPitch = RotationManager.RotatePhase.Inactive;
      this.prevYaw = 0.0F;
      this.prevPitch = 0.0F;
      this.renderYaw = 0.0F;
      this.renderPitch = 0.0F;
      this.prevRenderYaw = 0.0F;
      this.prevRenderPitch = 0.0F;
      this.priorityYaw = 0.0D;
      this.priorityPitch = 0.0D;
      this.keyYaw = "Luposulu best";
      this.keyPitch = "fr ^";
      this.moveLookYaw = 0.0F;
      this.moveYaw = 0.0F;
      this.moveOffset = 0.0F;
      this.move = false;
      this.packetsLeft = 0.0D;
      this.rotationHistory = Collections.synchronizedList(new ArrayList());
      this.tickRotationHistory = Collections.synchronizedList(new ArrayList());
   }

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onTick(TickEvent.Post event) {
      this.updateRender();
      this.packetsLeft = Math.min(this.packetsLeft, 1.0D);
      this.packetsLeft += (Double)RotationSettings.getInstance().packetRotations.get();
      synchronized(this.tickRotationHistory) {
         this.tickRotationHistory.add(0, new FloatFloatImmutablePair(this.prevYaw, this.prevPitch));
         OLEPOSSUtils.limitList(this.tickRotationHistory, 20);
      }
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if (this.rotatingYaw == RotationManager.RotatePhase.Rotating && SettingUtils.shouldVanillaRotate()) {
         BlackOut.mc.field_1724.method_36456(class_3532.method_16439(event.tickDelta, this.prevRenderYaw, this.renderYaw));
      }

      if (this.rotatingPitch == RotationManager.RotatePhase.Rotating && SettingUtils.shouldVanillaRotate()) {
         BlackOut.mc.field_1724.method_36457(class_3532.method_16439(event.tickDelta, this.prevRenderPitch, this.renderPitch));
      }

   }

   @Event
   public void onRotate(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         if (packet.method_36172()) {
            this.setPrev(packet.method_12271(0.0F), packet.method_12270(0.0F));
            this.prevRotation = System.currentTimeMillis();
         }
      }

   }

   @Event
   public void onSetback(PacketEvent.Received event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2708) {
         class_2708 packet = (class_2708)var3;
         this.setPrev(packet.method_11736(), packet.method_11739());
         this.updateRender();
      }

   }

   private void setPrev(float yaw, float pitch) {
      synchronized(this.rotationHistory) {
         this.rotationHistory.add(0, new FloatFloatImmutablePair(Math.abs(yaw - this.prevYaw), Math.abs(pitch - this.prevPitch)));
         OLEPOSSUtils.limitList(this.rotationHistory, 20);
      }

      this.prevYaw = yaw;
      this.prevPitch = pitch;
   }

   private void updateRender() {
      if ((Integer)RotationSettings.getInstance().renderSmoothness.get() <= 0) {
         this.renderYaw = this.prevYaw;
         this.renderPitch = this.prevPitch;
         this.prevRenderYaw = this.renderYaw;
         this.prevRenderPitch = this.renderPitch;
      } else {
         this.prevRenderYaw = this.renderYaw;
         this.prevRenderPitch = this.renderPitch;
         this.renderYaw = this.getRenderRotation(this.prevYaw, this.prevRenderYaw);
         this.renderPitch = this.getRenderRotation(this.prevPitch, this.prevRenderPitch);
      }

   }

   public float getRenderRotation(float current, float prev) {
      return prev + (current - prev) / (float)(Integer)RotationSettings.getInstance().renderSmoothness.get();
   }

   public int updateMove(float yaw, boolean move) {
      this.moveYaw = yaw;
      this.move = move;
      int offset = Math.round(class_3532.method_15393(yaw - this.moveLookYaw) / 45.0F);
      offset += this.moveOffset < -45.0F ? 1 : (this.moveOffset > 45.0F ? -1 : 0);
      this.moveOffset += (float)RotationUtils.yawAngle((double)yaw, (double)(this.moveLookYaw + (float)(offset * 45)));
      int i = offset % 8;
      return i < 0 ? 8 + i : i;
   }

   public float getNextYaw() {
      return this.rotated() ? this.nextYaw : this.prevYaw;
   }

   public float getNextPitch() {
      return this.rotated() ? this.nextPitch : this.prevPitch;
   }

   public void updateNext() {
      this.updateNextYaw();
      this.updateNextPitch();
      this.nextYaw = this.prevYaw + (float)RotationUtils.yawAngle((double)this.prevYaw, (double)this.nextYaw);
   }

   private void updateNextYaw() {
      float yaw = BlackOut.mc.field_1724.method_36454();
      if (System.currentTimeMillis() > this.timeYaw) {
         if (this.rotatingYaw == RotationManager.RotatePhase.Rotating) {
            this.rotatingYaw = RotationManager.RotatePhase.Returning;
         }
      } else {
         this.rotatingYaw = RotationManager.RotatePhase.Rotating;
      }

      if (this.rotatingYaw == RotationManager.RotatePhase.Returning) {
         if (Math.abs(RotationUtils.yawAngle((double)this.nextYaw, (double)yaw)) < SettingUtils.returnSpeed()) {
            this.rotatingYaw = RotationManager.RotatePhase.Inactive;
         } else {
            this.nextYaw = RotationUtils.nextYaw((double)this.prevYaw, (double)yaw, SettingUtils.returnSpeed());
         }
      }

      if (this.rotatingYaw == RotationManager.RotatePhase.Inactive) {
         this.nextYaw = yaw;
      }

   }

   private void updateNextPitch() {
      ElytraFly elytraFly = ElytraFly.getInstance();
      if (elytraFly.enabled && elytraFly.isBouncing()) {
         this.rotatingPitch = RotationManager.RotatePhase.Rotating;
         this.timePitch = System.currentTimeMillis() + 500L;
         this.priorityPitch = 69420.0D;
         this.nextPitch = elytraFly.getPitch();
      } else {
         float pitch = BlackOut.mc.field_1724.method_36455();
         if (System.currentTimeMillis() > this.timePitch) {
            if (this.rotatingPitch == RotationManager.RotatePhase.Rotating) {
               this.rotatingPitch = RotationManager.RotatePhase.Returning;
            }
         } else {
            this.rotatingPitch = RotationManager.RotatePhase.Rotating;
         }

         if (this.rotatingPitch == RotationManager.RotatePhase.Returning) {
            if ((double)Math.abs(this.nextPitch - pitch) < SettingUtils.returnSpeed()) {
               this.rotatingPitch = RotationManager.RotatePhase.Inactive;
            } else {
               this.nextPitch = RotationUtils.nextPitch((double)this.prevPitch, (double)pitch, SettingUtils.returnSpeed());
            }
         }

         if (this.rotatingPitch == RotationManager.RotatePhase.Inactive) {
            this.nextPitch = pitch;
         }

      }
   }

   public boolean rotated() {
      if (SharedFeatures.shouldPauseRotations()) {
         return false;
      } else {
         return this.nextYaw != this.prevYaw || this.nextPitch != this.prevPitch;
      }
   }

   public boolean yawActive() {
      return Managers.ROTATION.rotatingYaw != RotationManager.RotatePhase.Inactive || SharedFeatures.shouldPauseRotations() || PacketFly.getInstance().enabled;
   }

   public boolean pitchActive() {
      return Managers.ROTATION.rotatingPitch != RotationManager.RotatePhase.Inactive || SharedFeatures.shouldPauseRotations() || PacketFly.getInstance().enabled;
   }

   public static enum RotatePhase {
      Rotating,
      Returning,
      Inactive;

      // $FF: synthetic method
      private static RotationManager.RotatePhase[] $values() {
         return new RotationManager.RotatePhase[]{Rotating, Returning, Inactive};
      }
   }
}
