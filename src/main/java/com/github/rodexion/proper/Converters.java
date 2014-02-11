/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.github.rodexion.proper;

import static com.github.rodexion.proper.ConversionException.couldNotConvertMessage;

import com.github.rodexion.proper.util.Opt;
import com.github.rodexion.proper.util.PrimitiveTypeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.InterruptedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>A collection of default converter implementations</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class Converters {
  private static final Converter<?> voidConverter = new Converter<Object>() {
    @Override
    public boolean canConvert(Class<?> type) {
      return false;
    }

    @Override
    public Result<Object> convert(String key, String value, Proper.Info<Object> info) {
      return Result.skip();
    }

    @Override
    public String toString() {
      return "VoidConverter";
    }
  };

  /**
   * <p>Converter that always rejects requests for conversion.</p>
   *
   * @param <T> Conversion target type
   * @return Converter object (not-null)
   */
  @SuppressWarnings("unchecked")
  public static <T> Converter<T> voidConverter() {
    return (Converter<T>) voidConverter;
  }

  /**
   * <p>Converter that simply returns the original string.</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<String> stringConverter() {
    return new BaseConverter<String>(String.class) {
      @Override
      public String doConvert(String key, String value, Proper.Info<String> info) {
        return value;
      }
    };
  }

  /**
   * <p>Converter to {@link Byte}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Byte> byteConverter() {
    return new NumberBaseConverter<Byte>(Byte.class) {
      @Override
      Byte parseNumber(String value) {
        return Byte.parseByte(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Short}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Short> shortConverter() {
    return new NumberBaseConverter<Short>(Short.class) {
      @Override
      Short parseNumber(String value) {
        return Short.parseShort(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Integer}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Integer> intConverter() {
    return new NumberBaseConverter<Integer>(Integer.class) {
      @Override
      Integer parseNumber(String value) {
        return Integer.parseInt(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Long}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Long> longConverter() {
    return new NumberBaseConverter<Long>(Long.class) {
      @Override
      Long parseNumber(String value) {
        return Long.parseLong(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Float}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Float> floatConverter() {
    return new NumberBaseConverter<Float>(Float.class) {
      @Override
      Float parseNumber(String value) {
        return Float.parseFloat(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Double}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Double> doubleConverter() {
    return new NumberBaseConverter<Double>(Double.class) {
      @Override
      Double parseNumber(String value) {
        return Double.parseDouble(value);
      }
    };
  }

  /**
   * <p>Converter to {@link BigDecimal}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<BigDecimal> bigDecimalConverter() {
    return new NumberBaseConverter<BigDecimal>(BigDecimal.class) {
      @Override
      BigDecimal parseNumber(String value) {
        return new BigDecimal(value);
      }
    };
  }

  /**
   * <p>Converter to {@link BigInteger}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<BigInteger> bigIntegerConverter() {
    return new NumberBaseConverter<BigInteger>(BigInteger.class) {
      @Override
      BigInteger parseNumber(String value) {
        return new BigInteger(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Character}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Character> characterConverter() {
    return new BaseConverter<Character>(Character.class) {
      @Override
      public boolean canConvert(Class<?> type) {
        return super.canConvert(type) || char.class.equals(type);
      }

      @Override
      protected Character doConvert(String key, String value, Proper.Info<Character> info) throws Exception {
        checkNotNull(value, info);
        if (value.equals(" ")) {
          return ' ';
        }
        String v = value.trim();
        if (v.length() != 1) {
          throw new ConversionException("Could not convert '" +
                  v + "' to char: value has to be exactly 1 character");
        }
        return v.charAt(0);
      }
    };
  }

  /**
   * <p>Converter to {@link File}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<File> fileConverter() {
    return new BaseConverter<File>(File.class) {
      @Override
      protected File doConvert(String key, String value, Proper.Info<File> info) throws Exception {
        checkNotNull(value, info);
        return new File(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Path}</p>
   *
   * @return Converter object (not-null)
   */
  public static Converter<Path> pathConverter() {
    return new BaseConverter<Path>(Path.class) {
      @Override
      protected Path doConvert(String key, String value, Proper.Info<Path> info) throws Exception {
        checkNotNull(value, info);
        return Paths.get(value);
      }
    };
  }

  /**
   * <p>Converter to {@link Enum}</p>
   *
   * @return Converter object (not-null)
   */
  public static <T extends Enum<T>> Converter<T> enumConverter() {
    return new BaseConverter<T>(null) {
      @Override
      public boolean canConvert(Class<?> type) {
        return type.isEnum();
      }

      @SuppressWarnings("unchecked")
      @Override
      protected T doConvert(String key, String value, Proper.Info<T> info) throws Exception {
        checkNotNull(value, info);
        String v = value.trim().toLowerCase();
        for (T enumValue : info.getType().getEnumConstants()) {
          if (enumValue.name().toLowerCase().equals(v)) {
            return enumValue;
          }
        }
        throw new ConversionException(value, info);
      }

      @Override
      public String toString() {
        return "EnumConverter";
      }
    };
  }

  private static void checkNotNull(Object value, Proper.Info<?> info) {
    if (null == value) {
      throw new ConversionException(null, info, new NullPointerException("value is null"));
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  private static abstract class NumberBaseConverter<T> extends BaseConverter<T> {
    private final Opt<Class<T>> typeClass2;

    NumberBaseConverter(Class<T> typeClass) {
      super(typeClass);
      this.typeClass2 = Opt.of(PrimitiveTypeUtil.getPrimitiveClass(typeClass));
    }

    @Override
    public boolean canConvert(Class<?> type) {
      return super.canConvert(type) || type.equals(typeClass2.getOrNull());
    }

    @Override
    protected final T doConvert(String key, String value, Proper.Info<T> info) throws Exception {
      checkNotNull(value, info);
      String v = value.trim();
      try {
        return parseNumber(v);
      } catch (NumberFormatException e) {
        throw new ConversionException(couldNotConvertMessage(value,
                info.getKey(),
                typeClass2.getOr(getTypeClass()).getCanonicalName()),
                e);
      }
    }

    abstract T parseNumber(String value);


    @Override
    public String toString() {
      return "NumberConverter(" +
              "type=" + getTypeClass() +
              ", type2=" + typeClass2 +
              ')';
    }
  }

  /**
   * <p>Base class for creating custom converter objects.</p>
   *
   * @param <T>
   */
  @Data
  public abstract static class BaseConverter<T> implements Converter<T> {
    private final Class<T> typeClass;

    public final Result<T> ok(T value) {
      return Result.ok(value);
    }

    public final Result<T> fail(String errorMessage) {
      return Result.fail(errorMessage);
    }

    @Override
    public boolean canConvert(Class<?> type) {
      return type.equals(typeClass);
    }

    @Override
    public Result<T> convert(String key, String value, Proper.Info<T> info) {
      try {
        return ok(doConvert(key, value, info));
      } catch (Exception e) {
        if (e instanceof InterruptedException ||
                e instanceof InterruptedIOException) {
          Thread.currentThread().interrupt();
        }
        return fail(e.getMessage());
      }
    }

    protected abstract T doConvert(String key, String value, Proper.Info<T> info) throws Exception;

    public String toString() {
      return "Converter(type=" + getTypeClass() + ')';
    }
  }

  private Converters() {
  }
}
