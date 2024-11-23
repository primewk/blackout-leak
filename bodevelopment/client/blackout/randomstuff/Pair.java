package bodevelopment.client.blackout.randomstuff;

import net.minecraft.class_3545;

public class Pair<A, B> extends class_3545<A, B> {
   public Pair(A left, B right) {
      super(left, right);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Pair)) {
         return false;
      } else {
         Pair<?, ?> pair = (Pair)obj;
         return this.method_15442().equals(pair.method_15442()) && this.method_15441().equals(pair.method_15441());
      }
   }
}
