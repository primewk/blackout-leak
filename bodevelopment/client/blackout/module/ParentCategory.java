package bodevelopment.client.blackout.module;

import java.util.ArrayList;
import java.util.List;

public class ParentCategory implements Category {
   public static List<ParentCategory> categories = new ArrayList();
   public static ParentCategory COMBAT = new ParentCategory("Combat");
   public static ParentCategory MOVEMENT = new ParentCategory("Movement");
   public static ParentCategory VISUAL = new ParentCategory("Visual");
   public static ParentCategory MISC = new ParentCategory("Misc");
   public static ParentCategory LEGIT = new ParentCategory("Legit");
   public static ParentCategory CLIENT = new ParentCategory("Client");
   private final String name;

   public ParentCategory(String name) {
      categories.add(this);
      this.name = name;
   }

   public String name() {
      return this.name;
   }
}
