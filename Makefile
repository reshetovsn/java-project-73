setup:
	gradle wrapper --gradle-version 8.2.1

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

install:
	./gradlew install

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

image-build:
	docker build -t hexletcomponents/app:latest .

image-push:
	docker push hexletcomponents/app:latest

.PHONY: build
