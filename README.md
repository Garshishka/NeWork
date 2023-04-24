# NeWork

Мобильное приложение, напоминающее формат LinkedIn. Сервер приложения - https://netomedia.ru/swagger/.

## Функционал:
* Возможность создавать, редактировать и удалять посты, которые содержат текст, ссылки, геометку, вложение (одно из: звуковое, видео или изображение) и упоминания других пользователей сети.
* Стена постов всех пользователей сети.
* Стена постов отдельного пользователя сети.
* Возможность лайкать как свои, так и посты других пользователей сети на стенах.
* Возможность создавать, редактировать и удалять события, которые содержат текст, дату проведения события, формат (офлайн или онлайн), ссылки, геометку,  вложение (одно из: звуковое, видео или изображение) и упоминания других пользователей сети в качестве спикеров.
* Стена событий всех пользователей сети.
* Возможность как лайкать события, так и отмечать о своем участии в данных событиях на стене событий.
* Возможность указать у себя работу, редактировать или удалить ее. Работа содержит название, должность, дату начала и ее окончания.
* На стене постов отдельных пользователей можно смотреть их работы.

## Внешний вид:

### 1. Стена всех постов (начальный экран)

![PostFeedUi](https://user-images.githubusercontent.com/30876362/233847313-b444ac4f-b330-4c36-aece-8e06c905f032.PNG)

*Элементы со звездочкой опциональны

Кнопка взаимодействия появляется, если данный пост принадлежит пользователю. В ней можно редактировать или удалить данный пост

![bottomPost](https://user-images.githubusercontent.com/30876362/233846840-337bb26f-3892-4754-9124-2f52d5112468.PNG)

В нижней части поста показывается кнопка лайков и упоминания. Каждый из них показывает количество лайков/упоминаний, а также аватарки трех пользователей, 
которые нажали лайк/были упомянуты. Если таких пользователей больше трех, то после аватарок идет знак многоточия.

В зависимости от того, лайкнул ли пользователь пост, значок лайка будет заполнен или нет.

В панели навигации могут находиться следующие кнопки:
* ![PostFeedButton](https://user-images.githubusercontent.com/30876362/233847380-8c23eca6-6a99-4c16-9655-4207e990ae09.PNG) Все посты.
* ![MyWallButton](https://user-images.githubusercontent.com/30876362/233847419-e1f974b8-8498-4bd0-96c7-8c5fa024af62.PNG) Мои посты.
* ![EventsButton](https://user-images.githubusercontent.com/30876362/233847461-09ca4d4f-30f1-4560-8c20-daeb1381ade1.PNG) События.
* ![MyJobsButton](https://user-images.githubusercontent.com/30876362/233847509-2341131b-29cd-465d-8226-a04b1c563682.PNG) Мои работы.

По нажатию на аватар автора поста или на кнопку "Мои посты" происходит перемещение на

### 2. Стена моих постов/постов выбранного пользователя

![UserWallFeedUi](https://user-images.githubusercontent.com/30876362/233847779-ff6d911c-401a-4de7-934b-80c182a33d1b.PNG)

UI этой стены во многом идентичен общей стене постов, но он имеет сверху карточку пользователя, где указан пользователь, все посты которого мы смотрим на данный момент.

Также, если у пользователя указана хоть одна работа, то будет отображена кнопка работы.

По нажатию на кнопку работы или на кнопку "Мои работы" в панели навигации мы переходим на

### 3. Стена моих работ/работ выбранного пользователя 

![JobFeed](https://user-images.githubusercontent.com/30876362/233848248-a1fec337-0601-4848-ab3c-05da0efb1701.PNG)

*Элементы со звездочкой опциональны

Кнопка взаимодействия появляется, если данная работа принадлежит пользователю. В ней можно редактировать или удалить данную работу

### 4. Стена событий

![EventFeed](https://user-images.githubusercontent.com/30876362/233848758-5f2869ad-e51c-4e4a-9748-b2934a3760ff.PNG)

*Элементы со звездочкой опциональны

Стена событий во многом идентичная стене постов, за исключением нескольких элементов: Время события, формат события, спикеры и участники, причем участники являются кнопкой, аналогичной лайку.

## Добавление/редактирование элементов
 
### 1. Добавление/редактирование постов/событий
 
 ![AddEvent](https://user-images.githubusercontent.com/30876362/234088569-86488bde-915a-4b22-be5f-8896362cf5eb.png)

*Элементы, отмеченные звездочкой, присутствуют только у событий.
 
Панель добавления файлов снизу позволяет добавить в качестве прикрепленного файла один их трех типов файлов:
 
* ![AddPicture](https://user-images.githubusercontent.com/30876362/234088718-93911576-41b2-4b6d-b49a-004b5d69ce57.PNG) Картинку
* ![AddVideo](https://user-images.githubusercontent.com/30876362/234088755-4c474beb-b813-43fa-be9c-35a20d0efc6a.PNG) Видео
* ![AddSound](https://user-images.githubusercontent.com/30876362/234088803-f93b86b1-cb76-4da9-aa1a-f550c85ec9bc.PNG) Звук
 
По нажатию на кнопку добавления геотега открывается окно
 
### 2. Добавление/редактирование геотега

![MapFragment](https://user-images.githubusercontent.com/30876362/234089848-f0a1fa07-d147-4609-887e-c09925b0be2a.PNG)
 
Координаты выбранного места - середина экрана.
 
### 3. Добавление/редактирование пользователей (упоминаний/спикеров)
 
При создании/редактировании поста или события при нажатии кнопки ![AddPeople](https://user-images.githubusercontent.com/30876362/234088906-dfde7518-b218-4490-b2bc-6e8de9b12efc.PNG) "добавить пользователей" открывается данное окно. 
 
![AddUsers](https://user-images.githubusercontent.com/30876362/234090124-be4885b1-2722-445c-90dc-272b9dba5b44.PNG)
 
По нажатию на пользователя он добавляется в общий список отмеченных пользователей.
 
### 4. Добавление/редактирование работ

![AddJob](https://user-images.githubusercontent.com/30876362/234090169-71feea32-8d57-498b-b378-0d280de8c697.PNG)
 
Если не указать дату окончания работы, то она будет считаться активной "по нынешний день".
 
## Авторизация/регистрация пользователя

![SignIn](https://user-images.githubusercontent.com/30876362/234090207-e0f0d37f-3d44-4494-86d1-f3a34191ccb0.PNG)  ![SignUp](https://user-images.githubusercontent.com/30876362/234090224-e3d0638c-c57a-4c4c-8383-2a22d40ab9f9.PNG)
 
Если пользователь использует приложение без логина, то при выполнении определенных действий (добавление постов/событий, лайки/участия, переход на "мою стену" или "мои работы") его попросят залогиниться или зарегистрироваться 
 
При регистрации можно выбрать картинку в качестве аватарки. 

# Техническая сторона
 
## Онлайн связь
 
Для отправки и получения данных на сервер используется набор команд для Retrofit [Api Service](/app/src/main/java/ru/netology/nework/api/ApiService.kt) 
 
## Офлайн база данных для кэширования
 
Все полученные данные с сервера при помощи Room сохраняются в базу данных. 

Функции Room для постов [Post DAO](/app/src/main/java/ru/netology/nework/dao/PostDao.kt) и для событий [Event DAO](/app/src/main/java/ru/netology/nework/dao/EventDao.kt). 

Кроме того для пагинации используются Remote Key DAO для [постов](app/src/main/java/ru/netology/nework/dao/PostRemoteKeyDao.kt) и [событий](app/src/main/java/ru/netology/nework/dao/EventRemoteKeyDao.kt)

## [Репозитории данных](/app/src/main/java/ru/netology/nework/repository/)

### Посты/События

Для связи между получаемыми с сервера и хранимыми локально в БД данными и последующей их обработке используются репозитории для [постов](/app/src/main/java/ru/netology/nework/repository/posts/PostRepositoryImpl.kt) и [событий](/app/src/main/java/ru/netology/nework/repository/events/EventRepositoryImpl.kt).

Набор данных выдается через пагинацию при помощи медиаторов для [постов](app/src/main/java/ru/netology/nework/repository/posts/PostRemoteMediator.kt) и [событий](/app/src/main/java/ru/netology/nework/repository/events/EventRemoteMediator.kt).
Для стены постов одного пользователя используется свой [медиатор](/app/src/main/java/ru/netology/nework/repository/posts/PostRemoteMediatorUserWall.kt)

Выдаются посты или события через Flow.

### Пользователи

Для получения списка пользователей и работы с ним используется свой собственный [репозиторий](/app/src/main/java/ru/netology/nework/repository/users/UsersRepositoryImpl.kt)

Список пользователей передается как LiveData

### Прикрепленные файлы 

Для обработки прикрепленных файлов перед отправкой на сервер и последующей отправкой на сервер используется свой собственный [репозиторий](/app/src/main/java/ru/netology/nework/repository/media/MediaRepositoryImpl.kt)

## [View models](/app/src/main/java/ru/netology/nework/viewmodel/)

Слой взаимодействия между UI и репозиториями 

### Посты/События

Доступ к данным у стен [постов](/app/src/main/java/ru/netology/nework/viewmodel/PostViewModel.kt) и [событий](/app/src/main/java/ru/netology/nework/viewmodel/EventViewModel.kt), а также функции обработки находятся в них.
Для стены постов одного пользователя используется [подкласс вьюмодели постов](/app/src/main/java/ru/netology/nework/viewmodel/UserWallViewModel.kt), у которого другой доступ к данным и дополнительные функции.

### Пользователи

Для работы с репозиторием пользователей используется своя [вьюмодель](app/src/main/java/ru/netology/nework/viewmodel/UsersAndMapViewModel.kt). 

### Пользователи

Работы обходятся без репозитория, вся обработка данных происходит прямо в их [вьюмодели](/app/src/main/java/ru/netology/nework/viewmodel/JobViewModel.kt).

Список работ передается как LiveData

### Дополнительные вьюмодели

Для фрагментов также используются дополнительные вьюмодели, которые содержат информацию о [статусе аутентификации пользователя](app/src/main/java/ru/netology/nework/viewmodel/AuthViewModel.kt), а также функциях [логина](/app/src/main/java/ru/netology/nework/viewmodel/SignInViewModel.kt) и [регистрации](/app/src/main/java/ru/netology/nework/viewmodel/SignUpViewModel.kt).

## UI
 
Приложение использует одно основное активити [MainActivity](/app/src/main/java/ru/netology/nework/MainActivity.kt), которое лишь содержит меню логина. Остальное находится во [фрагментах](app/src/main/java/ru/netology/nework/fragment/), с соответствующими [адаптерами](/app/src/main/java/ru/netology/nework/adapter) и их [вьюхолдерами](/app/src/main/java/ru/netology/nework/viewholder/)

### [Стена всех постов](/app/src/main/java/ru/netology/nework/fragment/PostFeedFragment.kt)

Начальный экран.

Содержит стену постов, которая находится в Recycler View с пагинацией ([разметка](/app/src/main/res/layout/fragment_posts.xml)). Для размещения постов ([разметка постов](/app/src/main/res/layout/layout_post.xml)) в ней используется [PostViewHolder](/app/src/main/java/ru/netology/nework/viewholder/PostViewHolder.kt). 

### [Стена постов одного пользователя](app/src/main/java/ru/netology/nework/fragment/UserWallFragment.kt)

Является подклассом PostFeedFragment, который добавляет информацию о пользователе и использует свою вьюмодель для показа постов только определенного пользователя.

Использует такую же разметку. Разницы между "Моей стеной" и "стеной Х пользователя" технически нет - это все стена Х пользователя, только в одном случае мы используем ID залогиненного пользователя

### [Стена событий](/app/src/main/java/ru/netology/nework/fragment/EventFeedFragment.kt)

Содержит стену событий, которая находится в Recycler View с пагинацией ([разметка](/app/src/main/res/layout/fragment_events.xml)). Для размещения событий ([разметка событий](/app/src/main/res/layout/layout_event.xml)) в ней используется [EventViewHolder](/app/src/main/java/ru/netology/nework/viewholder/EventViewHolder.kt). 
Структура во многом идентична структуре системы работы с постами.

### [Стена работ](/app/src/main/java/ru/netology/nework/fragment/JobFragment.kt)
Содержит стену работ, которая находится в Recycler View ([разметка](/app/src/main/res/layout/fragment_jobs.xml)). Для размещения работ ([разметка работ](/app/src/main/res/layout/layout_job.xml)) в ней используется [JobViewHolder](/app/src/main/java/ru/netology/nework/viewholder/JobViewHolder.kt). 

У постов и событий добавление элементов происходит на отдельных фрагментах, но у работ за это отвечает просто дополнительное окно в этом же фрагменте.

### Добавление новых [постов](/app/src/main/java/ru/netology/nework/fragment/NewPostFragment.kt) и [событий](/app/src/main/java/ru/netology/nework/fragment/NewEventFragment.kt)

Отдельный фрагмент для добавление новых/редактирования старых элементов. Используются такие же вьюмодели, что и на стенах, для простого доступа к данным и функциям.

Разметка добавления/редактирования [постов](/app/src/main/res/layout/fragment_new_post.xml)/[событий](/app/src/main/res/layout/fragment_new_event.xml)

### [Список пользователей](/app/src/main/java/ru/netology/nework/fragment/UsersFragment.kt)

При добавлении пользователей в упоминания/спикеры открывается этот фрагмент([разметка](/app/src/main/res/layout/fragment_users.xml)). Для размещения пользователей ([разметка пользователя](/app/src/main/res/layout/layout_user.xml)) в нем используется [UserViewHolder](/app/src/main/java/ru/netology/nework/viewholder/UserViewHolder.kt). 

### [Дополнительные фрагменты](/app/src/main/java/ru/netology/nework/fragment/secondary/)

Также используются дополнительные фрагменты для:

* Открытия аудио файла
* Открытия видео файла
* Открытия изображения
* Открытия карты с геотегом (и также для выбора местоположения геотега при добавлении/редактировании его)
* Фрагмент логина
* Фрагмент регистрации

## Dependency Injection

Внедрение завимостей осуществляется через Dagger Hilt. Основные модули для работы DI находятся [здесь](/app/src/main/java/ru/netology/nework/di/).
