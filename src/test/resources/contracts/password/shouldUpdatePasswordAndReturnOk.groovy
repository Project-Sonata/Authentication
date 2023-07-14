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
                        "old_password": "old_password_123",
                        "new_password": "neW_password123"
                ]
        )
    }
    response {
        status OK()
        headers {
            contentType "application/json"
        }
        body(
                [
                        "updated": true
                ]
        )
    }
}