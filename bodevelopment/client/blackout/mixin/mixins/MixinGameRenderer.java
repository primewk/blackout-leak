package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.combat.misc.NoTrace;
import bodevelopment.client.blackout.module.modules.misc.Reach;
import bodevelopment.client.blackout.module.modules.visual.misc.FovModifier;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.modules.visual.misc.HandESP;
import bodevelopment.client.blackout.module.modules.visual.misc.Highlight;
import bodevelopment.client.blackout.module.modules.visual.misc.ViewModel;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.SharedFeatures;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3966;
import net.minecraft.class_4184;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_476;
import net.minecraft.class_5912;
import net.minecraft.class_636;
import net.minecraft.class_757;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_757.class})
public abstract class MixinGameRenderer {
   @Shadow
   public abstract void method_3203();

   @Shadow
   public abstract void method_3192(float var1, long var2, boolean var4);

   @Shadow
   public abstract boolean method_35765();

   @Shadow
   protected abstract void method_3172(class_4587 var1, class_4184 var2, float var3);

   @Redirect(
      method = {"render"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
)
   )
   private class_437 redirectCurrentScreen(class_310 instance) {
      return instance.field_1755 instanceof class_476 && SharedFeatures.shouldSilentScreen() ? null : instance.field_1755;
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void postRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
      Managers.PING.update();
   }

   @Inject(
      method = {"renderWorld"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V"
)}
   )
   private void onRenderWorldPre(float tickDelta, long limitTime, class_4587 matrices, CallbackInfo ci) {
      TimerList.updating.forEach(TimerList::update);
      TimerMap.updating.forEach(TimerMap::update);
      BlackOut.EVENT_BUS.post(RenderEvent.World.Pre.get(matrices, tickDelta));
   }

   @Inject(
      method = {"renderWorld"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V",
   shift = Shift.AFTER
)}
   )
   private void onRenderWorldPost(float tickDelta, long limitTime, class_4587 matrices, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(RenderEvent.World.Post.get(matrices, tickDelta));
   }

   @Redirect(
      method = {"renderWorld"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V"
)
   )
   private void renderHeldItems(class_757 instance, class_4587 matrices, class_4184 camera, float tickDelta) {
      HandESP.getInstance().draw(() -> {
         this.method_3172(matrices, camera, tickDelta);
      });
   }

   @Inject(
      method = {"preloadPrograms"},
      at = {@At("TAIL")}
   )
   private void onShaderLoad(class_5912 factory, CallbackInfo ci) {
      Shaders.loadPrograms();
      BlackOut.FONT.loadFont();
      BlackOut.BOLD_FONT.loadFont();
      BOTextures.init();
   }

   @Redirect(
      method = {"updateTargetedEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"
)
   )
   private float getBlockReach(class_636 instance) {
      Reach reach = Reach.getInstance();
      return reach.enabled ? ((Double)reach.blockReach.get()).floatValue() : instance.method_2904();
   }

   @Redirect(
      method = {"updateTargetedEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"
)
   )
   private boolean getEntityReach(class_636 instance) {
      return Reach.getInstance().enabled || instance.method_2926();
   }

   @Redirect(
      method = {"updateTargetedEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"
)
   )
   private class_239 raycast(class_1297 instance, double maxDistance, float tickDelta, boolean includeFluids) {
      Freecam freecam = Freecam.getInstance();
      if (!freecam.enabled) {
         return instance.method_5745(maxDistance, tickDelta, includeFluids);
      } else {
         class_243 vec3d = freecam.pos;
         class_243 vec3d2 = instance.method_5828(tickDelta);
         class_243 vec3d3 = vec3d.method_1031(vec3d2.field_1352 * maxDistance, vec3d2.field_1351 * maxDistance, vec3d2.field_1350 * maxDistance);
         return BlackOut.mc.field_1687.method_17742(new class_3959(vec3d, vec3d3, class_3960.field_17559, includeFluids ? class_242.field_1347 : class_242.field_1348, instance));
      }
   }

   @Redirect(
      method = {"updateTargetedEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;"
)
   )
   private class_243 cameraPos(class_1297 instance, float tickDelta) {
      Freecam freecam = Freecam.getInstance();
      if (!freecam.enabled) {
         return instance.method_5836(tickDelta);
      } else {
         BlackOut.mc.field_1765 = this.raycast(instance, (double)BlackOut.mc.field_1761.method_2904(), tickDelta, false);
         return freecam.pos;
      }
   }

   @ModifyConstant(
      method = {"updateTargetedEntity"},
      constant = {@Constant(
   doubleValue = 6.0D
)}
   )
   private double extendedRange(double constant) {
      Reach reach = Reach.getInstance();
      return reach.enabled ? (Double)reach.entityReach.get() : constant;
   }

   @Redirect(
      method = {"updateTargetedEntity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"
)
   )
   private class_3966 raycastEntities(class_1297 entity, class_243 min, class_243 max, class_238 box, Predicate<class_1297> predicate, double d) {
      return NoTrace.getInstance().enabled ? null : class_1675.method_18075(entity, min, max, box, predicate, d);
   }

   @Inject(
      method = {"getFov"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFov(class_4184 camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
      FovModifier modifier = FovModifier.getInstance();
      if (modifier.enabled) {
         cir.setReturnValue(this.getFOV(changingFov, modifier));
         cir.cancel();
      }

   }

   @Unique
   private double getFOV(boolean changing, FovModifier fovModifier) {
      if (this.method_35765()) {
         return 90.0D;
      } else if (!changing) {
         ViewModel handView = ViewModel.getInstance();
         return handView.enabled ? (Double)handView.fov.get() : 70.0D;
      } else {
         return fovModifier.getFOV();
      }
   }

   @Inject(
      method = {"shouldRenderBlockOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void outlineRender(CallbackInfoReturnable<Boolean> cir) {
      if (Highlight.getInstance().enabled) {
         cir.setReturnValue(false);
      }

   }
}
