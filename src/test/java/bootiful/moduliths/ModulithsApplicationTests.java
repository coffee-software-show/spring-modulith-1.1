package bootiful.moduliths;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithsApplicationTests {

    private final ApplicationModules applicationModules =
            ApplicationModules.of(ModulithsApplication.class);

    @Test
    void verifyModules() {
        this.applicationModules.verify();
    }

}
