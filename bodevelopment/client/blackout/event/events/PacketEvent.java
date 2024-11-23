package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import net.minecraft.class_2596;

public class PacketEvent {
   public static class Received {
      private static final PacketEvent.Received INSTANCE = new PacketEvent.Received();
      public class_2596<?> packet = null;

      public static PacketEvent.Received get(class_2596<?> packet) {
         INSTANCE.packet = packet;
         return INSTANCE;
      }
   }

   public static class Receive {
      public static class Post extends Cancellable {
         private static final PacketEvent.Receive.Post INSTANCE = new PacketEvent.Receive.Post();
         public class_2596<?> packet = null;

         public static PacketEvent.Receive.Post get(class_2596<?> packet) {
            INSTANCE.packet = packet;
            INSTANCE.setCancelled(false);
            return INSTANCE;
         }
      }

      public static class Pre {
         private static final PacketEvent.Receive.Pre INSTANCE = new PacketEvent.Receive.Pre();
         public class_2596<?> packet = null;

         public static PacketEvent.Receive.Pre get(class_2596<?> packet) {
            INSTANCE.packet = packet;
            return INSTANCE;
         }
      }
   }

   public static class Sent {
      private static final PacketEvent.Sent INSTANCE = new PacketEvent.Sent();
      public class_2596<?> packet = null;

      public static PacketEvent.Sent get(class_2596<?> packet) {
         INSTANCE.packet = packet;
         return INSTANCE;
      }
   }

   public static class Send extends Cancellable {
      private static final PacketEvent.Send INSTANCE = new PacketEvent.Send();
      public class_2596<?> packet = null;

      public static PacketEvent.Send get(class_2596<?> packet) {
         INSTANCE.packet = packet;
         INSTANCE.setCancelled(false);
         return INSTANCE;
      }
   }
}
