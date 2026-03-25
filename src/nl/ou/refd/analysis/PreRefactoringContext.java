package nl.ou.refd.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * A context container for pre-refactoring analysis.
 * Different refactorings can populate only the fields they require.
 *
 * Required: Refactoring
 * Optional: InstructionSet:selectedCode, very specific MethodSpecification to the targetMethod
 */
public final class PreRefactoringContext {
    private final Refactoring refactoring;
    private final InstructionSet selectedCode;
    private final MethodSpecification targetMethod;

    private PreRefactoringContext(Builder builder) {
        this.refactoring = Objects.requireNonNull(builder.refactoring, "refactoring is required");
        this.selectedCode = builder.selectedCode;
        this.targetMethod = builder.targetMethod;
    }

    public Refactoring getRefactoring() {
        return refactoring;
    }

    public Optional<InstructionSet> getSelectedCode() {
        return Optional.ofNullable(selectedCode);
    }

    public Optional<MethodSpecification> getTargetMethod() {
        return Optional.ofNullable(targetMethod);
    }

    public static Builder builder(Refactoring refactoring) {
        return new Builder(refactoring);
    }

    public static final class Builder {
        private final Refactoring refactoring;
        private InstructionSet selectedCode;
        private MethodSpecification targetMethod;

        public Builder(Refactoring refactoring) {
            this.refactoring = Objects.requireNonNull(refactoring, "refactoring is required");
        }

        public Builder selectedCode(InstructionSet selectedCode) {
            this.selectedCode = selectedCode;
            return this;
        }

        public Builder targetMethod(MethodSpecification targetMethod) {
            this.targetMethod = targetMethod;
            return this;
        }

        public PreRefactoringContext build() {
            return new PreRefactoringContext(this);
        }
    }
}
