package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public class BlockStateEvent extends Cancellable {
   private static final BlockStateEvent INSTANCE = new BlockStateEvent();
   public class_2338 pos = null;
   public class_2680 state = null;
   public class_2680 previousState = null;

   public static BlockStateEvent get(class_2338 pos, class_2680 state, class_2680 previousState) {
      INSTANCE.pos = pos;
      INSTANCE.state = state;
      INSTANCE.previousState = previousState;
      INSTANCE.setCancelled(false);
      return INSTANCE;
   }
}
