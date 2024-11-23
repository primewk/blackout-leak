package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Objects;
import net.minecraft.class_304;

public class Keystrokes extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> useBlur;
   private final Setting<Boolean> shadow;
   private final Setting<BlackOutColor> txtdColor;
   private final Setting<BlackOutColor> pressedtxtColor;
   private final Setting<BlackOutColor> backgroundColor;
   private final Setting<BlackOutColor> pressedColor;
   private final Setting<BlackOutColor> shadowColor;
   private final Setting<BlackOutColor> pressedShadow;

   public Keystrokes() {
      super("Keystrokes", "Shows your keystrokes");
      this.useBlur = this.sgGeneral.b("Blur", true, "Uses a blur effect", () -> {
         return true;
      });
      this.shadow = this.sgGeneral.b("Shadow", true, ".", () -> {
         return true;
      });
      this.txtdColor = this.sgGeneral.c("Text Color", new BlackOutColor(255, 255, 255, 255), ".");
      this.pressedtxtColor = this.sgGeneral.c("Pressed Text Color", new BlackOutColor(175, 175, 175, 255), ".");
      this.backgroundColor = this.sgGeneral.c("Background Color", new BlackOutColor(0, 0, 0, 50), "Background Color");
      this.pressedColor = this.sgGeneral.c("Pressed Color", new BlackOutColor(255, 255, 255, 50), "Pressed Color");
      SettingGroup var10001 = this.sgGeneral;
      BlackOutColor var10003 = new BlackOutColor(0, 0, 0, 100);
      Setting var10005 = this.shadow;
      Objects.requireNonNull(var10005);
      this.shadowColor = var10001.c("Shadow Color", var10003, "Shadow Color", var10005::get);
      var10001 = this.sgGeneral;
      var10003 = new BlackOutColor(255, 255, 255, 100);
      var10005 = this.shadow;
      Objects.requireNonNull(var10005);
      this.pressedShadow = var10001.c("Pressed Shadow", var10003, "Pressed Shadow Color", var10005::get);
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         this.setSize(44.0F, 48.0F);
         this.renderKey(18, 0, "W", BlackOut.mc.field_1690.field_1894);
         this.renderKey(0, 18, "A", BlackOut.mc.field_1690.field_1913);
         this.renderKey(18, 18, "S", BlackOut.mc.field_1690.field_1881);
         this.renderKey(36, 18, "D", BlackOut.mc.field_1690.field_1849);
         if ((Boolean)this.useBlur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 36.0F, 44.0F, 8.0F, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         boolean pressed = BlackOut.mc.field_1690.field_1903.method_1434();
         BlackOutColor color = pressed ? (BlackOutColor)this.pressedColor.get() : (BlackOutColor)this.backgroundColor.get();
         RenderUtils.rounded(this.stack, 0.0F, 36.0F, 44.0F, 8.0F, 3.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F, color.getRGB(), color.withAlpha((int)((double)color.alpha * 0.5D)).getRGB());
         RenderUtils.rounded(this.stack, 17.0F, 38.0F, 10.0F, 1.0F, 1.0F, 0.0F, pressed ? ((BlackOutColor)this.pressedtxtColor.get()).getRGB() : ((BlackOutColor)this.txtdColor.get()).getRGB(), ColorUtils.SHADOW100I);
         this.stack.method_22909();
      }
   }

   public void renderKey(int x, int y, String key, class_304 bind) {
      boolean pressed = bind.method_1434();
      if ((Boolean)this.useBlur.get()) {
         RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
            renderer.rounded((float)x, (float)y, 8.0F, 8.0F, 3.0F, 10);
         });
         Renderer.onHUDBlur();
      }

      RenderUtils.rounded(this.stack, (float)x, (float)y, 8.0F, 8.0F, 3.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F, pressed ? ((BlackOutColor)this.pressedColor.get()).getRGB() : ((BlackOutColor)this.backgroundColor.get()).getRGB(), pressed ? ((BlackOutColor)this.pressedShadow.get()).getRGB() : ((BlackOutColor)this.shadowColor.get()).getRGB());
      BlackOut.FONT.text(this.stack, key, 1.0F, (float)(x + 4), (float)(y + 4), pressed ? ((BlackOutColor)this.pressedtxtColor.get()).getColor() : ((BlackOutColor)this.txtdColor.get()).getColor(), true, true);
   }
}
