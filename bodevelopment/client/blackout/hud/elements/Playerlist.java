package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import net.minecraft.class_742;

public class Playerlist extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Playerlist.NameMode> nameMode;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> dynamic;
   private final Setting<Boolean> showPops;
   private final BackgroundMultiSetting background;
   private final TextColorMultiSetting textColor;
   private final Setting<BlackOutColor> good;
   private final Setting<BlackOutColor> bad;
   private final List<class_1297> players;
   private float currentLongest;
   private float longest;
   private float currentLongestPing;
   private float longestPing;
   private float bgLength;
   private float y;

   public Playerlist() {
      super("Playerlist", ".");
      this.nameMode = this.sgGeneral.e("Name Mode", Playerlist.NameMode.EntityName, "");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.blur = this.sgGeneral.b("Blur", true, "Renders a Blur effect");
      this.dynamic = this.sgGeneral.b("Use dynamic info colors", false, ".");
      this.showPops = this.sgGeneral.b("Show totem pops", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      var10001 = this.sgGeneral;
      BlackOutColor var10003 = new BlackOutColor(0, 225, 0, 255);
      Setting var10005 = this.dynamic;
      Objects.requireNonNull(var10005);
      this.good = var10001.c("Good", var10003, ".", var10005::get);
      var10001 = this.sgGeneral;
      var10003 = new BlackOutColor(150, 0, 0, 255);
      var10005 = this.dynamic;
      Objects.requireNonNull(var10005);
      this.bad = var10001.c("Bad", var10003, ".", var10005::get);
      this.players = new ArrayList();
      this.currentLongest = 0.0F;
      this.longest = 0.0F;
      this.currentLongestPing = 0.0F;
      this.longestPing = 0.0F;
      this.bgLength = 0.0F;
      this.y = 0.0F;
      this.setSize(10.0F, 10.0F);
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         this.setSize(this.bgLength > 10.0F ? this.bgLength : 10.0F, this.y + 6.0F);
         this.currentLongest = BlackOut.FONT.getWidth("PlayersHealthPing" + ((Boolean)this.showPops.get() ? "pops" : ""));
         this.currentLongestPing = BlackOut.FONT.getWidth("Ping");
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, this.bgLength, this.y + 6.0F, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, 0.0F, this.bgLength, this.y + 6.0F, 3.0F, 3.0F);
         }

         this.drawText(this.stack, 0.0F, 0.0F, "Players", "Health", "Ping", "Pops", true, 0.0F, 0.0F, 0.0F);
         this.y = 0.0F;
         this.players.forEach((player) -> {
            class_742 current = (class_742)player;
            String name = ((Playerlist.NameMode)this.nameMode.get()).getName(current);
            class_640 entry = BlackOut.mc.method_1562().method_2871(current.method_5667());
            int pingValue = entry == null ? 0 : entry.method_2959();
            float healthValue = (float)Math.round(current.method_6032() + current.method_6067());
            StatsManager.TrackerData trackerData = Managers.STATS.getStats(current);
            int popAmount = trackerData == null ? 0 : trackerData.pops;
            String ping = String.valueOf(pingValue);
            String health = String.valueOf(healthValue);
            String pops = String.valueOf(popAmount);
            if (BlackOut.FONT.getWidth(name + health + ping) > this.currentLongest) {
               this.currentLongest = BlackOut.FONT.getWidth(name + health + ping);
            }

            if (BlackOut.FONT.getWidth(ping) > this.currentLongestPing) {
               this.currentLongestPing = BlackOut.FONT.getWidth(ping);
            }

            this.drawFace(this.stack, 10.0F + this.y, current.method_52814().comp_1626());
            this.drawText(this.stack, 8.0F, 10.0F + this.y, name, health, ping, pops, false, healthValue, (float)pingValue, (float)popAmount);
            this.y += 10.0F;
         });
         this.longest = this.currentLongest;
         this.longestPing = this.currentLongestPing;
         this.stack.method_22909();
      }
   }

   private void drawText(class_4587 stack, float x, float y, String string, String string2, String string3, String string4, Boolean first, float health, float ping, float totemPops) {
      float drawX = first ? x + 8.0F : x;
      this.textColor.render(stack, string, 1.0F, x, y, false, false);
      if ((Boolean)this.dynamic.get() && !first) {
         BlackOut.FONT.text(stack, string2, 1.0F, drawX + this.longest, y, this.getHealthColor(health), false, false);
         BlackOut.FONT.text(stack, string3, 1.0F, drawX + this.longest + BlackOut.FONT.getWidth("Health") + 4.0F, y, this.getColor(ping), false, false);
         if ((Boolean)this.showPops.get()) {
            BlackOut.FONT.text(stack, string4, 1.0F, drawX + this.longest + BlackOut.FONT.getWidth("Health") + BlackOut.FONT.getWidth("Ping") + 4.0F, y, this.getColor(totemPops), false, false);
         }
      } else {
         this.textColor.render(stack, string2, 1.0F, drawX + this.longest, y, false, false);
         this.textColor.render(stack, string3, 1.0F, drawX + this.longest + BlackOut.FONT.getWidth("Health") + 4.0F, y, false, false);
         if ((Boolean)this.showPops.get()) {
            this.textColor.render(stack, string4, 1.0F, drawX + this.longest + BlackOut.FONT.getWidth("Health") + BlackOut.FONT.getWidth("Ping") + 8.0F, y, false, false);
         }
      }

      float txtWidth = (Boolean)this.showPops.get() ? BlackOut.FONT.getWidth("Health") + BlackOut.FONT.getWidth("Ping") + 6.0F : BlackOut.FONT.getWidth("Health");
      this.bgLength = this.longest + txtWidth + 12.0F + this.longestPing;
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.players.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (entity instanceof class_742 && this.shouldRender(entity)) {
               this.players.add(entity);
            }

         });
         this.players.sort(Comparator.comparing((entity) -> {
            return ((Playerlist.NameMode)this.nameMode.get()).getName(entity);
         }));
      }
   }

   public boolean shouldRender(class_1297 entity) {
      AntiBot antiBot = AntiBot.getInstance();
      if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && entity instanceof class_742) {
         class_742 player = (class_742)entity;
         if (antiBot.getBots().contains(player)) {
            return false;
         }
      }

      return entity != BlackOut.mc.field_1724 && entity instanceof class_742;
   }

   private void drawFace(class_4587 stack, float y, class_2960 renderSkin) {
      float size = 6.0F;
      if (renderSkin != null) {
         TextureRenderer.renderQuad(stack, 0.0F, y, size, size, 0.125F, 0.125F, 0.25F, 0.25F, BlackOut.mc.method_1531().method_4619(renderSkin).method_4624());
      }

   }

   private Color getHealthColor(float number) {
      return ColorUtils.lerpColor((double)Math.min(number, 1.0F), ((BlackOutColor)this.bad.get()).getColor(), ((BlackOutColor)this.good.get()).getColor());
   }

   private Color getColor(float number) {
      return ColorUtils.lerpColor((double)Math.min(number, 1.0F), ((BlackOutColor)this.good.get()).getColor(), ((BlackOutColor)this.bad.get()).getColor());
   }

   public static enum NameMode {
      Display,
      EntityName;

      private String getName(class_1297 entity) {
         String var10000;
         switch(this) {
         case Display:
            var10000 = entity.method_5476().getString();
            break;
         default:
            var10000 = entity.method_5477().getString();
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Playerlist.NameMode[] $values() {
         return new Playerlist.NameMode[]{Display, EntityName};
      }
   }
}
