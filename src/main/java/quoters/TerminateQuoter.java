package quoters;

import javax.annotation.PostConstruct;

@Profiling
@DeprecatedClass(newImpl = T1000.class)
public class TerminateQuoter implements Quoter {

  @InjectRandomInt(min = 2, max = 7)
  private int repeat;
  private String message;

  public TerminateQuoter() {
    System.out.println("Phase 1: " + repeat);
  }

  @PostConstruct
  public void init() {
    System.out.println("Phase 2: " + repeat);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @PostProxy
  public void sayQuote() {
    System.out.println("Phase 3");
    for (int i = 0; i < repeat; i++) {
      System.out.println("Message: " + message);
    }
  }

  public void setRepeat(int repeat) {
    this.repeat = repeat;
  }
}
