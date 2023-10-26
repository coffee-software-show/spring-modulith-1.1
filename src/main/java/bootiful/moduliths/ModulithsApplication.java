package bootiful.moduliths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ImportRuntimeHints( ModulithsApplication.Hints.class)
@SpringBootApplication
public class ModulithsApplication {
/*

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            var mc = MemberCategory.values();
            for (var t : Set.of("com.tngtech.archunit.core.importer.ModuleImportPlugin"))
                hints.reflection().registerType(TypeReference.of(t), mc);
        }
    }
*/

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }

}
