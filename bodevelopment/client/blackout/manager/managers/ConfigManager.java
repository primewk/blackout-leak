package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.ConfigType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.gui.clickgui.ClickGui;
import bodevelopment.client.blackout.hud.HudEditor;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.ClassUtils;
import bodevelopment.client.blackout.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.spongepowered.include.com.google.common.io.Files;

public class ConfigManager extends Manager {
   private final String[] configs = new String[8];
   private final boolean[] toSave = new boolean[8];
   private long previousSave = 0L;

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
      File file = FileUtils.getFile("config.txt");
      if (file.exists()) {
         List strings;
         try {
            strings = Files.readLines(file, Charset.defaultCharset());
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         if (strings.size() >= this.getConfigs().length) {
            for(int i = 0; i < this.getConfigs().length; ++i) {
               this.getConfigs()[i] = (String)strings.get(i);
            }
         } else {
            Arrays.fill(this.getConfigs(), "default");
         }
      } else {
         Arrays.fill(this.getConfigs(), "default");
      }

      this.set();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      boolean shouldSave = false;
      boolean[] var3 = this.toSave;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         boolean b = var3[var5];
         if (b) {
            shouldSave = true;
            break;
         }
      }

      if (shouldSave && !Managers.MODULE.getModules().isEmpty() && System.currentTimeMillis() > this.previousSave + 10000L && !(BlackOut.mc.field_1755 instanceof ClickGui) && !(BlackOut.mc.field_1755 instanceof HudEditor)) {
         this.writeCurrent();
      }

   }

   public void set() {
      File file = FileUtils.getFile("config.txt");
      FileUtils.addFile(file);
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < this.getConfigs().length; ++i) {
         builder.append(this.getConfigs()[i]);
         if (i < this.getConfigs().length - 1) {
            builder.append("\n");
         }
      }

      FileUtils.write(file, builder.toString());
   }

   public void readConfigs() {
      FileUtils.addFolder("configs");
      ConfigType[] var1 = ConfigType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ConfigType type = var1[var3];
         this.readConfig(this.getConfigs()[type.ordinal()], type);
      }

   }

   public void readConfig(String config, ConfigType type) {
      FileUtils.addFile("configs", config + ".json");
      JsonElement var5 = FileUtils.readElement("configs", config + ".json");
      JsonObject object;
      JsonObject bindObject;
      if (var5 instanceof JsonObject) {
         bindObject = (JsonObject)var5;
         object = bindObject;
      } else {
         object = new JsonObject();
      }

      switch(type) {
      case Combat:
      case Movement:
      case Visual:
      case Misc:
      case Legit:
      case Client:
         JsonElement moduleElement = object.get(type.name());
         if (moduleElement instanceof JsonObject) {
            JsonObject moduleObject = (JsonObject)moduleElement;
            Managers.MODULE.getModules().stream().filter(type.predicate).forEach((module) -> {
               this.readModule(module, moduleObject);
            });
         } else {
            Managers.MODULE.getModules().stream().filter(type.predicate).forEach((module) -> {
               module.settingGroups.forEach((group) -> {
                  group.settings.forEach(Setting::reset);
               });
               module.enabled = false;
            });
         }
         break;
      case HUD:
         Managers.HUD.clear();
         if (object.has("hud")) {
            var5 = object.get("hud");
            if (var5 instanceof JsonObject) {
               bindObject = (JsonObject)var5;
               this.readHudElements(bindObject);
            }
         }
         break;
      case Binds:
         if (object.has("binds")) {
            var5 = object.get("binds");
            if (var5 instanceof JsonObject) {
               bindObject = (JsonObject)var5;
               Managers.MODULE.getModules().forEach((module) -> {
                  if (bindObject.has(module.getFileName())) {
                     JsonElement patt4990$temp = bindObject.get(module.getFileName());
                     if (patt4990$temp instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject)patt4990$temp;
                        module.bind.read(moduleObject);
                        return;
                     }
                  }

                  module.bind.reset();
               });
               return;
            }
         }

         Managers.MODULE.getModules().forEach((module) -> {
            module.bind.reset();
         });
      }

   }

   private void readHudElements(JsonObject jsonObject) {
      jsonObject.asMap().forEach((property, element) -> {
         if (element instanceof JsonObject) {
            JsonObject object = (JsonObject)element;
            String[] strings = property.split("-");
            this.readModule(strings[0], Integer.parseInt(strings[1]), object);
         }

      });
   }

   private void readModule(Module module, JsonObject jsonObject) {
      JsonElement element = jsonObject.get(module.getFileName());
      if (element instanceof JsonObject) {
         JsonObject object = (JsonObject)element;
         if (object.has("enabled")) {
            module.enabled = object.get("enabled").getAsBoolean();
         } else {
            module.enabled = false;
         }

         module.readSettings(object);
      } else {
         module.settingGroups.forEach((group) -> {
            group.settings.forEach(Setting::reset);
         });
         module.enabled = false;
         this.writeModule(module, jsonObject);
      }
   }

   private void readModule(String name, int id, JsonObject jsonObject) {
      Class<? extends HudElement> element = Managers.HUD.getClass(name);
      if (element != null) {
         HudElement hudElement = (HudElement)ClassUtils.instance(element);
         Managers.HUD.getLoaded().put(id, hudElement);
         hudElement.id = id;
         if (jsonObject.has("enabled")) {
            hudElement.enabled = jsonObject.get("enabled").getAsBoolean();
         } else {
            hudElement.enabled = true;
         }

         float scale = 0.5625F;
         hudElement.x = jsonObject.has("positionX") ? jsonObject.get("positionX").getAsFloat() : 500.0F;
         hudElement.y = jsonObject.has("positionY") ? jsonObject.get("positionY").getAsFloat() : 500.0F * scale;
         JsonElement var8 = jsonObject.get("settings");
         if (var8 instanceof JsonObject) {
            JsonObject object = (JsonObject)var8;
            hudElement.readSettings(object);
         } else {
            hudElement.forEachSetting(Setting::reset);
            this.writeHudElement(hudElement, id, jsonObject);
         }
      }
   }

   public void writeCurrent() {
      ConfigType[] var1 = ConfigType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ConfigType configType = var1[var3];
         this.writeConfig(this.getConfigs()[configType.ordinal()], configType);
      }

   }

   public void writeConfig(String name, ConfigType type) {
      FileUtils.addFile("configs", name + ".json");
      JsonElement jsonElement = FileUtils.readElement("configs", name + ".json");
      JsonObject prevObject;
      if (jsonElement instanceof JsonObject) {
         JsonObject object = (JsonObject)jsonElement;
         prevObject = object;
      } else {
         prevObject = null;
      }

      boolean prevFound = prevObject != null;
      JsonObject configObject = new JsonObject();
      configObject.addProperty("description", prevFound && prevObject.has("description") ? prevObject.get("description").getAsString() : "");
      JsonObject timeObject = new JsonObject();
      LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
      timeObject.addProperty("year", time.getYear());
      timeObject.addProperty("month", time.getMonthValue());
      timeObject.addProperty("day", time.getDayOfMonth());
      timeObject.addProperty("hour", time.getHour());
      timeObject.addProperty("minute", time.getMinute());
      timeObject.addProperty("second", time.getSecond());
      configObject.add("lastSave", timeObject);
      JsonObject moduleObject = new JsonObject();
      if (type.predicate != null) {
         Managers.MODULE.getModules().stream().filter(type.predicate).forEach((module) -> {
            this.writeModule(module, moduleObject);
         });
      }

      ConfigType[] var10 = ConfigType.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         ConfigType configType = var10[var12];
         if (configType.predicate != null) {
            String key = configType.name();
            if (configType == type) {
               configObject.add(key, moduleObject);
            } else if (prevFound && prevObject.has(key)) {
               JsonElement element = prevObject.get(key);
               if (element instanceof JsonObject) {
                  JsonObject object = (JsonObject)element;
                  configObject.add(key, object);
               } else {
                  configObject.add(key, new JsonObject());
               }
            } else {
               configObject.add(key, new JsonObject());
            }
         }
      }

      JsonObject object;
      JsonObject bindObject;
      JsonElement var20;
      if (type == ConfigType.HUD) {
         bindObject = new JsonObject();
         Managers.HUD.forEachElement((id, elementx) -> {
            this.writeHudElement(elementx, id, bindObject);
         });
         configObject.add("hud", bindObject);
      } else {
         label62: {
            if (prevFound && prevObject.has("hud")) {
               var20 = prevObject.get("hud");
               if (var20 instanceof JsonObject) {
                  object = (JsonObject)var20;
                  configObject.add("hud", object);
                  break label62;
               }
            }

            configObject.add("hud", new JsonObject());
         }
      }

      if (type == ConfigType.Binds) {
         bindObject = new JsonObject();
         Managers.MODULE.getModules().forEach((module) -> {
            JsonObject jsonObject = new JsonObject();
            module.bind.write(jsonObject);
            bindObject.add(module.getFileName(), jsonObject);
         });
         configObject.add("binds", bindObject);
      } else {
         label55: {
            if (prevFound && prevObject.has("binds")) {
               var20 = prevObject.get("binds");
               if (var20 instanceof JsonObject) {
                  object = (JsonObject)var20;
                  configObject.add("binds", object);
                  break label55;
               }
            }

            configObject.add("binds", new JsonObject());
         }
      }

      FileUtils.write(FileUtils.getFile("configs", name + ".json"), configObject);
      this.previousSave = System.currentTimeMillis();
      this.toSave[type.ordinal()] = false;
   }

   private void writeHudElement(HudElement element, int id, JsonObject jsonObject) {
      String fileName = element.getClass().getSimpleName();
      JsonObject object = new JsonObject();
      JsonObject settingsObject = new JsonObject();
      object.addProperty("enabled", element.enabled);
      object.addProperty("positionX", element.x);
      object.addProperty("positionY", element.y);
      this.writeSettings(element, settingsObject);
      object.add("settings", settingsObject);
      jsonObject.add(fileName + "-" + id, object);
   }

   private void writeModule(Module module, JsonObject jsonObject) {
      String fileName = module.getFileName();
      JsonObject object = new JsonObject();
      jsonObject.add(fileName, object);
      this.writeSettings(module, object);
   }

   public void writeSettings(HudElement element, JsonObject jsonObject) {
      element.writeSettings(jsonObject);
   }

   public void writeSettings(Module module, JsonObject jsonObject) {
      jsonObject.addProperty("enabled", module.enabled);
      module.writeSettings(jsonObject);
   }

   public void save(ConfigType type) {
      this.toSave[type.ordinal()] = true;
   }

   public void saveAll() {
      Arrays.fill(this.toSave, true);
   }

   public void saveModule(Module module) {
      ConfigType[] var2 = ConfigType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ConfigType configType = var2[var4];
         Predicate<Module> predicate = configType.predicate;
         if (predicate != null && predicate.test(module)) {
            this.save(configType);
         }
      }

   }

   public String[] getConfigs() {
      return this.configs;
   }
}
