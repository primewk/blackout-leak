package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class Simulation extends Module {
   private static Simulation INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> autoMine;
   private final Setting<Boolean> pickSwitch;
   private final Setting<Boolean> quiverShoot;
   private final Setting<Boolean> hitReset;
   private final Setting<Boolean> stopSprint;
   private final Setting<Double> stopManagerContainer;

   public Simulation() {
      super("Simulation", "Simulates items spawning and blocks breaking when mining.", SubCategory.MISC, false);
      this.autoMine = this.sgGeneral.b("Auto Mine", true, ".");
      this.pickSwitch = this.sgGeneral.b("Pick Switch", true, ".");
      this.quiverShoot = this.sgGeneral.b("Quiver Shoot", true, ".");
      this.hitReset = this.sgGeneral.b("Hit Reset", true, "Resets weapon attack charge when sending an attack packet.");
      this.stopSprint = this.sgGeneral.b("Stop Sprint", false, "Stops sprinting when sending an attack packet.");
      this.stopManagerContainer = this.sgGeneral.d("Stop Manager Container", 0.5D, 0.0D, 5.0D, 0.05D, ".");
      INSTANCE = this;
   }

   public static Simulation getInstance() {
      return INSTANCE;
   }

   public boolean blocks() {
      return this.e(INSTANCE.autoMine);
   }

   public boolean pickSwitch() {
      return this.e(this.pickSwitch);
   }

   public boolean quiverShoot() {
      return this.e(this.quiverShoot);
   }

   public boolean hitReset() {
      return this.e(this.hitReset);
   }

   public boolean stopSprint() {
      return this.e(this.stopSprint);
   }

   public double managerStop() {
      return !this.enabled ? 0.0D : (Double)this.stopManagerContainer.get();
   }

   private boolean e(Setting<Boolean> s) {
      return this.enabled && (Boolean)s.get();
   }
}
