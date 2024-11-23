package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_2596;
import net.minecraft.class_7439;

public class SessionInfo extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<SessionInfo.Style> style;
   private final Setting<SessionInfo.Mode> mode;
   public final TextColorMultiSetting textColor;
   private final Setting<Boolean> bar;
   private final Setting<BlackOutColor> barColor;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final BackgroundMultiSetting background;
   private static final long startTime = System.currentTimeMillis();
   private int kills;
   private int deaths;
   private float height;
   private float width;
   private String ip;
   private boolean isDead;

   public SessionInfo() {
      super("Session Info", "Shows you information about your current play session");
      this.style = this.sgGeneral.e("Style", SessionInfo.Style.Blackout, ".");
      this.mode = this.sgGeneral.e("Kill Count Mode", SessionInfo.Mode.Chat, "How to count Kills");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.bar = this.sgGeneral.b("Bar", true, ".", () -> {
         return this.style.get() == SessionInfo.Style.Blackout;
      });
      this.barColor = this.sgGeneral.c("Bar Color", new BlackOutColor(255, 255, 255, 255), ".", () -> {
         return this.style.get() == SessionInfo.Style.Blackout && (Boolean)this.bar.get();
      });
      this.bg = this.sgGeneral.b("Background", true, ".", () -> {
         return this.style.get() == SessionInfo.Style.Blackout;
      });
      this.blur = this.sgGeneral.b("Blur", true, ".", () -> {
         return this.style.get() == SessionInfo.Style.Blackout;
      });
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.kills = 0;
      this.deaths = 0;
      this.height = 0.0F;
      this.width = 0.0F;
      this.ip = "";
      this.isDead = false;
      this.setSize(10.0F, 10.0F);
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String timeString = OLEPOSSUtils.getTimeString(System.currentTimeMillis() - startTime);
         this.ip = !BlackOut.mc.method_1496() && BlackOut.mc.method_1562() != null && BlackOut.mc.method_1562().method_45734() != null ? BlackOut.mc.method_1562().method_45734().field_3761 : "Singleplayer";
         this.stack.method_22903();
         if (BlackOut.mc.field_1724.method_29504() && !this.isDead) {
            ++this.deaths;
            this.isDead = true;
         } else if (!BlackOut.mc.field_1724.method_29504()) {
            this.isDead = false;
         }

         float num = 0.0F;
         switch((SessionInfo.Style)this.style.get()) {
         case Blackout:
            num = (Boolean)this.bar.get() ? 15.0F : BlackOut.FONT.getHeight() * 1.5F;
            this.height = num + BlackOut.FONT.getHeight() * 4.0F;
            this.width = BlackOut.FONT.getWidth("Session Info") * 1.5F + 14.0F;
            this.setSize(this.width, this.height);
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(0.0F, 0.0F, this.width, this.height, 3.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               this.background.render(this.stack, 0.0F, 0.0F, this.width, this.height, 3.0F, 3.0F);
            }

            this.textColor.render(this.stack, "Session Info", 1.5F, this.width / 2.0F, 0.0F, true, false);
            if ((Boolean)this.bar.get()) {
               RenderUtils.rounded(this.stack, 2.0F, BlackOut.FONT.getHeight() * 1.5F, this.width - 4.0F, 0.1F, 0.5F, 0.0F, ((BlackOutColor)this.barColor.get()).getRGB(), Color.WHITE.getRGB());
            }

            this.textColor.render(this.stack, this.ip, 1.0F, 5.0F, num, false, false);
            this.textColor.render(this.stack, "Kills: " + this.kills, 1.0F, 5.0F, num + BlackOut.FONT.getHeight(), false, false);
            this.textColor.render(this.stack, "Deaths: " + this.deaths, 1.0F, 5.0F, num + BlackOut.FONT.getHeight() * 2.0F, false, false);
            this.textColor.render(this.stack, timeString, 1.0F, 5.0F, num + BlackOut.FONT.getHeight() * 3.0F, false, false);
            break;
         case Exhibition:
            num = BlackOut.FONT.getHeight();
            this.width = BlackOut.FONT.getWidth(this.getLongest(timeString)) + 6.0F;
            this.height = BlackOut.FONT.getHeight() * 3.0F + 6.0F;
            this.stack.method_46416(-2.0F, -2.0F, 0.0F);
            this.setSize(this.width + 6.0F, this.height + 4.0F);
            RenderUtils.drawSkeetBox(this.stack, 0.0F, 0.0F, this.width + 10.0F, this.height + 8.0F, true);
            this.textColor.render(this.stack, this.ip, 1.0F, 4.0F, 4.0F, false, false);
            this.textColor.render(this.stack, "Kills: " + this.kills, 1.0F, 4.0F, 4.0F + num, false, false);
            this.textColor.render(this.stack, "Deaths: " + this.deaths, 1.0F, 4.0F, 4.0F + num * 2.0F, false, false);
            this.textColor.render(this.stack, timeString, 1.0F, 4.0F, 4.0F + num * 3.0F, false, false);
         }

         this.stack.method_22909();
      }
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.mode.get() == SessionInfo.Mode.Chat) {
            class_2596 var3 = event.packet;
            if (var3 instanceof class_7439) {
               class_7439 packet = (class_7439)var3;
               String unformattedText = packet.comp_763().getString();
               String name = BlackOut.mc.field_1724.method_5477().getString();
               String[] look = new String[]{"wurde von " + name, name + " killed ", "killed by " + name, "slain by " + name, "You received a reward for killing ", "while escaping " + name, "You have won", "You have killed"};
               if (unformattedText == null) {
                  return;
               }

               String[] var6 = look;
               int var7 = look.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String s = var6[var8];
                  if (unformattedText.contains(s)) {
                     ++this.kills;
                  }
               }
            }

         }
      }
   }

   private String getLongest(String time) {
      String[] texts = new String[]{"Session Info", this.ip, "Kills: " + this.kills, "Deaths: " + this.deaths, time};
      String longestString = "";
      int maxLength = 0;
      String[] var5 = texts;
      int var6 = texts.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String text = var5[var7];
         int currentLength = text.length();
         if (currentLength > maxLength) {
            maxLength = currentLength;
            longestString = text;
         }
      }

      return longestString;
   }

   public static enum Style {
      Blackout,
      Exhibition;

      // $FF: synthetic method
      private static SessionInfo.Style[] $values() {
         return new SessionInfo.Style[]{Blackout, Exhibition};
      }
   }

   public static enum Mode {
      Chat,
      Event;

      // $FF: synthetic method
      private static SessionInfo.Mode[] $values() {
         return new SessionInfo.Mode[]{Chat, Event};
      }
   }
}
