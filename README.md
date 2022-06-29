# java-filmorate

### ER-диаграмма базы данных проекта java-filmorate

<img height="400" src="https://github.com/AleksandrSamusev/java-filmorate/blob/add-friends-likes/src/main/resources/static/small.jpg" width="550"/>

### Таблица FILMS (пример)

|  film_id  | name                   | description        | release_date |  duration  |  likes  | genre   |  mpa_rating_id  |  rate  |
|:---------:|------------------------|--------------------|--------------|:----------:|:-------:|---------|:---------------:|:------:|
|     1     | Побег из Шоушенка      | \*описание фильма* | 1994-01-01   |    142     |  12364  | триллер |        4        |   9    |
|     2     | Крёстный отец          | \*описание фильма* | 1972-01-01   |    175     |  10987  | боевик  |        4        |   8    |
|     3     | Темный рыцарь          | \*описание фильма* | 2008-01-01   |    152     |  7964   | боевик  |        3        |   7    |
|     4     | Крёстный отец 2        | \*описание фильма* | 1974-01-01   |    202     |  7198   | боевик  |        4        |   6    |
|     5     | 12 рaзгневанных мужчин | \*описание фильма* | 1957-01-01   |     96     |  6547   | триллер |        5        |   5    |

### Таблица MPA_ratings (пример)

|  mpa_rating_id  | rating   |
|:---------------:|:---------|
|        1        | G        |
|        2        | PG       |
|        3        | PG - 13  |
|        4        | R        |
|        5        | NC - 17  |

### Примеры запросов:

#### 1) ТОП-3 фильмов по количеству лайков:

SELECT f.film_id AS film_ID, f.name AS film_name, f.likes <br />
FROM films AS f <br />
ORDER BY f.likes DESC <br />
LIMIT 3; <br />

#### результат запроса:

|  film_ID  | film_name         | likes |
|:---------:|-------------------|-------|
|     1     | Побег из Шоушенка | 12364 |
|     2     | Крёстный отец     | 10987 |
|     3     | Темный рыцарь     | 7964  |

<br />

#### 2) Вывести название, дату резиза и длительность фильмов, вышедших до 2000 г. Отсортировать по алфавиту:

SELECT f.name, f.release_date, f.duration <br />
FROM films AS f <br />
WHERE EXTRACT(year from f.release_date) < '2000' <br />
ORDER BY name ASC; <br />

#### результат запроса:

| name                   |  release_date  |  duration  |
|------------------------|:--------------:|:----------:|
| 12 рaзгневанных мужчин |   1957-01-01   |     96     |
| Крёстный отец          |   1972-01-01   |    175     |
| Крёстный отец 2        |   1974-01-01   |    202     |
| Побег из Шоушенка      |   1994-01-01   |    142     |

<br />

#### 3) Вывести все фильмы (рейтинг, id и название) категории "R". Отсортировать по алфавиту:

SELECT m.rating AS rating, f.film_id AS id, f.name <br />
FROM films as f <br />
INNER JOIN mpa_ratings AS m <br />
ON f.mpa_rating_id = m.mpa_rating_id <br />
WHERE m.rating = 'R' <br />
ORDER BY f.name ASC;

#### результат запроса:

|  rating  |  id   | name                   |
|:--------:|:-----:|------------------------|
|    R     |   2   | Крёстный отец          |
|    R     |   4   | Крёстный отец 2        |
|    R     |   1   | Побег из Шоушенка      |
