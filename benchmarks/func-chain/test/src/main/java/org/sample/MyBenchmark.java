/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import benches.ActualTestsKt;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {
    private List<Integer> arg;

    @Setup(Level.Trial)
    public void prepare() {
        Random rng = new Random(42);
        ArrayList<Integer> larg = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            larg.add(rng.nextInt());
        }
        arg = larg;
    }

    @Benchmark
    public int kotlinStream() {
        return ActualTestsKt.testChain(arg);
    }

    @Benchmark
    public int kotlinChain() {
        return ActualTestsKt.testChain(arg);
    }

    @Benchmark
    public int kotlinFused() {
        return ActualTestsKt.testLoopFused(arg);
    }

    @Benchmark
    public int javaOneLambda() {
        return (int) arg.stream()
                .filter(it -> {
                    if (it < 0) {
                        int x1 = it - (it / 2);
                        return (((x1 * x1) + 42) / 2) % 2 == 0;
                    } else {
                        int x1 = it - (it / 4);
                        return (((x1 * x1) + 42) / 2) % 2 == 0;
                    }
                })
                .count();
    }

    @Benchmark
    public int javaStream() {
        return (int) arg.stream()
                .map(it -> {
                    if (it < 0) {
                        return it - (it / 2);
                    } else {
                        return it - (it / 4);
                    }
                })
                .map(it -> it * it)
                .map(it -> it + 42)
                .map(it -> it / 2)
                .filter(it -> it % 2 == 0)
                .count();
    }

    @Benchmark
    public int javaFused() {
        int res = 0;
        for (Integer s : arg) {
            int raw = s;
            int tmp = raw < 0 ? raw - (raw / 2) : raw - (raw / 4);
            if ( (tmp * tmp + 42) / 2 % 2 == 0) {
                res += 1;
            }
        }
        return res;
    }

    @Benchmark
    public int javaUnboxedStream() {
        return (int) arg.stream()
                .mapToInt(it -> {
                    if (it < 0) {
                        return it - (it / 2);
                    } else {
                        return it - (it / 4);
                    }
                })
                .map(it -> it * it)
                .map(it -> it + 42)
                .map(it -> it / 2)
                .filter(it -> it % 2 == 0)
                .count();
    }

    @Benchmark
    public int naiveBaselineJava() {
        ArrayList<Integer> l1 = new ArrayList<>(arg.size());
        for (Integer s : arg) {
            l1.add(s < 0 ? s - (s / 2) : s - (s / 4));
        }

        ArrayList<Integer> l2 = new ArrayList<>(l1.size());
        for (Integer i : l1) {
            l2.add(i * i);
        }

        ArrayList<Integer> l3 = new ArrayList<>(l2.size());
        for (Integer i : l2) {
            l3.add(i + 42);
        }

        ArrayList<Integer> l4 = new ArrayList<>(l3.size());
        for (Integer i : l3) {
            l4.add(i / 2);
        }

        ArrayList<Integer> l5 = new ArrayList<>(l4.size());
        for (Integer i : l4) {
            if (i % 2 == 0) {
                l5.add(i);
            }
        }

        return l5.size();
    }

    @Benchmark
    public int unboxedStaticInlinedJava() {
        int[] l1 = new int[arg.size()];
        for (int i = 0; i < arg.size(); i++) {
            l1[i] = arg.get(i) < 0 ? arg.get(i) - (arg.get(i) / 2) : arg.get(i) - (arg.get(i) / 4);
        }

        int[] l2 = new int[l1.length];
        for (int i = 0; i < l1.length; i++) {
            l2[i] = l1[i] * l1[i];
        }

        int[] l3 = new int[l2.length];
        for (int i = 0; i < l2.length; i++) {
            l3[i] = l2[i] + 42;
        }

        int[] l4 = new int[l3.length];
        for (int i = 0; i < l3.length; i++) {
            l4[i] = l3[i] / 2;
        }

        int actualSize = 0; // kinda clever; does Java streams even do something like that?
        int[] l5 = new int[l4.length];
        for (int i = 0; i < l4.length; i++) {
            if (l4[i] % 2 == 0) {
                l5[i] = l4[i];
                actualSize++;
            }
        }

        return actualSize;
    }

    @Benchmark
    public int boxedStaticInlinedJava() {
        Integer[] l1 = new Integer[arg.size()];
        for (int i = 0; i < arg.size(); i++) {
            l1[i] = arg.get(i);
        }

        Integer[] l2 = new Integer[l1.length];
        for (int i = 0; i < l1.length; i++) {
            l2[i] = l1[i] * l1[i];
        }

        Integer[] l3 = new Integer[l2.length];
        for (int i = 0; i < l2.length; i++) {
            l3[i] = l2[i] + 42;
        }

        Integer[] l4 = new Integer[l3.length];
        for (int i = 0; i < l3.length; i++) {
            l4[i] = l3[i] / 2;
        }

        int actualSize = 0; // kinda clever; does Java streams even do something like that?
        Integer[] l5 = new Integer[l4.length];
        for (int i = 0; i < l4.length; i++) {
            if (l4[i] % 2 == 0) {
                l5[i] = l4[i];
                actualSize++;
            }
        }

        return actualSize;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .warmupIterations(20)
                .measurementIterations(10)
                .forks(1)
                .jvmArgs("-Xmx16m")
                .build();

        new Runner(opt).run();
    }

}
