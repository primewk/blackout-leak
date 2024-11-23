package bodevelopment.client.blackout.module.modules.misc.memes;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ChatUtils;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1657;

public class AutoMoan extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<AutoMoan.MoanMode> moanmode;
   private final Setting<Boolean> ignoreFriends;
   private final Setting<Integer> delay;
   private double timer;
   private static final String[] submissive = new String[]{"fuck me harder daddy", "deeper! daddy deeper!", "Fuck yes your so big!", "I love your cock %s!", "Do not stop fucking my ass before i cum!", "Oh your so hard for me", "Want to widen my ass up %s?", "I love you daddy", "Make my bussy pop", "%s loves my bussy so much", "i made %s cum so hard with my tight bussy", "Your cock is so big and juicy daddy!", "Please fuck me as hard as you can", "im %s's personal femboy cumdumpster!", "Please shoot your hot load deep inside me daddy!", "I love how %s's dick feels inside of me!", "%s gets so hard when he sees my ass!", "%s really loves fucking my ass really hard!", "why wont u say the last message"};
   private static final String[] dominant = new String[]{"Be a good boy for daddy", "I love pounding your ass %s!", "Give your bussy to daddy!", "I love how you drip pre-cum while i fuck your ass %s", "Slurp up and down my cock like a good boy", "Come and jump on daddy's cock %s", "I love how you look at me while you suck me off %s", "%s looks so cute when i fuck him", "%s's bussy is so incredibly tight!", "%s takes dick like the good boy he is", "I love how you shake your ass on my dick", "%s moans so cutely when i fuck his ass", "%s is the best cumdupster there is!", "%s is always horny and ready for his daddy's dick", "My dick gets rock hard every time i see %s", "why wont u say the last message"};

   public AutoMoan() {
      super("Auto Moan", "Moans sexual things to the closest person.", SubCategory.MEMES, true);
      this.moanmode = this.sgGeneral.e("Message Mode", AutoMoan.MoanMode.Submissive, "What kind of messages to send.");
      this.ignoreFriends = this.sgGeneral.b("Ignore Friends", true, "Doesn't send messages targeted to friends.");
      this.delay = this.sgGeneral.i("Tick Delay", 50, 0, 100, 1, "Tick delay between moans.");
      this.timer = 0.0D;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.timer = Math.min((double)(Integer)this.delay.get(), this.timer + event.frameTime);
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (!(this.timer++ < (double)(Integer)this.delay.get())) {
            this.MOAN();
            this.timer = 0.0D;
         }
      }
   }

   private void MOAN() {
      class_1657 target = this.getClosest();
      if (target != null) {
         String name = target.method_5477().getString();
         ((AutoMoan.MoanMode)this.moanmode.get()).send(name);
      }
   }

   private class_1657 getClosest() {
      if (BlackOut.mc.field_1687.method_18456().isEmpty()) {
         return null;
      } else {
         class_1657 closest = null;
         double closestDistance = -1.0D;
         Iterator var4 = BlackOut.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            double distance;
            do {
               do {
                  do {
                     if (!var4.hasNext()) {
                        return closest;
                     }

                     player = (class_1657)var4.next();
                  } while(player == BlackOut.mc.field_1724);
               } while((Boolean)this.ignoreFriends.get() && Managers.FRIENDS.isFriend(player));

               distance = BlackOut.mc.field_1724.method_5858(player);
            } while(closest != null && distance > closestDistance);

            closest = player;
            closestDistance = distance;
         }
      }
   }

   public static enum MoanMode {
      Dominant(AutoMoan.dominant),
      Submissive(AutoMoan.submissive);

      private final String[] messages;
      private int lastNum;

      private MoanMode(String[] messages) {
         this.messages = messages;
      }

      private void send(String targetName) {
         int num = ThreadLocalRandom.current().nextInt(0, this.messages.length - 1);
         if (num == this.lastNum) {
            num = num < this.messages.length - 1 ? num + 1 : 0;
         }

         this.lastNum = num;
         ChatUtils.sendMessage(this.messages[num].replace("%s", targetName));
      }

      // $FF: synthetic method
      private static AutoMoan.MoanMode[] $values() {
         return new AutoMoan.MoanMode[]{Dominant, Submissive};
      }
   }
}
