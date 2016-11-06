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