# Структура

1. Введение - синтаксические основы
    1.1. Nullability
    1.2. Smartcasts
    1.3. Живой пример (`Introduction.kt`)
2. Введение в проблему - смарткасты
    2.1. Простой пример с фейлом смарткаста к non-nullable type (`Wrapper function` section in `Problems.kt`)

    2.2. Простой пример с фейлом смарткаста при уточнении типа в стриме (`filterType()` function in `Problems.kt`)
    - Workaround from stdlib

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
