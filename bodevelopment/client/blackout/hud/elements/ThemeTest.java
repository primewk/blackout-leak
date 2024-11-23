package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;

public class ThemeTest extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> useBlur;
   private final Setting<Boolean> shadow;
   private int i;
   private float x;
   private float y;

   public ThemeTest() {
      super("Theme Test", ".");
      this.useBlur = this.sgGeneral.b("Blur", true, "Uses a blur effect", () -> {
         return true;
      });
      this.shadow = this.sgGeneral.b("Shadow", true, ".", () -> {
         return true;
      });
      this.i = 0;
      this.x = 0.0F;
      this.y = 0.0F;
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         this.setSize(20.0F, 20.0F);
         ThemeSettings themeSettings = ThemeSettings.getInstance();
         this.i = 0;
         this.x = 0.0F;
         this.y = 0.0F;
         themeSettings.getThemes().forEach((theme) -> {
            if ((Boolean)this.useBlur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(this.x, this.y, 100.0F, 30.0F, 2.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            RenderUtils.tenaRounded(this.stack, this.x, this.y, 100.0F, 30.0F, 2.0F, (Boolean)this.shadow.get() ? 2.0F : 0.0F, theme.mainWithAlpha(175), theme.secondaryWithAlpha(175), 1.5F);
            BlackOut.BOLD_FONT.text(this.stack, theme.getName(), 1.0F, this.x + 50.0F, this.y + 15.0F, Color.WHITE.getRGB(), true, true);
            ++this.i;
            this.x += 108.0F;
            if (this.i % 5 == 0) {
               this.y += 38.0F;
               this.x = 0.0F;
            }

         });
         this.stack.method_22909();
      }
   }
}
