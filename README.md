# API-тесты, проект №2
В данном проекте покрыты тестами ручки API для [Stellar Burgers](https://stellarburgers.nomoreparties.site)
## В проекте используется:
* Java 11
* Maven
* JUnit 4
* RestAssured
* Allure
## Запуск тестов
Склонировать репозиторий
```
git clone https://github.com/MikeKotal/ApiTestsProject-2.git
```
Локально запустить тесты, лежат по следующему пути:
```
src/test/java/stellar_burger
```
Для просмотра отчета в Allure, выполнить следующие комманды:
```
mvm clean test
mvn allure:serve
```