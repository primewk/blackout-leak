package bodevelopment.client.blackout.interfaces.functional;

@FunctionalInterface
public interface TripleConsumer<T1, T2, T3> {
   void accept(T1 var1, T2 var2, T3 var3);
}
