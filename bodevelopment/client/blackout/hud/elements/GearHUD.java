package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class GearHUD extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Double> textScale;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> shadow;
   private final Setting<List<class_1792>> items;
   private final BackgroundMultiSetting background;
   private final TextColorMultiSetting textColor;

   public GearHUD() {
      super("Gear HUD", ".");
      this.textScale = this.sgScale.d("Text Scale", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.blur = this.sgGeneral.b("Blur", true, "Renders a Blur effect");
      this.shadow = this.sgGeneral.b("Shadow", true, "Renders a Shadow");
      this.items = this.sgGeneral.il("Items", ".", class_1802.field_8301, class_1802.field_8288);
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.textColor = TextColorMultiSetting.of(this.sgColor, "Text");
      this.setSize(32.0F, 64.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         float textWidth = 10.0F;

         class_1792 item;
         for(Iterator var2 = ((List)this.items.get()).iterator(); var2.hasNext(); textWidth = Math.max(textWidth, BlackOut.FONT.getWidth(String.valueOf(this.getAmount(item))) * ((Double)this.textScale.get()).floatValue())) {
            item = (class_1792)var2.next();
         }

         textWidth += 2.0F;
         float backgroundWidth = textWidth + 16.0F;
         float length = (float)(((List)this.items.get()).size() * 16 + ((List)this.items.get()).size() * 6 - 6);
         this.setSize(backgroundWidth, length);
         this.stack.method_22903();
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, backgroundWidth, length, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, 0.0F, backgroundWidth, length, 3.0F, 3.0F);
         }

         Iterator var4 = ((List)this.items.get()).iterator();

         while(var4.hasNext()) {
            class_1792 item = (class_1792)var4.next();
            int amount = this.getAmount(item);
            this.textColor.render(this.stack, String.valueOf(amount), ((Double)this.textScale.get()).floatValue(), textWidth / 2.0F, 8.0F, true, true);
            RenderUtils.renderItem(this.stack, item, textWidth, 0.0F, 16.0F);
            this.stack.method_46416(0.0F, 22.0F, 0.0F);
         }

         this.stack.method_22909();
      }
   }

   private int getAmount(class_1792 item) {
      return InvUtils.count(true, true, (stack) -> {
         return stack.method_7909() == item;
      });
   }
}
