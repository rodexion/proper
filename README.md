proper
======

Utility for acquiring, validating and managing java system properties.

TODO!!!

```java
  //Consider "my.key" not set
  Proper.Ty<Integer> intProp = MyProper.ty("my.key", 123).build();
  assertThat(intProp.getValue()).isEqualTo(123);
  assertThat(MyProper.messages.contains("Not found my.key using default 123"));
  MyProper.messages.clear();

  //Set it to a correct value
  System.setProperty("my.key", "234");
  assertThat(intProp.getValue()).isEqualTo(234);
  assertThat(MyProper.messages).isEmpty();

  //Set it to a wrong value
  System.setProperty("my.key", "xyz");
  assertThat(intProp.getValue()).isEqualTo(123);//default
  assertThat(MyProper.messages.contains("Could not convert xyz to int"));
  MyProper.messages.clear();
```
