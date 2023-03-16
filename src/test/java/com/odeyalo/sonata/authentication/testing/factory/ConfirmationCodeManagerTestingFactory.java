package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeGenerator;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.confirmation.DelegatingPersistentConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.confirmation.NumericConfirmationCodeGenerator;
import lombok.Getter;

public class ConfirmationCodeManagerTestingFactory {

    public static ConfirmationCodeManager create() {
        return createPersistentManager();
    }

    public static DelegatingPersistentConfirmationCodeManager createPersistentManager() {
        return createPersistentManagerBuilder().build();
    }

    public static DelegatingPersistentConfirmationCodeManagerBuilder createPersistentManagerBuilder() {
        return new DelegatingPersistentConfirmationCodeManagerBuilder();
    }


    @Getter
    public static class DelegatingPersistentConfirmationCodeManagerBuilder {
        private ConfirmationCodeRepository confirmationCodeRepository = ConfirmationCodeRepositoryTestingFactory.create();
        private ConfirmationCodeGenerator parentGenerator = new NumericConfirmationCodeGenerator();

        public DelegatingPersistentConfirmationCodeManagerBuilder overrideConfirmationCodeRepository(ConfirmationCodeRepository confirmationCodeRepository) {
            this.confirmationCodeRepository = confirmationCodeRepository;
            return this;
        }

        public DelegatingPersistentConfirmationCodeManagerBuilder overrideParentGenerator(ConfirmationCodeGenerator parentGenerator) {
            this.parentGenerator = parentGenerator;
            return this;
        }

        public DelegatingPersistentConfirmationCodeManagerBuilder withPredefinedCodes(ConfirmationCode... codes) {
            for (ConfirmationCode code : codes) {
                confirmationCodeRepository.save(code);
            }
            return this;
        }

        public DelegatingPersistentConfirmationCodeManager build() {
            return new DelegatingPersistentConfirmationCodeManager(parentGenerator, confirmationCodeRepository);
        }
    }
}
