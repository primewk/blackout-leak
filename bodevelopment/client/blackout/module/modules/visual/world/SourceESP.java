package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.framebuffer.FrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_3486;
import net.minecraft.class_3610;

public class SourceESP extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> water;
   private final Setting<Boolean> lava;
   private final Setting<Double> range;
   private final Setting<Integer> bloom;
   private final Setting<BlackOutColor> fillColor;
   private final Setting<BlackOutColor> bloomColor;
   private long prevCalc;
   private final BlackOutColor white;
   private final List<Pair<class_2338, Boolean>> sources;

   public SourceESP() {
      super("Source ESP", "Highlights water and lava sources.", SubCategory.WORLD, true);
      this.water = this.sgGeneral.b("Water", true, ".");
      this.lava = this.sgGeneral.b("Lava", true, ".");
      this.range = this.sgGeneral.d("Range", 8.0D, 0.0D, 10.0D, 0.1D, ".");
      this.bloom = this.sgGeneral.i("Bloom", 3, 0, 10, 1, ".");
      this.fillColor = this.sgGeneral.c("Fill Color", new BlackOutColor(255, 0, 0, 50), "");
      this.bloomColor = this.sgGeneral.c("Bloom Color", new BlackOutColor(255, 0, 0, 150), "");
      this.prevCalc = 0L;
      this.white = new BlackOutColor(255, 255, 255, 255);
      this.sources = new ArrayList();
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (System.currentTimeMillis() - this.prevCalc > 100L) {
         this.find(BlackOut.mc.field_1724.method_24515(), (int)Math.ceil((Double)this.range.get()), (Double)this.range.get() * (Double)this.range.get());
         this.prevCalc = System.currentTimeMillis();
      }

      this.render();
   }

   @Event
   public void onRenderHud(RenderEvent.Hud.Pre event) {
      FrameBuffer buffer = Managers.FRAME_BUFFER.getBuffer("sourceESP");
      FrameBuffer bloomBuffer = Managers.FRAME_BUFFER.getBuffer("sourceESP-bloom");
      RenderUtils.renderBufferWith(buffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
         setup.color("clr", ((BlackOutColor)this.fillColor.get()).getRGB());
      }));
      if ((Integer)this.bloom.get() > 0) {
         bloomBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
         bloomBuffer.bind(true);
         RenderUtils.renderBufferWith(buffer, Shaders.screentex, new ShaderSetup((setup) -> {
            setup.set("alpha", 1.0F);
         }));
         bloomBuffer.unbind();
         RenderUtils.blurBufferBW("sourceESP-bloom", (Integer)this.bloom.get() + 1);
         bloomBuffer.bind(true);
         Renderer.setTexture(buffer.getTexture(), 1);
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.subtract, new ShaderSetup((setup) -> {
            setup.set("uTexture0", 0);
            setup.set("uTexture1", 1);
         }));
         bloomBuffer.unbind();
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
            setup.color("clr", ((BlackOutColor)this.bloomColor.get()).getRGB());
         }));
      }
   }

   private void render() {
      FrameBuffer buffer = Managers.FRAME_BUFFER.getBuffer("sourceESP");
      buffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
      buffer.bind(true);
      this.sources.forEach(this::renderShader);
      buffer.unbind();
   }

   private void renderShader(Pair<class_2338, Boolean> pair) {
      Render3DUtils.box(BoxUtils.get((class_2338)pair.method_15442()), this.white, (BlackOutColor)null, RenderShape.Sides);
   }

   private void find(class_2338 center, int r, double rangeSq) {
      this.sources.clear();

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               if (!((double)(x * x + y * y + z * z) > rangeSq)) {
                  class_2338 pos = center.method_10069(x, y, z);
                  class_3610 fluidState = BlackOut.mc.field_1687.method_8316(pos);
                  if (!fluidState.method_15769() && fluidState.method_15771()) {
                     if (fluidState.method_15767(class_3486.field_15517)) {
                        if ((Boolean)this.water.get()) {
                           this.sources.add(new Pair(pos, true));
                        }
                     } else if ((Boolean)this.lava.get() && fluidState.method_15767(class_3486.field_15518)) {
                        this.sources.add(new Pair(pos, false));
                     }
                  }
               }
            }
         }
      }

   }
}
