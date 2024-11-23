package bodevelopment.client.blackout.command;

import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Iterator;
import net.minecraft.class_124;

public class ToggleCommand extends Command {
   private final String lowerCase;
   private final String color;

   public ToggleCommand(String action, class_124 formatting) {
      super(action, action.toLowerCase() + " <name>");
      this.lowerCase = action.toLowerCase();
      this.color = formatting.toString();
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         StringBuilder builder = new StringBuilder();

         for(int i = 0; i < args.length; ++i) {
            builder.append(args[i]);
            if (i < args.length - 1) {
               builder.append(" ");
            }
         }

         String built = builder.toString();
         String idkRurAmogus = this.doStuff(built);
         Module module = this.getModule(idkRurAmogus);
         if (module == null) {
            Module similar = this.similar(idkRurAmogus);
            if (similar != null) {
               return String.format("%s couldn't find %s%s%s from modules, did you mean %s", class_124.field_1061, class_124.field_1079, built, class_124.field_1061, this.moduleNameString(similar));
            } else {
               return String.format("%s couldn't find %s%s%s from modules", class_124.field_1061, class_124.field_1079, built, class_124.field_1061);
            }
         } else if (module.toggleable()) {
            module.enable();
            String displayName = module.getDisplayName();
            String var10000 = this.color;
            return var10000 + this.lowerCase + "d " + class_124.field_1068 + this.moduleNameString(module);
         } else {
            return String.format("%s%s%s is not toggleable", class_124.field_1079, this.moduleNameString(module), class_124.field_1061);
         }
      } else {
         return this.format;
      }
   }

   private String moduleNameString(Module module) {
      String displayName = module.getDisplayName();
      return module.name.equals(module.getDisplayName()) ? module.name : String.format("%s (%s)", module.getDisplayName(), module.name);
   }

   private Module similar(String input) {
      Module best = null;
      double highest = 0.0D;
      Iterator var5 = Managers.MODULE.getModules().iterator();

      while(var5.hasNext()) {
         Module module = (Module)var5.next();
         double similarity = Math.max(OLEPOSSUtils.similarity(input, this.doStuff(module.name)), OLEPOSSUtils.similarity(input, this.doStuff(module.getDisplayName())));
         if (!(similarity <= highest)) {
            best = module;
            highest = similarity;
         }
      }

      return best;
   }

   private Module getModule(String name) {
      Module display = null;
      Iterator var3 = Managers.MODULE.getModules().iterator();

      while(var3.hasNext()) {
         Module module = (Module)var3.next();
         if (name.equals(this.doStuff(module.name))) {
            return module;
         }

         if (name.equals(this.doStuff(module.getDisplayName()))) {
            display = module;
         }
      }

      return display;
   }

   private String doStuff(String string) {
      return string.toLowerCase().replace(" ", "");
   }
}
