package bodevelopment.client.blackout.module.modules.client;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.managers.NotificationManager;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import net.minecraft.class_124;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class Notifications extends SettingsModule {
   private static Notifications INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgRender = this.addGroup("Render");
   public final Setting<Boolean> chatNotifications;
   public final Setting<Boolean> hudNotifications;
   public final Setting<Boolean> sound;
   private final Setting<Notifications.Style> style;
   private final Setting<Double> rounding;
   private final Setting<Boolean> bold;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> shadow;
   private final TextColorMultiSetting textColor;
   private final Setting<Integer> bgAlpha;
   private final Setting<BlackOutColor> bgColor;
   private final Setting<BlackOutColor> shadowColor;
   private final Setting<class_124> nameColor;
   private final Setting<class_124> bracketColor;
   private final Setting<class_124> txtColor;

   public Notifications() {
      super("Notifications", true, true);
      this.chatNotifications = this.sgGeneral.b("Chat Notifications", false, "Do we send notifications in chat.");
      this.hudNotifications = this.sgGeneral.b("Hud Notifications", true, "Do we render notifications on the hud.");
      this.sound = this.sgGeneral.b("Play Sound", true, ".");
      this.style = this.sgRender.e("Style", Notifications.Style.Classic, "What style to use", () -> {
         return true;
      });
      this.rounding = this.sgRender.d("Rounding", 0.5D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.style.get() == Notifications.Style.Classic || this.style.get() == Notifications.Style.New;
      });
      this.bold = this.sgRender.b("Bold", true, ".", () -> {
         return this.style.get() == Notifications.Style.Slim || this.style.get() == Notifications.Style.NewSlim;
      });
      this.blur = this.sgRender.b("Blur", true, ".");
      this.shadow = this.sgRender.b("Shadow", true, ".");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.bgAlpha = this.sgRender.i("Background Alpha", 175, 0, 255, 1, ".", () -> {
         return this.style.get() == Notifications.Style.New || this.style.get() == Notifications.Style.NewSlim;
      });
      this.bgColor = this.sgRender.c("Background Color", new BlackOutColor(0, 0, 0, 50), ".", () -> {
         return this.style.get() != Notifications.Style.New || this.style.get() != Notifications.Style.NewSlim;
      });
      this.shadowColor = this.sgRender.c("Shadow Color", new BlackOutColor(0, 0, 0, 100), ".", () -> {
         return this.style.get() != Notifications.Style.New || this.style.get() != Notifications.Style.NewSlim;
      });
      this.nameColor = this.sgRender.e("Name Color", class_124.field_1061, ".");
      this.bracketColor = this.sgRender.e("Bracket Color", class_124.field_1063, ".");
      this.txtColor = this.sgRender.e("Chat Text Color", class_124.field_1068, ".");
      INSTANCE = this;
   }

   public static Notifications getInstance() {
      return INSTANCE;
   }

   public String getClientPrefix() {
      return this.getPrefix("BlackOut");
   }

   public String getPrefix(String prefixName) {
      return String.format("%s[%s%s%s]%s", this.bracketColor.get(), this.nameColor.get(), prefixName, this.bracketColor.get(), this.txtColor.get());
   }

   public float render(class_4587 stack, NotificationManager.Notification n, float offset) {
      double delta = this.getAnimationProgress(n.startTime, n.time);
      float y = (float)BlackOut.mc.method_22683().method_4507() - offset;
      double bar = 1.0D - Math.min((double)(System.currentTimeMillis() - n.startTime), 1000.0D) / 1000.0D;
      float returnHeight = 0.0F;
      TextureRenderer var10000;
      switch(n.type) {
      case Enable:
         var10000 = BOTextures.getEnableIconRenderer();
         break;
      case Disable:
         var10000 = BOTextures.getDisableIconRenderer();
         break;
      case Info:
         var10000 = BOTextures.getInfoIconRender();
         break;
      default:
         var10000 = BOTextures.getAlertIconRenderer();
      }

      TextureRenderer t = var10000;
      int alpha = this.style.get() == Notifications.Style.Old ? 255 : 125;
      Color var24;
      switch(n.type) {
      case Enable:
         var24 = this.style.get() != Notifications.Style.New && this.style.get() != Notifications.Style.NewSlim ? new Color(42, 121, 42, alpha) : new Color(0, 175, 0, (Integer)this.bgAlpha.get());
         break;
      case Disable:
         var24 = this.style.get() != Notifications.Style.New && this.style.get() != Notifications.Style.NewSlim ? new Color(255, 0, 0, alpha) : new Color(185, 0, 0, (Integer)this.bgAlpha.get());
         break;
      case Info:
         var24 = this.style.get() != Notifications.Style.New && this.style.get() != Notifications.Style.NewSlim ? new Color(135, 135, 255, alpha) : new Color(80, 80, 80, (Integer)this.bgAlpha.get());
         break;
      default:
         var24 = new Color(255, 200, 20, this.style.get() != Notifications.Style.New && this.style.get() != Notifications.Style.NewSlim ? alpha : (Integer)this.bgAlpha.get());
      }

      Color c = var24;
      float fHeight = (Boolean)this.bold.get() ? BlackOut.BOLD_FONT.getHeight() : BlackOut.FONT.getHeight();
      stack.method_22903();
      float tWidth;
      float width;
      float height;
      float width;
      float tWidth;
      float tHeight;
      switch((Notifications.Style)this.style.get()) {
      case Classic:
         returnHeight = 40.0F;
         tWidth = Math.max(150.0F, BlackOut.FONT.getWidth(n.text) * 2.0F + 50.0F);
         width = (float)((double)BlackOut.mc.method_22683().method_4480() - (double)(tWidth + 20.0F) * delta);
         int r = this.getRounding();
         stack.method_46416(width + (float)r - 5.0F, y + (float)r - 5.0F, 0.0F);
         width = tWidth - (float)(r * 2) + 10.0F;
         tWidth = (float)(40 - r * 2 + 10);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, width, tWidth, (float)r, 10);
            });
            Renderer.onHUDBlur();
         }

         RenderUtils.rounded(stack, 0.0F, 0.0F, width, tWidth, (float)r, (Boolean)this.shadow.get() ? 3.0F : 0.0F, ((BlackOutColor)this.bgColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         stack.method_46416((float)(25 - r), (float)(25 - r), 0.0F);
         RenderUtils.circle(stack, 0.0F, 0.0F, 16.0F, c.getRGB());
         BlackOut.FONT.text(stack, "i", 3.0F, 0.0F, 1.0F, Color.WHITE, true, true);
         this.textColor.render(stack, n.text, 2.0F, 25.0F, 0.0F, false, true);
         break;
      case Slim:
         returnHeight = 30.0F;
         tWidth = fHeight * 3.0F;
         width = this.getWidth(n.text) * 2.0F + 4.0F;
         height = (float)((double)BlackOut.mc.method_22683().method_4480() - (double)(width + 20.0F) * delta);
         stack.method_46416(height, y, 0.0F);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, width, tWidth, 6.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         RenderUtils.rounded(stack, 0.0F, 0.0F, width, tWidth, 6.0F, (Boolean)this.shadow.get() ? 6.0F : 0.0F, ((BlackOutColor)this.bgColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         this.textColor.render(stack, n.text, 2.0F, 2.0F, fHeight / 2.0F + 1.0F, false, false, (Boolean)this.bold.get());
         break;
      case NewSlim:
         returnHeight = 30.0F;
         tWidth = (float)t.getWidth() / 4.8F;
         width = (float)t.getHeight() / 4.8F;
         height = fHeight * 3.0F;
         width = this.getWidth(n.text) * 2.0F + 6.0F + width;
         tWidth = (float)((double)BlackOut.mc.method_22683().method_4480() - (double)(width + 20.0F) * delta);
         stack.method_46416(tWidth, y, 0.0F);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, width, height, 6.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         RenderUtils.rounded(stack, 0.0F, 0.0F, width, height, 6.0F, (Boolean)this.shadow.get() ? 6.0F : 0.0F, c.getRGB(), c.getRGB());
         t.quad(stack, -1.0F, -1.0F, tWidth, width);
         this.textColor.render(stack, n.text, 2.0F, 4.0F + tWidth, fHeight / 2.0F + 1.0F, false, false, (Boolean)this.bold.get());
         break;
      case New:
         returnHeight = 40.0F;
         tWidth = Math.max(BlackOut.BOLD_FONT.getWidth(n.bigText) * 2.5F, BlackOut.FONT.getWidth(n.text) * 2.0F);
         width = Math.max(150.0F, tWidth + 50.0F);
         height = (float)((double)BlackOut.mc.method_22683().method_4480() - (double)(width + 20.0F) * delta);
         int r = this.getRounding();
         stack.method_46416(height + (float)r - 5.0F, y + (float)r - 5.0F, 0.0F);
         tWidth = width - (float)(r * 2) + 10.0F;
         tHeight = (float)(40 - r * 2 + 10);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, tWidth, tHeight, (float)r, 10);
            });
            Renderer.onHUDBlur();
         }

         RenderUtils.rounded(stack, 0.0F, 0.0F, tWidth, tHeight, (float)r, (Boolean)this.shadow.get() ? 5.0F : 0.0F, c.getRGB(), c.getRGB());
         stack.method_46416((float)(25 - r), (float)(25 - r), 0.0F);
         float tWidth = (float)t.getWidth() / 5.0F;
         float tHeight = (float)t.getHeight() / 5.0F;
         t.quad(stack, -tWidth / 2.0F, -tHeight / 2.0F, tWidth, tHeight);
         this.textColor.render(stack, n.bigText, 2.5F, 25.0F, -BlackOut.BOLD_FONT.getHeight() * 2.5F + 9.0F, false, true, true);
         this.textColor.render(stack, n.text, 2.0F, 25.0F, BlackOut.FONT.getHeight() * 2.0F - 2.0F, false, true);
         break;
      case Old:
         returnHeight = 65.0F;
         tWidth = Math.max(BlackOut.BOLD_FONT.getWidth(n.bigText) * 2.5F, BlackOut.FONT.getWidth(n.text) * 2.0F);
         width = Math.max(150.0F, tWidth + 50.0F);
         height = 60.0F;
         width = (float)((double)BlackOut.mc.method_22683().method_4480() - (double)(width + 20.0F) * delta);
         stack.method_46416(width - 5.0F, y - 5.0F, 0.0F);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, width, height, 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         RenderUtils.rounded(stack, 0.0F, 0.0F, width, height, 0.0F, (Boolean)this.shadow.get() ? 5.0F : 0.0F, ((BlackOutColor)this.bgColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         RenderUtils.quad(stack, 0.0F, height - 5.0F, width, 5.0F, ((BlackOutColor)this.bgColor.get()).getRGB());
         RenderUtils.quad(stack, 0.0F, height - 5.0F, (float)((double)width * bar), 5.0F, c.getRGB());
         this.textColor.render(stack, n.bigText, 2.5F, 5.0F, 5.0F, false, false, true);
         this.textColor.render(stack, n.text, 2.2F, 5.0F, height - 15.0F - BlackOut.FONT.getHeight() * 1.3F, false, false);
         tWidth = (float)t.getWidth() / 7.0F;
         tHeight = (float)t.getHeight() / 7.0F;
         t.quad(stack, width - tWidth - 5.0F, 5.0F, tWidth, tHeight);
      }

      stack.method_22909();
      return class_3532.method_16439((float)delta, returnHeight, returnHeight + 20.0F);
   }

   private double getAnimationProgress(long start, long time) {
      double delta;
      if (System.currentTimeMillis() - start < time / 2L) {
         delta = Math.min((double)(System.currentTimeMillis() - start), 500.0D) / 500.0D;
         return AnimUtils.easeOutQuart(delta);
      } else {
         delta = Math.min((double)(start + time - System.currentTimeMillis()), 500.0D) / 500.0D;
         return AnimUtils.easeOutBack(delta);
      }
   }

   private int getRounding() {
      return (int)Math.round((Double)this.rounding.get() * 20.0D);
   }

   private float getWidth(String text) {
      return (Boolean)this.bold.get() ? BlackOut.BOLD_FONT.getWidth(text) : BlackOut.FONT.getWidth(text);
   }

   public static enum Style {
      Classic,
      Slim,
      New,
      Old,
      NewSlim;

      // $FF: synthetic method
      private static Notifications.Style[] $values() {
         return new Notifications.Style[]{Classic, Slim, New, Old, NewSlim};
      }
   }

   public static enum Type {
      Enable,
      Disable,
      Info,
      Alert;

      // $FF: synthetic method
      private static Notifications.Type[] $values() {
         return new Notifications.Type[]{Enable, Disable, Info, Alert};
      }
   }
}
