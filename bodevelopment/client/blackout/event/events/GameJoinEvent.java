package bodevelopment.client.blackout.event.events;

import net.minecraft.class_2678;

public class GameJoinEvent {
   private static final GameJoinEvent INSTANCE = new GameJoinEvent();
   public class_2678 packet = null;

   public static GameJoinEvent get(class_2678 packet) {
      INSTANCE.packet = packet;
      return INSTANCE;
   }
}
