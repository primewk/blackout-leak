package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.class_1291;
import net.minecraft.class_1293;

public class Effects extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgColor = this.addGroup("Color");
   public final Setting<Effects.Style> style;
   public final Setting<Effects.Side> side;
   public final Setting<Effects.Order> order;
   private final Setting<Boolean> rn;
   private final Setting<Boolean> up;
   private final Setting<Double> minWidth;
   private final Setting<Boolean> blur;
   private final BackgroundMultiSetting background;
   public final Setting<Effects.ColorMode> colorMode;
   private final TextColorMultiSetting textColor;
   private final Setting<BlackOutColor> infoColor;
   private final String[] romanNumerals;
   private float offset;
   private final List<Effects.Component> components;

   public Effects() {
      super("Effects", "Shows you current effects on screen");
      this.style = this.sgGeneral.e("Style", Effects.Style.Blackout, ".");
      this.side = this.sgGeneral.e("Side", Effects.Side.Left, ".");
      this.order = this.sgGeneral.e("Order", Effects.Order.Longest, ".");
      this.rn = this.sgGeneral.b("Roman Numerals", false, "Might break things");
      this.up = this.sgGeneral.b("Render Up", false, ".");
      this.minWidth = this.sgGeneral.d("Min Width", 0.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.style.get() == Effects.Style.Blackout;
      });
      this.blur = this.sgGeneral.b("Blur", true, ".", () -> {
         return this.style.get() == Effects.Style.Blackout;
      });
      this.background = BackgroundMultiSetting.of(this.sgGeneral, () -> {
         return this.style.get() == Effects.Style.Blackout;
      }, (String)null);
      this.colorMode = this.sgColor.e("Text Color Mode", Effects.ColorMode.Custom, "What color to for the text use");
      this.textColor = TextColorMultiSetting.of(this.sgColor, () -> {
         return this.colorMode.get() == Effects.ColorMode.Custom;
      }, "Text");
      this.infoColor = this.sgColor.c("Info Color", new BlackOutColor(200, 200, 200, 255), "Info Color");
      this.romanNumerals = new String[]{"I", "II", "III", "IV", "V"};
      this.components = new ArrayList();
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.method_6088() != null) {
            this.stack.method_22903();
            float width = BlackOut.FONT.getWidth("idunnoman 4:20");
            this.setSize(width, BlackOut.FONT.getHeight() * 2.0F + 1.0F);
            if (this.side.get() == Effects.Side.Right) {
               this.stack.method_46416(width, 0.0F, 0.0F);
            }

            Comparator<Entry<class_1291, class_1293>> comparator = Comparator.comparingDouble(this::getWidth);
            BlackOut.mc.field_1724.method_6088().entrySet().stream().sorted(this.order.get() == Effects.Order.Shortest ? comparator : comparator.reversed()).forEach((entry) -> {
               this.stack.method_46416(0.0F, this.render((class_1291)entry.getKey(), (class_1293)entry.getValue()), 0.0F);
            });
            this.stack.method_22909();
         }
      }
   }

   private float render(class_1291 effect, class_1293 effectInstance) {
      int timeS = (int)Math.floor((double)effectInstance.method_5584() / 20.0D);
      int var10000 = (int)Math.floor((double)((float)timeS / 60.0F));
      String timeString = var10000 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
      String levelString = this.levelString(effectInstance);
      String nameString = effect.method_5560().getString();
      float returnHeight = 0.0F;
      switch((Effects.Style)this.style.get()) {
      case Blackout:
         float width = Math.max(this.getWidth(effect, effectInstance), BlackOut.FONT.getWidth(timeString) + 4.0F);
         float height = BlackOut.FONT.getHeight() * 2.0F + 1.0F;
         float rad = 3.0F;
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(((Effects.Side)this.side.get()).getSide(width), 0.0F, width, height, rad, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, ((Effects.Side)this.side.get()).getSide(width), 0.0F, width, height, rad, 3.0F);
         switch((Effects.ColorMode)this.colorMode.get()) {
         case Custom:
            this.textColor.render(this.stack, nameString, 1.0F, ((Effects.Side)this.side.get()).getSide(width) + 2.0F, 5.0F, false, true);
            break;
         case Effect:
            BlackOut.FONT.text(this.stack, nameString, 1.0F, ((Effects.Side)this.side.get()).getSide(width) + 2.0F, 5.0F, new Color(effect.method_5556()), false, true);
         }

         BlackOut.FONT.text(this.stack, levelString, 1.0F, ((Effects.Side)this.side.get()).getSide(width) + width - 5.0F, 5.0F, ((BlackOutColor)this.infoColor.get()).getColor(), true, true);
         BlackOut.FONT.text(this.stack, timeString, 1.0F, ((Effects.Side)this.side.get()).getSide(width) + 2.0F, height - BlackOut.FONT.getHeight() / 2.0F, ((BlackOutColor)this.infoColor.get()).getColor(), false, true);
         returnHeight = height + rad * 2.0F + 3.0F;
         break;
      case Simple:
         this.offset = 0.0F;
         this.components.clear();
         this.components.add(new Effects.Component(nameString, false));
         this.components.add(new Effects.Component(levelString, true));
         this.components.add(new Effects.Component("(" + timeString + ")", true));
         this.components.forEach((component) -> {
            if (component.info) {
               BlackOut.FONT.text(this.stack, component.text, 1.0F, this.offset, 0.0F, ((BlackOutColor)this.infoColor.get()).getColor(), false, true);
            } else {
               switch((Effects.ColorMode)this.colorMode.get()) {
               case Custom:
                  this.textColor.render(this.stack, component.text, 1.0F, this.offset, 0.0F, false, true);
                  break;
               case Effect:
                  BlackOut.FONT.text(this.stack, component.text, 1.0F, this.offset, 0.0F, new Color(effect.method_5556()), false, true);
               }
            }

            this.offset += component.width + BlackOut.FONT.getWidth(" ");
         });
         returnHeight = BlackOut.FONT.getHeight();
      }

      return (Boolean)this.up.get() ? -returnHeight : returnHeight;
   }

   private float getWidth(Entry<class_1291, class_1293> entry) {
      return this.getWidth((class_1291)entry.getKey(), (class_1293)entry.getValue());
   }

   private float getWidth(class_1291 effect, class_1293 effectInstance) {
      return this.getWidth(effect.method_5560().getString(), String.valueOf(effectInstance.method_5578()));
   }

   private float getWidth(String name, String level) {
      return Math.max(BlackOut.FONT.getWidth(name + level) + 10.0F, ((Double)this.minWidth.get()).floatValue());
   }

   private String levelString(class_1293 instance) {
      if (instance.method_5578() < 0) {
         return "-";
      } else {
         return (Boolean)this.rn.get() && instance.method_5578() < this.romanNumerals.length ? this.romanNumerals[instance.method_5578()] : String.valueOf(instance.method_5578() + 1);
      }
   }

   public static enum Style {
      Simple,
      Blackout;

      // $FF: synthetic method
      private static Effects.Style[] $values() {
         return new Effects.Style[]{Simple, Blackout};
      }
   }

   public static enum Side {
      Left,
      Right;

      private float getSide(float width) {
         float var10000;
         switch(this) {
         case Left:
            var10000 = 0.0F;
            break;
         case Right:
            var10000 = -width;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Effects.Side[] $values() {
         return new Effects.Side[]{Left, Right};
      }
   }

   public static enum Order {
      Shortest,
      Longest;

      // $FF: synthetic method
      private static Effects.Order[] $values() {
         return new Effects.Order[]{Shortest, Longest};
      }
   }

   public static enum ColorMode {
      Custom,
      Effect;

      // $FF: synthetic method
      private static Effects.ColorMode[] $values() {
         return new Effects.ColorMode[]{Custom, Effect};
      }
   }

   private static class Component {
      private final String text;
      private final float width;
      private final boolean info;

      private Component(String text, boolean info) {
         this.text = text;
         this.width = BlackOut.FONT.getWidth(text);
         this.info = info;
      }
   }
}
