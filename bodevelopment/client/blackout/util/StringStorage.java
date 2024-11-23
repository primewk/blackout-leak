package bodevelopment.client.blackout.util;

import net.minecraft.class_3532;

public class StringStorage {
   private static final String[] adjectives = new String[]{"Fat", "Goofy", "Funny", "Sad", "Mad", "Large", "Former", "Massive", "Huge", "Angry", "Legal", "Nice", "Cute", "Happy", "Poor", "Hot", "Strong", "Known", "Scared", "Old", "Fast", "Epic", "Best", "Wide", "Smart"};
   private static final String[] substantives = new String[]{"Dog", "Pig", "Bear", "Player", "Salmon", "Fish", "Sheep", "Cow", "Bat", "Goose", "Ostrich", "Emu", "Kiwi", "Hog", "Sloth", "Noob", "Person", "Kid", "Rat", "Mouse", "Cat", "Bird"};

   public static String randomAdj() {
      return (String)getRandom(adjectives);
   }

   public static String randomSub() {
      return (String)getRandom(substantives);
   }

   private static <T> T getRandom(T[] array) {
      return array[(int)Math.round(class_3532.method_16436(Math.random(), 0.0D, (double)(array.length - 1)))];
   }
}
