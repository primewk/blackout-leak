package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_241;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public class PhaseESP extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<String> infoText;
   private final Setting<Double> scale;
   private final Setting<Double> scaleInc;
   private final Setting<Double> yOffset;
   public final Setting<Boolean> bg;
   public final Setting<Boolean> rounded;
   public final Setting<Boolean> shadow;
   private final Setting<Boolean> blur;
   private final Setting<BlackOutColor> txt;
   private final Setting<BlackOutColor> bgClose;
   private final Setting<BlackOutColor> bgFar;
   private final Setting<BlackOutColor> shdwClose;
   private final Setting<BlackOutColor> shdwFar;
   private final List<class_1297> players;
   private final class_4587 stack;

   public PhaseESP() {
      super("Phase ESP", "Renders a text on players if they are phased", SubCategory.ENTITIES, true);
      this.infoText = this.sgGeneral.s("Info Text", "Phased", "What to say on the tag");
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.scaleInc = this.sgGeneral.d("Scale Increase", 1.0D, 0.0D, 5.0D, 0.05D, "How much should the scale increase when enemy is further away.");
      this.yOffset = this.sgGeneral.d("Y", 0.0D, 0.0D, 1.0D, 0.01D, ".");
      this.bg = this.sgGeneral.b("Background", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.rounded = var10001.b("Rounded", true, ".", var10005::get);
      var10001 = this.sgGeneral;
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.shadow = var10001.b("Shadow", true, ".", var10005::get);
      var10001 = this.sgGeneral;
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.blur = var10001.b("Blur", true, ".", var10005::get);
      this.txt = this.sgColor.c("Text Color", new BlackOutColor(255, 255, 255, 255), ".");
      var10001 = this.sgColor;
      BlackOutColor var10003 = new BlackOutColor(8, 8, 8, 120);
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.bgClose = var10001.c("Background Close", var10003, ".", var10005::get);
      var10001 = this.sgColor;
      var10003 = new BlackOutColor(0, 0, 0, 120);
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.bgFar = var10001.c("Background Far", var10003, ".", var10005::get);
      var10001 = this.sgColor;
      var10003 = new BlackOutColor(8, 8, 8, 100);
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.shdwClose = var10001.c("Shadow Close", var10003, ".", var10005::get);
      var10001 = this.sgColor;
      var10003 = new BlackOutColor(0, 0, 0, 100);
      var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.shdwFar = var10001.c("Shadow Far", var10003, ".", var10005::get);
      this.players = new ArrayList();
      this.stack = new class_4587();
   }

   private String getText() {
      String dn = (String)this.infoText.get();
      return dn.isEmpty() ? "Phased" : dn;
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.players.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (this.shouldRender(entity)) {
               this.players.add(entity);
            }

         });
         this.players.sort(Comparator.comparingDouble((entity) -> {
            return -BlackOut.mc.field_1773.method_19418().method_19326().method_1022(entity.method_19538());
         }));
      }
   }

   public void renderNameTag(double tickDelta, class_1297 entity) {
      double x = class_3532.method_16436(tickDelta, entity.field_6014, entity.method_23317());
      double y = class_3532.method_16436(tickDelta, entity.field_6036, entity.method_23318());
      double z = class_3532.method_16436(tickDelta, entity.field_5969, entity.method_23321());
      float d = (float)BlackOut.mc.field_1773.method_19418().method_19326().method_1023(x, y, z).method_1033();
      float s = this.getScale(d);
      this.stack.method_22903();
      class_241 f = RenderUtils.getCoords(x, y - (Double)this.yOffset.get(), z, true);
      if (f == null) {
         this.stack.method_22909();
      } else {
         this.stack.method_46416(f.field_1343, f.field_1342, 0.0F);
         this.stack.method_22905(s, s, s);
         String text = this.getText();
         float length = BlackOut.FONT.getWidth(text);
         this.stack.method_22903();
         this.stack.method_46416(-length / 2.0F, -9.0F, 0.0F);
         double easedValue = AnimUtils.easeOutQuint(class_3532.method_15350((double)d / 100.0D, 0.0D, 1.0D));
         Color color = ColorUtils.lerpColor(easedValue, ((BlackOutColor)this.bgClose.get()).getColor(), ((BlackOutColor)this.bgFar.get()).getColor());
         Color shadowColor = ColorUtils.lerpColor(easedValue, ((BlackOutColor)this.shdwClose.get()).getColor(), ((BlackOutColor)this.shdwFar.get()).getColor());
         if ((Boolean)this.bg.get()) {
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(-2.0F, -5.0F, length + 4.0F, 10.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            RenderUtils.rounded(this.stack, -2.0F, -5.0F, length + 4.0F, 10.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F, color.getRGB(), shadowColor.getRGB());
         }

         BlackOut.FONT.text(this.stack, text, 1.0F, 0.0F, 0.0F, ((BlackOutColor)this.txt.get()).getColor(), false, true);
         this.stack.method_22909();
         this.stack.method_22909();
      }
   }

   public boolean shouldRender(class_1297 entity) {
      if (!(entity instanceof class_1657)) {
         return false;
      } else {
         AntiBot antiBot = AntiBot.getInstance();
         if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && entity instanceof class_742 && antiBot.getBots().contains(entity)) {
            return false;
         } else if (!OLEPOSSUtils.inside(entity, entity.method_5829().method_35580(0.04D, 0.06D, 0.04D))) {
            return false;
         } else {
            return entity != BlackOut.mc.field_1724 ? true : Freecam.getInstance().enabled;
         }
      }
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         GlStateManager._disableDepthTest();
         GlStateManager._enableBlend();
         GlStateManager._disableCull();
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         this.players.forEach((entity) -> {
            this.renderNameTag((double)event.tickDelta, entity);
         });
         this.stack.method_22909();
      }
   }

   private float getScale(float d) {
      float distSqrt = (float)Math.sqrt((double)d);
      return ((Double)this.scale.get()).floatValue() * 8.0F / distSqrt + ((Double)this.scaleInc.get()).floatValue() / 20.0F * distSqrt;
   }
}
