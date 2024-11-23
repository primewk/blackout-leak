package bodevelopment.client.blackout.rendering.shader;

import bodevelopment.client.blackout.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20C;

public class ShaderReader {
   private static final String GLSL_VERSION = "150";
   private static final Map<ShaderReader.UnBuilt, String> shaders = new HashMap();
   private static final Map<String, String> replacements = new HashMap();

   private static void put(String... strings) {
      for(int i = 0; i < strings.length; i += 2) {
         replacements.put(strings[i], strings[i + 1]);
      }

   }

   public static int create(String name) {
      String[] s = getShaders(name);
      if (name.equals("lines")) {
         System.out.println(s[0]);
         System.out.println("\n\n\n\nthis the next : \n\n\n\n");
         System.out.println(s[1]);
      }

      int fragId = GL20C.glCreateShader(35632);
      int vertId = GL20C.glCreateShader(35633);
      GL20C.glShaderSource(fragId, s[0]);
      GL20C.glShaderSource(vertId, s[1]);
      GL20C.glCompileShader(vertId);
      GL20C.glCompileShader(fragId);
      int id = GL20C.glCreateProgram();
      GL20C.glAttachShader(id, fragId);
      GL20C.glAttachShader(id, vertId);
      GL20C.glLinkProgram(id);
      GL20C.glDetachShader(id, vertId);
      GL20C.glDetachShader(id, fragId);
      GL20C.glValidateProgram(id);
      return id;
   }

   private static String[] getShaders(String shaderName) {
      String frag = "";
      String vert = "";
      String string = readStreamToString(FileUtils.getResourceStream("shader", "shaders.blackout"));
      Iterator var4 = string.lines().toList().iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         if (line.startsWith(shaderName + ":")) {
            String[] paths = line.substring(shaderName.length() + 2).split(", ");
            frag = paths[0];
            vert = paths[1];
            break;
         }
      }

