package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import net.minecraft.class_1268;
import net.minecraft.class_3965;

public class InteractBlockEvent extends Cancellable {
   private static final InteractBlockEvent INSTANCE = new InteractBlockEvent();
   public class_3965 hitResult = null;
   public class_1268 hand = null;

   public static InteractBlockEvent get(class_3965 hitResult, class_1268 hand) {
      INSTANCE.hitResult = hitResult;
      INSTANCE.hand = hand;
      INSTANCE.setCancelled(false);
      return INSTANCE;
   }
}
