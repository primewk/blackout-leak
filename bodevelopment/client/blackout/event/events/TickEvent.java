package bodevelopment.client.blackout.event.events;

public class TickEvent {
   public static class Post extends TickEvent {
      private static final TickEvent.Post INSTANCE = new TickEvent.Post();

      public static TickEvent.Post get() {
         return INSTANCE;
      }
   }

   public static class Pre extends TickEvent {
      private static final TickEvent.Pre INSTANCE = new TickEvent.Pre();

      public static TickEvent.Pre get() {
         return INSTANCE;
      }
   }
}