      return getShaders(frag, vert);
   }

   private static String[] getShaders(String frag, String vert) {
      AtomicReference<String> fs = new AtomicReference();
      AtomicReference<String> vs = new AtomicReference();
      shaders.forEach((unBuilt, string) -> {
         String str = unBuilt.file + "." + unBuilt.name;
         if (str.equals(frag)) {
            fs.set(string);
         }

         if (str.equals(vert)) {
            vs.set(string);
         }

      });
      return new String[]{(String)fs.get(), (String)vs.get()};
   }

   private static String modify(String original, int i) {
      Entry entry;
      for(Iterator var2 = replacements.entrySet().iterator(); var2.hasNext(); original = original.replace("$" + (String)entry.getKey(), (CharSequence)entry.getValue())) {
         entry = (Entry)var2.next();
      }

      String var10000;
      if (i <= 0) {
         var10000 = original;
      } else {
         --i;
         var10000 = modify(original, i);
      }

      return var10000;
   }

   private static String readStreamToString(InputStream inputStream) {
      try {
         return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void loadShaders() {
      List<ShaderReader.UnBuilt> unBuilts = new ArrayList();
      Iterator var1 = readStreamToString(FileUtils.getResourceStream("shader", "load.blackout")).lines().toList().iterator();

      while(var1.hasNext()) {
         String file = (String)var1.next();
         InputStream stream = FileUtils.getResourceStream("shader", "shaders", file + ".shader");
         unBuilts.addAll(readShaders(file, readStreamToString(stream)));
      }

      shaders.clear();
      unBuilts.forEach((s) -> {
         processImports(s, unBuilts);
         shaders.put(s, s.build());
      });
   }

   private static void processImports(ShaderReader.UnBuilt unBuilt, List<ShaderReader.UnBuilt> list) {
      List<ShaderReader.UnBuilt.ShaderMethod> imported = new ArrayList();
      unBuilt.imports.forEach((i) -> {
         ShaderReader.UnBuilt.ShaderMethod method = findMethod(i, list);
         if (method != null) {
            imported.add(0, method);
         }

      });
      imported.forEach((method) -> {
         unBuilt.methods.add(0, method);
      });
   }

   private static ShaderReader.UnBuilt.ShaderMethod findMethod(String name, List<ShaderReader.UnBuilt> list) {
      String[] parts = name.split("\\.");
      Iterator var3 = list.iterator();

      while(true) {
         ShaderReader.UnBuilt unBuilt;
         do {
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               unBuilt = (ShaderReader.UnBuilt)var3.next();
            } while(!unBuilt.file.equals(parts[0]));
         } while(!unBuilt.name.equals(parts[1]));

         Iterator var5 = unBuilt.methods.iterator();

         while(var5.hasNext()) {
            ShaderReader.UnBuilt.ShaderMethod method = (ShaderReader.UnBuilt.ShaderMethod)var5.next();
            if (method.name.equals(parts[2])) {
               return method;
            }
         }
      }
   }

   private static List<ShaderReader.UnBuilt> readShaders(String file, String content) {
      content = modify(content, 5);
      List<ShaderReader.UnBuilt> shaders = new ArrayList();
      int inline = 0;
      ShaderReader.BetterBuilder builder = new ShaderReader.BetterBuilder();
      String[] var5 = content.split("");
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String c = var5[var7];
         if (c.equals("{")) {
            ++inline;
         }

         if (c.equals("}")) {
            --inline;
         }

         switch(builder.stage) {
         case 0:
            if (c.equals("{") && inline == 1) {
               builder.save("name");
               builder.nextStage();
               builder.reset();
            } else if (c.equals("\n")) {
               builder.reset();
            } else if (!c.equals(" ") && inline == 0) {
               builder.append(c);
            }
            break;
         case 1:
            if (inline == 0) {
               shaders.add(readShader(builder.get("name"), file, builder.get()));
               builder.fullReset();
            } else {
               builder.append(c);
            }
         }
      }

      return shaders;
   }

   private static ShaderReader.UnBuilt readShader(String name, String file, String string) {
      List<ShaderReader.UnBuilt.ShaderMethod> methods = readMethods(string);
      List<ShaderReader.UnBuilt.ShaderField> fields = readFields(string);
      List<String> imports = readImports(string);
      return new ShaderReader.UnBuilt(name, file, methods, fields, imports);
   }

   private static List<String> readImports(String string) {
      List<String> imports = new ArrayList();
      ShaderReader.BetterBuilder builder = new ShaderReader.BetterBuilder();
      String[] var3 = string.split("");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String c = var3[var5];
         switch(builder.stage) {
         case 0:
            if (builder.is(c, "import")) {
               builder.reset();
               builder.nextStage();
            } else if (c.equals("\n") || c.equals(" ")) {
               builder.reset();
            }
            break;
         case 1:
            if (!c.equals(" ") && !c.equals("\n")) {
               if (c.equals(";")) {
                  imports.add(builder.get());
                  builder.fullReset();
               } else {
                  builder.append(c);
               }
            } else {
               builder.reset();
            }
         }
      }

      return imports;
   }

   private static List<ShaderReader.UnBuilt.ShaderMethod> readMethods(String string) {
      List<String> dividers = new ArrayList();
      dividers.add(" ");
      dividers.add("(");
      dividers.add(")");
      List<ShaderReader.UnBuilt.ShaderMethod> methods = new ArrayList();
      int inline = 0;
      int methodInline = 0;
      boolean insideMethod = false;
      boolean isPre = false;
      ShaderReader.BetterBuilder builder = new ShaderReader.BetterBuilder();
      String[] var8 = string.split("");
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String c = var8[var10];
         switch(builder.stage) {
         case 0:
            String sus = builder.getA(c);
            if (!sus.equals("fun ") && !sus.equals("@fun ")) {
               if (c.equals("\n") || c.equals(" ")) {
                  builder.reset();
               }
               break;
            }

            isPre = sus.equals("@fun ");
            builder.reset();
            builder.nextStage();
            break;
         case 1:
            if (dividers.contains(c)) {
               builder.save("type");
               builder.nextStage();
               builder.reset();
            } else {
               builder.append(c);
            }
            break;
         case 2:
            if (dividers.contains(c)) {
               builder.save("name");
               builder.nextStage();
               builder.reset();
            } else {
               builder.append(c);
            }
            break;
         case 3:
            if (c.equals(")")) {
               builder.save("args");
               builder.nextStage();
               builder.reset();
            } else {
               builder.append(c);
            }
            break;
         case 4:
            if (c.equals("{")) {
               ++inline;
               if (!insideMethod) {
                  methodInline = inline;
                  builder.reset();
                  insideMethod = true;
               } else {
                  builder.append(c);
               }
            } else if (c.equals("}")) {
               --inline;
               if (inline < methodInline && insideMethod) {
                  methods.add(new ShaderReader.UnBuilt.ShaderMethod(builder.get("name"), builder.get("type"), builder.get("args"), builder.get(), isPre));
                  builder.fullReset();
                  insideMethod = false;
                  isPre = false;
               } else {
                  builder.append(c);
               }
            } else {
               builder.append(c);
            }
         }
      }

      return methods;
   }

   private static List<ShaderReader.UnBuilt.ShaderField> readFields(String string) {
      List<ShaderReader.UnBuilt.ShaderField> fields = new ArrayList();
      List<String> prefixes = new ArrayList();
      prefixes.add("uniform");
      prefixes.add("out");
      prefixes.add("in");
      prefixes.add("const");
      boolean hasValue = false;
      ShaderReader.BetterBuilder builder = new ShaderReader.BetterBuilder();
      String[] var5 = string.split("");
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String c = var5[var7];
         int i = builder.stage;
         String value;
         String name;
         switch(i) {
         case 0:
            value = builder.getA(c);
            name = value.substring(0, value.length() - 1);
            if (prefixes.contains(name)) {
               builder.save("prefix", name);
               builder.reset();
               builder.nextStage();
            } else if (c.equals("\n") || c.equals(" ")) {
               builder.reset();
            }
            break;
         case 1:
            if (c.equals(" ")) {
               builder.save("type");
               builder.reset();
               builder.nextStage();
            } else {
               builder.append(c);
            }
            break;
         case 2:
            if (c.equals("=")) {
               builder.save("name");
               builder.reset();
               hasValue = true;
            } else if (c.equals(";")) {
               if (hasValue) {
                  value = builder.get();
                  name = builder.get("name");
               } else {
                  value = "";
                  name = builder.get();
               }

               fields.add(new ShaderReader.UnBuilt.ShaderField(name, builder.get("type"), builder.get("prefix"), value));
               hasValue = false;
               builder.fullReset();
            } else if (!c.equals(" ")) {
               builder.append(c);
            }
         }
      }

      return fields;
   }

   static {
      put("ver", "#version 150", "matrices", "uniform mat4 ModelViewMat;\nuniform mat4 ProjMat;", "posclr", "in vec3 Position;\nin vec4 Color;", "posuv", "in vec3 Position;\nin vec2 UV0;", "pi", "3.14159265358979323846", "e", "2.7182818284590452354", "alpha", "uniform float uAlpha;", "res", "uniform vec2 uResolution;");
   }

   private static record UnBuilt(String name, String file, List<ShaderReader.UnBuilt.ShaderMethod> methods, List<ShaderReader.UnBuilt.ShaderField> fields, List<String> imports) {
      private UnBuilt(String name, String file, List<ShaderReader.UnBuilt.ShaderMethod> methods, List<ShaderReader.UnBuilt.ShaderField> fields, List<String> imports) {
         this.name = name;
         this.file = file;
         this.methods = methods;
         this.fields = fields;
         this.imports = imports;
      }

      private String build() {
         StringBuilder builder = new StringBuilder();
         builder.append("#version").append(" ").append("150").append("\n");
         Iterator var2 = this.fields.iterator();

         while(var2.hasNext()) {
            ShaderReader.UnBuilt.ShaderField uniform = (ShaderReader.UnBuilt.ShaderField)var2.next();
            builder.append(uniform.build()).append("\n");
         }

         var2 = this.methods.iterator();

         ShaderReader.UnBuilt.ShaderMethod method;
         while(var2.hasNext()) {
            method = (ShaderReader.UnBuilt.ShaderMethod)var2.next();
            if (method.pre) {
               builder.append(method.build()).append("\n");
            }
         }

         var2 = this.methods.iterator();

         while(var2.hasNext()) {
            method = (ShaderReader.UnBuilt.ShaderMethod)var2.next();
            if (!method.pre) {
               builder.append(method.build()).append("\n");
            }
         }

         return builder.toString();
      }

      public String name() {
         return this.name;
      }

      public String file() {
         return this.file;
      }

      public List<ShaderReader.UnBuilt.ShaderMethod> methods() {
         return this.methods;
      }

      public List<ShaderReader.UnBuilt.ShaderField> fields() {
         return this.fields;
      }

      public List<String> imports() {
         return this.imports;
      }

      private static record ShaderField(String name, String type, String string, String value) {
         private ShaderField(String name, String type, String string, String value) {
            this.name = name;
            this.type = type;
            this.string = string;
            this.value = value;
         }

         private String build() {
            StringBuilder builder = new StringBuilder();
            if (this.string != null) {
               builder.append(this.string).append(" ");
            }

            builder.append(this.type).append(" ").append(this.name);
            if (!this.value.isEmpty()) {
               builder.append(" = ").append(this.value);
            }

            return builder.append(";").toString();
         }

         public String name() {
            return this.name;
         }

         public String type() {
            return this.type;
         }

         public String string() {
            return this.string;
         }

         public String value() {
            return this.value;
         }
      }

      private static record ShaderMethod(String name, String type, String args, String content, boolean pre) {
         private ShaderMethod(String name, String type, String args, String content, boolean pre) {
            this.name = name;
            this.type = type;
            this.args = args;
            this.content = content;
            this.pre = pre;
         }

         private String build() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.type).append(" ");
            builder.append(this.name).append("(").append(this.args).append(") {");
            builder.append(this.content).append("}");
            return builder.toString();
         }

         public String name() {
            return this.name;
         }

         public String type() {
            return this.type;
         }

         public String args() {
            return this.args;
         }

         public String content() {
            return this.content;
         }

         public boolean pre() {
            return this.pre;
         }
      }
   }

   private static class BetterBuilder {
      private final StringBuilder stringBuilder = new StringBuilder();
      private final Map<String, String> saved = new HashMap();
      private int stage = 0;

      private void nextStage() {
         ++this.stage;
      }

      private void resetStage() {
         this.stage = 0;
      }

      private ShaderReader.BetterBuilder append(String string) {
         this.stringBuilder.append(string);
         return this;
      }

      private boolean is(String string) {
         return this.stringBuilder.toString().equals(string);
      }

      private boolean is(String string, String string2) {
         return this.append(string).get().equals(string2);
      }

      private void reset() {
         this.stringBuilder.delete(0, this.stringBuilder.length() + 1);
      }

      private void fullReset() {
         this.reset();
         this.saved.clear();
         this.resetStage();
      }

      private void save(String key, String value) {
         this.saved.put(key, value);
      }

      private void save(String key) {
         this.saved.put(key, this.get());
      }

      private String get(String key) {
         return (String)this.saved.get(key);
      }

      private String get() {
         return this.stringBuilder.toString();
      }

      private String getA(String string) {
         return this.append(string).get();
      }
   }
}
