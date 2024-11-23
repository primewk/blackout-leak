package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import net.minecraft.class_1113;

public class PlaySoundEvent extends Cancellable {
   private static final PlaySoundEvent INSTANCE = new PlaySoundEvent();
   public class_1113 sound = null;

   public static PlaySoundEvent get(class_1113 sound) {
      INSTANCE.setCancelled(false);
      INSTANCE.sound = sound;
      return INSTANCE;
   }
}
