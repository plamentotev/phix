# Създаване на задание

За разлика от повечето сходни продукти, Phix позволява голяма гъвкавост при проверката на
коректност на предадените решения. Това кратко ръководство ще използва за пример създаването
на задание, което ще проверява решенията, като сравнява генерирания от тях изход с предварително
зададен коректен изход. След като го прочетете и усвоите основите на създаването на задания,
ще може да създавате задания, които да използват много по-сложни методи за проверка - например
използването на JUnit тестове(или други сходни технологии за unit тестове).

## Добавяне на ново задание

За да създадете ново задание добавете директория в директорията със заданията.
Името на директорията трябва да е същото като id-то на заданието. Например, ако искаме заданието да е с id
multiplication и заданията се намират в `assignments`(което е настройката по подразбиране):

    $ cd assignments
    $ mkdir multiplication
    $ cd multiplication

Всяко задание трябва да съдържа файл в yaml формат, който съдържа описание на заданието.
Този файл задължително се казва `manifest.yml`.

    $ touch manifest.yml

Добавете следното съдържание към файла:

    id: multiplication
    title: Умножение
    description: >
      Напишете програма на Java, която за вход получава цяло числото n, последвано от n на брой редове,
      съдържащи двойка цели числа. За изход програмата да връща резултата от умножението на двойките числа.
      
      Например за следния вход
      
      3
      2 3
      12 3
      4 30
      
      Програмата трябва да връща
      
      6
      36
      120
    initialCode: |
      public class Multiplication {
      
          public static void main(String[] args) {
              // Напишете решението си тук
          }
      
      }

* `id` - използва се навсякъде, където се реферира заданието. Трябва да съвпада с името на директорията, в което се намира.
         Може да съдържа само латински букви, цифри и тирета.
* `title` - кратко заглавие на заданието.
* `description` - описание на заданието.
* `initialCode` - първоначален код. Този код трябва да бъде допълнен от решаващия заданието.
                  Ако решението трябва да приеме определена форма(например ако трябва да се реализира клас с дадено име или интерфейс),
                  това е удобен начин тя да се обясни. Не задължително поле.

След като зададохме условието на задачата, трябва да определим и начина, по който решенията ще се проверяват.
Phix има архитектура, която позволява да се добавят нови начини за проверка, но в момента се поддържат само два - ръчна проверка и Docker.
Ръчната проверка означава, че решението само се записва и трябва да бъде проверено от човек. Docker използва Docker контейнери за да компилира
и провери валидността на решението. В нашия пример ще използваме Docker. Добавете следния ред към `manifest.yml`:

    type: docker
    
## Създаване на Docker изображения

За да провери верността на решението, Phix използва до три контейнера:

* контейнер за компилация на решението. Ако процесът приключи с код за грешка, то се смята, че резултатът от изпълнението на решението е
  грешка при компилацията. Освен за компилиране на изходен код на езици като C++ и Java, този контейнер може да се използва и за проверка
  на синтаксиса на скриптови езици като JavaScript и Python. Използването на този контейнер не е задължително.
* контейнер за изпълнение на решението. Ако се използва контейнер за компилация, то компилираното решение се копира в този контейнер и
  се изпълнява. Ако процесът завърши с код за грешка, то се смята, че резултатът от изпълнението e грешка при изпълнение.
  Това е единственият задължителен контейнер.
* контейнер за проверка на верността на решението. В този контейнер се копира стандартният изход от изпълнението на решението.
  Ако процесът приключи с код за грешка се смята, че предаденото решение не е вярно. Използването на този контейнер не е задължително.
  Ако не се използва то, ако контейнерът за изпълнение върне грешка, решението се смята за невярно, а не че е приключило с грешка при изпълнение.
  Това позволява коректността на решение да се провери още при негово изпълнение - например чрез изпълнението на компонентен тест.
  
За да може Phix да създаде и използва тези контейнери е необходимо да създадем Docker изображения(images). Те ще бъдат използвани, когато
се оценява дадено решение, за да се създадат горе описаните контейнери.

Първо нека създадем изображението за компилиране на предаденото решение. В работна директория(различна от тази използвана за заданията),
създайте празна папка(името няма значение), която задължително съдържа само един файл - `Dockerfile`:

    FROM openjdk:8
    WORKDIR /var/src/solution
    ENTRYPOINT ["javac", "Multiplication.java"]

