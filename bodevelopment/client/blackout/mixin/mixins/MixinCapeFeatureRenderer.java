package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.misc.Streamer;
import bodevelopment.client.blackout.util.Capes;
import net.minecraft.class_1664;
import net.minecraft.class_2960;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_972.class})
public class MixinCapeFeatureRenderer {
   @Unique
   private class_2960 cape;

   @Redirect(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isPartVisible(Lnet/minecraft/client/render/entity/PlayerModelPart;)Z"
)
   )
   private boolean redirectIsCapeVisible(class_742 instance, class_1664 playerModelPart) {
      this.cape = Capes.getCape(instance);
      if (instance == BlackOut.mc.field_1724) {
         Streamer streamer = Streamer.getInstance();
         if (streamer.enabled && (Boolean)streamer.skin.get()) {
            return false;
         }
      }

      return this.cape != null || instance.method_7348(playerModelPart);
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/SkinTextures;capeTexture()Lnet/minecraft/util/Identifier;"
)
   )
   private class_2960 redirectCapeTexture(class_8685 instance) {
      return this.cape != null ? this.cape : instance.comp_1627();
   }
}
