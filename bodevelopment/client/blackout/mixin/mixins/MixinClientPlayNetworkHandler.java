package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorPlayerMoveC2SPacket;
import bodevelopment.client.blackout.module.modules.misc.NoRotate;
import bodevelopment.client.blackout.module.modules.visual.misc.NoRender;
import bodevelopment.client.blackout.module.modules.visual.world.Ambience;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_2828.class_2830;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public class MixinClientPlayNetworkHandler {
   @Unique
   private float y;
   @Unique
   private float p;

   @Inject(
      method = {"onGameJoin"},
      at = {@At("TAIL")}
   )
   private void onJoin(class_2678 packet, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(GameJoinEvent.get(packet));
   }

   @Redirect(
      method = {"onPlayerPositionLook"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;setYaw(F)V"
)
   )
   private void rubberbandYaw(class_1657 instance, float v) {
      if (!NoRotate.getInstance().enabled) {
         instance.method_36456(v);
      }

      this.y = v;
   }

   @Redirect(
      method = {"onPlayerPositionLook"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;setPitch(F)V"
)
   )
   private void rubberbandPitch(class_1657 instance, float v) {
      if (!NoRotate.getInstance().enabled) {
         instance.method_36457(v);
      }

      this.p = v;
   }

   @Redirect(
      method = {"onPlayerPositionLook"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 1
)
   )
   private void sendFull(class_2535 instance, class_2596<?> packet) {
      class_2830 moveC2SPacket = (class_2830)packet;
      NoRotate noRotate = NoRotate.getInstance();
      if (!noRotate.enabled) {
         instance.method_10743(moveC2SPacket);
      } else {
         switch((NoRotate.NoRotateMode)noRotate.mode.get()) {
         case Set:
            instance.method_10743(moveC2SPacket);
            break;
         case Spoof:
            ((AccessorPlayerMoveC2SPacket)moveC2SPacket).setYaw(this.y);
            ((AccessorPlayerMoveC2SPacket)moveC2SPacket).setPitch(this.p);
            instance.method_10743(moveC2SPacket);
         }

      }
   }

   @Redirect(
      method = {"onWorldTimeUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"
)
   )
   private void setWorldTime(class_638 instance, long timeOfDay) {
      Ambience ambience = Ambience.getInstance();
      if (ambience.enabled && (Boolean)ambience.modifyTime.get()) {
         instance.method_8435((long)(Integer)ambience.time.get());
      } else {
         instance.method_8435(timeOfDay);
      }

   }

   @Redirect(
      method = {"onEntityStatus"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"
)
   )
   private void showTotemAnimation(class_757 instance, class_1799 floatingItem) {
      NoRender noRender = NoRender.getInstance();
      if (!noRender.enabled || !(Boolean)noRender.totem.get()) {
         instance.method_3189(floatingItem);
      }

   }
}
