package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.entities.ShaderESP;
import bodevelopment.client.blackout.module.modules.visual.world.Brightness;
import net.minecraft.class_1297;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_898.class})
public class MixinEntityRenderDispatcher {
   @Shadow
   private boolean field_4681;

   @Redirect(
      method = {"render"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadows:Z",
   opcode = 180
)
   )
   private boolean shouldRenderShadows(class_898 instance) {
      Brightness brightness = Brightness.getInstance();
      return (!brightness.enabled || brightness.mode.get() != Brightness.Mode.Gamma) && this.field_4681;
   }

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)
   )
   private <T extends class_1297> void onRender(class_897<T> instance, T entity, float yaw, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light) {
      ShaderESP esp = ShaderESP.getInstance();
      if (esp.enabled && esp.shouldRender(entity)) {
         esp.onRender(instance, entity, yaw, tickDelta, matrices, vertexConsumers, light);
      } else {
         instance.method_3936(entity, yaw, tickDelta, matrices, vertexConsumers, light);
      }

   }
}
