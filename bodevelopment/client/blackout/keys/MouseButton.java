package bodevelopment.client.blackout.keys;

public class MouseButton extends Pressable {
   public MouseButton(int key) {
      super(key);
   }

   public boolean isPressed() {
      return this.key == -1 ? false : MouseButtons.get(this.key);
   }

   public String getName() {
      return MouseButtons.getKeyName(this.key);
   }
}
