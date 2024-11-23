package bodevelopment.client.blackout.module.setting.multisettings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.framebuffer.FrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_238;

public class BoxMultiSetting {
   public static int id = 0;
   public final Setting<BoxMultiSetting.BoxRenderMode> mode;
   public final Setting<RenderShape> shape;
   public final Setting<BlackOutColor> lineColor;
   public final Setting<BlackOutColor> sideColor;
   public final Setting<Integer> bloom;
   public final Setting<Boolean> blur;
   public final Setting<BlackOutColor> insideColor;
   public final Setting<BlackOutColor> bloomColor;
   private final String insideBufferName;
   private final String bloomBufferName;
   private float shaderAlpha;

   @Event
   public void onRenderHud(RenderEvent.Hud.Pre event) {
      if (this.mode.get() == BoxMultiSetting.BoxRenderMode.Shader) {
         FrameBuffer insideBuffer = Managers.FRAME_BUFFER.getBuffer(this.insideBufferName);
         FrameBuffer bloomBuffer = Managers.FRAME_BUFFER.getBuffer(this.bloomBufferName);
         ThemeSettings themeSettings = ThemeSettings.getInstance();
         if ((Boolean)this.blur.get()) {
            float prevAlpha = Renderer.getAlpha();
            Renderer.setAlpha(this.shaderAlpha);
            Renderer.on3DBlur();
            Renderer.setTexture(Managers.FRAME_BUFFER.getBuffer("3dblur").getTexture(), 1);
            RenderUtils.renderBufferWith(insideBuffer, Shaders.screentexoverlay, new ShaderSetup((setup) -> {
               setup.set("uTexture0", 0);
               setup.set("uTexture1", 1);
            }));
            Renderer.setAlpha(prevAlpha);
         }

         RenderUtils.renderBufferWith(insideBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
            setup.color("clr", ((BlackOutColor)this.insideColor.get()).alphaMulti((double)this.shaderAlpha).getRGB());
         }));
         if ((Integer)this.bloom.get() > 0) {
            bloomBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
            bloomBuffer.bind(true);
            RenderUtils.renderBufferWith(insideBuffer, Shaders.screentex, new ShaderSetup((setup) -> {
               setup.set("alpha", 1.0F);
            }));
            bloomBuffer.unbind();
            RenderUtils.blurBufferBW(this.bloomBufferName, (Integer)this.bloom.get() + 1);
            bloomBuffer.bind(true);
            Renderer.setTexture(insideBuffer.getTexture(), 1);
            RenderUtils.renderBufferWith(bloomBuffer, Shaders.subtract, new ShaderSetup((setup) -> {
               setup.set("uTexture0", 0);
               setup.set("uTexture1", 1);
            }));
            bloomBuffer.unbind();
            RenderUtils.renderBufferWith(bloomBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
               setup.color("clr", ((BlackOutColor)this.bloomColor.get()).alphaMulti((double)this.shaderAlpha).getRGB());
            }));
         }

         insideBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
      }
   }

   private BoxMultiSetting(SettingGroup sg, String name, BlackOutColor defaultColor, SingleOut<Boolean> visible) {
      if (name == null) {
         name = "";
      } else {
         name = name + " ";
      }

      this.insideBufferName = "insideBuffer-" + id;
      this.bloomBufferName = "bloomBuffer-" + id;
      ++id;
      this.mode = sg.e(name + "Render Mode", BoxMultiSetting.BoxRenderMode.Normal, ".", visible);
      this.shape = sg.e(name + "Shape", RenderShape.Full, ".");
      this.lineColor = sg.c(name + "Line Color", defaultColor.withAlpha(255), ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Normal && (Boolean)visible.get();
      });
      this.sideColor = sg.c(name + "Side Color", defaultColor.withAlpha(50), ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Normal && (Boolean)visible.get();
      });
      this.bloom = sg.i(name + "Bloom", 3, 0, 10, 1, ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Shader && (Boolean)visible.get();
      });
      this.blur = sg.b(name + "Blur", false, ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Shader && (Boolean)visible.get();
      });
      this.insideColor = sg.c(name + "Inside Color", defaultColor.withAlpha(50), ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Shader && (Boolean)visible.get();
      });
      this.bloomColor = sg.c(name + "Bloom Color", defaultColor.withAlpha(150), ".", () -> {
         return this.mode.get() == BoxMultiSetting.BoxRenderMode.Shader && (Boolean)visible.get();
      });
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return BlackOut.mc.field_1724 == null || BlackOut.mc.field_1687 == null;
      });
   }

   public void render(class_238 box) {
      this.render(box, 1.0F, 1.0F);
   }

   public void render(class_238 box, float alpha, float alphaS) {
      switch((BoxMultiSetting.BoxRenderMode)this.mode.get()) {
      case Normal:
         Render3DUtils.box(box, ((BlackOutColor)this.sideColor.get()).alphaMulti((double)alpha), ((BlackOutColor)this.lineColor.get()).alphaMulti((double)alpha), (RenderShape)this.shape.get());
         break;
      case Shader:
         this.shaderAlpha = alphaS;
         FrameBuffer insideBuffer = Managers.FRAME_BUFFER.getBuffer(this.insideBufferName);
         insideBuffer.bind(true);
         Render3DUtils.box(box, BlackOutColor.WHITE, (BlackOutColor)null, RenderShape.Sides);
         insideBuffer.unbind();
      }

   }

   public static BoxMultiSetting of(SettingGroup sg) {
      return of(sg, (String)null);
   }

   public static BoxMultiSetting of(SettingGroup sg, String name) {
      return of(sg, name, () -> {
         return true;
      });
   }

   public static BoxMultiSetting of(SettingGroup sg, String name, SingleOut<Boolean> visible) {
      return new BoxMultiSetting(sg, name, new BlackOutColor(255, 0, 0, 255), visible);
   }

   public static enum BoxRenderMode {
      Normal,
      Shader;

      // $FF: synthetic method
      private static BoxMultiSetting.BoxRenderMode[] $values() {
         return new BoxMultiSetting.BoxRenderMode[]{Normal, Shader};
      }
   }
}
