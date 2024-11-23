package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_243;
import net.minecraft.class_7134;

public class Coordinates extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Boolean> otherWorld;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> rounded;
   private final TextColorMultiSetting textColor;
   private final TextColorMultiSetting infoColor;
   private final BackgroundMultiSetting background;
   private final List<HudElement.Component> components;
   private float offset;
   private int i;
   private boolean drawingOther;

   public Coordinates() {
      super("Coordinates", "Shows your current coordinates");
      this.otherWorld = this.sgGeneral.b("Show Other world", true, ".");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.rounded = this.sgGeneral.b("Rounded", true, "Renders a background", () -> {
         return (Boolean)this.bg.get() || (Boolean)this.blur.get();
      });
      this.textColor = TextColorMultiSetting.of(this.sgColor, "Text");
      this.infoColor = TextColorMultiSetting.of(this.sgColor, "Info");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.components = new ArrayList();
      this.offset = 0.0F;
      this.i = 0;
      this.drawingOther = false;
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String text = this.getString(BlackOut.mc.field_1724.method_19538());
         String otherWorldText = "Blackmen Client coordinate calculation system thread 42069 has failed the mathing of the coordinates please hit your pc with a hammer to fix the issue";
         this.stack.method_22903();
         float height;
         float width;
         if (this.drawingOther) {
            if (BlackOut.mc.field_1724.method_37908().method_44013() == class_7134.field_37666) {
               otherWorldText = this.getString(BlackOut.mc.field_1724.method_19538().method_1021(0.125D));
            } else if (BlackOut.mc.field_1724.method_37908().method_44013() == class_7134.field_37667) {
               otherWorldText = this.getString(BlackOut.mc.field_1724.method_19538().method_1021(8.0D));
            }

            height = BlackOut.FONT.getHeight() * 2.0F - 2.0F;
            width = Math.max(BlackOut.FONT.getWidth(text), BlackOut.FONT.getWidth(otherWorldText));
         } else {
            height = BlackOut.FONT.getHeight();
            width = BlackOut.FONT.getWidth(text);
         }

         this.setSize(width, BlackOut.FONT.getHeight());
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, this.drawingOther ? -BlackOut.FONT.getHeight() : 0.0F, width, height, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, this.drawingOther ? -BlackOut.FONT.getHeight() : 0.0F, width, height, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 3.0F);
         }

         this.drawingOther = false;
         this.updateComponents(BlackOut.mc.field_1724.method_19538());
         this.drawComponents(0.0F);
         if ((Boolean)this.otherWorld.get() && BlackOut.mc.field_1724.method_37908().method_44013() != class_7134.field_37668) {
            class_243 pos = null;
            if (BlackOut.mc.field_1724.method_37908().method_44013() == class_7134.field_37666) {
               pos = BlackOut.mc.field_1724.method_19538().method_1021(0.125D);
            } else if (BlackOut.mc.field_1724.method_37908().method_44013() == class_7134.field_37667) {
               pos = BlackOut.mc.field_1724.method_19538().method_1021(8.0D);
            }

            if (pos != null) {
               this.updateComponents(pos);
            }

            this.drawComponents(-BlackOut.FONT.getHeight() - 2.0F);
            this.drawingOther = true;
         }

         this.stack.method_22909();
      }
   }

   private void drawComponents(float y) {
      this.components.forEach((component) -> {
         if (this.i % 2 == 0) {
            this.infoColor.render(this.stack, component.text, 1.0F, this.offset, y, false, false);
         } else {
            this.textColor.render(this.stack, component.text, 1.0F, this.offset, y, false, false);
         }

         this.offset += component.width;
         ++this.i;
      });
   }

   private String getString(class_243 pos) {
      return String.format("X: %.1f Y: %.1f Z: %.1f", pos.field_1352, pos.field_1351, pos.field_1350);
   }

   private void updateComponents(class_243 pos) {
      this.i = 1;
      this.offset = 0.0F;
      this.components.clear();
      this.components.add(new HudElement.Component("X: "));
      List var10000 = this.components;
      String var10003 = this.format(pos.field_1352);
      var10000.add(new HudElement.Component(var10003 + " "));
      this.components.add(new HudElement.Component("Y: "));
      var10000 = this.components;
      var10003 = this.format(pos.field_1351);
      var10000.add(new HudElement.Component(var10003 + " "));
      this.components.add(new HudElement.Component("Z: "));
      this.components.add(new HudElement.Component(this.format(pos.field_1350)));
   }

   private String format(Double d) {
      return String.format("%.1f", d);
   }
}
