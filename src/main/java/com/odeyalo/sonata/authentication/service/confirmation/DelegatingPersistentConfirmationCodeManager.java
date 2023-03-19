package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Optional;

import static com.odeyalo.sonata.authentication.entity.ConfirmationCode.LifecycleStage.ACTIVATED;
import static com.odeyalo.sonata.authentication.entity.ConfirmationCode.LifecycleStage.DENIED;

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
    public ConfirmationCode generateCode(User user, int length, int lifetimeMinutes) {
        ConfirmationCode generatedCode = delegateGenerator.generateCode(user, length, lifetimeMinutes);
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
            changeConfirmationCodeStateAndSave(confirmationCode, DENIED, false);
            return ConfirmationCodeCheckResult.ALREADY_EXPIRED;
        }
        ConfirmationCode.LifecycleStage lifecycleStage = confirmationCode.getLifecycleStage();

        if ((lifecycleStage == ACTIVATED) || (lifecycleStage == DENIED)) {
            return ConfirmationCodeCheckResult.ALREADY_ACTIVATED;
        }

        changeConfirmationCodeStateAndSave(confirmationCode, ACTIVATED, true);
        return ConfirmationCodeCheckResult.VALID;
    }

    @Override
    public void deleteCode(String codeValue) {
        confirmationCodeRepository.deleteByCodeValue(codeValue);
        logger.info("Removed value from repository: {}", codeValue);
    }

    @Override
    public void deleteCode(ConfirmationCode code) {
        if (code.getId() != null) {
            confirmationCodeRepository.deleteById(code.getId());
            return;
        }
        confirmationCodeRepository.deleteByCodeValue(code.getCode());
    }

    @Override
    public ConfirmationCode.LifecycleStage getLifecycleStage(ConfirmationCode code) {
        return confirmationCodeRepository.findConfirmationCodeByCodeValue(code.getCode())
                .map(ConfirmationCode::getLifecycleStage)
                .orElse(null);
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
        Assert.notNull(code, "The ConfirmationCode must be not null!");
        Assert.notNull(stage, "The LifecycleStage must be not null!");
        return changeLifecycleStage(code.getCode(), stage);
    }

    @Override
    public ConfirmationCode changeLifecycleStage(String codeValue, ConfirmationCode.LifecycleStage stage) {
        return confirmationCodeRepository.findConfirmationCodeByCodeValue(codeValue)
                .map((confirmationCode) -> {
                    confirmationCode.setLifecycleStage(stage);
                    return (ConfirmationCode) confirmationCodeRepository.save(confirmationCode);
                })
                .orElse(null);
    }

    private void changeConfirmationCodeStateAndSave(ConfirmationCode confirmationCode, ConfirmationCode.LifecycleStage currentStage, boolean activated) {
        confirmationCode.setLifecycleStage(currentStage);
        confirmationCode.setActivated(activated);
        confirmationCodeRepository.save(confirmationCode);
    }
}
