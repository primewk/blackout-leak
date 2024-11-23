package bodevelopment.client.blackout.randomstuff;

import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.manager.Managers;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import net.minecraft.class_1297;
import net.minecraft.class_238;

public class ExtrapolationMap {
   private final Map<class_1297, class_238> boxMap = new ConcurrentHashMap();

   public void update(EpicInterface<class_1297, Integer> extrapolation) {
      Managers.EXTRAPOLATION.extrapolateMap(this.boxMap, extrapolation);
   }

   public class_238 get(class_1297 player) {
      return !this.boxMap.containsKey(player) ? player.method_5829() : (class_238)this.boxMap.get(player);
   }

   public Map<class_1297, class_238> getMap() {
      return this.boxMap;
   }

   public int size() {
      return this.boxMap.size();
   }

   public boolean contains(class_1297 player) {
      return this.boxMap.containsKey(player);
   }

   public Set<Entry<class_1297, class_238>> entrySet() {
      return this.boxMap.entrySet();
   }

   public void forEach(BiConsumer<class_1297, class_238> consumer) {
      this.boxMap.forEach(consumer);
   }

   public void clear() {
      this.boxMap.clear();
   }
}
