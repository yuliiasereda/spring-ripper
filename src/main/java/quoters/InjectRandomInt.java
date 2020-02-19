package quoters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
//Source: at the time of compilation it will not be in bytecode (override)
//Class will get into the byte code, but through reflection it cannot be counted - it will not be //DEFAULT
public @interface InjectRandomInt {
  int min();
  int max();
}
