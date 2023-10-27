package bootiful.moduliths;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithsApplicationTest {

    private final ApplicationModules applicationModules =
            ApplicationModules.of(ModulithsApplication.class);

    @Test
    void verify()  {
        this.applicationModules.verify();
    }
}
