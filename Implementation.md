How can function verify anything (say, hint the type) of some object (say, its argument)?
  - Output value is dependent on the object, like:

  ```java
  fun Foo(obj: Any?) : Boolean {
      return obj == null
  }
  ```

  Here we say: "If `obj` is `null`, then return value is `True` and `False` otherwise".
  We can write it more concise: `null → True; ¬null → False`

  - Termination of function depends on the object. Most simple case is when function throws for some values of the object, like:

  ```java
  fun Foo(obj: Any?) : Unit

  ```

We want to make work at lest:


```
  run { ... }
```

```
assert(...)

```

```
filter  { it is String }
```


```
fun foo(t : Any?) : Any? {
    if (t == null) return null;
    ...
}

foo (5)
```

Интересная мысль: через наши эффекты можно выразить типы любой функции
Интересная мысль: enum и sealed classes сколько-то одинаковые вещи
На подмуать: хорошо ли, что у нас в некоторых местах стоит и значений и тип
Проблема:
  1. Написали функцию с эффектами
  2. Заиспользовали в коде, сделали смарткасты, которые отразились на байткоде
  3. Скомпилировали, рады
  4. Потом поменяли функцию, у нее изменились эффекты
  5. Смарт-касты больше не валидны
  6. Слинковали
  7. Все падает в ран-тайме
Проблема:
  - Есть места, где мы можем выводить эффекты в тайне от ползьователя (например, локально объявленная лямбда, локальыне функции, прайват поля)
  - Есть места, где эффекты должны быть заявлены явно (например, public API), иначе случится то, что описано в предыдущем пункте
На подумать: робастность всей этой хуйни. Сломать любой ценой!
