# Структура

1. Введение - синтаксические основы
    1.1. Nullability
    1.2. Smartcasts
    1.3. Живой пример (`Introduction.kt`)
2. Введение в проблему - смарткасты
    2.1. Простой пример с фейлом смарткаста к non-nullable type (`Wrapper function` section in `Problems.kt`)

    2.2. Простой пример с фейлом смарткаста при уточнении типа в стриме (`filterType()` function in `Problems.kt`)
    - Workaround from stdlib

    - JUnit Asserts 

    Усложняем:

    2.3. Средний пример уточнения типа в стриме (`peekCanSpecifyTypeToo()` function in `Problems.kt`)
    Мысль: мы понимаем, что нам нужно узнавать что-то про поведение функции, иначе не победить

    Вопрос: а может ну его нафиг, будет руками кастовать?
    Ответ: философский.
      - ~~Телкам~~ людям нравится: [1](https://blog.jooq.org/2016/03/31/10-features-i-wish-java-would-steal-from-the-kotlin-language/), [2](http://petersommerhoff.com/dev/kotlin/kotlin-for-java-devs/), [3](https://blog.goposse.com/smart-casts-in-kotlin-6c541654e0d1#.sdthv064q)
      - Является частью nullability-safety, которое нравится людям еще больше (any-blog-post-about-kotlin-here)

3. Введение в проблему - сайд-эффекты функций
  Мысль: проблемы бывают не только со смарткастами.
  Пример с инициализацией переменной в лямбде (`initInLambda()` in `Problems.kt`)
  Аж три проблемы:
    - Компилятор должен понять, что лямбда точно будет вызвана
    - Компилятор должен понять, что лямбда проинициализирует переменную
    - Компилятор должен понять, что никто больше не попытается проинициализировать переменную

    Про сайд-эффекты вообще можно разговаривать долго и интересно. (сделать аппендикс с кулстори про nullability на стероидах)


4. Введение в проблему - loop fusion для функционального chain-style
  Неожиданно, вообще другая степь.
    - Требует межпроцедурного (*intraprocedural*) анализа!
    - Пример: `Streams` section in the `Problems.kt`
    - Бенчмарки

5. Summary: если мы хотим знать о коде больше, значит мы должны знать больше о том *как* работают процедуры.

6. Подводим к мысли про эффекты:
    - Зачем вообще нужны типы?
      > Well-typed programs don't go wrong
      > -- <cite> Robin Milner, 1978

      > Ну-ну
      > -- <cite> Javascript </cite>

    - Определение:

      > The effect of an expression is a concise summary of the observable side-effects that the expression may have when it is evaluated.
      > -- <cite> Lucassen, Gifford, 1988 (Polymorphic effect system) </cite>

    - Известные примеры из жизни:
      - Checked exception in Java
      - Const methods in C++
      - Monads in Haskell
      - @Null/@NotNull

8. Статьи про эффекты:
    - <cite> Lucassen, Gifford, 1988 (Polymorphic effect system) </cite>. Адъ и Содомъ в мозгоубивающем синтаксе. read/write эффекты. Задумывалась как подспорье планировщику в параллельных программах.

    -  "An Object-Oriented Effects System". Greenhouse, Boyland. 1999. JAva! Теже самые read/write, только теперь с более жизненной целью - выявить зависимости по данным, дабы облегчить жизнь компилятору в его оптимизациях, реордерингах и спекуляциях. Очень долгие гимнастические упражнения с регионами памяти, поинтерами и полиморфизмом, а потом еще больше - чтобы это можно было использовать не в трех с половиной примерах из статьи.

    - "SafeJava: A unified type system fot safe programming". Chandrasekhar Boyapati, 1998. 160 pages-long thesis, constructs some extension of Java that incrorporate a lot of type safety and checks. From effects POV, only read-write effects and immutability are considered (immutability is interesting though)

    - "MJ: An imperative core calculus for Java and Java with effects". Bierman, Prakinson, Pitts. 2003 - formalization of some (significant) part of Java + simple read-write effect system.

    - "Types for Atomicity: Static Checking and Inference for Java". Flanagan, Freund, Lifshin. **2008**. - about data races and concurrency.

7. Контракты.
  - Тройки хоара.
  - Некоторый синтаксис записи эффектов
  - По сути просто немного статичесикх чеков на методах
  - By design тяготеют к описанию изменений в данных, хотя Бертран Мейер неплохо поработал.
  - Некоторые эффекты тяжело выразить как изменение в данных - например, бросание исключения, вызов функции и т.д.

8. Существующие практические работы
  - Contracts в C#. По сути, статические ассерты + плюшки типа генерации тестов, документации и ворнингов.
  ```
  ContractClassFor(typeof(IArray))]
internal abstract class IArrayContract : IArray
{
    int IArray.Add(Object value)
    {
        // Returns the index in which an item was inserted.
        Contract.Ensures(Contract.Result<int>() >= -1);
        Contract.Ensures(Contract.Result<int>() < ((IArray)this).Count);
        return default(int);
    }
    Object IArray.this[int index]
    {
        get
        {
            Contract.Requires(index >= 0);
            Contract.Requires(index < ((IArray)this).Count);
            return default(int);
        }
        set
        {
            Contract.Requires(index >= 0);
            Contract.Requires(index < ((IArray)this).Count);
        }
    }
  ```
  Исполнившийся контракт не влияет на работу компилятора (кажется)!

  - JSR-308 (Java Checker Framework)
    Фреймворк для создания и использования аннотаций, реализующих некоторые чеки.

    Примеры аннотаций: @Pure/@SideEffectFree/@Deterministic, @Tainted/@Untainted, @MonotonicNotNull

    Судя по Retention=RUNTIME, осуществляет не только статические проверки.

    Иногда даже умеет сам выводить.

    В целом, бедновато. Уточнение типов - даже не мечтаем.

  - Eifel.
    - Те самые контракты
    - Но на стероидах
    - Еще и мощная выводилка ("Inferring better contracts". Yi Wei, Carlo A. Furia, Nikolay Kazmin, Bertrand Meyer)
    - Есть серьезное подозрение, что большинство того, что мы хотим выразить, выразимо в системе контрактов эйфеля.
    - Поэтому если бы вместо джавы у нас был эйфель, то мы бы обязательно вкорячили наши эффекты и смарткасты в имеющуюся систему!
    - Но у нас джава, так что следующий слайд.

  -
