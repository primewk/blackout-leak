package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_742;
import net.minecraft.class_1297.class_5529;

public class AntiBot extends Module {
   private static AntiBot INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<AntiBot.HandlingMode> mode;
   public final Setting<Boolean> WD;
   public final Setting<Boolean> smart;
   private final Setting<Integer> range;
   public final Setting<Boolean> inv;
   public final Setting<Boolean> nameCheck;
   public final Setting<Boolean> bedWars;
   public final Setting<Boolean> notif;
   public final Setting<Boolean> remove;
   private final List<class_742> bots;
   private String info;

   public AntiBot() {
      super("Anti Bot", "Removes AntiCheat bots", SubCategory.MISC_COMBAT, true);
      this.mode = this.sgGeneral.e("How to handle bots", AntiBot.HandlingMode.Ignore, ".");
      this.WD = this.sgGeneral.b("Watchdog", true, "Watchdog check");
      this.smart = this.sgGeneral.b("Smart", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.smart;
      Objects.requireNonNull(var10008);
      this.range = var10001.i("Check Range", 24, 0, 64, 1, "In what range to check for the players", var10008::get);
      this.inv = this.sgGeneral.b("Invisible", false, "Treats invisible players as bots");
      this.nameCheck = this.sgGeneral.b("Name Check", false, "Checks the players name to see if they are a bot");
      this.bedWars = this.sgGeneral.b("Bed Wars", false, "Checks for Bedwars npcs");
      this.notif = this.sgGeneral.b("Send Notification", false, "Sends a notification when adding a bot to the list");
      this.remove = this.sgGeneral.b("Remove Notification", false, "Sends a notification when adding a bot to the list");
      this.bots = new ArrayList();
      this.info = "";
      INSTANCE = this;
   }

   public static AntiBot getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return this.getInformation();
   }

   public void onDisable() {
      this.bots.clear();
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         OLEPOSSUtils.limitList(this.bots, 100);
         BlackOut.mc.field_1687.method_18456().forEach((player) -> {
            if ((Boolean)this.WD.get()) {
               if (player.method_5667() == null) {
                  this.addBot(player);
               } else {
                  this.removeBot(player);
               }

               this.info = this.WD.name;
            }

            if ((Boolean)this.smart.get()) {
               if (player.field_6012 < 10 && BlackOut.mc.field_1724.field_6012 > 10 && BlackOut.mc.field_1724.method_5739(player) < (float)(Integer)this.range.get() && player != BlackOut.mc.field_1724) {
                  this.addBot(player);
               }

               if (BlackOut.mc.field_1724.method_5739(player) > (float)(Integer)this.range.get()) {
                  this.removeBot(player);
               }

               this.info = this.smart.name;
            }

            if ((Boolean)this.inv.get()) {
               if (player.method_5767()) {
                  this.bots.add(player);
               } else {
                  this.removeBot(player);
               }

               this.info = this.inv.name;
            }

            if ((Boolean)this.nameCheck.get()) {
               if (player.method_5477().getString().contains("[NPC]") || player.method_5477().getString().contains("§") || player.method_5477().getString().contains("CIT-")) {
                  this.addBot(player);
               }

               this.info = this.nameCheck.name;
            }

            if ((Boolean)this.bedWars.get()) {
               if (player.method_5477().getString().contains("SHOP") || player.method_5477().getString().contains("UPGRADES")) {
                  this.addBot(player);
               }

               this.info = this.bedWars.name;
            }

         });
         if (this.mode.get() == AntiBot.HandlingMode.Remove) {
            this.getBots().forEach((bot) -> {
               BlackOut.mc.field_1687.method_2945(bot.method_5628(), class_5529.field_26999);
            });
         }

      }
   }

   @Event
   public void onGameJoin(GameJoinEvent event) {
      this.bots.clear();
   }

   private void addBot(class_742 bot) {
      if (!this.bots.contains(bot)) {
         this.bots.add(bot);
         if ((Boolean)this.notif.get()) {
            Managers.NOTIFICATIONS.addNotification(bot.method_5477().getString() + " has been flagged as a bot!", this.getDisplayName(), 2.0D, Notifications.Type.Info);
         }
      }

   }

   private void removeBot(class_742 bot) {
      this.bots.remove(bot);
      if ((Boolean)this.notif.get() && (Boolean)this.remove.get()) {
         Managers.NOTIFICATIONS.addNotification(bot.method_5477().getString() + " was set as a player!", this.getDisplayName(), 2.0D, Notifications.Type.Info);
      }

   }

   public List<class_742> getBots() {
      return this.bots;
   }

   private String getInformation() {
      String var10000;
      if (this.getEnabled() == 1) {
         var10000 = this.info;
         return var10000 + " " + this.bots.size();
      } else {
         var10000 = ((AntiBot.HandlingMode)this.mode.get()).name();
         return var10000 + " " + (this.bots.isEmpty() ? "0" : this.bots.size());
      }
   }

   private int getEnabled() {
      int i = 0;
      if ((Boolean)this.WD.get()) {
         ++i;
      }

      if ((Boolean)this.inv.get()) {
         ++i;
      }

      if ((Boolean)this.nameCheck.get()) {
         ++i;
      }

      if ((Boolean)this.smart.get()) {
         ++i;
      }

      if ((Boolean)this.bedWars.get()) {
         ++i;
      }

      return i;
   }

   public static enum HandlingMode {
      Remove,
      Ignore;

      // $FF: synthetic method
      private static AntiBot.HandlingMode[] $values() {
         return new AntiBot.HandlingMode[]{Remove, Ignore};
      }
   }
}
