package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_2596;
import net.minecraft.class_2827;
import net.minecraft.class_3532;
import net.minecraft.class_6374;

public class PingSpoof extends Module {
   private static PingSpoof INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<PingSpoof.SpoofMode> mode;
   private final Setting<Integer> extra;
   private final Setting<Integer> jitter;
   private final Setting<Integer> jitterInterval;
   private int ji;
   private long nextJ;

   public PingSpoof() {
      super("Ping Spoof", "Increases your ping.", SubCategory.MISC, true);
      this.mode = this.sgGeneral.e("Spoof Mode", PingSpoof.SpoofMode.Fake, "Real mode actually increases your ping.");
      this.extra = this.sgGeneral.i("Extra", 50, 0, 1000, 10, ".");
      this.jitter = this.sgGeneral.i("Jitter", 5, 0, 1000, 10, ".");
      this.jitterInterval = this.sgGeneral.i("Jitter Interval", 5, 0, 20, 1, ".", () -> {
         return this.mode.get() == PingSpoof.SpoofMode.Real;
      });
      this.ji = 0;
      this.nextJ = 0L;
      INSTANCE = this;
   }

   public static PingSpoof getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      String var10000 = ((PingSpoof.SpoofMode)this.mode.get()).name();
      return var10000 + " " + this.extra.get() + " " + this.ji;
   }

   public void refresh() {
      if (System.currentTimeMillis() > this.nextJ) {
         this.ji = (int)Math.round(Math.random() * (double)(Integer)this.jitter.get());
         this.nextJ = System.currentTimeMillis() + Math.round(class_3532.method_16436(Math.random(), (double)((float)(Integer)this.jitterInterval.get() / 2.0F), (double)(Integer)this.jitterInterval.get() * 1.5D) * 50.0D);
      }

   }

   public boolean shouldDelay(class_2596<?> packet) {
      return this.mode.get() == PingSpoof.SpoofMode.Real || packet instanceof class_6374 || packet instanceof class_2827;
   }

   public int getPing() {
      return this.mode.get() == PingSpoof.SpoofMode.Fake ? (Integer)this.extra.get() : (Integer)this.extra.get() + this.ji;
   }

   public static enum SpoofMode {
      Fake,
      Real;

      // $FF: synthetic method
      private static PingSpoof.SpoofMode[] $values() {
         return new PingSpoof.SpoofMode[]{Fake, Real};
      }
   }
}
