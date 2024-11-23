package bodevelopment.client.blackout.enums;

import java.util.List;

public enum FilterMode {
   Whitelist,
   Blacklist;

   public <T> boolean shouldAccept(T item, List<T> list) {
      return list.contains(item) ^ !this.equals(Whitelist);
   }

   // $FF: synthetic method
   private static FilterMode[] $values() {
      return new FilterMode[]{Whitelist, Blacklist};
   }
}
