#!/bin/bash

ENV_FILE='test-env-variables.env'

set -o allexport && source "$ENV_FILE" && set +o allexport

echo "Set environment variables from $ENV_FILE"

mvn test
