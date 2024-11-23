package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import net.minecraft.class_1297;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class Breadcrumbs extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Boolean> onlyMoving;
   private final Setting<Double> size;
   private final Setting<Double> delay;
   private final Setting<Double> renderTime;
   public final Setting<Breadcrumbs.ColorMode> colorMode;
   private final Setting<Double> saturation;
   private final Setting<BlackOutColor> clr;
   private final Setting<BlackOutColor> clr1;
   private final Setting<Integer> iAlpha;
   private final Setting<Integer> oAlpha;
   private final class_4587 stack;
   private final RenderList<class_243> list;
   private long lastAddition;

   public Breadcrumbs() {
      super("Breadcrumbs", "Draws a trail behind you with particless", SubCategory.ENTITIES, true);
      this.onlyMoving = this.sgGeneral.b("Only Moving", true, ".");
      this.size = this.sgGeneral.d("Size", 3.0D, 1.0D, 10.0D, 0.1D, ".");
      this.delay = this.sgGeneral.d("Delay", 0.1D, 0.0D, 3.0D, 0.01D, ".");
      this.renderTime = this.sgGeneral.d("Render Time", 3.0D, 0.001D, 20.0D, 0.05D, ".");
      this.colorMode = this.sgColor.e("Color Mode", Breadcrumbs.ColorMode.Custom, "What color to use");
      this.saturation = this.sgColor.d("Rainbow Saturation", 0.8D, 0.0D, 1.0D, 0.1D, ".", () -> {
         return this.colorMode.get() == Breadcrumbs.ColorMode.Rainbow;
      });
      this.clr = this.sgColor.c("Inside Color", new BlackOutColor(255, 255, 255, 100), ".");
      this.clr1 = this.sgColor.c("Outside Color", new BlackOutColor(175, 175, 175, 100), ".");
      this.iAlpha = this.sgColor.i("Inside Alpha", 150, 0, 255, 1, ".", () -> {
         return this.colorMode.get() == Breadcrumbs.ColorMode.Rainbow;
      });
      this.oAlpha = this.sgColor.i("Outside Alpha", 50, 0, 255, 1, ".", () -> {
         return this.colorMode.get() == Breadcrumbs.ColorMode.Rainbow;
      });
      this.stack = new class_4587();
      this.list = RenderList.getList(true);
      this.lastAddition = System.currentTimeMillis();
   }

   @Event
   public void onGameJoin(GameJoinEvent event) {
      this.list.clear();
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         if ((double)this.lastAddition + (Double)this.delay.get() * 1000.0D < (double)System.currentTimeMillis()) {
            this.addDot((double)event.tickDelta, BlackOut.mc.field_1724);
            this.lastAddition = System.currentTimeMillis();
         }

         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         this.list.update((pos, time, d) -> {
            this.drawDot(this.stack, pos, d);
         });
         this.stack.method_22909();
      }
   }

   private void addDot(double tickDelta, class_1297 entity) {
      if (!(Boolean)this.onlyMoving.get() || entity.field_6014 != entity.method_23317() || entity.field_6036 != entity.method_23318() || entity.field_5969 != entity.method_23321()) {
         double x = class_3532.method_16436(tickDelta, entity.field_6014, entity.method_23317());
         double y = class_3532.method_16436(tickDelta, entity.field_6036, entity.method_23318());
         double z = class_3532.method_16436(tickDelta, entity.field_5969, entity.method_23321());
         this.list.add(new class_243(x, y, z), (Double)this.renderTime.get());
      }
   }

   private void drawDot(class_4587 stack, class_243 vec, double delta) {
      class_241 f = RenderUtils.getCoords(vec.field_1352, vec.field_1351, vec.field_1350, true);
      if (f != null) {
         Color[] colors = this.getColors();
         Color color1 = colors[0];
         Color color2 = colors[1];
         float alpha = (float)(1.0D - delta);
         float prevAlpha = Renderer.getAlpha();
         Renderer.setAlpha(alpha);
         float s = ((Double)this.size.get()).floatValue();
         RenderUtils.rounded(stack, f.field_1343, f.field_1342, 0.0F, 0.0F, s * 2.0F, s * 2.0F, color2.getRGB(), color2.getRGB());
         RenderUtils.rounded(stack, f.field_1343, f.field_1342, 0.0F, 0.0F, s, s, color1.getRGB(), color1.getRGB());
         Renderer.setAlpha(prevAlpha);
      }
   }

   private Color[] getColors() {
      Color[] colors = new Color[2];
      switch((Breadcrumbs.ColorMode)this.colorMode.get()) {
      case Custom:
         colors[0] = ((BlackOutColor)this.clr.get()).getColor();
         colors[1] = ((BlackOutColor)this.clr1.get()).getColor();
         break;
      case Rainbow:
         int rainbowColor = ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 150L);
         colors[0] = new Color(rainbowColor >> 16 & 255, rainbowColor >> 8 & 255, rainbowColor & 255, (Integer)this.iAlpha.get());
         rainbowColor = ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 300L);
         colors[1] = new Color(rainbowColor >> 16 & 255, rainbowColor >> 8 & 255, rainbowColor & 255, (Integer)this.oAlpha.get());
         break;
      case Wave:
         colors[0] = ColorUtils.getWave(((BlackOutColor)this.clr.get()).getColor(), ((BlackOutColor)this.clr1.get()).getColor(), 1.0D, 1.0D, 1);
         colors[1] = ColorUtils.getWave(((BlackOutColor)this.clr.get()).getColor(), ((BlackOutColor)this.clr1.get()).getColor(), 1.0D, 1.0D, 1);
      }

      return colors;
   }

   public static enum ColorMode {
      Rainbow,
      Custom,
      Wave;

      // $FF: synthetic method
      private static Breadcrumbs.ColorMode[] $values() {
         return new Breadcrumbs.ColorMode[]{Rainbow, Custom, Wave};
      }
   }
}
