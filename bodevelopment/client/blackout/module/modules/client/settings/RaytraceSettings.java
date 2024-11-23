package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.NCPRaytracer;
import java.util.Objects;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class RaytraceSettings extends SettingsModule {
   private static RaytraceSettings INSTANCE;
   private final SettingGroup sgInteract = this.addGroup("Interact");
   private final SettingGroup sgPlace = this.addGroup("Place");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgMine = this.addGroup("Mine");
   public final Setting<Boolean> interactTrace;
   private final Setting<RaytraceSettings.PlaceTraceMode> interactMode;
   private final Setting<Double> interactHeight;
   private final Setting<Double> interactHeight1;
   private final Setting<Double> interactHeight2;
   private final Setting<Double> interactExposure;
   private final Setting<Boolean> interactNCP;
   public final Setting<Boolean> placeTrace;
   private final Setting<RaytraceSettings.PlaceTraceMode> placeMode;
   private final Setting<Double> placeHeight;
   private final Setting<Double> placeHeight1;
   private final Setting<Double> placeHeight2;
   private final Setting<Double> placeExposure;
   private final Setting<Boolean> placeNCP;
   public final Setting<Boolean> attackTrace;
   private final Setting<RaytraceSettings.AttackTraceMode> attackMode;
   private final Setting<Double> attackHeight;
   private final Setting<Double> attackHeight1;
   private final Setting<Double> attackHeight2;
   private final Setting<Double> attackExposure;
   private final Setting<Boolean> attackNCP;
   public final Setting<Boolean> mineTrace;
   private final Setting<RaytraceSettings.PlaceTraceMode> mineMode;
   private final Setting<Double> mineHeight;
   private final Setting<Double> mineHeight1;
   private final Setting<Double> mineHeight2;
   private final Setting<Double> mineExposure;
   private final Setting<Boolean> mineNCP;
   private class_3959 raycastContext;
   private class_3965 result;
   private int hit;

   public RaytraceSettings() {
      super("Raytrace", false, true);
      this.interactTrace = this.sgInteract.b("Interact Trace", false, "Raytraces when interacting.");
      SettingGroup var10001 = this.sgInteract;
      RaytraceSettings.PlaceTraceMode var10003 = RaytraceSettings.PlaceTraceMode.SinglePoint;
      Setting var10005 = this.interactTrace;
      Objects.requireNonNull(var10005);
      this.interactMode = var10001.e("Interact Mode", var10003, "Interact trace mode.", var10005::get);
      this.interactHeight = this.sgInteract.d("Interact Height", 0.5D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.SinglePoint && (Boolean)this.interactTrace.get();
      });
      this.interactHeight1 = this.sgInteract.d("Interact Height 1", 0.25D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.interactTrace.get();
      });
      this.interactHeight2 = this.sgInteract.d("Interact Height 2", 0.75D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.interactTrace.get();
      });
      this.interactExposure = this.sgInteract.d("Interact Exposure", 50.0D, 0.0D, 100.0D, 1.0D, "How many % of the block should be seen.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.Exposure && (Boolean)this.interactTrace.get();
      });
      this.interactNCP = this.sgInteract.b("Interact NCP", false, ".");
      this.placeTrace = this.sgPlace.b("Place Trace", false, "Raytraces when placing.");
      var10001 = this.sgPlace;
      var10003 = RaytraceSettings.PlaceTraceMode.SinglePoint;
      var10005 = this.placeTrace;
      Objects.requireNonNull(var10005);
      this.placeMode = var10001.e("Place Mode", var10003, "Place trace mode.", var10005::get);
      this.placeHeight = this.sgPlace.d("Place Height", 0.5D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.SinglePoint && (Boolean)this.placeTrace.get();
      });
      this.placeHeight1 = this.sgPlace.d("Place Height 1", 0.25D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.placeTrace.get();
      });
      this.placeHeight2 = this.sgPlace.d("Place Height 2", 0.75D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.placeTrace.get();
      });
      this.placeExposure = this.sgPlace.d("Place Exposure", 50.0D, 0.0D, 100.0D, 1.0D, "How many % of the block should be seen.", () -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.Exposure && (Boolean)this.placeTrace.get();
      });
      this.placeNCP = this.sgPlace.b("Place NCP", false, ".");
      this.attackTrace = this.sgAttack.b("Attack Trace", false, "Raytraces when attacking.");
      var10001 = this.sgAttack;
      RaytraceSettings.AttackTraceMode var1 = RaytraceSettings.AttackTraceMode.SinglePoint;
      var10005 = this.attackTrace;
      Objects.requireNonNull(var10005);
      this.attackMode = var10001.e("Attack Mode", var1, "Attack trace mode.", var10005::get);
      this.attackHeight = this.sgAttack.d("Attack Height", 0.5D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.attackMode.get() == RaytraceSettings.AttackTraceMode.SinglePoint && (Boolean)this.attackTrace.get();
      });
      this.attackHeight1 = this.sgAttack.d("Attack Height 1", 0.25D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.attackMode.get() == RaytraceSettings.AttackTraceMode.DoublePoint && (Boolean)this.attackTrace.get();
      });
      this.attackHeight2 = this.sgAttack.d("Attack Height 2", 0.75D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.attackMode.get() == RaytraceSettings.AttackTraceMode.DoublePoint && (Boolean)this.attackTrace.get();
      });
      this.attackExposure = this.sgAttack.d("Attack Exposure", 50.0D, 0.0D, 100.0D, 1.0D, "How many % of the block should be seen.", () -> {
         return this.attackMode.get() == RaytraceSettings.AttackTraceMode.Exposure && (Boolean)this.attackTrace.get();
      });
      this.attackNCP = this.sgAttack.b("Attack NCP", false, ".");
      this.mineTrace = this.sgMine.b("Mine Trace", false, "Raytraces when mining.");
      var10001 = this.sgMine;
      var10003 = RaytraceSettings.PlaceTraceMode.SinglePoint;
      var10005 = this.interactTrace;
      Objects.requireNonNull(var10005);
      this.mineMode = var10001.e("Mine Mode", var10003, "Interact trace mode.", var10005::get);
      this.mineHeight = this.sgMine.d("Mine Height", 0.5D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.SinglePoint && (Boolean)this.interactTrace.get();
      });
      this.mineHeight1 = this.sgMine.d("Mine Height 1", 0.25D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.interactTrace.get();
      });
      this.mineHeight2 = this.sgMine.d("Mine Height 2", 0.75D, -2.0D, 2.0D, 0.05D, "Raytraces to x blocks above the bottom.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.interactTrace.get();
      });
      this.mineExposure = this.sgMine.d("Mine Exposure", 50.0D, 0.0D, 100.0D, 1.0D, "How many % of the block should be seen.", () -> {
         return this.interactMode.get() == RaytraceSettings.PlaceTraceMode.Exposure && (Boolean)this.interactTrace.get();
      });
      this.mineNCP = this.sgMine.b("Mine NCP", false, ".");
      this.hit = 0;
      INSTANCE = this;
   }

   public static RaytraceSettings getInstance() {
      return INSTANCE;
   }

   public boolean interactTrace(class_2338 pos) {
      return !(Boolean)this.interactTrace.get() ? true : this.blockTrace(pos, (Boolean)this.interactNCP.get(), this.interactMode, this.interactExposure, this.interactHeight, this.interactHeight1, this.interactHeight2);
   }

   public boolean placeTrace(class_2338 pos) {
      return !(Boolean)this.placeTrace.get() ? true : this.blockTrace(pos, (Boolean)this.placeNCP.get(), this.placeMode, this.placeExposure, this.placeHeight, this.placeHeight1, this.placeHeight2);
   }

   private boolean blockTrace(class_2338 pos, boolean ncp, Setting<RaytraceSettings.PlaceTraceMode> mode, Setting<Double> exposure, Setting<Double> ph, Setting<Double> ph1, Setting<Double> ph2) {
      this.updateContext();
      class_243 vec;
      int x;
      int y;
      int z;
      class_243 vec2;
      switch((RaytraceSettings.PlaceTraceMode)mode.get()) {
      case SinglePoint:
         vec = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + (Double)ph.get(), (double)pos.method_10260() + 0.5D);
         if (ncp) {
            return this.ncpRaytrace(vec, BoxUtils.get(pos));
         }

         ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec);
         this.result = DamageUtils.raycast(this.raycastContext, false);
         return this.result.method_17783() == class_240.field_1333 || this.result.method_17777().equals(pos);
      case DoublePoint:
         vec = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + (Double)ph1.get(), (double)pos.method_10260() + 0.5D);
         class_243 to2 = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + (Double)ph2.get(), (double)pos.method_10260() + 0.5D);
         if (ncp) {
            return this.ncpRaytrace(vec, BoxUtils.get(pos)) || this.ncpRaytrace(to2, BoxUtils.get(pos));
         }

         ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec);
         this.result = DamageUtils.raycast(this.raycastContext, false);
         if (this.result.method_17777().equals(pos)) {
            return true;
         }

         ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(to2);
         this.result = DamageUtils.raycast(this.raycastContext, false);
         return this.result.method_17783() == class_240.field_1333 || this.result.method_17777().equals(pos);
      case Sides:
         vec = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D);
         class_2350[] var14 = class_2350.values();
         y = var14.length;

         for(z = 0; z < y; ++z) {
            class_2350 dir = var14[z];
            class_243 vec2 = vec.method_1031((double)((float)dir.method_10148() / 2.0F), (double)((float)dir.method_10164() / 2.0F), (double)((float)dir.method_10165() / 2.0F));
            if (ncp) {
               return this.ncpRaytrace(vec2, BoxUtils.get(pos));
            }

            ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec.method_1031((double)((float)dir.method_10148() / 2.0F), (double)((float)dir.method_10164() / 2.0F), (double)((float)dir.method_10165() / 2.0F)));
            this.result = DamageUtils.raycast(this.raycastContext, false);
            if (this.result.method_17783() == class_240.field_1333 || this.result.method_17777().equals(pos)) {
               return true;
            }
         }

         return false;
      case Exposure:
         vec = new class_243((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
         this.hit = 0;

         for(x = 0; x <= 2; ++x) {
            for(y = 0; y <= 2; ++y) {
               for(z = 0; z <= 2; ++z) {
                  vec2 = vec.method_1031(0.1D + (double)x * 0.4D, 0.1D + (double)y * 0.4D, 0.1D + (double)z * 0.4D);
                  if (ncp) {
                     if (this.ncpRaytrace(vec2, BoxUtils.get(pos)) && (double)(++this.hit) >= (Double)exposure.get() / 100.0D * 27.0D) {
                        return true;
                     }
                  } else {
                     ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec2);
                     this.result = DamageUtils.raycast(this.raycastContext, false);
                     if (this.result.method_17783() == class_240.field_1333 || this.result.method_17777().equals(pos)) {
                        ++this.hit;
                        if ((double)this.hit >= (Double)exposure.get() / 100.0D * 27.0D) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      case Any:
         vec = new class_243((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
         this.hit = 0;

         for(x = 0; x <= 2; ++x) {
            for(y = 0; y <= 2; ++y) {
               for(z = 0; z <= 2; ++z) {
                  vec2 = vec.method_1031(0.1D + (double)x * 0.4D, 0.1D + (double)y * 0.4D, 0.1D + (double)z * 0.4D);
                  if (ncp) {
                     if (this.ncpRaytrace(vec2, BoxUtils.get(pos))) {
                        return true;
                     }
                  } else {
                     ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec2);
                     this.result = DamageUtils.raycast(this.raycastContext, false);
                     if (this.result.method_17783() == class_240.field_1333 || this.result.method_17777().equals(pos)) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean attackTrace(class_238 box) {
      if (!(Boolean)this.attackTrace.get()) {
         return true;
      } else {
         this.updateContext();
         class_243 vec;
         double xl;
         double yl;
         double zl;
         int x;
         int y;
         int z;
         class_243 vec2;
         switch((RaytraceSettings.AttackTraceMode)this.attackMode.get()) {
         case SinglePoint:
            vec = new class_243((box.field_1323 + box.field_1320) / 2.0D, box.field_1322 + (Double)this.attackHeight.get(), (box.field_1321 + box.field_1324) / 2.0D);
            if ((Boolean)this.attackNCP.get()) {
               return this.ncpRaytrace(vec, box);
            }

            ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), vec, class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
            return DamageUtils.raycast(DamageUtils.raycastContext, false).method_17783() != class_240.field_1332;
         case DoublePoint:
            vec = new class_243((box.field_1323 + box.field_1320) / 2.0D, box.field_1322 + (Double)this.attackHeight1.get(), (box.field_1321 + box.field_1324) / 2.0D);
            class_243 to2 = new class_243((box.field_1323 + box.field_1320) / 2.0D, box.field_1322 + (Double)this.attackHeight2.get(), (box.field_1321 + box.field_1324) / 2.0D);
            if (!(Boolean)this.attackNCP.get()) {
               ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), vec, class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
               if (DamageUtils.raycast(DamageUtils.raycastContext, false).method_17783() != class_240.field_1332) {
                  return true;
               }

               ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), to2, class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
               return DamageUtils.raycast(DamageUtils.raycastContext, false).method_17783() != class_240.field_1332;
            }

            return this.ncpRaytrace(vec, box) || this.ncpRaytrace(to2, box);
         case Exposure:
            vec = new class_243(box.field_1323, box.field_1322, box.field_1321);
            xl = box.method_17939();
            yl = box.method_17940();
            zl = box.method_17941();
            this.hit = 0;

            for(x = 0; x <= 2; ++x) {
               for(y = 0; y <= 2; ++y) {
                  for(z = 0; z <= 2; ++z) {
                     vec2 = vec.method_1031(class_3532.method_16436((double)((float)x / 2.0F), xl * 0.1D, xl * 0.9D), class_3532.method_16436((double)((float)y / 2.0F), yl * 0.1D, yl * 0.9D), class_3532.method_16436((double)((float)z / 2.0F), zl * 0.1D, zl * 0.9D));
                     if ((Boolean)this.attackNCP.get()) {
                        if (this.ncpRaytrace(vec2, box) && (double)(++this.hit) >= (Double)this.attackExposure.get() / 100.0D * 27.0D) {
                           return true;
                        }
                     } else {
                        ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec2);
                        this.result = DamageUtils.raycast(this.raycastContext, false);
                        if (this.result.method_17783() != class_240.field_1332) {
                           ++this.hit;
                           if ((double)this.hit >= (Double)this.attackExposure.get() / 100.0D * 27.0D) {
                              return true;
                           }
                        }
                     }
                  }
               }
            }

            return false;
         case Any:
            vec = new class_243(box.field_1323, box.field_1322, box.field_1321);
            xl = box.method_17939();
            yl = box.method_17940();
            zl = box.method_17941();

            for(x = 0; x <= 2; ++x) {
               for(y = 0; y <= 2; ++y) {
                  for(z = 0; z <= 2; ++z) {
                     vec2 = vec.method_1031(class_3532.method_16436((double)((float)x / 2.0F), xl * 0.1D, xl * 0.9D), class_3532.method_16436((double)((float)y / 2.0F), yl * 0.1D, yl * 0.9D), class_3532.method_16436((double)((float)z / 2.0F), zl * 0.1D, zl * 0.9D));
                     if ((Boolean)this.attackNCP.get()) {
                        if (this.ncpRaytrace(vec2, box)) {
                           return true;
                        }
                     } else {
                        ((IRaycastContext)this.raycastContext).blackout_Client$setEnd(vec2);
                        this.result = DamageUtils.raycast(this.raycastContext, false);
                        if (this.result.method_17783() != class_240.field_1332) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean mineTrace(class_2338 pos) {
      return !(Boolean)this.mineTrace.get() ? true : this.blockTrace(pos, (Boolean)this.mineNCP.get(), this.mineMode, this.mineExposure, this.mineHeight, this.mineHeight1, this.mineHeight2);
   }

   private boolean ncpRaytrace(class_243 to, class_238 box) {
      return NCPRaytracer.raytrace(BlackOut.mc.field_1724.method_33571(), to, box);
   }

   private void updateContext() {
      if (this.raycastContext == null) {
         this.raycastContext = new class_3959(BlackOut.mc.field_1724.method_33571(), (class_243)null, class_3960.field_17558, class_242.field_1347, BlackOut.mc.field_1724);
      } else {
         ((IRaycastContext)this.raycastContext).blackout_Client$setStart(BlackOut.mc.field_1724.method_33571());
      }

   }

   public static enum PlaceTraceMode {
      SinglePoint,
      DoublePoint,
      Sides,
      Exposure,
      Any;

      // $FF: synthetic method
      private static RaytraceSettings.PlaceTraceMode[] $values() {
         return new RaytraceSettings.PlaceTraceMode[]{SinglePoint, DoublePoint, Sides, Exposure, Any};
      }
   }

   public static enum AttackTraceMode {
      SinglePoint,
      DoublePoint,
      Exposure,
      Any;

      // $FF: synthetic method
      private static RaytraceSettings.AttackTraceMode[] $values() {
         return new RaytraceSettings.AttackTraceMode[]{SinglePoint, DoublePoint, Exposure, Any};
      }
   }
}
