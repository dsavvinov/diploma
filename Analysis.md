## IDEA annotations
Details: https://www.jetbrains.com/help/idea/2016.2/contract-annotations.html

  - Has boring annotations (Null/NotNull/NotNls)
  - has "Contract annotaion"

    ```
    @Contract("null, null, null -> null; !null, _, _ -> !null; _, null, false -> fail", pure)
    ```

    means that:
      - if all 3 args of function is null, then return type is not null
      - if first at least arg is not null then return type is not null
      - if 2nd arg is null then 3rd can't be false (and vice versa) -> statically asserts!
      - it is pure function
      -
  **TODO**: take a closer look at implementation

## JSR-308

Details: http://www.oracle.com/technetwork/articles/java/ma14-architect-annotations-2177655.html

Out-of-box Java Annotations:
  - `@Deprecated`, `@Override`, `@SupressWarning`, `@FunctionalInterface`, `@SafeVarargs`

    `@FunctionalInterface` -- means that type declaration intended to be a function interface (like `Consumer`, `Supplier`, `Predicate`, etc). For example, let us consider following interface and method, accepting that interface among with some collection

    ```java
    @FunctionalInterface
    public interface IntPredicateLike {
       boolean foo(int value);
    }

    public static class IntPredicateLikeImpl implements IntPredicateLike {
        @Override
        public boolean foo(int value) {
            return value > 0;
        }
    }

    public static int CountIfLike(Collection<Integer> collection,
                                 IntPredicateLike predicate) {
        int cnt = 0;
        for (Integer e : collection) {
            if (predicate.foo(e)) {
                cnt += 1;
            }
        }
        return cnt;
    }
    ```

    Then we can use lambda instead of anonymous class/explicit implementation where that
    predicate is expected:

    ```java
    CountIfLike(someCollection, new IntPredicateLike() {
            @Override
            public boolean foo(int value) {
                return value > 0;
            }
        });
    ```

    is equivalent to:

    ```java
    CountIfLike(someCollection, value -> value > 0);
    ```

    This is because `IntPredicateLike` conforms to one of the *Functional interfaces*,
    defined in Java 8. However, those checks are performed implicitly, so it's not
    always easy to check if defined interface is indeed satisfy *Functional interface*
    requirments. So, `@FunctionalInterface` predicate will warn us if our interface isn't
    requirment isn't in fact *Functional interface*. Like this:

    ```java
    @FunctionalInterface
    public interface IntPredicateLike {
        boolean foo(int value);
        int neverUsed(String something);
    }

    > Error:(8, 5) java: Unexpected @FunctionalInterface annotation
      JavaDefaultAnnotations.IntPredicateLike is not a functional interface
      multiple non-overriding abstract methods found in interface
      JavaDefaultAnnotations.IntPredicateLike
    ```

    `@SafeVarargs` -- explained here https://docs.oracle.com/javase/7/docs/api/java/lang/SafeVarargs.html.
    Long story short, Java allows potentially unsafe operations on elements of varargs.
    This annotation will check for such operations and issue a warning.

  - *Checker framework* also has a lot of other annotations.
  More details below.

### Checker framework

#### General descsription and setup ####
Checker framework is a plug-in to **javac**, that will run some annotation-checking stuff
along with usual java compiliaction. It is backward comaptible, meaning that one can annotate
his/her code and fully check it using *Checker framework*, and then distribute that code
without any additional changes -- annotations will be just ignored when compiled by usual
**javac**.

