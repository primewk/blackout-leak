package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.RoundedColorMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_4587;

public class ArmorHUD extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Boolean> reversed;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> armorBG;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> shadow;
   private final Setting<Boolean> bar;
   private final Setting<Boolean> text;
   private final Setting<Boolean> centerText;
   private final BackgroundMultiSetting background;
   private final RoundedColorMultiSetting armorBar;
   private final TextColorMultiSetting textColor;

   public ArmorHUD() {
      super("Armor HUD", ".");
      this.reversed = this.sgGeneral.b("Reversed", false, ".");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.armorBG = this.sgGeneral.b("Armor BG", true, ".");
      this.blur = this.sgGeneral.b("Blur", true, "Renders a Blur effect");
      this.shadow = this.sgGeneral.b("Shadow", true, "Renders a Shadow");
      this.bar = this.sgGeneral.b("% Bar", false, "Renders a bar");
      this.text = this.sgGeneral.b("% Text", true, ".");
      this.centerText = this.sgGeneral.b("Center Text", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.armorBar = RoundedColorMultiSetting.of(this.sgGeneral, "Armor Bar");
      this.textColor = TextColorMultiSetting.of(this.sgColor, "Text");
      this.setSize(80.0F, 19.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.setSize(80.0F, 19.0F);
         this.stack.method_22903();
         this.draw(this.stack);
         this.stack.method_22909();
      }
   }

   private void draw(class_4587 stack) {
      float bgHeight = (Boolean)this.bar.get() ? 20.0F : 14.0F;
      if ((Boolean)this.blur.get()) {
         RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
            renderer.rounded(0.0F, 0.0F, 80.0F, bgHeight, 3.0F, 10);
         });
         Renderer.onHUDBlur();
      }

      if ((Boolean)this.bg.get()) {
         this.background.render(stack, 0.0F, 0.0F, 80.0F, bgHeight, 3.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F);
      }

      for(int i = 0; i < 4; ++i) {
         class_1799 itemStack = (class_1799)BlackOut.mc.field_1724.method_31548().field_7548.get((Boolean)this.reversed.get() ? i : 3 - i);
         int durabilityPercentage = Math.round((float)((itemStack.method_7936() - itemStack.method_7919()) * 100) / (float)itemStack.method_7936());
         float durability = (float)durabilityPercentage / 100.0F;
         if ((Boolean)this.armorBG.get()) {
            if (this.background.isStatic()) {
               this.background.render(stack, (float)(2 + 22 * i), 2.0F, 10.0F, 10.0F, 3.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F);
            } else {
               RenderUtils.rounded(stack, (float)(2 + 22 * i), 2.0F, 10.0F, 10.0F, 3.0F, 0.0F, (new Color(0, 0, 0, 100)).getRGB(), Color.BLACK.getRGB());
            }
         }

         if (!itemStack.method_7960()) {
            RenderUtils.renderItem(stack, itemStack.method_7909(), (float)(-1 + 22 * i), -1.0F, 16.0F);
            class_2487 nbt = itemStack.method_7969();
            if (nbt == null || !nbt.method_10545("Unbreakable") || !nbt.method_10577("Unbreakable")) {
               if ((Boolean)this.text.get()) {
                  this.textColor.render(stack, durabilityPercentage + " %", 0.6F, (float)(22 * i + ((Boolean)this.centerText.get() ? 7 : 0)), 12.0F, (Boolean)this.centerText.get(), true);
               }

               if ((Boolean)this.bar.get()) {
                  Color yes = new Color(0, 0, 0, 85);
                  RenderUtils.rounded(stack, (float)(22 * i), 19.0F, 14.0F, 0.3F, 1.0F, 0.0F, yes.getRGB(), yes.getRGB());
                  this.armorBar.render(stack, (float)(22 * i), 19.0F, 14.0F * durability, 0.3F, 1.0F, 0.0F);
               }
            }
         }
      }

   }

   private boolean armorFound() {
      for(int i = 0; i < 4; ++i) {
         class_1799 itemStack = (class_1799)BlackOut.mc.field_1724.method_31548().field_7548.get(i);
         if (!itemStack.method_7960()) {
            return true;
         }
      }

      return false;
   }
}
