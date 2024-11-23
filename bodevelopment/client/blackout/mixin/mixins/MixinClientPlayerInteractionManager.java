package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoMine;
import bodevelopment.client.blackout.module.modules.misc.AntiRotationSync;
import bodevelopment.client.blackout.module.modules.misc.HandMine;
import bodevelopment.client.blackout.randomstuff.FakePlayerEntity;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_634;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_7204;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class MixinClientPlayerInteractionManager {
   @Shadow
   @Final
   private class_310 field_3712;
   @Shadow
   private float field_3715;
   @Shadow
   private class_1799 field_3718;
   @Shadow
   public class_2338 field_3714;
   @Shadow
   private boolean field_3717;
   @Shadow
   private float field_3713;
   @Unique
   private class_2338 position = null;
   @Unique
   private class_2350 dir = null;

   @Shadow
   public abstract class_1269 method_2896(class_746 var1, class_1268 var2, class_3965 var3);

   @Shadow
   protected abstract void method_41931(class_638 var1, class_7204 var2);

   @Shadow
   public abstract boolean method_2899(class_2338 var1);

   @Shadow
   public abstract int method_51888();

   @Inject(
      method = {"attackBlock"},
      at = {@At("HEAD")}
   )
   private void onAttack(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      this.position = pos;
      this.dir = direction;
   }

   @Redirect(
      method = {"attackBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
   ordinal = 1
)
   )
   private void onStart(class_636 instance, class_638 world, class_7204 packetCreator) {
      AutoMine autoMine = AutoMine.getInstance();
      if (!autoMine.enabled) {
         HandMine handMine = HandMine.getInstance();
         if (!handMine.enabled) {
            this.method_41931(world, packetCreator);
         } else {
            class_2680 blockState = world.method_8320(this.position);
            boolean bl = !blockState.method_26215();
            boolean canInstant = bl && handMine.getDelta(this.position, blockState.method_26165(this.field_3712.field_1724, this.field_3712.field_1724.method_37908(), this.position)) >= 1.0F;
            Runnable runnable = () -> {
               this.method_41931(world, (sequence) -> {
                  if (bl && this.field_3715 == 0.0F) {
                     blockState.method_26179(this.field_3712.field_1687, this.position, this.field_3712.field_1724);
                  }

                  if (bl && canInstant) {
                     handMine.onInstant(this.position, () -> {
                        this.method_2899(this.position);
                     });
                  } else {
                     this.field_3717 = true;
                     this.field_3714 = this.position;
                     this.field_3718 = this.field_3712.field_1724.method_6047();
                     this.field_3715 = 0.0F;
                     this.field_3713 = 0.0F;
                     this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, this.method_51888());
                  }

                  return new class_2846(class_2847.field_12968, this.position, this.dir, sequence);
               });
            };
            if (canInstant) {
               handMine.onInstant(this.position, runnable);
            } else {
               runnable.run();
            }

         }
      } else {
         class_2680 blockState = world.method_8320(this.position);
         boolean bl = !blockState.method_26215();
         if (bl && this.field_3715 == 0.0F) {
            blockState.method_26179(this.field_3712.field_1687, this.position, this.field_3712.field_1724);
         }

         if (bl && blockState.method_26165(this.field_3712.field_1724, this.field_3712.field_1724.method_37908(), this.position) >= 1.0F) {
            this.method_2899(this.position);
         } else {
            this.field_3717 = true;
            this.field_3714 = this.position;
            this.field_3718 = this.field_3712.field_1724.method_6047();
            this.field_3715 = 0.0F;
            this.field_3713 = 0.0F;
            this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, this.method_51888());
         }

         autoMine.onStart(this.position);
      }
   }

   @Redirect(
      method = {"attackBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 0
)
   )
   private void onAbort(class_634 instance, class_2596<?> packet) {
      AutoMine autoMine = AutoMine.getInstance();
      if (!autoMine.enabled) {
         instance.method_52787(packet);
      } else {
         autoMine.onAbort(this.position);
      }

   }

   @Inject(
      method = {"updateBlockBreakingProgress"},
      at = {@At("HEAD")}
   )
   private void onUpdateProgress(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      this.position = pos;
   }

   @Redirect(
      method = {"updateBlockBreakingProgress"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"
)
   )
   private float calcDelta2(class_2680 instance, class_1657 playerEntity, class_1922 blockView, class_2338 pos) {
      HandMine handMine = HandMine.getInstance();
      float vanilla = instance.method_26165(playerEntity, blockView, pos);
      return handMine.enabled ? handMine.getDelta(pos, vanilla) : vanilla;
   }

   @Redirect(
      method = {"updateBlockBreakingProgress"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
   ordinal = 1
)
   )
   private void onStop(class_636 instance, class_638 world, class_7204 packetCreator) {
      AutoMine autoMine = AutoMine.getInstance();
      if (autoMine.enabled) {
         autoMine.onStop(this.position);
      } else {
         HandMine handMine = HandMine.getInstance();
         if (handMine.enabled) {
            handMine.onEnd(this.position, () -> {
               this.method_41931(world, packetCreator);
            });
         } else {
            this.method_41931(world, packetCreator);
         }
      }

   }

   @Redirect(
      method = {"cancelBlockBreaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
)
   )
   private void cancel(class_634 instance, class_2596<?> packet) {
      AutoMine autoMine = AutoMine.getInstance();
      if (!autoMine.enabled) {
         instance.method_52787(packet);
      } else {
         autoMine.onAbort(this.field_3714);
      }

   }

   @Redirect(
      method = {"interactItem"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
)
   )
   private void onRotationSync(class_634 instance, class_2596<?> packet) {
      if (!AntiRotationSync.getInstance().enabled) {
         instance.method_52787(packet);
      } else {
         instance.method_52787(new class_2830(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround()));
      }

   }

   @Redirect(
      method = {"attackEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"
)
   )
   private void onAttack(class_1657 instance, class_1297 target) {
      if (!(target instanceof FakePlayerEntity)) {
         instance.method_7324(target);
      }

   }
}
