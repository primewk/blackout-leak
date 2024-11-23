package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.font.CustomFontRenderer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Keybinds extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final TextColorMultiSetting textColor;
   private final Setting<BlackOutColor> bindColor;
   private final Setting<Boolean> bg;
   private final BackgroundMultiSetting background;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> rounded;
   private int i;
   private boolean checked;
   private float width;
   private float length;

   public Keybinds() {
      super("Keybinds", "Shows currently enabled bound modules.");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.bindColor = this.sgGeneral.c("Bind Color", new BlackOutColor(128, 128, 128, 50), ".");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.rounded = this.sgGeneral.b("Rounded", true, "", () -> {
         return (Boolean)this.bg.get() || (Boolean)this.blur.get();
      });
      this.i = 0;
      this.checked = false;
      this.width = 0.0F;
      this.length = 0.0F;
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         Comparator<Module> comparator = Comparator.comparingDouble((m) -> {
            CustomFontRenderer var10000 = BlackOut.FONT;
            String var10001 = m.getDisplayName();
            return (double)var10000.getWidth(var10001 + ((KeyBind)m.bind.get()).getName());
         });
         List<Module> modules = Managers.MODULE.getModules().stream().filter((module) -> {
            if (!module.enabled) {
               return false;
            } else {
               return ((KeyBind)module.bind.get()).value != null;
            }
         }).sorted(comparator.reversed()).toList();
         this.i = 0;
         this.stack.method_22903();
         this.checked = false;
         modules.forEach((module) -> {
            String text = module.getDisplayName();
            String bind = " [" + ((KeyBind)module.bind.get()).getName() + "]";
            if (!this.checked) {
               this.width = BlackOut.FONT.getWidth(text + bind);
               this.length = ((BlackOut.FONT.getHeight() + 3.0F) * (float)this.i + BlackOut.FONT.getHeight() + 3.0F) * (float)modules.size();
               this.checked = true;
               if ((Boolean)this.blur.get()) {
                  RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                     renderer.rounded(0.0F, (BlackOut.FONT.getHeight() + 3.0F) * (float)this.i, this.width + 8.0F, this.length + 3.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
                  });
                  Renderer.onHUDBlur();
               }

               if ((Boolean)this.bg.get()) {
                  this.background.render(this.stack, 0.0F, 0.0F, this.width + 8.0F, this.length + 3.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 3.0F);
               }

               this.setSize(this.width + 8.0F, this.length + 3.0F);
            }

            this.textColor.render(this.stack, text, 1.0F, 4.0F, (BlackOut.FONT.getHeight() + 3.0F) * (float)this.i + 3.0F, false, false);
            BlackOut.FONT.text(this.stack, bind, 1.0F, 4.0F + BlackOut.FONT.getWidth(text), (BlackOut.FONT.getHeight() + 3.0F) * (float)this.i + 3.0F, ((BlackOutColor)this.bindColor.get()).getColor(), false, false);
            ++this.i;
         });
         this.stack.method_22909();
      }
   }
}
