package bodevelopment.client.blackout.event.events;

public class MouseScrollEvent {
   private static final MouseScrollEvent INSTANCE = new MouseScrollEvent();
   public double horizontal = 0.0D;
   public double vertical = 0.0D;

   public static MouseScrollEvent get(double horizontal, double vertical) {
      INSTANCE.horizontal = horizontal;
      INSTANCE.vertical = vertical;
      return INSTANCE;
   }
}
