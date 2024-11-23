package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.RotationManager;
import bodevelopment.client.blackout.module.modules.misc.AntiHunger;
import bodevelopment.client.blackout.module.modules.movement.NoSlow;
import bodevelopment.client.blackout.module.modules.movement.Sprint;
import bodevelopment.client.blackout.module.modules.movement.TickShift;
import bodevelopment.client.blackout.module.modules.movement.Velocity;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.modules.visual.misc.SwingModifier;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import net.minecraft.class_1268;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_634;
import net.minecraft.class_743;
import net.minecraft.class_744;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2831;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_746.class})
public abstract class MixinClientPlayerEntity {
   @Shadow
   private float field_3941;
   @Shadow
   private float field_3925;
   @Shadow
   public class_744 field_3913;
   @Unique
   private static boolean sent = false;
   @Unique
   private static boolean wasMove = false;
   @Unique
   private static boolean wasRotation = false;

   @Shadow
   protected abstract boolean method_46743();

   @Inject(
      method = {"swingHand(Lnet/minecraft/util/Hand;)V"},
      at = {@At("HEAD")}
   )
   private void swingHand(class_1268 hand, CallbackInfo ci) {
      SwingModifier.getInstance().startSwing(hand);
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")}
   )
   private void sendPacketsHead(CallbackInfo ci) {
      sent = false;
      wasMove = false;
      wasRotation = false;
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
)
   )
   private void onSendPacket(class_634 instance, class_2596<?> packet) {
      boolean var10000;
      class_2828 moveC2SPacket;
      label21: {
         sent = true;
         if (packet instanceof class_2828) {
            moveC2SPacket = (class_2828)packet;
            if (moveC2SPacket.method_36171()) {
               var10000 = true;
               break label21;
            }
         }

         var10000 = false;
      }

      label16: {
         wasMove = var10000;
         if (packet instanceof class_2828) {
            moveC2SPacket = (class_2828)packet;
            if (moveC2SPacket.method_36172()) {
               var10000 = true;
               break label16;
            }
         }

         var10000 = false;
      }

      wasRotation = var10000;
      instance.method_52787(packet);
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("TAIL")}
   )
   private void sendPacketsTail(CallbackInfo ci) {
      if (!sent && Managers.ROTATION.rotated() && (Managers.ROTATION.rotatingYaw != RotationManager.RotatePhase.Inactive || Managers.ROTATION.rotatingPitch != RotationManager.RotatePhase.Inactive)) {
         BlackOut.mc.method_1562().method_52787(new class_2831(Managers.ROTATION.nextYaw, Managers.ROTATION.nextPitch, Managers.PACKET.isOnGround()));
         wasRotation = true;
         sent = true;
      }

      TickShift tickShift = TickShift.getInstance();
      if (tickShift.enabled && tickShift.canCharge(sent, wasMove)) {
         tickShift.unSent = Math.min((double)(Integer)tickShift.packets.get(), tickShift.unSent + (Double)tickShift.chargeSpeed.get());
      }

      BlackOut.EVENT_BUS.post(MoveEvent.PostSend.get());
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"
)
   )
   private float getYaw(class_746 instance) {
      return instance == BlackOut.mc.field_1724 ? Managers.ROTATION.getNextYaw() : instance.method_36454();
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"
)
   )
   private float getPitch(class_746 instance) {
      return instance == BlackOut.mc.field_1724 ? Managers.ROTATION.getNextPitch() : instance.method_36455();
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastYaw:F",
   opcode = 180
)
   )
   private float prevYaw(class_746 instance) {
      return instance == BlackOut.mc.field_1724 ? Managers.ROTATION.prevYaw : this.field_3941;
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastPitch:F",
   opcode = 180
)
   )
   private float prevPitch(class_746 instance) {
      return instance == BlackOut.mc.field_1724 ? Managers.ROTATION.prevPitch : this.field_3925;
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)
   )
   private boolean usingItem(class_746 instance) {
      return instance == BlackOut.mc.field_1724 ? NoSlow.shouldSlow() : instance.method_6115();
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"
)
   )
   private boolean isOnGround(class_746 instance) {
      if (instance == BlackOut.mc.field_1724) {
         AntiHunger antiHunger = AntiHunger.getInstance();
         if (antiHunger.enabled && (Boolean)antiHunger.moving.get()) {
            return false;
         }
      }

      return instance.method_24828();
   }

   @Redirect(
      method = {"sendSprintingPacket"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSprinting()Z"
)
   )
   private boolean sprinting(class_746 instance) {
      if (instance == BlackOut.mc.field_1724) {
         AntiHunger antiHunger = AntiHunger.getInstance();
         if (antiHunger.enabled && (Boolean)antiHunger.sprint.get()) {
            return false;
         }
      }

      return instance.method_5624();
   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushOutOfBlocks(double x, double z, CallbackInfo ci) {
      if (this == BlackOut.mc.field_1724) {
         Velocity velocity = Velocity.getInstance();
         if (velocity.enabled && (Boolean)velocity.blockPush.get()) {
            ci.cancel();
         }

      }
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSprinting()Z"
)
   )
   private boolean forwardMovement(class_746 value) {
      if (this != BlackOut.mc.field_1724) {
         return value.method_5624();
      } else {
         Sprint sprint = Sprint.getInstance();
         boolean hasInput;
         if (!SettingUtils.grimMovement() && SettingUtils.strictSprint()) {
            hasInput = Managers.ROTATION.move && Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.moveYaw, (double)Managers.ROTATION.nextYaw)) <= 45.0D;
         } else {
            hasInput = this.field_3913.method_20622() || sprint.enabled && sprint.shouldSprint();
         }

         boolean cantSprint = !hasInput || !this.method_46743();
         if (value.method_5681()) {
            if (!value.method_24828() && !this.field_3913.field_3903 && cantSprint || !value.method_5799()) {
               value.method_5728(false);
            }
         } else if (cantSprint || value.field_5976 && !value.field_34927 || value.method_5799() && !value.method_5869()) {
            value.method_5728(false);
         }

         return false;
      }
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/Input;tick(ZF)V"
)
   )
   private void tickInput(class_744 instance, boolean slowDown, float slowDownFactor) {
      if (this != BlackOut.mc.field_1724) {
         instance.method_3129(slowDown, slowDownFactor);
      } else {
         Freecam freecam = Freecam.getInstance();
         if (freecam.enabled) {
            freecam.resetInput((class_743)instance);
         } else {
            instance.method_3129(slowDown, slowDownFactor);
         }

      }
   }
}
