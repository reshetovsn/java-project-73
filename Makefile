setup:
	gradle wrapper --gradle-version 8.2.1

clean:
	gradle clean

build:
	gradle clean build

start:
	gradle bootRun --args='--spring.profiles.active=dev'

install:
	gradle install

start-prod:
	gradle bootRun --args='--spring.profiles.active=prod'

generate-migrations:
	gradle generateMigrations

lint:
	gradle checkstyleMain checkstyleTest

test:
	gradle test

report:
	gradle jacocoTestReport

check-updates:
	gradle dependencyUpdates

image-build:
	docker build -t hexletcomponents/app:latest .

image-push:
	docker push hexletcomponents/app:latest

.PHONY: build