Повече за формата на файла може да намерите в [ръководството на Docker](https://docs.docker.com/engine/reference/builder/).
Това изображение ще използва за основа изображение, което има OpenJDK версия 8. Именно нея ще използваме за да компилираме решението.
Освен това ще компилираме `Multiplication.java`, разположено в `var/src/solution`. Как ще окажем на Phix да разположи решението
именно в този файл ще разгледаме по-късно. За да създадете изображението, от директорията, която се намира новосъздадения `Dockerfile`
изпълнете следната команда:

    $ docker build -t multiplication-compile .

В друга празна директория създайте нов файл с име `Dockerfile`:

    FROM openjdk:8
    WORKDIR /var/src/solution
    COPY run.sh /var/src/solution/
    COPY input /var/src/solution/
    ENTRYPOINT ["/bin/sh", "run.sh"]

Той копира два файла - скрипт(`run.sh`), който ще се използва за изпълнение на решението и файл, съдържащ входа, който ще се подаде
на решението.

Съдържанието на `run.sh`

    java Multiplication < input

Съдържанието на `input`:

    6
    0 0
    0 12
    15 0
    1 1
    14 1
    17 78

За да създадете изображението, от директорията, в която се намират описаните по-горе файлове изпълнете следната команда:

    $ docker build -t multiplication-run .

Накрая ще създадем изображението, което ще се използва да провери дали изходът от изпълнението на решението съвпада с очаквания.
В нова празна папка създайте нов `Dockerfile`:

    FROM openjdk:8
    WORKDIR /var/src/solution
    COPY expected_output /var/src/solution/
    ENTRYPOINT ["diff", "-wq", "expected_output", "output"]

Той копира `expected_output` - файл съдържащ очаквания изход:

    0
    0
    0
    1
    14
    1326

Той ще бъде сравнен използвайки `diff` инструмента. Ако реалният изход не съвпада с очаквания, той ще върне код за грешка,
така че Phix ще знае, че решението не е било правилно.

## Конфигуриране на заданието

След като създадохме изображенията, само трябва да добавим конфигурация към манифеста на заданието.
Към `manifest.yml` добавете:

    executorParams:
        # Docker изображение, което да се използва за компилация
        compilationImage: multiplication-compile
        # Директорията, в която да се разположи предаденото решението
        solutionPath: /var/src/solution
        # Името на файла, в която да се разположи предаденото решението
        solutionFileName: Multiplication.java
        # Директорията, която съдържа резултата от компилацията
        compilationOutputPath: /var/src/solution
        # Docker изображение, което да се използва за изпълнение на решението
        runImage: multiplication-run
        # Директорията, в която да се разположи резултатът от компилацията
        runCompilationArtifactPath: /var/src/
        # Максималния размер на изхода генериран от изпълнението на програмата (в байтове)
        runMaxLogSize: 1024
        # Максималния размер на паметта, която може да се използва от решението (в байтове)
        runMaxMemory: 1073741824
        # Docker изображение, което да се използва за проверка на верността на решението
        verificationImage: multiplication-verify
        # Директорията, в която да се разположи изходът от изпълнението на решението
        verificationOutputPath: /var/src/solution
        # Името на файла, в който да се разположи изходът от изпълнението на решението
        verificationOutputFileName: output
        # Общото време(компилация, изпълнение, проверка) в секунди, предоставено на решението. Ако се надвиши, то изпълнението се прекратява.
        timeLimit: 60000


Целият `manifest.yml` трябва да изглежда така:
    
    id: multiplication
    title: Умножение
    description: >
      Напишете програма на Java, която за вход получава цяло числото n, последвано от n на брой редове,
      съдържащи двойка цели числа. За изход програмата да връща резултата от умножението на двойките числа.
      
      Например за следния вход
      
      3
      2 3
      12 3
      4 30
      
      Програмата трябва да връща
      
      6
      36
      120
    initialCode: |
      public class Multiplication {
      
          public static void main(String[] args) {
              // Напишете решението си тук
          }
      
      }

    type: docker
    executorParams:
        # Docker изображение, което да се използва за компилация
        compilationImage: multiplication-compile
        # Директорията, в която да се разположи предаденото решението
        solutionPath: /var/src/solution
        # Името на файла, в която да се разположи предаденото решението
        solutionFileName: Multiplication.java
        # Директорията, която съдържа резултата от компилацията
        compilationOutputPath: /var/src/solution
        # Docker изображение, което да се използва за изпълнение на решението
        runImage: multiplication-run
        # Директорията, в която да се разположи резултатът от компилацията
        runCompilationArtifactPath: /var/src/
        # Максималният размер на изхода генериран от изпълнението на програмата (в байтове)
        runMaxLogSize: 1024
        # Максималният размер на паметта, която може да се използва от решението (в байтове)
        runMaxMemory: 1073741824
        # Docker изображение, което да се използва за проверка на верността на решението
        verificationImage: multiplication-verify
        # Директорията, в която да се разположи изходът от изпълнението на решението
        verificationOutputPath: /var/src/solution
        # Името на файла, в който да се разположи изходът от изпълнението на решението
        verificationOutputFileName: output
        # Общото време(компилация, изпълнение, проверка) в секунди, предоставено на решението. Ако се надвиши, то изпълнението се прекратява.
        timeLimit: 60000

Пълен списък на параметрите, може да намерите [тук](executor_params.html).