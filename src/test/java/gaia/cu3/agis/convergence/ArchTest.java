package gaia.cu3.agis.convergence;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("gaia.cu3.agis.convergence");

        noClasses()
            .that()
                .resideInAnyPackage("gaia.cu3.agis.convergence.service..")
            .or()
                .resideInAnyPackage("gaia.cu3.agis.convergence.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..gaia.cu3.agis.convergence.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
