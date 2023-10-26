package bootiful.moduliths;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.Set;

@SpringBootApplication
@ImportRuntimeHints(ModulithsApplication.Hints.class)
public class ModulithsApplication {

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            for (var clzz : Set.of("com.tngtech.archunit.core.importer.ModuleImportPlugin"))
                hints.reflection().registerType(TypeReference.of(clzz), MemberCategory.values());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }

}
