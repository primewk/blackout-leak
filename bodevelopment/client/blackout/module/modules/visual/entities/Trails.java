package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.functional.DoubleFunction;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Trails extends Module {
   private static Trails INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<List<class_1299<?>>> entities;
   private final Setting<Trails.HeightMode> renderHeight;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeTime;
   private final Setting<Double> maxFrequency;
   private final Setting<Double> lineWidth;
   public final Setting<Trails.ColorMode> colorMode;
   private final Setting<Double> speed;
   private final Setting<Double> saturation;
   private final Setting<BlackOutColor> clr;
   private final Setting<BlackOutColor> clr1;
   private final Map<class_1297, Trails.Line> map;
   private long prevAdd;

   public Trails() {
      super("Trails", "Renders lines behind entities.", SubCategory.ENTITIES, true);
      this.entities = this.sgGeneral.el("Entities", "", class_1299.field_6082);
      this.renderHeight = this.sgGeneral.e("Render Height", Trails.HeightMode.Feet, "");
      this.renderTime = this.sgGeneral.d("Render Time", 0.0D, 0.0D, 10.0D, 0.1D, ".");
      this.fadeTime = this.sgGeneral.d("Fade Time", 5.0D, 0.0D, 10.0D, 0.1D, ".");
      this.maxFrequency = this.sgGeneral.d("Max Frequency", 40.0D, 1.0D, 100.0D, 1.0D, ".");
      this.lineWidth = this.sgGeneral.d("Line Width", 2.5D, 0.5D, 5.0D, 0.05D, ".");
      this.colorMode = this.sgColor.e("Color Mode", Trails.ColorMode.Custom, "What color to use");
      this.speed = this.sgColor.d("Wave Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return this.colorMode.get() == Trails.ColorMode.Wave;
      });
      this.saturation = this.sgColor.d("Rainbow Saturation", 0.8D, 0.0D, 1.0D, 0.1D, ".", () -> {
         return this.colorMode.get() == Trails.ColorMode.Rainbow;
      });
      this.clr = this.sgColor.c("Line Color", new BlackOutColor(255, 255, 255, 255), ".", () -> {
         return this.colorMode.get() != Trails.ColorMode.Rainbow;
      });
      this.clr1 = this.sgColor.c("Wave Color", new BlackOutColor(175, 175, 175, 255), ".", () -> {
         return this.colorMode.get() != Trails.ColorMode.Rainbow;
      });
      this.map = new HashMap();
      this.prevAdd = 0L;
      INSTANCE = this;
   }

   public static Trails getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.addPositions((double)event.tickDelta);
         class_4587 stack = Render3DUtils.matrices;
         class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
         stack.method_22903();
         Render3DUtils.setRotation(stack);
         Render3DUtils.start();
         this.map.forEach((entity, line) -> {
            Color lineColor = this.getColor();
            line.render(stack, camPos, lineColor, (Double)this.renderTime.get(), (Double)this.fadeTime.get());
         });
         Render3DUtils.end();
         stack.method_22909();
      }
   }

   private void addPositions(double tickDelta) {
      if (!((double)(System.currentTimeMillis() - this.prevAdd) < 1000.0D / (Double)this.maxFrequency.get())) {
         this.prevAdd = System.currentTimeMillis();
         BlackOut.mc.field_1687.method_18112().forEach((entity) -> {
            if (((List)this.entities.get()).contains(entity.method_5864())) {
               if (this.map.containsKey(entity)) {
                  ((Trails.Line)this.map.get(entity)).positions.add(new Pair((class_243)((Trails.HeightMode)this.renderHeight.get()).function.apply(entity, tickDelta), System.currentTimeMillis()));
               } else {
                  Trails.Line line = new Trails.Line();
                  line.positions.add(new Pair((class_243)((Trails.HeightMode)this.renderHeight.get()).function.apply(entity, tickDelta), System.currentTimeMillis()));
                  this.map.put(entity, line);
               }

            }
         });
         this.map.entrySet().removeIf((entry) -> {
            return ((Trails.Line)entry.getValue()).positions.isEmpty();
         });
      }
   }

   private Color getColor() {
      Color color = Color.WHITE;
      switch((Trails.ColorMode)this.colorMode.get()) {
      case Custom:
         color = ((BlackOutColor)this.clr.get()).getColor();
         break;
      case Rainbow:
         int rainbowColor = ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 150L);
         color = new Color(rainbowColor >> 16 & 255, rainbowColor >> 8 & 255, rainbowColor & 255, ((BlackOutColor)this.clr.get()).alpha);
         break;
      case Wave:
         color = ColorUtils.getWave(((BlackOutColor)this.clr.get()).getColor(), ((BlackOutColor)this.clr1.get()).getColor(), (Double)this.speed.get(), 1.0D, 1);
      }

      return color;
   }

   public static enum HeightMode {
      Feet(OLEPOSSUtils::getLerpedPos),
      Middle((entity, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(entity, tickDelta).method_1031(0.0D, entity.method_5829().method_17940() / 2.0D, 0.0D);
      });

      private final DoubleFunction<class_1297, Double, class_243> function;

      private HeightMode(DoubleFunction<class_1297, Double, class_243> function) {
         this.function = function;
      }

      // $FF: synthetic method
      private static Trails.HeightMode[] $values() {
         return new Trails.HeightMode[]{Feet, Middle};
      }
   }

   public static enum ColorMode {
      Rainbow,
      Custom,
      Wave;

      // $FF: synthetic method
      private static Trails.ColorMode[] $values() {
         return new Trails.ColorMode[]{Rainbow, Custom, Wave};
      }
   }

   private static class Line {
      private final List<Pair<class_243, Long>> positions = new ArrayList();

      private void render(class_4587 stack, class_243 camPos, Color color, double renderTime, double fadeTime) {
         this.positions.removeIf((pairx) -> {
            return (double)(System.currentTimeMillis() - (Long)pairx.method_15441()) > (renderTime + fadeTime) * 1000.0D;
         });
         if (this.positions.size() >= 2) {
            RenderSystem.setShader(class_757::method_34535);
            class_289 tessellator = class_289.method_1348();
            class_287 bufferBuilder = tessellator.method_1349();
            bufferBuilder.method_1328(class_5596.field_27377, class_290.field_29337);
            RenderSystem.lineWidth(((Double)Trails.getInstance().lineWidth.get()).floatValue());
            Matrix4f matrix4f = stack.method_23760().method_23761();
            Matrix3f normalMatrix = stack.method_23760().method_23762();
            float r = (float)color.getRed() / 255.0F;
            float g = (float)color.getGreen() / 255.0F;
            float b = (float)color.getBlue() / 255.0F;
            float a = (float)color.getAlpha() / 255.0F;

            for(int i = 0; i < this.positions.size() - 1; ++i) {
               Pair<class_243, Long> pair = (Pair)this.positions.get(i);
               Pair<class_243, Long> nextPair = (Pair)this.positions.get(i + 1);
               class_243 vec = (class_243)pair.method_15442();
               class_243 nextVec = (class_243)nextPair.method_15442();
               float alpha = this.getAlpha((Long)pair.method_15441(), renderTime, fadeTime) * a;
               float alpha2 = this.getAlpha((Long)nextPair.method_15441(), renderTime, fadeTime) * a;
               class_243 diff = nextVec.method_1020(vec);
               class_243 normal = diff.method_1021(1.0D / diff.method_1033());
               bufferBuilder.method_22918(matrix4f, (float)(vec.field_1352 - camPos.field_1352), (float)(vec.field_1351 - camPos.field_1351), (float)(vec.field_1350 - camPos.field_1350)).method_22915(r, g, b, alpha).method_23763(normalMatrix, (float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
               bufferBuilder.method_22918(matrix4f, (float)(nextVec.field_1352 - camPos.field_1352), (float)(nextVec.field_1351 - camPos.field_1351), (float)(nextVec.field_1350 - camPos.field_1350)).method_22915(r, g, b, alpha2).method_23763(normalMatrix, (float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
            }

            tessellator.method_1350();
         }
      }

      private float getAlpha(long time, double renderTime, double fadeTime) {
         return (float)(1.0D - Math.max((double)(System.currentTimeMillis() - time) / 1000.0D - renderTime, 0.0D) / fadeTime);
      }
   }
}
