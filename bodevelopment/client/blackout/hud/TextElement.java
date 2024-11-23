package bodevelopment.client.blackout.hud;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Objects;
import net.minecraft.class_4587;

public class TextElement extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> rounded;
   private final BackgroundMultiSetting background;
   private final TextColorMultiSetting textColor;
   private final TextColorMultiSetting infoColor;

   public TextElement(String name, String description) {
      super(name, description);
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.blur = this.sgGeneral.b("Blur", true, "Renders a Blur effect");
      this.rounded = this.sgGeneral.b("Rounded", true, "Renders a background", () -> {
         return (Boolean)this.bg.get() || (Boolean)this.blur.get();
      });
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.textColor = TextColorMultiSetting.of(this.sgColor, "Text");
      this.infoColor = TextColorMultiSetting.of(this.sgColor, "Info");
   }

   protected void drawElement(class_4587 stack, String text, String info) {
      stack.method_22903();
      float width = BlackOut.FONT.getWidth(text + " " + info);
      this.setSize(width, BlackOut.FONT.getHeight());
      if ((Boolean)this.blur.get()) {
         RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
            renderer.rounded(0.0F, 0.0F, width, BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
         });
         Renderer.onHUDBlur();
      }

      if ((Boolean)this.bg.get()) {
         this.background.render(stack, 0.0F, 0.0F, width, BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 3.0F);
      }

      this.textColor.render(stack, text, 1.0F, 0.0F, 0.0F, false, false);
      this.infoColor.render(stack, info, 1.0F, BlackOut.FONT.getWidth(text + " "), 0.0F, false, false);
      stack.method_22909();
   }
}
