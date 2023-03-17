package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * {@link ConfirmationCodeManager} implementation that generate the confirmation code by delegating and store the generated
 * confirmation codes in {@link ConfirmationCodeRepository}.
 * <p>
 * Note:
 * This Manager does not provide ability to expire code after N times of entering wrong code.
 * Manager does not provide ability to prevent brute-force attacks
 * Also this Manager does not attached to specific session.
 * This manager will activate any code if the code is correct, regardless of who typed the code (even if it is not the user who requested it).
 * </p>
 */
public class DelegatingPersistentConfirmationCodeManager implements ConfirmationCodeManager {
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final ConfirmationCodeGenerator delegateGenerator;
    private final Logger logger = LoggerFactory.getLogger(DelegatingPersistentConfirmationCodeManager.class);

    public DelegatingPersistentConfirmationCodeManager(ConfirmationCodeGenerator delegateGenerator, ConfirmationCodeRepository confirmationCodeRepository) {
        this.delegateGenerator = delegateGenerator;
        this.confirmationCodeRepository = confirmationCodeRepository;
    }

    @Override
    public Optional<ConfirmationCode> findByCodeValue(String codeValue) {
        return confirmationCodeRepository.findConfirmationCodeByCodeValue(codeValue);
    }

    @Override
    public ConfirmationCode generateCode(int length, int lifetimeMinutes) {
        ConfirmationCode generatedCode = delegateGenerator.generateCode(length, lifetimeMinutes);
        ConfirmationCode savedCode = confirmationCodeRepository.save(generatedCode);
        logger.info("Generated and saved the code: {}", savedCode);
        return savedCode;
    }

    @Override
    public ConfirmationCodeCheckResult verifyCodeAndActive(String codeValue) {
        Optional<ConfirmationCode> optional = confirmationCodeRepository.findConfirmationCodeByCodeValue(codeValue);
        if (optional.isEmpty()) {
            return ConfirmationCodeCheckResult.INVALID_CODE;
        }
        ConfirmationCode confirmationCode = optional.get();

        if (confirmationCode.isExpired()) {
            changeConfirmationCodeStateAndSave(confirmationCode, ConfirmationCode.LifecycleStage.DENIED, false);
            return ConfirmationCodeCheckResult.ALREADY_EXPIRED;
        }

        changeConfirmationCodeStateAndSave(confirmationCode, ConfirmationCode.LifecycleStage.ACTIVATED, true);
        return ConfirmationCodeCheckResult.VALID;
    }

    @Override
    public void deleteCode(String codeValue) {
        confirmationCodeRepository.deleteByCodeValue(codeValue);
        logger.info("Removed value from repository: {}", codeValue);
    }

    @Override
    public void deleteCode(ConfirmationCode code) {

    }

    @Override
    public ConfirmationCode.LifecycleStage getLifecycleStage(ConfirmationCode code) {
        code = confirmationCodeRepository.findConfirmationCodeByCodeValue(code.getCode()).orElse(null);
        if (code == null) {
            return null;
        }
        return code.getLifecycleStage();
    }

    @Override
    public ConfirmationCode.LifecycleStage getLifecycleStage(String codeValue) {
        Optional<ConfirmationCode> optional = confirmationCodeRepository.findConfirmationCodeByCodeValue(codeValue);
        return optional
                .map(ConfirmationCode::getLifecycleStage)
                .orElse(null);
    }

    @Override
    public ConfirmationCode changeLifecycleStage(ConfirmationCode code, ConfirmationCode.LifecycleStage stage) {
        return null;
    }

    @Override
    public ConfirmationCode changeLifecycleStage(String codeValue, ConfirmationCode.LifecycleStage stage) {
        return null;
    }

    private void changeConfirmationCodeStateAndSave(ConfirmationCode confirmationCode, ConfirmationCode.LifecycleStage currentStage, boolean activated) {
        confirmationCode.setLifecycleStage(currentStage);
        confirmationCode.setActivated(activated);
        confirmationCodeRepository.save(confirmationCode);
    }
}
