package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.JsonTestUtils;
import com.odeyalo.sonata.authentication.controller.support.DataRequestAssociationService;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.dto.request.ConfirmationCodeData;
import com.odeyalo.sonata.authentication.dto.response.MfaConfirmationSubmissionResultResponse;
import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.entity.settings.UserSettings;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
import com.odeyalo.sonata.authentication.exceptions.MissingConfirmationCodeValueException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeGenerator;
import com.odeyalo.sonata.authentication.service.confirmation.DelegatingPersistentConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.confirmation.NumericConfirmationCodeGenerator;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import com.odeyalo.sonata.authentication.testing.assertations.MailMessageAssert;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.odeyalo.sonata.authentication.controller.support.DataRequestAssociationService.AssociatedWith.Store.HEADER;
import static com.odeyalo.sonata.authentication.dto.ErrorAdditionalInfoKeys.UNSUPPORTED_MFA_METHOD;
import static com.odeyalo.sonata.authentication.exceptions.handler.GlobalExceptionHandlerController.MFA_METHOD_DOES_NOT_SUPPORTED;
import static com.odeyalo.sonata.authentication.exceptions.handler.GlobalExceptionHandlerController.MISSING_CONFIRMATION_CODE_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MfaControllerTest {
    public static final String NOT_EXISTING_METHOD = "NOT_EXISTING_METHOD";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataRequestAssociationService dataRequestAssociationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;
    @MockBean
    private MailSender mailSender;
    private static final String DIGITS_ONLY_REGEX = "^\\D*(\\d\\D*){6}$";
    public static final String EMAIL_MFA_METHOD_NAME = "email";
    public static final String MFA_METHOD_REQUEST_PARAMETER_NAME = "method";
    public static final String START_MFA_LOGIN_URL = "/mfa/login";
    public static final String CHECK_MFA_CONFIRMATION_URL = "/mfa/login/check";

    @TestConfiguration
    public static class MfaControllerTestConfiguration {
        @Bean
        @Primary
        public ConfirmationCodeGenerator confirmationCodeGenerator(ConfirmationCodeRepository confirmationCodeRepository) {
            return new DelegatingPersistentConfirmationCodeManager(new NumericConfirmationCodeGenerator(), confirmationCodeRepository);
        }
    }

    @Test
    @DisplayName("Start mfa login with null user in attributes store and expect MalformedLoginSessionException to be thrown")
    void startMfaProcessForNullUser_andExpectException() throws Exception {
        mockMvc.perform(post(START_MFA_LOGIN_URL)
                        .param(MFA_METHOD_REQUEST_PARAMETER_NAME, EMAIL_MFA_METHOD_NAME))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof MalformedLoginSessionException))
                .andDo(print());
    }

    @Test
    @DisplayName("Start email mfa login with not null user in attributes store and expect email mfa process to start")
    void startMfaProcessForUser_andExpectEmailMfaProcessToStart() throws Exception {
        // given
        User user = createUserWithMfaTypes(UserMfaSettings.MfaType.EMAIL);
        saveUserAndUpdateId(user);
        // when
        MockHttpServletRequestBuilder builder = post(START_MFA_LOGIN_URL)
                .param(MFA_METHOD_REQUEST_PARAMETER_NAME, EMAIL_MFA_METHOD_NAME);
        associateDataAndCopyToBuilder(builder, AuthController.LOGIN_ASSOCIATED_USER_KEY, user, new MockHttpServletRequest(), new MockHttpServletResponse());
        // then
        MvcResult result = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(print())
                .andReturn();

        // Using mock to check if message was actually sent
        ArgumentCaptor<MailMessage> argument = ArgumentCaptor.forClass(MailMessage.class);
        Mockito.verify(mailSender).send(argument.capture());
        MailMessage actual = argument.getValue();

        MailMessageAssert.forMessage(actual)
                .content()
                .mustContainRegex(DIGITS_ONLY_REGEX)
                .and()
                .receiver()
                .exactlyEquals(EmailReceiver.of(user.getEmail()));

    }

    @Test
    @DisplayName("Start mfa login using non-existing method and expect exception to be thrown")
    void startMfaLoginUsingNotExistingMethod_andExpectException() throws Exception {
        // given
        User user = createUserWithMfaTypes();
        saveUserAndUpdateId(user);
        // when
        MockHttpServletRequestBuilder builder = post(START_MFA_LOGIN_URL)
                .param(MFA_METHOD_REQUEST_PARAMETER_NAME, NOT_EXISTING_METHOD);
        associateDataAndCopyToBuilder(builder, AuthController.LOGIN_ASSOCIATED_USER_KEY, user, new MockHttpServletRequest(), new MockHttpServletResponse());

        // then
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print())
                .andReturn();

        ApiErrorDetailsInfo info = JsonTestUtils.convertToPojo(mvcResult, ApiErrorDetailsInfo.class);

        assertEquals(MFA_METHOD_DOES_NOT_SUPPORTED, info.getErrorDetails(), "Error details must be equal!");
        assertEquals(NOT_EXISTING_METHOD, info.getAdditionalInfo().get(UNSUPPORTED_MFA_METHOD),
                String.format("Additional info must contain element with key '%s'!", UNSUPPORTED_MFA_METHOD));

        Exception resolvedException = mvcResult.getResolvedException();

        assertThat(resolvedException)
                .as("The exception must be thrown if the MFA method is not supported")
                .isExactlyInstanceOf(UnsupportedMfaMethodException.class);
    }

    @Test
    @DisplayName("Check mfa confirmation with correct code and expect true and user info")
    void checkMfaConfirmationWithValidCode_andExpectTrue() throws Exception {
        // given
        User user = createUserWithMfaTypes(UserMfaSettings.MfaType.EMAIL);
        saveUserAndUpdateId(user);
        ConfirmationCode confirmationCode = confirmationCodeRepository.save(ConfirmationCodeFaker.numeric().user(user).get());

        ConfirmationCodeData data = new ConfirmationCodeData(confirmationCode.getCode());
        String json = JsonTestUtils.convertToJson(data);

        MockHttpServletRequestBuilder builder = post(CHECK_MFA_CONFIRMATION_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        associateDataAndCopyToBuilder(builder, AuthController.LOGIN_ASSOCIATED_USER_KEY, user, request, servletResponse);
        associateDataAndCopyToBuilder(builder, MfaController.MFA_METHOD_KEY, "email", request, servletResponse);
        // when
        MvcResult result = mockMvc.perform(builder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // then
        MfaConfirmationSubmissionResultResponse response = JsonTestUtils.convertToPojo(result, MfaConfirmationSubmissionResultResponse.class);
        assertEquals(UserInfo.from(user), response.getInfo(), "The info about user must be presented and valid!");
        assertTrue(response.isResult(), "If everything is okay, then true must be returned");
    }


    @Test
    @DisplayName("Check mfa confirmation with invalid code and expect false")
    void checkMfaConfirmationWithInvalidCode_andExpectFalse() throws Exception {
        // given
        User user = createUserWithMfaTypes(UserMfaSettings.MfaType.EMAIL);
        saveUserAndUpdateId(user);

        String json = JsonTestUtils.convertToJson(new ConfirmationCodeData("invalid_code"));

        MockHttpServletRequestBuilder builder = post(CHECK_MFA_CONFIRMATION_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        associateDataAndCopyToBuilder(builder, AuthController.LOGIN_ASSOCIATED_USER_KEY, user, request, servletResponse);
        associateDataAndCopyToBuilder(builder, MfaController.MFA_METHOD_KEY, "email", request, servletResponse);
        // when
        MvcResult result = mockMvc.perform(builder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        // then
        MfaConfirmationSubmissionResultResponse response = JsonTestUtils.convertToPojo(result, MfaConfirmationSubmissionResultResponse.class);
        assertNull(response.getInfo(), "The info about user must be null if code is invalid!");
        assertFalse(response.isResult(), "If code is invalid, then false must be returned");
    }

    @Test
    @DisplayName("Check mfa confirmation with null code and expect false and exception")
    void checkMfaConfirmationWithNullCode_andExpectFalse() throws Exception {
        // given
        User user = createUserWithMfaTypes(UserMfaSettings.MfaType.EMAIL);
        saveUserAndUpdateId(user);

        String json = JsonTestUtils.convertToJson(new ConfirmationCodeData(null));

        MockHttpServletRequestBuilder builder = post(CHECK_MFA_CONFIRMATION_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        associateDataAndCopyToBuilder(builder, AuthController.LOGIN_ASSOCIATED_USER_KEY, user, request, servletResponse);
        associateDataAndCopyToBuilder(builder, MfaController.MFA_METHOD_KEY, "email", request, servletResponse);
        // when
        MvcResult result = mockMvc.perform(builder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        // then
        ApiErrorDetailsInfo response = JsonTestUtils.convertToPojo(result, ApiErrorDetailsInfo.class);
        assertEquals(MISSING_CONFIRMATION_CODE_VALUE, response.getErrorDetails(), "Details must be equal to MISSING_CONFIRMATION_CODE_VALUE");
        Exception resolvedException = result.getResolvedException();
        assertEquals(resolvedException.getClass(), MissingConfirmationCodeValueException.class, "The MissingConfirmationCodeValueException must be thrown if code value is null");
    }



    private void saveUserAndUpdateId(User user) {
        User saved = userRepository.save(user);
        user.setId(saved.getId());
    }

    private void associateDataAndCopyToBuilder(MockHttpServletRequestBuilder builder, String key, Object obj, MockHttpServletRequest request, HttpServletResponse response) {
        DataRequestAssociationService.AssociatedWith associatedWith = dataRequestAssociationService.associateData(key, obj, request, response);
        String name = associatedWith.name();
        String value = associatedWith.value();

        // Copy the session and cookies from the response to this request
        builder.session(request.getSession() != null ? (MockHttpSession) request.getSession() : new MockHttpSession());

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            builder.cookie(cookies);
        }
        // Add the extra header to request if DataRequestAssociationService associate the data using header
        if (associatedWith.store() == HEADER) {
            builder.header(name, value);
        }
    }

    private User createUserWithMfaTypes(UserMfaSettings.MfaType... types) {
        User user = UserFaker.create().get();
        UserSettings settings = UserSettings.empty(user);
        for (UserMfaSettings.MfaType type : types) {
            settings.getUserMfaSettings().addAuthorizedMfaType(type);
        }
        user.setUserSettings(settings);
        return user;
    }
}
