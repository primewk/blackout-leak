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

public class DevInfo extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final TextColorMultiSetting textColor;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> rounded;
   private final Setting<Boolean> typeColor;
   private final BackgroundMultiSetting background;
   private final List<HudElement.Component> components;
   private float offset;

   public DevInfo() {
      super("Dev Info", ".");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.rounded = this.sgGeneral.b("Rounded", true, "Renders a background", () -> {
         return (Boolean)this.bg.get() || (Boolean)this.blur.get();
      });
      this.typeColor = this.sgGeneral.b("Use Type Color", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.components = new ArrayList();
      this.offset = 0.0F;
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.components.clear();
         BlackOut.Type var10000 = BlackOut.TYPE;
         String text = "Private " + var10000 + " Build - 2.0.0";
         this.components.add(new HudElement.Component("Private "));
         this.components.add(new HudElement.Component(BlackOut.TYPE.name(), (Boolean)this.typeColor.get() ? BlackOut.TYPECOLOR : null, true));
         this.components.add(new HudElement.Component(" Build - 2.0.0"));
         this.stack.method_22903();
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, BlackOut.FONT.getWidth(text), BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, 0.0F, BlackOut.FONT.getWidth(text), BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 3.0F);
         }

         this.setSize(BlackOut.FONT.getWidth(text), BlackOut.FONT.getHeight());
         this.offset = 0.0F;
         this.components.forEach((component) -> {
            if (component.color == null) {
               this.textColor.render(this.stack, component.text, 1.0F, this.offset, 0.0F, false, false, component.bold);
            } else if (component.bold) {
               BlackOut.BOLD_FONT.text(this.stack, component.text, 1.0F, this.offset, 0.0F, component.color, false, false);
            } else {
               BlackOut.FONT.text(this.stack, component.text, 1.0F, this.offset, 0.0F, component.color, false, false);
            }

            this.offset += component.width;
         });
         this.stack.method_22909();
      }
   }
}
