package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import net.minecraft.class_1297;

public class EntityAddEvent {
   public static class Post {
      private static final EntityAddEvent.Post INSTANCE = new EntityAddEvent.Post();
      public int id = 0;
      public class_1297 entity = null;

      public static EntityAddEvent.Post get(int id, class_1297 entity) {
         INSTANCE.id = id;
         INSTANCE.entity = entity;
         return INSTANCE;
      }
   }

   public static class Pre extends Cancellable {
      private static final EntityAddEvent.Pre INSTANCE = new EntityAddEvent.Pre();
      public int id = 0;
      public class_1297 entity = null;

      public static EntityAddEvent.Pre get(int id, class_1297 entity) {
         INSTANCE.id = id;
         INSTANCE.entity = entity;
         INSTANCE.setCancelled(false);
         return INSTANCE;
      }
   }
}
