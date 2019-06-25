package bench

import groovy.transform.CompileStatic
import groovy.transform.TailRecursive
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
class FactorialBench {

    static final Random random = new Random()
    static final int limit = 100_000

    @Benchmark
    void a_standard() {
        def factorial
        factorial = { n, acc = 1G -> 1 >= n ? acc : factorial(n - 1, acc * n ) }

        for (int i = 0; i < limit; i++) {
            factorial(30 + random.nextInt(5))
        }
    }

    @Benchmark
    @CompileStatic
    void a_standard_sc() {
        Closure factorial
        factorial = { int n, acc = 1G -> 1 >= n ? acc : factorial(n - 1, ((BigInteger) acc) * BigInteger.valueOf((long) n)) }

        for (int i = 0; i < limit; i++) {
            factorial(30 + random.nextInt(5))
        }
    }

    @Benchmark
    void b_trampoline() {
        def factorial
        factorial = { n, acc = 1G -> 1 >= n ? acc : factorial.trampoline(n - 1, acc * n ) }.trampoline()

        for (int i = 0; i < limit; i++) {
            factorial(30 + random.nextInt(5))
        }
    }

    @Benchmark
    @CompileStatic
    void b_trampoline_sc() {
        Closure factorial
        factorial = { int n, acc = 1G -> 1 >= n ? acc : factorial.trampoline(n - 1, ((BigInteger) acc) * BigInteger.valueOf((long) n)) }.trampoline()

        for (int i = 0; i < limit; i++) {
            factorial(30 + random.nextInt(5))
        }
    }

    @Benchmark
    void c_tailRecursive() {
        for (int i = 0; i < limit; i++) {
            factorialTailRecursive(30 + random.nextInt(5))
        }
    }

    @Benchmark
    @CompileStatic
    void c_tailRecursive_sc() {
        for (int i = 0; i < limit; i++) {
            factorialTailRecursiveSC(30 + random.nextInt(5))
        }
    }


    @TailRecursive
    static factorialTailRecursive(n, acc = 1G) {
        1 >= n ? acc : factorialTailRecursive(n - 1, n * acc)
    }

    @TailRecursive
    @CompileStatic
    static BigInteger factorialTailRecursiveSC(int n, BigInteger acc = 1G) {
        1 >= n ? acc : factorialTailRecursiveSC(n - 1, acc * BigInteger.valueOf((long) n))
    }
}
