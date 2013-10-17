proper
======

Utility for acquiring, validating and managing java system properties.

Motivation
==========

When dealing with dozens or more Java system properties, to configure 
your software, managing them in a systematic fashion becomes tricky. 

`proper` allows you to always keep track of all your system properties, 
add custom meta-data to them, and apply systematic rules on property 
related errors.

Simple Usage
============

The easiest way to declare a property using `proper`:

```java
@ProperScannable
public class MyConstants{
  public static final int INT_PROP = Proper.tyBuilder("example.prop.int", 123)
                                           .build().getValue();
```

Above will create a retrieve the value of the `"example.prop.int"` system 
property, defaulting to `123` if it is not defined. The default property 
builder comes with an `int` converter, that will convert the value to 
an integer, falling back to the default value in case of failure.

The `@ProperScannable` annotation is needed to use the property scanning
tool described later on.

Custom Validation and Listening
===============================

You can inject custom validation logic for your validation. In order to
do this, you're encouraged to create your own property builder class instead
of directly using the one in `Proper` class. Here I have created `MyProper.ty`
builder:

```java
public static class MyProper {
  static final List<String> errors = new ArrayList<>();

  public static <T> PropertyBuilder<T> ty(String key, T defaultValue) {
    return Proper.tyBuilder(key, defaultValue)
      //Register a custom property listener
     .propertyListener(new PropertyListeners.BasePropertyListener() {
        @Override
        public void conversionFailed(String value, String conversionError, Proper.Info<?> info) {
          errors.add("Could not convert " + value + " to int");
        }

        @Override
        public void validationAfterConversionFailed(Object value, String validationError, Proper.Info<?> info) {
          errors.add(validationError);
        }
     })
     //Setup common custom validation for all Integers
     .validatorProvider(ValidatorProviders.builder()
       .add(new Validators.BaseValidator<Integer>(Integer.class) {
         @Override
         public Result afterConversion(Integer value, Proper.Info<Integer> info) {
           return 0 < value && value < 100 ? ok() : fail("0 < x < 100 is not satisfied");
         }
       }).build()
     );
  }

  public List<String> getErrors(){ return errors; }
}
```

The above exposes two features: 

1. A property listener is registered, that will allow you to catch 
   validation errors (and more) at runtime to inform the user.
2. A common validator on `Integer` type is added, that will be 
   applied to all system properties of int type.

For a full list of things you can do with the property builder, refer 
to the `com.github.rodexion.proper.PropertyBuilder` class (TODO: add javadoc).

Implementing Validation Checkers, etc.
======================================

`proper` comes with a property scanner tool which collects all declared 
properties, and a simple validator that makes use of the scanner to then
validate all properties at a certain point it time (usually application startup).

```java
  public static void main(String ... args){
    //Scanner that will search all classes annotated
    //with @ProperScannable annotation for property declarations
    ProperScanner scanner = ProperScanners.scanner("com.myApp");

    //Run the scan.
    ScanResult result = scanner.scan();

    //Do something you declared properties:
    // * Show them to the user
    // * Write them to a CSV file
    // * etc.
    for(ProperDecl declaration : result.getDeclarations()){
      declatation.getProperty().getInfo()...
    }
  }
```

The above will also give you access to access to any custom attributes
defined on the properties, so you can build comprehensive database of all
your system proprerites. Can be useful when debugging, generating manuals,
creating propery diffs between different versions of your application, etc.

Scanner leverages [reflections](http://code.google.com/p/reflections/ "Reflections") library to perform the scanning.

Custom Attributes
=================

You can extend your property builder to include additional attributes 
for to easy property management. Before, I have added a required description and a 
value unit attributes to all my properties.

```java
public static class MyProper {    

  public static <T> PropertyBuilder<T> ty(String key, 
                                          T defaultValue,
                                          String description,
                                          String valueUnit) {
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("description", description);
    attrs.put("unit", unit);
    return Proper.tyBuilder(key, defaultValue)
      ...
      .attributes(attrs);
  }
}
```


Notes on the Design Decisions
=============================

* I have opted not to use annotations on property fields, for the reasons that annotations are impossible
  to customise, and assign dynamic values. This would greately limit tool's usability.
