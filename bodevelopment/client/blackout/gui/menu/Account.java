package bodevelopment.client.blackout.gui.menu;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.mixin.IMinecraftClient;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_320;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4844;
import net.minecraft.class_320.class_321;
import org.jetbrains.annotations.Nullable;

public class Account {
   public static final float WIDTH = 500.0F;
   public static final float HEIGHT = 65.0F;
   private float progress = 0.0F;
   private float pulse = 0.0F;
   private String name;
   private String script;
   private UUID uuid;
   private String accessToken;
   private Optional<String> xuid;
   private Optional<String> clientId;
   private class_321 accountType;

   public String getName() {
      return this.name;
   }

   public String getScript() {
      return this.script;
   }

   public Account(String script) {
      this.script = script;
      this.name = AccountScriptReader.nameFromScript(script);
      this.uuid = class_4844.method_43344(this.name);
      this.accessToken = "";
      this.xuid = Optional.empty();
      this.clientId = Optional.empty();
      this.accountType = class_321.field_1988;
   }

   public Account(class_320 session) {
      this.name = session.method_1676();
      this.script = session.method_1676();
      this.uuid = session.method_44717();
      this.accessToken = session.method_1674();
      this.xuid = session.method_38741();
      this.clientId = session.method_38740();
      this.accountType = session.method_35718();
      this.progress = 1.0F;
   }

   public Account(JsonObject object) {
      if (object.has("name")) {
         this.name = object.get("name").getAsString();
      }

      if (object.has("script")) {
         this.script = object.get("script").getAsString();
      }

      if (object.has("uuid")) {
         JsonElement var3 = object.get("uuid");
         if (var3 instanceof JsonObject) {
            JsonObject uuidObject = (JsonObject)var3;
            if (uuidObject.has("mostSigBits") && uuidObject.has("leastSigBits")) {
               this.uuid = new UUID(uuidObject.get("mostSigBits").getAsLong(), uuidObject.get("leastSigBits").getAsLong());
            }
         }
      }

      if (object.has("accessToken")) {
         this.accessToken = object.get("accessToken").getAsString();
      }

      if (object.has("xuid")) {
         this.xuid = this.readOptional(object.get("xuid").getAsString());
      }

      if (object.has("clientId")) {
         this.clientId = this.readOptional(object.get("clientId").getAsString());
      }

      if (object.has("accountType")) {
         this.accountType = this.getAccountType(object.get("accountType").getAsString());
      }

   }

   public Account(String name, String script, UUID uuid, String accessToken, Optional<String> xuid, Optional<String> clientId, class_321 accountType) {
      this.name = name;
      this.script = script;
      this.uuid = uuid;
      this.accessToken = accessToken;
      this.xuid = xuid;
      this.clientId = clientId;
      this.accountType = accountType;
   }

   public Account.AccountClickResult onClick(float clickX, float clickY, int button, boolean pressed) {
      if (!pressed) {
         return Account.AccountClickResult.Nothing;
      } else {
         float dx = class_3532.method_15363(clickX, 0.0F, 500.0F) - clickX;
         float dy = class_3532.method_15363(clickY, 0.0F, 65.0F) - clickY;
         if (Math.sqrt((double)(dx * dx + dy * dy)) > 25.0D) {
            return Account.AccountClickResult.Nothing;
         } else if (button == 1) {
            return Account.AccountClickResult.Refresh;
         } else if (button == 0) {
            return Account.AccountClickResult.Select;
         } else {
            return button == 2 ? Account.AccountClickResult.Delete : Account.AccountClickResult.Nothing;
         }
      }
   }

   public void render(class_4587 stack, float x, float y, float delta) {
      stack.method_22903();
      stack.method_46416(x, y, 0.0F);
      this.updateProgress(delta * 1.5F);
      Color txtColor = this.equals(Managers.ALT.selected) ? Color.gray : Color.WHITE;
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.rounded(0.0F, 0.0F, 500.0F, 65.0F, 25.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.rounded(stack, 0.0F, 0.0F, 500.0F, 65.0F, 25.0F, 10.0F, (new Color(0, 0, 0, 35)).getRGB(), (new Color(0, 0, 0, 225)).getRGB());
      BlackOut.FONT.text(stack, this.name, 3.0F, 10.0F, 12.0F, txtColor, false, true);
      if (this.script != null) {
         BlackOut.FONT.text(stack, this.script, 2.0F, 10.0F, 50.0F, txtColor, false, true);
      }

      stack.method_22909();
   }

   private void updateProgress(float delta) {
      if (this.equals(Managers.ALT.selected)) {
         this.progress = Math.min(this.progress + delta, 1.0F);
      } else {
         this.progress = Math.max(this.progress - delta, 0.0F);
      }

   }

   public void refresh() {
      this.name = AccountScriptReader.nameFromScript(this.script);
      this.uuid = class_4844.method_43344(this.name);
      Managers.ALT.save();
      this.pulse = 1.0F;
   }

   public void setAccess(class_320 session) {
      this.accessToken = session.method_1674();
      this.xuid = session.method_38741();
      this.clientId = session.method_38740();
      Managers.ALT.save();
   }

   public void setSession() {
      ((IMinecraftClient)BlackOut.mc).blackout_Client$setSession(this.name, this.uuid, this.accessToken, this.xuid, this.clientId, this.accountType);
   }

   private class_321 getAccountType(String from) {
      String var2 = from.toLowerCase();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1106578487:
         if (var2.equals("legacy")) {
            var3 = 1;
         }
         break;
      case -1068624430:
         if (var2.equals("mojang")) {
            var3 = 2;
         }
         break;
      case 108411:
         if (var2.equals("msa")) {
            var3 = 0;
         }
      }

      class_321 var10000;
      switch(var3) {
      case 0:
         var10000 = class_321.field_34962;
         break;
      case 1:
         var10000 = class_321.field_1990;
         break;
      case 2:
         var10000 = class_321.field_1988;
         break;
      default:
         throw new IllegalStateException("Unexpected account type: " + from + " name: " + this.name + " script: " + this.script);
      }

      return var10000;
   }

   public JsonObject asJson() {
      if (this.name == null) {
         if (this.script == null) {
            return null;
         }

         this.name = AccountScriptReader.nameFromScript(this.script);
      }

      JsonObject object = new JsonObject();
      object.addProperty("script", this.nullable(this.script));
      object.addProperty("name", this.name);
      JsonObject uuidObject = new JsonObject();
      uuidObject.addProperty("mostSigBits", this.uuid.getMostSignificantBits());
      uuidObject.addProperty("leastSigBits", this.uuid.getLeastSignificantBits());
      object.add("uuid", uuidObject);
      object.addProperty("xuid", this.getFromOptional(this.xuid));
      object.addProperty("clientId", this.getFromOptional(this.clientId));
      object.addProperty("accountType", this.nullable(this.accountType.method_38742()));
      return object;
   }

   private String nullable(@Nullable String string) {
      return string == null ? "<NULL>" : string;
   }

   private String getFromOptional(Optional<String> optional) {
      try {
         return (String)optional.orElse("<EMPTY>");
      } catch (Exception var3) {
         System.out.println(this.name);
         throw new RuntimeException(var3);
      }
   }

   private Optional<String> readOptional(String string) {
      return string.equals("<EMPTY>") ? Optional.empty() : Optional.of(string);
   }

   public static enum AccountClickResult {
      Nothing,
      Select,
      Delete,
      Refresh;

      // $FF: synthetic method
      private static Account.AccountClickResult[] $values() {
         return new Account.AccountClickResult[]{Nothing, Select, Delete, Refresh};
      }
   }
}
