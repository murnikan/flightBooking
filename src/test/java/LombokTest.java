import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LombokTest {

    @Getter @Setter
    static class A {
        private String x;
    }

    @Test
    void lombokGeneratedAccessorsWork() {
        A a = new A();
        a.setX("ok");
        assertEquals("ok", a.getX());
    }
}
//тест работоспособности ломбока