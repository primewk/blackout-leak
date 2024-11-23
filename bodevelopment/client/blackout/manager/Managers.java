package bodevelopment.client.blackout.manager;

import bodevelopment.client.blackout.manager.managers.AltManager;
import bodevelopment.client.blackout.manager.managers.BlockManager;
import bodevelopment.client.blackout.manager.managers.ClickGuiManager;
import bodevelopment.client.blackout.manager.managers.CommandManager;
import bodevelopment.client.blackout.manager.managers.ConfigManager;
import bodevelopment.client.blackout.manager.managers.EntityManager;
import bodevelopment.client.blackout.manager.managers.ExtrapolationManager;
import bodevelopment.client.blackout.manager.managers.FakePlayerManager;
import bodevelopment.client.blackout.manager.managers.FrameBufferManager;
import bodevelopment.client.blackout.manager.managers.FriendsManager;
import bodevelopment.client.blackout.manager.managers.HUDManager;
import bodevelopment.client.blackout.manager.managers.ModuleManager;
import bodevelopment.client.blackout.manager.managers.NotificationManager;
import bodevelopment.client.blackout.manager.managers.PacketManager;
import bodevelopment.client.blackout.manager.managers.ParticleManager;
import bodevelopment.client.blackout.manager.managers.PingManager;
import bodevelopment.client.blackout.manager.managers.RotationManager;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.manager.managers.TPSManager;
import bodevelopment.client.blackout.manager.managers.UtilsManager;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public class Managers {
   public static final AltManager ALT = new AltManager();
   public static final BlockManager BLOCK = new BlockManager();
   public static final ClickGuiManager CLICK_GUI = new ClickGuiManager();
   public static final CommandManager COMMANDS = new CommandManager();
   public static final ConfigManager CONFIG = new ConfigManager();
   public static final EntityManager ENTITY = new EntityManager();
   public static final ExtrapolationManager EXTRAPOLATION = new ExtrapolationManager();
   public static final FakePlayerManager FAKE_PLAYER = new FakePlayerManager();
   public static final FrameBufferManager FRAME_BUFFER = new FrameBufferManager();
   public static final FriendsManager FRIENDS = new FriendsManager();
   public static final HUDManager HUD = new HUDManager();
   public static final ModuleManager MODULE = new ModuleManager();
   public static final NotificationManager NOTIFICATIONS = new NotificationManager();
   public static final PacketManager PACKET = new PacketManager();
   public static final ParticleManager PARTICLE = new ParticleManager();
   public static final PingManager PING = new PingManager();
   public static final RotationManager ROTATION = new RotationManager();
   public static final StatsManager STATS = new StatsManager();
   public static final TPSManager TPS = new TPSManager();
   public static final UtilsManager UTILS = new UtilsManager();

   public static void init() {
      forEach((field) -> {
         try {
            ((Manager)field.get(field)).init();
         } catch (IllegalAccessException var2) {
            throw new RuntimeException(var2);
         }
      });
      forEach((field) -> {
         try {
            ((Manager)field.get(field)).postInit();
         } catch (IllegalAccessException var2) {
            throw new RuntimeException(var2);
         }
      });
   }

   private static void forEach(Consumer<? super Field> consumer) {
      Arrays.stream(Managers.class.getDeclaredFields()).forEach(consumer);
   }
}
