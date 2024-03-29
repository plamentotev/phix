# Phix

Phix e система за проверка на задачи по програмиране чрез използване на автоматични тестове.
Може да се използва както от преподаватели, които искат да автоматизират проверката на задачи или домашни по програмиране,
така и за провеждане на интервюта за работа за програмисти.

Повечето сходни продукти проверяват коректността на решенията чрез подаване на входни данни и сравняването
на стандартния изход с предварително зададени стойности. Phix позволява по-голяма гъвкавост в избора на
метод за проверка, като същевременно запазва разумно ниво на сигурност и стабилност.

## Системни изисквания

Phix изисква Java Runtime Environment версия 8(Java 9 не се поддържа засега).
Допълнително, ако искате да използвате Docker за проверка на задачите, ще ви трябва инсталирана версия 1.13.1
или по-нова. За повече информация как да инсталирате Docker може да намерите в
[официалното ръководство](https://docs.docker.com/engine/installation/).

**ЗАБЕЛЕЖКА:** От съображения за сигурност е по-добре Docker демонът да бъде разположен на отделна самостоятелна,
реална или виртуална машина. Макар и трудно, излизането от ограниченията на Docker не е невъзможно -
повече информация по въпросите свързани със сигурността и Docker може да намерите
[тук](https://docs.docker.com/engine/security/security/).

## Стартиране

Свалете последната версия на Phix от [тук](https://github.com/plamentotev/phix/releases).
За да стартирате Phix сървъра изпълнете:

    $ java -jar phix.jar

**ЗАБЕЛЕЖКА:** Phix не поддържа оторизация и аутентикация. Уверете се, че имате защитна стена
или друга подходяща защита, която да предотврати нежелан достъп към сървъра.

## Компилиране

Може да компилирате последната версия на изходния код. За това са необходими:

* Apache Maven 3.3 или по-нова версия
* Java Development Kit версия 8 (Java 9 не се поддържа засега)

За да компилирате проекта изпълнете:

    $ mvn clean verify

Полученият след компилацията JAR файл се намира в `target/` директорията.

## Конфигуриране

За да конфигурирате Phix, може да използвате променливи на средата(Environment variables).
Например за да промените порта, на който Phix слуша за връзки, стартирайте сървъра със следната команда:

    $ java -Dserver.port=8090 -jar phix-1.0.0.jar
    
Под Linux това е еквивалентно на това да изпълните следните две команди:

    $ export SERVER_PORT=8090
    $ java -jar phix-1.0.0.jar

За Windows:

    C:\> SET SERVER_PORT=8090
    C:\> java -jar phix-1.0.0.jar

Пълния списък с опции може да намерите [тук](options.html).

## Добавяне на задания

Кратко ръководство за създаване на задания може да намерите [тук](assignments_guide.html).

## Приложно-програмен интерфейс

Phix предоставя уеб базиран приложно-програмен интерфейс за предаване на задачи и автоматичната им проверка.
Документация за предоставените REST ресурси може да намерите [тук](api.html)

## Произход на името на проекта
Името на проекта, произхожда от митологичното същество [сфинкс](https://en.wikipedia.org/wiki/Sphinx) което,
според древногръцката митология, пази входа на Тива и задава гатанка на всеки, желаещ да премине.
Тъй като Sphinx е име използвано от [популярен инструмен за създаване на документации](http://www.sphinx-doc.org/),
проектът е кръстен на Фикс - името, дадено на сфинкса от Хезиод в Теогония.
