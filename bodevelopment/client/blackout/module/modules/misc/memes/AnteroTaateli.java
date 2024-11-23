package bodevelopment.client.blackout.module.modules.misc.memes;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ChatUtils;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.class_1657;

public class AnteroTaateli extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> iFriends;
   private final Setting<Double> delay;
   private double timer;
   private final Random r;
   private int lastIndex;
   private final String[] messages;

   public AnteroTaateli() {
      super("Auto Andrew Tate", "What colour is your bugatti?", SubCategory.MEMES, true);
      this.iFriends = this.sgGeneral.b("Ignore Friends", true, "Do we ignore friends");
      this.delay = this.sgGeneral.d("Delay", 50.0D, 0.0D, 100.0D, 1.0D, "How much delay to use");
      this.timer = 0.0D;
      this.r = new Random();
      this.lastIndex = 0;
      this.messages = new String[]{"Hey brokies top G here.", "Top G drinks sparkling water and breathes air.", "I hate dead people all you do is fucking laying down like pussies.", "Get up and do some push-ups.", "Top G is never late time is just running ahead of schedule.", "<NAME>, what color is your Bugatti?", "Hello i am Andrew Tate and you are a brokie.", "Instead of playing a block game how bout you pick up some women.", "We are living inside of The Matrix, and I’m Morpheus.", "The Matrix has attacked me.", "Fucking vape! Vape comes out of the motherfucker. Fucking vape!", "You don't need vape breathe air!", "Are you good enough on your worst day to defeat your opponents on their best day?", "Being poor, weak and broke is your fault. The only person who can make you rich and strong is you. Build yourself.", "The biggest difference between success and failure is getting started.", "There was a guy who looked at me obviously trying to hurt my dignity so i pulled out my RPG and obliterated that fucker", "Being rich is even better than you imagine it to be.", "Your a fucking brokie!"};
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.timer;
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_1657 bugatti = this.getClosest();
         if (this.timer >= (Double)this.delay.get() && bugatti != null) {
            this.timer = 0.0D;
            ChatUtils.sendMessage(this.getMessage(bugatti));
         }
      }

   }

   private String getMessage(class_1657 pl) {
      int index = this.r.nextInt(0, this.messages.length);
      String msg = this.messages[index];
      if (index == this.lastIndex) {
         if (index >= this.messages.length - 1) {
            index = 0;
         } else {
            ++index;
         }
      }

      this.lastIndex = index;
      return msg.replace("<NAME>", pl.method_5477().getString());
   }

   private class_1657 getClosest() {
      class_1657 closest = null;
      float distance = -1.0F;
      if (!BlackOut.mc.field_1687.method_18456().isEmpty()) {
         Iterator var3 = BlackOut.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return closest;
                     }

                     player = (class_1657)var3.next();
                  } while(player == BlackOut.mc.field_1724);
               } while((Boolean)this.iFriends.get() && Managers.FRIENDS.isFriend(player));
            } while(closest != null && BlackOut.mc.field_1724.method_5739(player) >= distance);

            closest = player;
            distance = (float)BlackOut.mc.field_1724.method_19538().method_1022(player.method_19538());
         }
      } else {
         return closest;
      }
   }
}
