package bodevelopment.client.blackout.gui.menu;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.helpers.ScrollHelper;
import bodevelopment.client.blackout.helpers.SmoothScrollHelper;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.MainMenuSettings;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.SoundUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_429;
import net.minecraft.class_442;
import net.minecraft.class_4587;
import net.minecraft.class_500;
import net.minecraft.class_526;

public class MainMenu {
   private static final MainMenu INSTANCE = new MainMenu();
   public final String[] buttonNames = new String[]{"Singleplayer", "Multiplayer", "AltManager", "Options", "Quit"};
   private class_442 titleScreen;
   private final String splashText = this.getSplash();
   private final class_4587 stack = new class_4587();
   public static final int EMPTY_COLOR = (new Color(0, 0, 0, 0)).getRGB();
   private float windowHeight;
   private float progress = 0.0F;
   private float mx;
   private float my;
   private boolean altManagerOpen = false;
   private long altManagerTime = 0L;
   private float delta;
   private float altLength = 0.0F;
   private final TextField textField = new TextField();
   private final ScrollHelper scroll = (new SmoothScrollHelper(0.5F, 20.0F, () -> {
      return 0.0F;
   }, () -> {
      return Math.max(this.altLength - 2000.0F, 0.0F);
   })).limit(5.0F);
   private boolean playedStartup = false;
   private final Runnable[] runnables;

