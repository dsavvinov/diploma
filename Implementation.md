## Syntax

`Effect Schema` = **{** `Assertion`\* **}**

`Assertion` = `Premise` **⊢** `Effect`

`Premise` = `Ident` **is** `Type` **|** `Ident` **==** `Value`

`Effect` = `Application` **|** **return** `Value` **|** **hints** `Type` **|** **throws** `Exception`

`Application` = `Effect Schema` {**<** `Type`\* **>** }? (`Ident`\*)

`Ident` = Any identifier

`Type` = Any kotlin type

`Exception` = Any exception

-----

Possible upgrade (and most probably, necessary): partially applied `Effect Schema`'s.

`Premise` = `Effect Schema` **is** `Effect Schema` **|** `Effect Schema` **==** `Effect Schema` ...

`Effect Schema` **==** `Effect Schema`

```
  for (premise in Left) {
    let effect_left be effect of premise
    let assertion be the matched assertion for premise in right effect schema
    let effect_right be the assertion.effect
    if (effect_left.return == effect_right.return) {
      add Assertion(premise -> True) to resulting effect scheme
    }
  }
```


## Evaluation

