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
                        "old_password": "invalid",
                        "new_password": "neW_password123"
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
                                "code": "old_password_mismatch",
                                "description": "Old password in invalid",
                                "possible_solution": "Use correct old password"
                        ]
                ]
        )
    }
}