   public MainMenu() {
      Runnable[] var10001 = new Runnable[]{() -> {
         BlackOut.mc.method_1507(new class_526(this.titleScreen));
      }, () -> {
         BlackOut.mc.method_1507(new class_500(this.titleScreen));
      }, () -> {
         if (this.altManagerOpen) {
            this.closeAltManager();
         } else {
            this.openAltManager();
         }

      }, () -> {
         BlackOut.mc.method_1507(new class_429(this.titleScreen, BlackOut.mc.field_1690));
      }, null};
      class_310 var10004 = BlackOut.mc;
      Objects.requireNonNull(var10004);
      var10001[4] = var10004::method_1592;
      this.runnables = var10001;
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return !(BlackOut.mc.field_1755 instanceof class_442);
      });
   }

   public static MainMenu getInstance() {
      return INSTANCE;
   }

   public void set(class_442 titleScreen) {
      this.titleScreen = titleScreen;
   }

   public void render(int mouseX, int mouseY, float delta) {
      if (!this.playedStartup) {
         SoundUtils.play(1.0F, 10.0F, "startup");
         this.playedStartup = true;
      }

      this.delta = delta / 20.0F;
      float scale = (float)BlackOut.mc.method_22683().method_4480() / 2000.0F;
      this.windowHeight = (float)BlackOut.mc.method_22683().method_4507() / (float)BlackOut.mc.method_22683().method_4480() * 2000.0F;
      float switchProgress = this.getSwitchProgress();
      float menuAlpha = class_3532.method_15363(class_3532.method_37960(switchProgress, 0.4F, 0.0F), 0.0F, 1.0F);
      float altManagerAlpha = class_3532.method_15363(class_3532.method_37960(switchProgress, 0.6F, 1.0F), 0.0F, 1.0F);
      float bgDarkness = (float)(1.0D - AnimUtils.easeInOutCubic((double)(Math.min(Math.abs(switchProgress - 0.5F), 0.5F) / 0.5F)));
      this.updateMousePos(mouseX, mouseY, scale);
      this.startRender(scale, bgDarkness);
      if (menuAlpha > 0.0F) {
         Renderer.setAlpha(menuAlpha);
         MainMenuSettings.getInstance().getRenderer().render(this.stack, this.windowHeight, this.mx, this.my, this.splashText);
         this.renderIconButtons();
         this.renderDevs();
      }

      if (altManagerAlpha > 0.0F) {
         Renderer.setAlpha(altManagerAlpha);
         this.renderAltManager();
      }

      this.endRender();
   }

   private void renderAltManager() {
      this.renderAltManagerTitle();
      this.renderCurrentSession();
      this.renderTextField();
      this.renderAccounts();
   }

   private void renderDevs() {
      String devText = "Made by KassuK and OLEPOSSU";
      float x = 1000.0F;
      float y = this.windowHeight / 2.0F;
      BlackOut.FONT.text(this.stack, devText, 2.0F, x - BlackOut.FONT.getWidth(devText) * 2.0F - 2.0F, y - BlackOut.FONT.getHeight() * 2.0F - 2.0F, new Color(255, 255, 255, 50), false, false);
   }

   private void renderTextField() {
      if (!this.textField.isEmpty()) {
         this.progress = Math.min(this.progress + this.delta, 1.0F);
      } else {
         this.progress = Math.max(this.progress - this.delta, 0.0F);
      }

      this.textField.render(this.stack, 4.0F, (double)this.mx, (double)this.my, -200.0F, 400.0F, 400.0F, 0.0F, 24.0F, 48.0F, new Color(255, 255, 255, (int)Math.floor((double)(this.progress * 255.0F))), new Color(0, 0, 0, (int)Math.floor((double)(this.progress * 30.0F))));
   }

   private void renderCurrentSession() {
      Managers.ALT.currentSession.render(this.stack, -940.0F, this.windowHeight / 2.0F - 65.0F - 60.0F, this.delta);
   }

   private void renderAccounts() {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      this.altLength = -90.0F;
      this.stack.method_22903();
      this.stack.method_46416(0.0F, -this.scroll.get(), 0.0F);
      this.stack.method_46416(-250.0F, this.windowHeight / -2.0F + 200.0F, 0.0F);
      Managers.ALT.getAccounts().forEach((account) -> {
         account.render(this.stack, 0.0F, 0.0F, this.delta);
         float amogus = 155.0F;
         this.stack.method_46416(0.0F, amogus, 0.0F);
         this.altLength += amogus;
      });
      this.stack.method_22909();
   }

   private void renderAltManagerTitle() {
      BlackOut.BOLD_FONT.text(this.stack, "Alt Manager", 8.5F, 0.0F, this.windowHeight / -2.0F + 100.0F, Color.WHITE, true, true);
   }

   private float getSwitchProgress() {
      float f = Math.min((float)(System.currentTimeMillis() - this.altManagerTime) / 600.0F, 1.0F);
      return this.altManagerOpen ? f : 1.0F - f;
   }

   private void clickMenu(int button, boolean pressed) {
      if (pressed && button == 0) {
         if (this.mx >= -210.0F && this.mx <= 210.0F) {
            int i = MainMenuSettings.getInstance().getRenderer().onClick(this.mx, this.my);
            if (i >= 0) {
               SoundUtils.play(1.0F, 3.0F, "menubutton");
               this.runnables[i].run();
            }
         } else if (this.my >= this.windowHeight / 2.0F - 54.0F && this.my <= this.windowHeight - 12.0F) {
            float offset = this.mx + 986.0F;

            for(int i = 0; i < 3; ++i) {
               if (offset >= (float)(-10 + i * 54) && offset <= (float)(32 + i * 54)) {
                  this.onClickIconButton(i);
                  SoundUtils.play(1.0F, 3.0F, "menubutton");
               }
            }
         }

      }
   }

   private void clickAltManager(int button, boolean pressed) {
      if (!this.textField.click(button, pressed)) {
         float x = this.mx + 250.0F;
         float y = this.my + this.windowHeight / 2.0F - 200.0F;

         for(Iterator var5 = Managers.ALT.getAccounts().iterator(); var5.hasNext(); y -= 155.0F) {
            Account account = (Account)var5.next();
            if (this.clickAccount(account, x, y, button, pressed)) {
               return;
            }
         }

         if (!this.clickAccount(Managers.ALT.currentSession, this.mx + 960.0F, this.my - this.windowHeight / 2.0F + 65.0F + 40.0F, button, pressed)) {
            ;
         }
      }
   }

   private boolean clickAccount(Account account, float x, float y, int button, boolean pressed) {
      Account.AccountClickResult result = account.onClick(x, y, button, pressed);
      if (result != Account.AccountClickResult.Nothing) {
         this.handleAltClick(account, result);
         return true;
      } else {
         return false;
      }
   }

   private void handleAltClick(Account account, Account.AccountClickResult result) {
      switch(result) {
      case Nothing:
      default:
         break;
      case Select:
         Managers.ALT.set(account);
         break;
      case Delete:
         Managers.ALT.remove(account);
         break;
      case Refresh:
         account.refresh();
      }

      SoundUtils.play(1.0F, 3.0F, "menubutton");
   }

   private void renderIconButtons() {
      this.stack.method_22903();
      float x = -1000.0F;
      float y = this.windowHeight / 2.0F;
      this.stack.method_46416(x + 14.0F, y - 44.0F, 0.0F);

      for(int i = 0; i < 3; ++i) {
         this.renderIconButton(i);
      }

      this.stack.method_22909();
   }

   private void onClickIconButton(int i) {
      switch(i) {
      case 0:
         this.openLink("https://github.com/KassuK1/Blackout-Client");
         break;
      case 1:
         this.openLink("https://discord.com/invite/mmWz9Dz4Y9");
         break;
      case 2:
         this.openLink("https://www.youtube.com/@BlackOutDevelopment");
      }

   }

   private void openLink(String string) {
      class_156.method_668().method_670(string);
   }

   private void renderIconButton(int i) {
      TextureRenderer var10000;
      switch(i) {
      case 1:
         var10000 = BOTextures.getDiscordIconRenderer();
         break;
      case 2:
         var10000 = BOTextures.getYoutubeIconRenderer();
         break;
      default:
         var10000 = BOTextures.getGithubIconRenderer();
      }

      TextureRenderer t = var10000;
      float width = (float)t.getWidth() / 2.0F;
      float height = (float)t.getHeight() / 2.0F;
      RenderUtils.drawLoadedBlur("title", this.stack, (renderer) -> {
         renderer.rounded(5.0F, 5.0F, 22.0F, 22.0F, 10.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.rounded(this.stack, 5.0F, 5.0F, 22.0F, 22.0F, 10.0F, 3.0F, (new Color(0, 0, 0, 35)).getRGB(), (new Color(0, 0, 0, 225)).getRGB());
      t.quad(this.stack, 0.0F, 0.0F, width, height);
      this.stack.method_46416(54.0F, 0.0F, 0.0F);
   }

   private void updateMousePos(int mouseX, int mouseY, float scale) {
      this.mx = (float)(mouseX * RenderUtils.getScale()) - (float)BlackOut.mc.method_22683().method_4480() / 2.0F;
      this.my = (float)(mouseY * RenderUtils.getScale()) - (float)BlackOut.mc.method_22683().method_4507() / 2.0F;
      this.mx /= scale;
      this.my /= scale;
   }

   private void startRender(float scale, float bgDarkness) {
      this.stack.method_22903();
      RenderUtils.unGuiScale(this.stack);
      MainMenuSettings.getInstance().getRenderer().renderBackground(this.stack, (float)BlackOut.mc.method_22683().method_4480(), (float)BlackOut.mc.method_22683().method_4507(), this.mx, this.my);
      this.stack.method_22905(scale, scale, scale);
      this.stack.method_46416((float)BlackOut.mc.method_1522().field_1480 / 2.0F / scale, (float)BlackOut.mc.method_1522().field_1477 / 2.0F / scale, 0.0F);
   }

   private void blurBackground() {
      RenderUtils.loadBlur("title", 3);
      RenderUtils.drawLoadedBlur("title", this.stack, (renderer) -> {
         renderer.quadShape(0.0F, 0.0F, (float)BlackOut.mc.method_22683().method_4480(), (float)BlackOut.mc.method_22683().method_4507(), 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.loadBlur("title", 6);
   }

   private void endRender() {
      this.stack.method_22909();
   }

   private String getSplash() {
      String[] splashTexts = new String[]{"The best in the business", "The real opp stoppa", "Sponsored by Columbian cartels", "The GOAT assistance software", "Recommended by 9/10 dentists", "Made in Finland", "Innit bruv", "Based & red-pilled", "Bravo 6 blacking-out", "A shark in the water"};
      return splashTexts[(new Random()).nextInt(0, splashTexts.length - 1)];
   }

   private void openAltManager() {
      this.altManagerOpen = true;
      this.altManagerTime = System.currentTimeMillis();
   }

   private void closeAltManager() {
      this.altManagerOpen = false;
      this.altManagerTime = System.currentTimeMillis();
   }

   @Event
   public void onMouse(MouseButtonEvent buttonEvent) {
      float switchProgress = this.getSwitchProgress();
      if (switchProgress == 0.0F) {
         this.clickMenu(buttonEvent.button, buttonEvent.pressed);
      }

      if (switchProgress == 1.0F) {
         this.clickAltManager(buttonEvent.button, buttonEvent.pressed);
      }

   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.pressed && event.key == 256) {
         if (this.getSwitchProgress() == 1.0F) {
            this.closeAltManager();
         }
      } else if (event.pressed && event.key == 257) {
         Managers.ALT.add(new Account(this.textField.getContent()));
         this.textField.clear();
      } else {
         this.textField.type(event.key, event.pressed);
      }

   }
}
