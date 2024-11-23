package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_3532;
import net.minecraft.class_7439;

public class AutoChatGame extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> chance;
   private final Setting<AutoChatGame.DelayMode> delayMode;
   private final Setting<Double> minDelay;
   private final Setting<Double> maxDelay;
   private final Setting<Double> chatOpenTime;
   private final Setting<Double> shiftTime;
   private final Setting<Double> altTime;
   private final Setting<Double> letterTime;
   private final Setting<Double> numberTime;
   private final Setting<Double> specialTime;
   private final Setting<Double> enterTime;
   private String message;
   private long sendTime;
   private final List<Character> shiftChars;
   private final List<Character> altChars;

   public AutoChatGame() {
      super("Auto Chat Game", ".", SubCategory.MISC, true);
      this.chance = this.sgGeneral.d("Chance", 1.0D, 0.0D, 1.0D, 0.01D, "");
      this.delayMode = this.sgGeneral.e("Delay Mode", AutoChatGame.DelayMode.Dumb, "");
      this.minDelay = this.sgGeneral.d("Min Delay", 1.0D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Dumb;
      });
      this.maxDelay = this.sgGeneral.d("Max Delay", 2.0D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Dumb;
      });
      this.chatOpenTime = this.sgGeneral.d("Chat Open Time", 1.0D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.shiftTime = this.sgGeneral.d("Shift Time", 0.1D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.altTime = this.sgGeneral.d("Alt Time", 0.5D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.letterTime = this.sgGeneral.d("Letter Time", 0.2D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.numberTime = this.sgGeneral.d("Number Time", 0.3D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.specialTime = this.sgGeneral.d("Special Char Time", 0.3D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.enterTime = this.sgGeneral.d("Enter Time", 0.1D, 0.0D, 10.0D, 0.1D, "", () -> {
         return this.delayMode.get() == AutoChatGame.DelayMode.Smart;
      });
      this.sendTime = 0L;
      this.shiftChars = new ArrayList();
      this.altChars = new ArrayList();
      this.initChars();
   }

   @Event
   public void onMessage(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_7439) {
         class_7439 packet = (class_7439)var3;
         String text = packet.comp_763().getString();
         if (this.shouldSend(text)) {
            this.message = text.split("\"")[1];
            double delay = this.getDelay();
            this.sendTime = System.currentTimeMillis() + Math.round(delay * 1000.0D);
            Managers.NOTIFICATIONS.addNotification(String.format("Answering to a chat game in %.1fs", delay), this.getDisplayName(), 5.0D, Notifications.Type.Info);
         }
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.message != null) {
         if (System.currentTimeMillis() > this.sendTime) {
            ChatUtils.sendMessage(this.message);
            this.message = null;
         }
      } else {
         this.message = null;
      }
   }

   private boolean shouldSend(String string) {
      if (Math.random() > (Double)this.chance.get()) {
         return false;
      } else {
         return string.contains("CHAT GAME") && string.contains("First to type word");
      }
   }

   private double getDelay() {
      double var10000;
      switch((AutoChatGame.DelayMode)this.delayMode.get()) {
      case Dumb:
         var10000 = class_3532.method_16436(Math.random(), (Double)this.minDelay.get(), (Double)this.maxDelay.get());
         break;
      case Smart:
         var10000 = this.getSmartDelay(this.message);
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private double getSmartDelay(String string) {
      double total = (Double)this.chatOpenTime.get();
      int state = 0;
      char[] var5 = string.toCharArray();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c = var5[var7];
         byte reqState;
         if (!Character.isUpperCase(c) && !this.shiftChars.contains(c)) {
            if (this.altChars.contains(c)) {
               reqState = 2;
            } else {
               reqState = 0;
            }
         } else {
            reqState = 1;
         }

         if (state != reqState) {
            double var10001;
            switch(reqState) {
            case 1:
               var10001 = (Double)this.shiftTime.get();
               break;
            case 2:
               var10001 = (Double)this.altTime.get();
               break;
            default:
               var10001 = 0.0D;
            }

            total += var10001;
            state = reqState;
         }

         if (Character.isDigit(c)) {
            total += (Double)this.numberTime.get();
         } else if ((c <= '@' || c >= '[') && (c <= '`' || c >= '{')) {
            total += (Double)this.specialTime.get();
         } else {
            total += (Double)this.letterTime.get();
         }
      }

      return total + (Double)this.enterTime.get();
   }

   private void initChars() {
      this.shiftChars.add('>');
      this.shiftChars.add(';');
      this.shiftChars.add(':');
      this.shiftChars.add('_');
      this.shiftChars.add('*');
      this.shiftChars.add('^');
      this.shiftChars.add('`');
      this.shiftChars.add('?');
      this.shiftChars.add('!');
      this.shiftChars.add('"');
      this.shiftChars.add('#');
      this.shiftChars.add('¤');
      this.shiftChars.add('%');
      this.shiftChars.add('&');
      this.shiftChars.add('/');
      this.shiftChars.add('(');
      this.shiftChars.add(')');
      this.shiftChars.add('=');
      this.shiftChars.add('½');
      this.altChars.add('|');
      this.altChars.add('@');
      this.altChars.add('£');
      this.altChars.add('$');
      this.altChars.add('€');
      this.altChars.add('{');
      this.altChars.add('[');
      this.altChars.add(']');
      this.altChars.add('}');
      this.altChars.add('\\');
      this.altChars.add('~');
   }

   public static enum DelayMode {
      Dumb,
      Smart;

      // $FF: synthetic method
      private static AutoChatGame.DelayMode[] $values() {
         return new AutoChatGame.DelayMode[]{Dumb, Smart};
      }
   }
}
