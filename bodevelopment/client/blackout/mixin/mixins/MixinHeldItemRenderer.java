package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.modules.visual.misc.SwingModifier;
import bodevelopment.client.blackout.module.modules.visual.misc.ViewModel;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_759.class})
public abstract class MixinHeldItemRenderer {
   @Unique
   private class_4587 matrices;
   @Unique
   private class_1799 item;
   @Unique
   private boolean mainHand;
   @Unique
   private class_4597 vertexConsumers;
   @Unique
   private int light;

   @Shadow
   public abstract void method_3233(class_1309 var1, class_1799 var2, class_811 var3, boolean var4, class_4587 var5, class_4597 var6, int var7);

   @ModifyArgs(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)
   )
   private void setArgs(Args args) {
      this.matrices = (class_4587)args.get(7);
      this.item = (class_1799)args.get(5);
      this.mainHand = args.get(3) == class_1268.field_5808;
      this.vertexConsumers = (class_4597)args.get(8);
      this.light = (Integer)args.get(9);
      SwingModifier module = SwingModifier.getInstance();
      if (module.enabled) {
         args.set(6, module.getY((class_1268)args.get(3)));
         args.set(4, module.getSwing((class_1268)args.get(3)));
      }

   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preRenderItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      ViewModel viewModel = ViewModel.getInstance();
      if (viewModel.enabled) {
         if (viewModel.shouldCancel(hand)) {
            ci.cancel();
         } else {
            viewModel.transform(matrices, hand);
         }

      }
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At("TAIL")}
   )
   private void postRenderItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      ViewModel viewModel = ViewModel.getInstance();
      if (viewModel.enabled) {
         viewModel.post(matrices);
      }

   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)}
   )
   private void onRenderItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      ViewModel viewModel = ViewModel.getInstance();
      if (viewModel.enabled) {
         viewModel.scaleAndRotate(matrices, hand);
      }

   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
   shift = Shift.AFTER
)}
   )
   private void onRenderItemPost(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      ViewModel viewModel = ViewModel.getInstance();
      if (viewModel.enabled) {
         viewModel.postRender(matrices);
      }

   }

   @Redirect(
      method = {"renderFirstPersonItem"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z"
)
   )
   private boolean redirectRiptideTransform(class_742 instance) {
      if (instance.method_31550()) {
         return true;
      } else if (this.mainHand && Aura.getInstance().blockTransform(this.matrices)) {
         this.method_3233(instance, this.item, this.mainHand ? class_811.field_4322 : class_811.field_4321, !this.mainHand, this.matrices, this.vertexConsumers, this.light);
         this.matrices.method_22909();
         return true;
      } else {
         return false;
      }
   }
}
