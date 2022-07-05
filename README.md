# java-filmorate

### ER-диаграмма базы данных проекта java-filmorate

<img height="400" src="https://github.com/AleksandrSamusev/java-filmorate/blob/ER-diagram/src/main/resources/static/diagram.png?raw=true">

### Таблица users (пример)"

| user_id |      email      | login  |    name     |  birthday   |
|:-------:|:---------------:|:------:|:-----------:|:-----------:|
|    1    |  user1@mail.ru  | user_1 | user_1_name | 1980-01-01  |
|    2    |  user2@mail.ru  | user_2 | user_2_name | 1920-04-21  |
|    3    |  user3@mail.ru  | user_3 | user_3_name | 1990-05-14  |
|    4    |  user4@mail.ru  | user_4 | user_4_name | 1995-02-17  |
|    5    |  user5@mail.ru  | user_5 | user_5_name | 1992-03-19  |

#### Индексы: <br />

"users_pkey" PRIMARY KEY, btree (user_id)  <br />
"uq_users_email" UNIQUE CONSTRAINT, btree (email)  <br />

#### Ссылки извне: <br />

TABLE "friendship" CONSTRAINT "friendship_friend_id_fkey" FOREIGN KEY (friend_id) REFERENCES users(user_id) <br />
TABLE "friendship" CONSTRAINT "friendship_user_id_fkey" FOREIGN KEY (user_id) REFERENCES users(user_id) <br />

<br />

### Таблица friendship (пример)

| user_id | friend_id | is_confirmed |
|:-------:|:---------:|:------------:|
|    1    |     2     |     true     |
|    2    |     1     |     true     |
|    1    |     3     |     true     |
|    3    |     1     |     true     |
|    1    |     4     |    false     |
|    1    |     5     |    false     |

#### Индексы: <br />

"friendship_pkey" PRIMARY KEY, btree (user_id, friend_id) <br />

#### Ограничения-проверки: <br />

"chk_if_equal" CHECK (user_id <> friend_id) <br />

#### Ограничения внешнего ключа: <br />

"friendship_friend_id_fkey" FOREIGN KEY (friend_id) REFERENCES users(user_id) <br />
"friendship_user_id_fkey" FOREIGN KEY (user_id) REFERENCES users(user_id)

<br /><br />

#### Примеры запросов:

#### 1. Получить список имен всех друзей пользователя с id=1

SELECT f.user_id AS USER_ID, u.name AS FRIEND_NAME <br />
FROM friendship AS f <br />
INNER JOIN users AS u ON u.user_id = f.friend_id <br />
WHERE f.user_id = 1 AND f.is_confirmed IS TRUE; <br />

#### Вывод:

| user_id | friend_name |
|:-------:|:-----------:|
|    1    | user_2_name |
|    1    | user_3_name |

<br />

#### 2. Посчитать количество друзей у пользователя с id=1

SELECT user_id, COUNT(*) <br />
FROM friendship <br />
WHERE user_id=1 AND is_confirmed IS TRUE <br />
GROUP BY user_id; <br />

#### Вывод:

| user_id | count |
|:-------:|:-----:|
|    1    |   2   |

<br />

#### 3. Получить список пользователей, запросивших дружбу у пользователя id=1

SELECT user_id, friend_id AS REQUEST_FROM <br />
FROM friendship <br />
WHERE user_id = 1 AND is_confirmed IS FALSE <br />
GROUP BY user_id, friend_id; <br />

#### Вывод:

| user_id | request_from |
|:-------:|:------------:|
|    1    |      4       |
|    1    |      5       |

<br />

### Таблица films (пример)

| film_id | name                               | description        | release_date | duration |  likes  | genre   | mpa_rating | rate |
|:-------:|------------------------------------|--------------------|--------------|:--------:|:-------:|:--------|:-----------|:----:|
|    1    | Отступники                         | \*описание фильма* | 2006-01-01   |   151    | 1294769 | боевик  | NC-17      |  10  |
|    2    | Умница Уилл Хантинг                | \*описание фильма* | 1997-01-01   |   126    | 943990  | драма   | PG         |  9   |
|    3    | Начало                             | \*описание фильма* | 2010-01-01   |   148    | 2285903 | триллер | PG-13      |  8   |
|    4    | Интерстеллар                       | \*описание фильма* | 2014-01-01   |   169    | 1747726 | триллер | G          |  8   |
|    5    | Темный рыцарь: Возрождение легенды | \*описание фильма* | 2012-01-01   |   164    | 1663443 | боевик  | PG-13      |  7   |

#### Индексы:

"films_pkey" PRIMARY KEY, btree (film_id) <br />

#### Ограничения-проверки:

"chk_genre" CHECK (genre::text = 'комедия'::text OR genre::text = 'драма'::text OR genre::text = 'мультфильм'::
text <br />
OR genre::text = 'триллер'::text OR genre::text = 'документальный'::text OR genre::text = 'боевик'::text) <br />
"chk_mpa_rating" CHECK (mpa_rating::text = 'G'::text OR mpa_rating::text = 'PG'::text  <br />
OR mpa_rating::text = 'PG-13'::text OR mpa_rating::text = 'R'::text OR mpa_rating::text = 'NC-17'::text) <br />

<br />

#### Примеры запросов:

#### 1. топ-3 фильмов по количеству лайков с сортировкой по уменьшению:

SELECT f.film_id AS film_ID, f.name AS film_name, f.likes <br />
FROM films AS f <br />
ORDER BY f.likes DESC <br />
LIMIT 3; <br />

#### Вывод:

| film_ID | film_name                          | likes   |
|:-------:|------------------------------------|---------|
|    3    | Начало                             | 2285903 |
|    4    | Интерстеллар                       | 1747726 |
|    5    | Темный рыцарь: Возраждение легенды | 1663443 |

<br />

#### 2. Вывести название, дату резиза и продолжительность фильмов, вышедших до 2000 г. Отсортировать по алфавиту:

SELECT f.name, f.release_date, f.duration <br />
FROM films AS f <br />
WHERE EXTRACT(year from f.release_date) < '2000' <br />
ORDER BY name ASC; <br />

#### результат запроса:

| name                | release_date |  duration  |
|---------------------|:------------:|:----------:|
| Умница Уилл Хантинг |  1997-01-01  |     96     |

<br />

#### 3. Вывести все фильмы (рейтинг, id и название) категории "PG-13". Отсортировать по алфавиту:

SELECT mpa_rating AS rating, f.film_id AS id, f.name  <br />
FROM films as f <br />
WHERE mpa_rating = 'PG-13' <br />
ORDER BY f.name ASC; <br />

#### результат запроса:

| rating | id  | name                               |
|:------:|:---:|------------------------------------|
| PG-13  |  3  | Начало                             |
| PG-13  |  5  | Темный рыцарь: Возрождение легенды |
