package contracts.password

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method PUT()
        urlPath("/auth/settings/password-change") {
            queryParameters {
                parameter "userId": value(1)
            }
        }
        headers {
            contentType "application/json"
        }
        body(
                [
                        "old_password": "old_password123",
                        "new_password": "invalid"
                ]
        )
    }
    response {
        status BAD_REQUEST()
        headers {
            contentType "application/json"
        }
        body(
                [
                        "updated": false,
                        "error_details": [
                                "code": "new_password_pattern_mismatch",
                                "description": "New password has invalid format",
                                "possible_solution": "Use correct format for password"
                        ]
                ]
        )
    }
}