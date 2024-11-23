package bodevelopment.client.blackout.gui.clickgui.screens;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.FileUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.class_3532;
import org.apache.commons.lang3.mutable.MutableDouble;

public class ConfigScreen extends ClickGuiScreen {
   private float prevLength = 300.0F;
   private long prevUpdate = 0L;
   private boolean first;
   private final Map<String, MutableDouble> configs = new HashMap();
   private static final int lineColor = (new Color(50, 50, 50, 255)).getRGB();
   private boolean typing = false;
   private boolean isCloud = false;
   private final TextField textField = new TextField();
   private final int id = SelectedComponent.nextId();
   private final List<ConfigScreen.CloudConfig> cloudConfigs = new ArrayList();
   private static final String[] iconNames = new String[]{"Combat", "Movement", "Visual", "Misc", "Legit", "Settings", "HUD", "Binds"};
   private static final TextureRenderer[] icons = new TextureRenderer[]{BOTextures.getCombatIconRenderer(), BOTextures.getMovementIconRenderer(), BOTextures.getVisualIconRenderer(), BOTextures.getMiscIconRenderer(), BOTextures.getGhostIconRenderer(), BOTextures.getSettingsIconRenderer(), BOTextures.getHudIconRenderer(), BOTextures.getBindsIconRenderer()};

   public ConfigScreen() {
      super("Configs", 800.0F, 500.0F, true);
   }

   protected float getLength() {
      return this.prevLength;
   }

   public void render() {
      if (System.currentTimeMillis() - this.prevUpdate > 500L) {
         this.updateConfigs();
      }

      this.updateDelete();
      this.renderConfigs();
      this.renderText();
      this.renderBottomBG();
      this.renderBottom();
   }

   public void onMouse(int button, boolean state) {
      if (!(this.my > (double)(this.height - 100.0F))) {
         if (!(this.my < 0.0D)) {
            if (state) {
               if (button == 0) {
                  if (this.textField.click(0, true) && this.typing) {
                     SelectedComponent.setId(this.id);
                  } else {
                     double offsetY = this.my + (double)this.scroll.get() - 50.0D;

                     double offsetX;
                     for(Iterator var5 = this.configs.entrySet().iterator(); var5.hasNext(); offsetY -= 70.0D) {
                        Entry<String, MutableDouble> config = (Entry)var5.next();
                        offsetX = this.mx - 215.0D;
                        if (offsetX * offsetX + offsetY * offsetY < 200.0D) {
                           this.duplicate((String)config.getKey());
                           break;
                        }

                        offsetX -= 35.0D;
                        if (offsetX * offsetX + offsetY * offsetY < 200.0D) {
                           this.delete((String)config.getKey());
                           break;
                        }

                        for(int i = 0; i < 8; ++i) {
                           offsetX -= 65.0D;
                           if (offsetX * offsetX + offsetY * offsetY < 1000.0D) {
                              this.set((String)config.getKey(), i);
                              break;
                           }
                        }
                     }

                     double offsetX = this.mx - (double)(this.width / 2.0F);
                     offsetX = class_3532.method_15350(offsetX, -50.0D, 50.0D);
                     double dx = offsetX - offsetX;
                     dx *= dx;
                     double dy = offsetY * offsetY;
                     if (dx + dy <= 400.0D) {
                        if (offsetX > 0.0D) {
                           this.clickedCloud();
                        } else {
                           this.clickedAdd();
                        }
                     }

                     offsetY -= 70.0D;

                     for(Iterator var13 = this.cloudConfigs.iterator(); var13.hasNext(); offsetY -= 70.0D) {
                        ConfigScreen.CloudConfig config = (ConfigScreen.CloudConfig)var13.next();
                        if (Math.abs(offsetY) < 35.0D && config.content().isDone()) {
                           this.downloadConfig(config);
                           this.updateConfigs();
                           break;
                        }
                     }

                  }
               }
            }
         }
      }
   }

   public void onKey(int key, boolean state) {
      if (this.typing) {
         if (state && key == 257) {
            if (this.isCloud) {
               this.requestCloudConfigs(this.textField.getContent());
            } else {
               this.addConfig(this.textField.getContent());
            }

            this.typing = false;
            SelectedComponent.reset();
         } else {
            this.textField.type(key, state);
         }
      }
   }

   private void clickedCloud() {
      this.typing = true;
      this.isCloud = true;
      this.textField.clear();
      this.textField.setContent("KassuK1/blackout-configs");
   }

