package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PopEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.Objects;
import net.minecraft.class_1294;
import net.minecraft.class_742;

public class Notifier extends Module {
   private static Notifier INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgPops = this.addGroup("Pops");
   private final SettingGroup sgWeakness = this.addGroup("Weakness");
   public final Setting<Notifier.Mode> mode;
   private final Setting<Boolean> pops;
   private final Setting<Boolean> iOwn;
   private final Setting<Boolean> iFriends;
   private final Setting<Boolean> weakness;
   private final Setting<Boolean> single;
   private final Setting<Double> delay;
   private double timer;
   private boolean last;

   public Notifier() {
      super("Notifier", "Notifies you about events", SubCategory.MISC_COMBAT, true);
      this.mode = this.sgGeneral.e("Notify mode", Notifier.Mode.Hud, "How to notify you");
      this.pops = this.sgPops.b("Pop Counter", true, "Counts Totem Pops");
      SettingGroup var10001 = this.sgPops;
      Setting var10005 = this.pops;
      Objects.requireNonNull(var10005);
      this.iOwn = var10001.b("Ignore Own", true, "Does not send count friends pops", var10005::get);
      var10001 = this.sgPops;
      var10005 = this.pops;
      Objects.requireNonNull(var10005);
      this.iFriends = var10001.b("Ignore Friends", true, "Does not send count friends pops", var10005::get);
      this.weakness = this.sgWeakness.b("Weakness", true, "Notifies about getting weakness");
      var10001 = this.sgWeakness;
      var10005 = this.weakness;
      Objects.requireNonNull(var10005);
      this.single = var10001.b("Single", true, "Only sends it once", var10005::get);
      var10001 = this.sgWeakness;
      Setting var10008 = this.weakness;
      Objects.requireNonNull(var10008);
      this.delay = var10001.d("Delay", 5.0D, 0.0D, 100.0D, 1.0D, "Tick delay between alerts", var10008::get);
      this.timer = 0.0D;
      this.last = false;
      INSTANCE = this;
   }

   public static Notifier getInstance() {
      return INSTANCE;
   }

   @Event
   public void onPop(PopEvent event) {
      if ((Boolean)this.pops.get()) {
         if (!(Boolean)this.iOwn.get() || !event.player.equals(BlackOut.mc.field_1724)) {
            if (!(Boolean)this.iFriends.get() || !Managers.FRIENDS.isFriend(event.player)) {
               this.sendNotification(this.getPopString(event.player, event.number));
            }
         }
      }
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Boolean)this.weakness.get()) {
            if (!BlackOut.mc.field_1724.method_6059(class_1294.field_5911)) {
               if (this.last) {
                  this.last = false;
                  this.sendNotification("You no longer have weakness!");
               }

            } else {
               if ((Boolean)this.single.get()) {
                  if (!this.last) {
                     this.last = true;
                     this.sendNotification("You have weakness!");
                  }
               } else if (this.timer > 0.0D) {
                  --this.timer;
               } else {
                  this.timer = (Double)this.delay.get();
                  this.last = true;
                  this.sendNotification("You have weakness!");
               }

            }
         }
      }
   }

   private String getPopString(class_742 player, int pops) {
      String var10000 = player.method_5477().getString();
      return var10000 + " has popped their " + pops + this.getSuffix(pops) + " totem!";
   }

   private String getSuffix(int i) {
      if (i >= 11 && i <= 13) {
         return "th";
      } else {
         String var10000;
         switch(i % 10) {
         case 1:
            var10000 = "st";
            break;
         case 2:
            var10000 = "nd";
            break;
         case 3:
            var10000 = "rd";
            break;
         default:
            var10000 = "th";
         }

         return var10000;
      }
   }

   private void sendNotification(String info) {
      switch((Notifier.Mode)this.mode.get()) {
      case Hud:
         Managers.NOTIFICATIONS.addNotification(info, this.getDisplayName(), 2.0D, Notifications.Type.Info);
         break;
      case Chat:
         this.sendMessage(info);
      }

   }

   public static enum Mode {
      Chat,
      Hud;

      // $FF: synthetic method
      private static Notifier.Mode[] $values() {
         return new Notifier.Mode[]{Chat, Hud};
      }
   }
}
