package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationInfo info) {
        UserRegistrationConfirmationResponseDto dto = new UserRegistrationConfirmationResponseDto("We sent confirmation letter to your email. Check it out");
        if (!EmailValidator.getInstance().isValid(info.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, ApiErrorDetailsInfo.ErrorDetails.INVALID_EMAIL));
        }
        return getSuccessResponse(info, dto);
    }

    private ResponseEntity<UserRegistrationConfirmationResponseDto> getSuccessResponse(UserRegistrationInfo info, UserRegistrationConfirmationResponseDto dto) {
        Link link = Link.of("/confirm", LinkRelation.of("confirmation_url"));
        Link selfRel = getSelfRel(info);

        dto.add(link);
        dto.add(selfRel);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private static Link getSelfRel(UserRegistrationInfo info) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).registerUser(info)).withSelfRel();
    }
}
