package bodevelopment.client.blackout.event.events;

import net.minecraft.class_742;

public class PopEvent {
   private static final PopEvent INSTANCE = new PopEvent();
   public class_742 player;
   public int number = 0;

   public static PopEvent get(class_742 player, int number) {
      INSTANCE.player = player;
      INSTANCE.number = number;
      return INSTANCE;
   }
}