[Complete reference]( http://types.cs.washington.edu/checker-framework/current/checker-framework-manual.html)

[IDEA set-up.](
http://types.cs.washington.edu/checker-framework/current/checker-framework-manual.html#intellij)
**NB:** You **have** to replace "CHECKERFRAMEWORK" in paths into real path to the
*Checker Framework*. Even if some environment variable with the same name is set up,
it won't be expanded proeprly.

#### Overview of features ####
Has much more interesting annotations:

  - [`@SideEffectFree`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/dataflow/qual/SideEffectFree.html), [`@Deterministic`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/dataflow/qual/Deterministic.html), [`@Pure`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/dataflow/qual/Pure.html) -- and we even can tell annotation processor to ensure if annotated method satisfies those annotations by enabling `-AcheckPurityAnnotations`. Sounds
  cool, huh?
  Here we can describe more interesting features, like some kind of **contracts**: [ `@EnsuresNotNull`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/nullness/qual/EnsuresNonNull.html).
  **NB**. Contract on particular value can be broken if some methods are called, which
  are not proven to keep contract on that value. Example:

    ```java
    @Nullable Object myField;

    int computeValue() { ... }

    void m() {
      ...
      if (myField != null) {
        int result = computeValue();
        myField.toString(); // even though we checked nullability of myField, it's still
                            // could be set back to null after call of computeValue()!
      }
    }
    ```

    For such cases *Checker framework* has those purity-ness annotations like `@Pure`. Note that this is quite strong annotation (in fact, `computeValue()`) doesn't have to be `@Pure` -- it would be enough to know that it won't touch `myField`.

    Those annotations can be not only checked automatically by the checker, but even suggested (i.e. checker can infer those contracts sometimes!)

  - Assertions about method contracts: `@EnsuresNotNull`, `@RequiresQualifier`,
  `@EnsuresQualifier`, etc (**note** checks about bound of type!)

And some fuuny and curious features:
  - [`@MonotonicNotNull`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/nullness/qual/MonotonicNonNull.html) -- propety may be null, but if it became not-null, then it will never be null again (e.g. lazy-initialized fields)

  -  `@GuardedBy` (object can be accessed only when holding specific lock), `@ReadOnly`, `@Regex`

  - [`@Tainted`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/tainting/qual/Tainted.html)
  -- marks untrusted values, obtained from
  potentially malicious source. Inverse of [`@Untainted`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/tainting/qual/Untainted.html)
  -- trusted values, correspondingly. Can be used
  to prevent SQL-injections, XSS, leakage of private data, etc.

  - [`@Signed`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/signedness/qual/Signed.html)
  -- marks signed value (opposite of the [`@Unsigned`](http://types.cs.washington.edu/checker-framework/current/api/org/checkerframework/checker/signedness/qual/Unsigned.html))
  - `@Const`
  - Type refinements (essentially generalization of Kotlin smartcasts)

#### API and writing custom checker ####

[API Reference](http://types.cs.washington.edu/checker-framework/current/api/)

[Manual](http://types.cs.washington.edu/checker-framework/current/checker-framework-manual.html#writing-a-checker)

## C# contracts ##

[Entry point of the reference](https://msdn.microsoft.com/en-us/library/dd264808(v=vs.110).aspx)

Main entities:

  - *Preconditions*, i.e.
    - `Contract.requires(x != null)`,
    - `Contract.requires<ArgumentNullException(x != null)`.

    [Full spec](https://msdn.microsoft.com/en-us/library/system.diagnostics.contracts.contract.requires(v=vs.110).aspx)

  - *Postconditions*, i.e.
    - `Contract.ensures(this.field > 0)`,
    - `Contract.ensuresOnThrow<InvalidArguemntException>(this.field > 0)`
    - `Contract.ensures(Contract.Result > 0)` (returns only positive values),

    [Full spec](https://msdn.microsoft.com/en-us/library/system.diagnostics.contracts.contract.ensures(v=vs.110).aspx)

  - *Invarinats*. Class can declara special method consisting of invariants-checks, that will verify
    correctness of the object, e.g.
    ```cs
    [ContractInvariantMethod]
    protected void ObjectInvariant ()
    {
      Contract.Invariant ( this.y >= 0 );
      Contract.Invariant ( this.x > this.y );
      ...
    }
    ```

    To clarify, all those checks **performed in the run-time**, so it slow down performance.

    [Full Spec](https://msdn.microsoft.com/en-us/library/dd412890(v=vs.110).aspx)


## Theoretical foundations
### Hoare triples
In fact, C# follow theory of contracts pretty closely. According to theory, there are
3 types of contracts too:

  - *Preconditions*
  - *Postconditions*
  - *Invarints*. **Note**: all nullability checks are in fact special cases of the invariants.
  - *Assertions*. Widely-known asserts.

Bazillions of articles from [Sergey Teplyakov blog](http://sergeyteplyakov.blogspot.ru/2013/10/articles.html#design_by_contract)

### Aliasing

> Aliasing occurs at some program point during execution when two or more names exist for the same location
> -- <cite>Landi, Ryder. "Pointer-induced Aliasing: A Problem Classification", №9</cite>



### Effect system

> An effect is a statioc description of the side-effects an expression may perform when it's evaluated. Just as a type describes *what* an expression computes, an effect describes *how* an expression computes."
> -- <cite> Gifford et al. at __№7__ article (Report on the FX Programming language) </cite>

> The effect of an expression is a concise summary of the observable side-effects that the expression may have when it is evaluated.  
> -- <cite> Lucassen, Gifford, 1988 (Polymorphic effect system) </cite>

Generally, this article is useless, because it's old and outdated, and the syntax is terrible.
However, this in an interesting specie as it is a first try to formalize and use effect system in practice.

What effect system can express?

- Which variables particular procedure may read or write during call
- What other procedures can particular procedure may call (__№ 10__)
- Which procedures are atomic and, apparently, even tell us about data races (__№ 11__)
- Which objects are immutable (needs point 1 to check if immutability is preserved correctly)


#### Haskell DDC

https://wiki.haskell.org/DDC

#### __№ 5 article__.
Much more interesting -- realtively fresh (well, 2000 is not fresh anymore, but meh), and, most important, solves pretty much our problem: effect system for Java, expressed in annotations, is proprosed.

They define *region* as an "encapsulation of mutable state". Basically, every mutable variable is a region itself. But only this would be totally not enough for convenient usage, so they build hierarchy on regions, introducing *abstract regions* -- regions, that are not assigned to any field. There are two sources of abstract regions -- static class declaration induces one static region, and usual class declaration induces *set of regions* (one for each instance of that class).
Root of hierarchy is an abstract region `All`, it contains all regions for static classes. Then, `All` contains `Instance` -- region-counterpart of `Object`.
As all abstract regions have some class tied to them, and as all Java-classes form hierarchy, there are similar inheritance hierarchy on regions too.
Also, they extend Java-class modifiers (like `public`, `static`, etc) on regions.

One lingering difference from out task is that authors don't try to use this data in compiler, leaving it for compiler-writers. Therefore, this effect system is essentially a complex and powerful static assertion framework, which is not exactly what we wanted. Also, variety of effects is disappointing -- only effects of read/writes to a regions are used.

> **NB**. There is a subtle but very crucial detail about any effect system combined with inheritance. We **can't** let successors of a class declare more effects than ancestor had -- otherwise, effect system soundness will be compromised. Indeed, consider instance `a: A` and `b: B`, and `B <: A`, and `B` has more effects, than `A`. Therefore, when we use some instance of `A` we have no chance to determine if additional effects of `B` can be fired or not.

Authors invent *unshared fields* -- looks like it's some hint to effects-checker that particular field is truly *unshared* and acces to it doesn't provide significant side-effect. More about it and some formalization of unshared fields can be found in __№8 article__.


Now: DCE, Smart casts

1. Dead code elimination
  ```java
  class Foo {
    val bar: Int
    init {
      run {
        bar = 5
      }
    }
  }
  ```

  Compiler doesn't know that lambda for "run" will ever be called!

2. Smart cast improvement
  JUnit. AssertEquals/AssertTrue/AssertIsSubtypeOf

  ```java
  fun foo(s: String?) {
    var x: String? = null
    if (s != null) {
        x = s
    }
    if (x != null) {
        run {
            // Looks like smart cast is safe here
            x.hashCode()
        }
    }
  }
  ```

3. Loop fusion
  ```
  c : Collection<Descriptor>
  c.filterIsInstance<Functions>.map { }
  ```

4. Relaxing (or maybe strengthening?) warnings
  ```
    def <T> foo(c : Collection<T>) {
      val stringList = c as List<String>  // warning as unsafe cast
      println(stringList)   // this is a safe usage of unsafe cast, however
    }

    ...
    val lst = listOf(1, 2, 3)
    foo(lst)
    ...
  ```

> NB. Annotations can't be inherited!
