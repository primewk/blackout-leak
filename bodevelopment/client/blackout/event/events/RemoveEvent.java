package bodevelopment.client.blackout.event.events;

import net.minecraft.class_1297;
import net.minecraft.class_1297.class_5529;

public class RemoveEvent {
   private static final RemoveEvent INSTANCE = new RemoveEvent();
   public class_1297 entity;
   public class_5529 removalReason;

   public static RemoveEvent get(class_1297 entity, class_5529 removalReason) {
      INSTANCE.entity = entity;
      INSTANCE.removalReason = removalReason;
      return INSTANCE;
   }
}
