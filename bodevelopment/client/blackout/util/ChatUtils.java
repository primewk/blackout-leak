package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.mixin.IChatHud;
import net.minecraft.class_2561;

public class ChatUtils {
   public static void addMessage(Object object) {
      addMessage(object.toString());
   }

   public static void addMessage(String text, Object... objects) {
      addMessage(String.format(text, objects));
   }

   public static void addMessage(String text) {
      addMessage(class_2561.method_30163(text));
   }

   public static void addMessage(String text, int id) {
      addMessage(class_2561.method_30163(text), id);
   }

   public static void addMessage(class_2561 text) {
      ((IChatHud)BlackOut.mc.field_1705.method_1743()).blackout_Client$addMessageToChat(text, -1);
   }

   public static void addMessage(class_2561 text, int id) {
      ((IChatHud)BlackOut.mc.field_1705.method_1743()).blackout_Client$addMessageToChat(text, id);
   }

   public static void sendMessage(String text) {
      if (text.startsWith("/")) {
         BlackOut.mc.method_1562().method_45730(text.substring(1));
      } else {
         BlackOut.mc.method_1562().method_45729(text);
      }

   }
}
