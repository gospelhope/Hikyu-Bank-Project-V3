#!/usr/bin/env sh
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$project_dir/backend"
exec ./mvnw spring-boot:run