   private void clickedAdd() {
      this.typing = true;
      this.isCloud = false;
      this.textField.clear();
   }

   private void addConfig(String name) {
      if (!name.isEmpty() && !name.isBlank() && !name.contains("/") && !name.contains("\\")) {
         FileUtils.addFile("configs", name + ".json");
         this.updateConfigs();
      }
   }

   private void downloadConfig(ConfigScreen.CloudConfig config) {
      File file = this.getNewConfigFile(config.name());
      if (file != null && !file.exists()) {
         try {
            FileUtils.addFile(file);
            FileUtils.write(file, (String)config.content().get());
         } catch (ExecutionException | InterruptedException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   private void requestCloudConfigs(String repo) {
      if (!repo.isEmpty() && !repo.isBlank() && repo.contains("/")) {
         this.cloudConfigs.clear();
         CompletableFuture.runAsync(() -> {
            try {
               InputStream configStream = (new URL(String.format("https://raw.githubusercontent.com/%s/main/configs.txt", repo))).openStream();
               BufferedReader read = new BufferedReader(new InputStreamReader(configStream));
               read.lines().forEach((line) -> {
                  this.readLine(repo, line);
               });
               read.close();
               Iterator var4 = this.cloudConfigs.iterator();

               while(var4.hasNext()) {
                  ConfigScreen.CloudConfig cloudConfig = (ConfigScreen.CloudConfig)var4.next();

                  try {
                     this.readConfig(cloudConfig);
                  } catch (IOException var7) {
                  }
               }
            } catch (IOException var8) {
            }

         });
      }
   }

   private void readConfig(ConfigScreen.CloudConfig cloudConfig) throws IOException {
      InputStream configStream = (new URL(String.format("https://raw.githubusercontent.com/%s/main/configs/%s.json", cloudConfig.repo(), cloudConfig.name()))).openStream();

      try {
         cloudConfig.content().complete(new String(configStream.readAllBytes()));
      } catch (Throwable var6) {
         if (configStream != null) {
            try {
               configStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (configStream != null) {
         configStream.close();
      }

   }

   private void readLine(String repo, String line) {
      String[] strings = line.split(":");
      if (strings.length >= 2) {
         String name = strings[0];
         String description = strings[1];
         int firstIndex = description.indexOf("\"");
         if (firstIndex >= 0) {
            description = description.substring(firstIndex + 1);
            int lastIndex = description.lastIndexOf("\"");
            if (lastIndex >= 0) {
               this.cloudConfigs.add(new ConfigScreen.CloudConfig(name, description.substring(0, lastIndex), repo, new CompletableFuture()));
            }
         }
      }
   }

   private void updateDelete() {
      double offsetY = this.my + (double)this.scroll.get() - 50.0D;
      double offsetX = this.mx - 250.0D;
      double dx = offsetX * offsetX;

      for(Iterator var7 = this.configs.entrySet().iterator(); var7.hasNext(); offsetY -= 70.0D) {
         Entry<String, MutableDouble> config = (Entry)var7.next();
         MutableDouble mutableDouble = (MutableDouble)config.getValue();
         if (offsetY * offsetY + dx < 200.0D) {
            mutableDouble.setValue(Math.min(mutableDouble.getValue() + (double)this.frameTime, 5.0D));
         } else {
            mutableDouble.setValue(Math.max(mutableDouble.getValue() - (double)(this.frameTime * 5.0F), 0.0D));
         }
      }

   }

   private void set(String config, int i) {
      Managers.CONFIG.saveAll();
      Managers.CONFIG.writeCurrent();
      Managers.CONFIG.getConfigs()[i] = config;
      Managers.CONFIG.set();
      Managers.CONFIG.readConfigs();
   }

   private void duplicate(String config) {
      File newFile = this.getNewConfigFile(config);
      if (newFile != null && !newFile.exists()) {
         File fromFile = FileUtils.getFile("configs", config + ".json");
         if (fromFile.exists()) {
            FileUtils.addFile(newFile);
            FileUtils.write(newFile, FileUtils.readString(fromFile));
            this.updateConfigs();
         }
      }
   }

   private File getNewConfigFile(String name) {
      File newFile = FileUtils.getFile("configs", name + ".json");
      if (!newFile.exists()) {
         return newFile;
      } else {
         char lastChar = name.charAt(name.length() - 1);
         String withoutNumber = Character.isDigit(lastChar) ? name.substring(0, name.length() - 1) : name;

         for(int i = Character.isDigit(lastChar) ? Integer.parseInt(String.valueOf(lastChar)) : 1; i <= 9 && (newFile = FileUtils.getFile("configs", withoutNumber + i + ".json")).exists(); ++i) {
         }

         return newFile;
      }
   }

   private void delete(String config) {
      if (this.configs.containsKey(config) && !(((MutableDouble)this.configs.get(config)).getValue() < 5.0D)) {
         String[] var2 = Managers.CONFIG.getConfigs();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String name = var2[var4];
            if (name.equals(config)) {
               return;
            }
         }

         File file = FileUtils.getFile("configs", config + ".json");
         if (file.exists()) {
            file.delete();
            this.updateConfigs();
         }

      }
   }

   private void renderText() {
      this.textField.setActive(this.typing);
      if (this.typing) {
         this.textField.render(this.stack, 2.0F, this.mx, this.my, this.width / 2.0F - 250.0F, this.height - 150.0F, 500.0F, 0.0F, 20.0F, 15.0F, Color.WHITE, GuiColorUtils.bg2);
      }
   }

   private void renderConfigs() {
      this.prevLength = (float)((this.configs.size() + this.cloudConfigs.size()) * 70 + 165);
      this.stack.method_22903();
      this.stack.method_46416(0.0F, 15.0F, 0.0F);
      this.stack.method_46416(0.0F, -this.scroll.get(), 0.0F);
      this.first = true;
      this.configs.forEach(this::renderConfig);
      this.renderAdd();
      this.stack.method_46416(0.0F, 70.0F, 0.0F);
      this.first = true;
      this.cloudConfigs.forEach(this::renderCloudConfig);
      this.stack.method_22909();
   }

   private void renderAdd() {
      this.stack.method_22903();
      this.stack.method_46416(this.width / 2.0F, 30.0F, 0.0F);
      RenderUtils.roundedLeft(this.stack, -50.0F, 0.0F, 50.0F, 0.0F, 20.0F, 15.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.roundedRight(this.stack, 0.0F, 0.0F, 50.0F, 0.0F, 20.0F, 15.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
      BOTextures.getCloudIconRenderer().quad(this.stack, 15.0F, -20.0F, 40.0F, 40.0F);
      BOTextures.getPlusIconRenderer().quad(this.stack, -55.0F, -20.0F, 40.0F, 40.0F);
      RenderUtils.line(this.stack, 0.0F, -20.0F, 0.0F, 20.0F, lineColor);
      this.stack.method_22909();
   }

   private void renderBottomBG() {
      RenderUtils.roundedBottom(this.stack, 0.0F, this.height - 100.0F, this.width, 60.0F, 10.0F, 0.0F, GuiColorUtils.bg2.getRGB(), 0);
      RenderUtils.topFade(this.stack, -10.0F, this.height - 120.0F, this.width + 20.0F, 20.0F, GuiColorUtils.bg2.getRGB());
      RenderUtils.line(this.stack, -10.0F, this.height - 100.0F, this.width + 10.0F, this.height - 100.0F, lineColor);
   }

   private void renderBottom() {
      this.stack.method_22903();
      this.stack.method_46416(250.0F, this.height - 65.0F, 0.0F);

      for(int i = 0; i < 8; ++i) {
         this.stack.method_46416(65.0F, 0.0F, 0.0F);
         icons[i].quad(this.stack, -20.0F, -30.0F, 40.0F, 40.0F);
         BlackOut.FONT.text(this.stack, iconNames[i], 1.6F, 0.0F, 20.0F, Color.WHITE, true, true);
      }

      this.stack.method_22909();
   }

   private void renderCloudConfig(ConfigScreen.CloudConfig config) {
      if (!this.first) {
         RenderUtils.line(this.stack, -10.0F, 0.0F, this.width + 10.0F, 0.0F, lineColor);
      }

      this.first = false;
      int color = (config.content().isDone() ? Color.WHITE : Color.GRAY).getRGB();
      Color descColor = config.content().isDone() ? Color.GRAY : Color.DARK_GRAY;
      BOTextures.getCloud2IconRenderer().quad(this.stack, 10.0F, 15.0F, 40.0F, 40.0F, color);
      BlackOut.FONT.text(this.stack, config.name(), 2.5F, 70.0F, 24.0F, color, false, true);
      BlackOut.FONT.text(this.stack, config.description(), 1.8F, 70.0F, 46.0F, descColor, false, true);
      this.stack.method_46416(0.0F, 70.0F, 0.0F);
   }

   private void renderConfig(String name, MutableDouble mutableDouble) {
      if (!this.first) {
         RenderUtils.line(this.stack, -10.0F, 0.0F, this.width + 10.0F, 0.0F, lineColor);
      }

      this.first = false;
      this.stack.method_22903();
      this.stack.method_46416(250.0F, 35.0F, 0.0F);
      boolean inUse = false;

      for(int i = 0; i < 8; ++i) {
         this.stack.method_46416(65.0F, 0.0F, 0.0F);
         boolean is = Managers.CONFIG.getConfigs()[i].equals(name);
         if (is) {
            inUse = true;
            RenderUtils.rounded(this.stack, 0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 0.0F, Color.WHITE.getRGB(), Color.WHITE.getRGB());
         }

         RenderUtils.roundedShadow(this.stack, 0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 10.0F, ColorUtils.SHADOW100I);
      }

      this.stack.method_22909();
      BOTextures.getCopyIconRenderer().quad(this.stack, 200.0F, 20.0F, 30.0F, 30.0F, Color.WHITE.getRGB());
      double time = mutableDouble.getValue();
      if (!inUse) {
         BOTextures.getTrashIconRenderer().quad(this.stack, 235.0F, 20.0F, 30.0F, 30.0F, this.trashColor(time));
         BlackOut.FONT.text(this.stack, String.format("%.1f", 5.0D - time), 2.0F, 250.0F, 35.0F, this.trashTextColor(time), true, true);
         (time >= 5.0D ? BOTextures.getLockOpenIconRenderer() : BOTextures.getLockIconRenderer()).quad(this.stack, 235.0F, 20.0F, 30.0F, 30.0F, this.lockColor(time));
      } else {
         BOTextures.getTrashIconRenderer().quad(this.stack, 235.0F, 20.0F, 30.0F, 30.0F, Color.RED.getRGB());
      }

      BlackOut.FONT.text(this.stack, name, 2.5F, 10.0F, 35.0F, Color.WHITE, false, true);
      this.stack.method_46416(0.0F, 70.0F, 0.0F);
   }

   private int trashTextColor(double time) {
      double alpha;
      if (time <= 1.0D) {
         alpha = Math.sqrt(time);
      } else if (time >= 4.5D) {
         alpha = 1.0D - Math.sqrt((time - 4.5D) * 2.0D);
      } else {
         alpha = 1.0D;
      }

      return (int)(alpha * 255.0D) << 24 | 16777215;
   }

   private int lockColor(double time) {
      double alpha;
      if (time <= 0.7D) {
         alpha = 0.0D;
      } else if (time <= 1.2D) {
         alpha = Math.sqrt(time - 0.7D) / 2.0D;
      } else if (time >= 5.0D) {
         alpha = 1.0D;
      } else {
         alpha = Math.sqrt(0.5D) / 2.0D;
      }

      return (int)(alpha * 255.0D) << 24 | 16777215;
   }

   private int trashColor(double time) {
      double alpha;
      if (time <= 1.0D) {
         alpha = 1.0D - Math.sqrt(time);
      } else {
         alpha = 0.0D;
      }

      return (int)(alpha * 255.0D) << 24 | 16777215;
   }

   private void updateConfigs() {
      this.prevUpdate = System.currentTimeMillis();

      try {
         List<String> notFound = new ArrayList(this.configs.keySet());
         Files.newDirectoryStream(FileUtils.getFile("configs").toPath(), (path) -> {
            return !Files.isDirectory(path, new LinkOption[0]) && path.getFileName().toString().endsWith(".json");
         }).forEach((path) -> {
            String fileName = path.getFileName().toString();
            String configName = fileName.substring(0, fileName.length() - 5);
            notFound.remove(configName);
            if (!this.configs.containsKey(configName)) {
               this.configs.put(configName, new MutableDouble(0.0D));
            }
         });
         Map var10001 = this.configs;
         Objects.requireNonNull(var10001);
         notFound.forEach(var10001::remove);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private static record CloudConfig(String name, String description, String repo, CompletableFuture<String> content) {
      private CloudConfig(String name, String description, String repo, CompletableFuture<String> content) {
         this.name = name;
         this.description = description;
         this.repo = repo;
         this.content = content;
      }

      public String name() {
         return this.name;
      }

      public String description() {
         return this.description;
      }

      public String repo() {
         return this.repo;
      }

      public CompletableFuture<String> content() {
         return this.content;
      }
   }
}
