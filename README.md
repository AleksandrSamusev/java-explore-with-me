1. PR develop - https://github.com/AleksandrSamusev/java-explore-with-me/pull/1

## "Explore with me" - приложение для шеринга мероприятиями и событиями

- [общая информация о приложении](#общая-информация-о-приложении)
- [описание структуры приложения](#структура-приложения)
- [ER-диаграмма (основная часть проекта)](#ER-диаграмма)
- [диаграмма компонентов](#диаграмма-компонентов)
- [дополнительная функциональность - рейтинги](#дополнительная-функциональность-(рейтинги))
- [ER-диаграмма (основная часть + дополнительная функциональность)](#ER-диаграмма-с-дополнительной-функиональностью)

### Общая информация о приложении

Приложение дает возможность делиться информацией об интересных событиях и помогает найти компанию для участия в них.

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком
планировании поиск информации и переговоры. Какие намечаются мероприятия, свободны ли в этот момент друзья, как всех
пригласить и где собраться. Приложение — афиша, где можно предложить какое-либо событие от выставки до похода в кино и
набрать компанию для участия в нём.

### Структура приложения

Приложение состоит из двух основных сервисов:

- основной сервис
- сервис статистики

Основной сервис - содержит всю необходимую логику для работы приложения. API основного сервиса состоит из трех частей.
Первая — публичная, доступна без регистрации любому пользователю сети. Вторая — закрытая, доступна только авторизованным
пользователям. Третья — административная, для администраторов сервиса.

Сервис статистики - хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

Взаимодействие сервисов друг с другом происходит при помощи Http-клиента.
Хранение данных сервисов организовано в двух независимых БД.

### ER диаграмма

![ER-диаграмма](https://github.com/AleksandrSamusev/java-explore-with-me/blob/feature/mainapp/src/main/resources/img/er-diagramm(EWM).jpg?raw=true)

### Диаграмма компонентов

![диаграмма компонентов](https://github.com/AleksandrSamusev/java-explore-with-me/blob/feature/mainapp/src/main/resources/img/components_diagramm.png?raw=true)

### Дополнительная функциональность (рейтинги)

Система рейтинов состоит из:

- рейтинг события (100 бальная система)
- рейтинг автора события (10-ти бальная система)
- ранг пользователя ("новичок", "опытный", "специалист", "эксперт")

**Рейтинг события** - показатель, рассчитывающийся как процент пользователей, зарегистрировавших для участия в событии
от общего количества просмотров данного события.
Количество пользователей, зарегистрировавшихся для участия в событии кешируется в соответствующей переменной
экземпляра Event и обновляется при подтверждении заявки на участие через соответствующий эндпоинт.
Общее количество просмотров данного события запрашивается при помощи http-клиента у сервиса статистики.

Ограничения при формировании рейтинга:

- для событий с ограниченным числом участников расчет рейтинга устанавливается окончательно при регистрации
  последнего возможного участника. В случае отмены каким-либо участником заявки по любым причинам
  расчет рейтинга возобновится пока регистрация на событие остается открытой. Окончательно рейтинг
  устанавливается при наступлении даты события.

- для событий с неограниченным числом участников расчет рейтинга устанавливается окончательно при
  наступлении даты и времени события.
- автор не может регистрироваться на свое же событие

Эти ограничения наложены для сохранения доставерной статистической
информации о популярности события, на случай если если после окончания события останется возможность посмотреть
информацию о нем (а возможности зарегистрироваться не будет) и рейтинг будет постоянно уменьшаться. Это
снижение также поведет за собой снижение рейтинга автора события.

**Рейтинг автора** - показатель, расчитывающийся по определенному алгоритму, в основе которого количество
положительных и отрицательных ревью событий, оргазованных автором.

Ограничения при формировании рейтинга:

- только пользователи с подтвержденной регистрацией могут оставлять оставлять ревью на событие
- автор не может оставлять отзыв на свое же событие

**Ранг пользователя** - показатель активности пользователя, зависящий от количества опубликованных отзвывов
на события. Пользователи с количеством ревью 1-10 имеют ранг "novice", 11-20 - "experienced",
21-30 - "specialist", более 31 - "expert".

### ER диаграмма с дополнительной функиональностью

![ER-диаграмма-с-дополнительной-функиональностью](https://github.com/AleksandrSamusev/java-explore-with-me/blob/feature/mainapp/src/main/resources/img/er-diagramm(feature).jpg?raw=true)