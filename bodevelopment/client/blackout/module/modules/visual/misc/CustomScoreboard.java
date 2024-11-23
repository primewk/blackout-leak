package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.class_4587;

public class CustomScoreboard extends Module {
   private static CustomScoreboard INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   public final Setting<Boolean> remove;
   private final Setting<Double> scale;
   private final Setting<Integer> addedY;
   public final Setting<Boolean> useFont;
   public final Setting<Boolean> blur;
   public final Setting<Boolean> background;
   public final Setting<Boolean> shadow;
   public final TextColorMultiSetting textColor;
   public final Setting<BlackOutColor> bgColor;
   public final Setting<BlackOutColor> shadowColor;
   private final class_4587 stack;
   public String objectiveName;
   public Color objectiveColor;
   public Collection<String> texts;
   private float y;

   public CustomScoreboard() {
      super("Scoreboard", "Modifies the games scoreboard", SubCategory.MISC_VISUAL, true);
      this.remove = this.sgGeneral.b("Remove", false, "Stops the scoreboard from rendering");
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 10.0D, 0.05D, "0");
      this.addedY = this.sgGeneral.i("Added Y", 0, 0, 500, 10, ".");
      this.useFont = this.sgGeneral.b("Use CustomFont", true, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.blur = this.sgGeneral.b("Blur", true, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.background = this.sgGeneral.b("Background", true, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.shadow = this.sgGeneral.b("Shadow", true, ".", () -> {
         return !(Boolean)this.remove.get() && (Boolean)this.background.get();
      });
      this.textColor = TextColorMultiSetting.of(this.sgColor, () -> {
         return (Boolean)this.useFont.get() && !(Boolean)this.remove.get();
      }, "Text");
      this.bgColor = this.sgColor.c("Background Color", new BlackOutColor(0, 0, 0, 50), ".", () -> {
         return (Boolean)this.background.get() && !(Boolean)this.remove.get();
      });
      this.shadowColor = this.sgColor.c("Shadow Color", new BlackOutColor(0, 0, 0, 100), ".", () -> {
         return (Boolean)this.background.get() && (Boolean)this.shadow.get() && !(Boolean)this.remove.get();
      });
      this.stack = new class_4587();
      this.y = 0.0F;
      INSTANCE = this;
   }

   public static CustomScoreboard getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.objectiveName != null && this.texts != null && !(Boolean)this.remove.get()) {
            float width = Math.max(this.getLongest(this.texts) * 2.0F + 20.0F, BlackOut.FONT.getWidth(this.objectiveName) * 2.0F + 40.0F);
            float length = (float)(this.texts.size() + 2) * BlackOut.FONT.getHeight() * 2.0F + 6.0F;
            this.stack.method_22903();
            RenderUtils.unGuiScale(this.stack);
            this.stack.method_22904((double)((float)BlackOut.mc.method_22683().method_4480()) - (double)(width + 8.0F) * (Double)this.scale.get(), (double)((float)BlackOut.mc.method_22683().method_4507() / 2.0F + (float)(Integer)this.addedY.get()), 0.0D);
            this.stack.method_22905(((Double)this.scale.get()).floatValue(), ((Double)this.scale.get()).floatValue(), 0.0F);
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(0.0F, 0.0F, width, length + 4.0F, 6.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.background.get()) {
               RenderUtils.rounded(this.stack, 0.0F, 0.0F, width, length + 4.0F, 6.0F, 6.0F, ((BlackOutColor)this.bgColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
            }

            BlackOut.FONT.text(this.stack, this.objectiveName, 2.0F, width / 2.0F, 0.0F, this.objectiveColor, true, false);
            this.y = BlackOut.FONT.getHeight() * 2.0F + 4.0F;
            this.texts.forEach((text) -> {
               this.textColor.render(this.stack, text, 2.0F, 0.0F, this.y, false, false);
               this.y += BlackOut.FONT.getHeight() * 2.0F + 2.0F;
            });
            this.stack.method_22909();
         }
      }
   }

   private float getLongest(Collection<String> text) {
      String longestString = "";
      Iterator var3 = text.iterator();

      while(var3.hasNext()) {
         String str = (String)var3.next();
         if (str.length() > longestString.length()) {
            longestString = str;
         }
      }

      return BlackOut.FONT.getWidth(longestString);
   }
}
