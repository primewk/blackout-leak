package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.framebuffer.BlendFrameBuffer;
import bodevelopment.client.blackout.rendering.framebuffer.FrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;

public class HandESP extends Module {
   private static HandESP INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Integer> dist;
   private final Setting<Boolean> texture;
   private final Setting<BlackOutColor> outsideColor;
   private final Setting<BlackOutColor> insideColor;
   public final Setting<HandESP.ColorMode> colormode;
   private final Setting<Double> saturation;
   private final Setting<Double> waveSpeed;
   private final Setting<Double> waveLength;
   private final Setting<BlackOutColor> waveColor;

   public HandESP() {
      super("Hand ESP", "Modifies how hands are rendered.", SubCategory.MISC_VISUAL, true);
      this.dist = this.sgGeneral.i("Distance", 5, 1, 10, 1, ".");
      this.texture = this.sgGeneral.b("Texture", false, ".");
      this.outsideColor = this.sgGeneral.c("Outside Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.insideColor = this.sgGeneral.c("Inside Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.colormode = this.sgGeneral.e("Mode", HandESP.ColorMode.Custom, "What style to use");
      this.saturation = this.sgGeneral.d("Rainbow Saturation", 0.8D, 0.0D, 1.0D, 0.1D, ".", () -> {
         return this.colormode.get() == HandESP.ColorMode.Rainbow;
      });
      this.waveSpeed = this.sgGeneral.d("Wave Speed", 2.0D, 0.0D, 10.0D, 0.1D, "Slower wave effect", () -> {
         return this.colormode.get() == HandESP.ColorMode.Wave;
      });
      this.waveLength = this.sgGeneral.d("Wave Length", 2.0D, 0.0D, 5.0D, 0.1D, "Longer wave effect", () -> {
         return this.colormode.get() == HandESP.ColorMode.Wave;
      });
      this.waveColor = this.sgGeneral.c("Wave Color", new BlackOutColor(125, 125, 125, 255), "Text Color For The Wave", () -> {
         return this.colormode.get() == HandESP.ColorMode.Wave;
      });
      INSTANCE = this;
   }

   public static HandESP getInstance() {
      return INSTANCE;
   }

   public void draw(Runnable runnable) {
      if (!this.enabled) {
         runnable.run();
      } else {
         BlendFrameBuffer buffer = Managers.FRAME_BUFFER.getBlend("handESP");
         buffer.start();
         runnable.run();
         buffer.unbind();
      }
   }

   public void renderHud() {
      FrameBuffer buffer = Managers.FRAME_BUFFER.getBuffer("handESP");
      FrameBuffer bloomBuffer = Managers.FRAME_BUFFER.getBuffer("handESP-bloom");
      if ((Boolean)this.texture.get()) {
         RenderUtils.renderBufferWith(buffer, Shaders.screentexcolor, new ShaderSetup((setup) -> {
            setup.color("clr", ((BlackOutColor)this.insideColor.get()).getRGB());
         }));
      }

      buffer.bind(true);
      RenderUtils.renderBufferWith(buffer, Shaders.convert, new ShaderSetup());
      buffer.unbind();
      if ((Integer)this.dist.get() > 0) {
         bloomBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
         bloomBuffer.bind(true);
         RenderUtils.renderBufferWith(buffer, Shaders.screentex, new ShaderSetup((setup) -> {
            setup.set("alpha", 1.0F);
         }));
         bloomBuffer.unbind();
         RenderUtils.blurBufferBW("handESP-bloom", (Integer)this.dist.get() + 1);
         bloomBuffer.bind(true);
         Renderer.setTexture(buffer.getTexture(), 1);
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.subtract, new ShaderSetup((setup) -> {
            setup.set("uTexture0", 0);
            setup.set("uTexture1", 1);
         }));
         bloomBuffer.unbind();
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
            setup.color("clr", this.getColor(false));
         }));
         buffer.clear(1.0F, 1.0F, 1.0F, 0.0F);
      }
   }

   private int getColor(boolean inside) {
      int var10000;
      switch((HandESP.ColorMode)this.colormode.get()) {
      case Custom:
         var10000 = inside ? ((BlackOutColor)this.insideColor.get()).getRGB() : ((BlackOutColor)this.outsideColor.get()).getRGB();
         break;
      case Rainbow:
         var10000 = this.getRainbowColor(inside ? ((BlackOutColor)this.insideColor.get()).alpha : ((BlackOutColor)this.outsideColor.get()).alpha);
         break;
      case Wave:
         var10000 = ColorUtils.getWave(inside ? ((BlackOutColor)this.insideColor.get()).getColor() : ((BlackOutColor)this.outsideColor.get()).getColor(), ((BlackOutColor)this.waveColor.get()).getColor(), (Double)this.waveSpeed.get(), (Double)this.waveLength.get(), 1).getRGB();
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private int getRainbowColor(int alpha) {
      Color color = new Color(ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 150L));
      color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
      return color.getRGB();
   }

   public static enum ColorMode {
      Rainbow,
      Custom,
      Wave;

      // $FF: synthetic method
      private static HandESP.ColorMode[] $values() {
         return new HandESP.ColorMode[]{Rainbow, Custom, Wave};
      }
   }
}
