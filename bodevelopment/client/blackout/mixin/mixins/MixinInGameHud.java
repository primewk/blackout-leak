package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.modules.client.BlurSettings;
import bodevelopment.client.blackout.module.modules.visual.entities.ShaderESP;
import bodevelopment.client.blackout.module.modules.visual.misc.Crosshair;
import bodevelopment.client.blackout.module.modules.visual.misc.CustomScoreboard;
import bodevelopment.client.blackout.module.modules.visual.misc.HandESP;
import bodevelopment.client.blackout.module.modules.visual.misc.NoRender;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import net.minecraft.class_266;
import net.minecraft.class_2960;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_5251;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public class MixinInGameHud {
   @Final
   @Shadow
   private static class_2960 field_2019;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void preRender(class_332 context, float tickDelta, CallbackInfo ci) {
      BlurSettings blur = BlurSettings.getInstance();
      if (Renderer.shouldLoad3DBlur()) {
         RenderUtils.loadBlur("3dblur", blur.get3DBlurStrength());
      }

      HandESP handESP = HandESP.getInstance();
      if (handESP.enabled) {
         handESP.renderHud();
      }

      ShaderESP shaderESP = ShaderESP.getInstance();
      if (shaderESP.enabled) {
         shaderESP.onRenderHud();
      }

      if (Renderer.shouldLoadHUDBlur()) {
         RenderUtils.loadBlur("hudblur", blur.getHUDBlurStrength());
      }

      BlackOut.EVENT_BUS.post(RenderEvent.Hud.Pre.get(context, tickDelta));
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void postRender(class_332 context, float tickDelta, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(RenderEvent.Hud.Post.get(context, tickDelta));
   }

   @Inject(
      method = {"renderStatusEffectOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderStatusEffectOverlay(class_332 context, CallbackInfo ci) {
      if (NoRender.getInstance().enabled && (Boolean)NoRender.getInstance().effectOverlay.get()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderScoreboardSidebar"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderScoreboard(class_332 context, class_266 objective, CallbackInfo ci) {
      CustomScoreboard customScoreboard = CustomScoreboard.getInstance();
      if (customScoreboard.enabled) {
         ci.cancel();
         customScoreboard.objectiveName = objective.method_1114().method_27662().getString();
         class_5251 clr = objective.method_1114().method_10866().method_10973();
         int rgbValue = 1;
         if (clr != null) {
            rgbValue = clr.method_27716();
         }

         customScoreboard.objectiveColor = new Color(rgbValue >> 16 & 255, rgbValue >> 8 & 255, rgbValue & 255);
      }

   }

   @Inject(
      method = {"renderOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectPumpkinBlur(class_332 context, class_2960 texture, float opacity, CallbackInfo callback) {
      if (NoRender.getInstance().enabled && (Boolean)NoRender.getInstance().pumpkin.get() && field_2019.equals(texture)) {
         callback.cancel();
      }

   }

   @Inject(
      method = {"renderCrosshair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawCrosshair(class_332 context, CallbackInfo ci) {
      if (Crosshair.getInstance().enabled) {
         ci.cancel();
      }

   }
}
