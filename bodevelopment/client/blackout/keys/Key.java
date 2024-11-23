package bodevelopment.client.blackout.keys;

public class Key extends Pressable {
   public Key(int key) {
      super(key);
   }

   public boolean isPressed() {
      return this.key == -1 ? false : Keys.get(this.key);
   }

   public String getName() {
      return Keys.getKeyName(this.key);
   }
}
