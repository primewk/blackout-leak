package bodevelopment.client.blackout.event.events;

public class MouseButtonEvent {
   private static final MouseButtonEvent INSTANCE = new MouseButtonEvent();
   public int button = 0;
   public boolean pressed = false;

   public static MouseButtonEvent get(int button, boolean pressed) {
      INSTANCE.button = button;
      INSTANCE.pressed = pressed;
      return INSTANCE;
   }
}
