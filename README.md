# News-App
![Screenshot_20221208-230444_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526972-d4ed425d-bcc0-4907-8dac-62964fa79fe9.jpg)
![Screenshot_20221208-230458_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527604-d5fa414d-ae2f-45af-9872-b2559fd10685.jpg)
![Screenshot_20221208-231255_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527639-d6ff8ea3-5d00-459b-b9db-91cbff362b3a.jpg)
![Screenshot_20221208-231346_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527680-6e8ffebd-3616-4ca2-87b0-41bb28f3ca6c.jpg)
![Screenshot_20221208-231405_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527713-bec0762a-52e2-4db0-8ec5-3e9951c4d138.jpg)
![Screenshot_20221208-231413_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527741-9ccf65d3-c10f-433f-b03e-9a44b6445279.jpg)
![Screenshot_20221208-231421_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527762-f3bf8ad0-e011-480f-869a-7fec71581096.jpg)
![Screenshot_20221208-231425_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206527787-1c5de451-fe45-4e17-8523-f65c47727078.jpg)

## Overview
* Helps users to discover latest trending stories/news from various news sources. 
* Fetches data from [NewsAPI](https://newsapi.org/). 
* Follows MVVM with clean architecture.
* Users can view news, search headlines, save news in local, view saved news and delete saved news.

## Libraries used
* Room Library
* Retrofit
* Kotlin Coroutines
* View Model
* Live Data
* Data Binding
* Recycler View with DiffUtil
* Navigation Architecture Component
* Glide
* Hilt
* Search View

## Deep Dive
###### MVVM Clean Architecture
![MVVM Clean Architecture](https://user-images.githubusercontent.com/39825424/206482851-5dac75fe-1bbc-4f4b-ac48-a6ee51614ee7.jpg)

* Presentation Layer
   * When user interacts via activity/fragment, views start observing the required live data emitted by view models.
   * View models expose data to views. These are not aware of the consumers of data. Their primary responsibility is to emit live data.
   * In this project, viewModelScope coroutine builders are used so that coroutine is tied to view model lifecycle and gets canceled if view model is cleared which helps in avoiding memory leaks.

* Domain Layer
  * This layer contains use cases, repository interface and repository implementation class.
  * Use case classess do not contain any android components. These are simple Kotlin classess which invoke methods in repository.
  * Repository gets data from data sources and provides to use cases.

* Data Layer
   * This layer is the data provider. Data fetched from remote is inserted into local database. When internet connection is available, remote API calls are made. In the absence of internet connection, data will be fetched from local database.
   * It contains Remote Data Source and Local Data Source interfaces and corresponding implementation classess.
   * To understand the status of API response, sealed class - Resource is used which specifies if status is Success, Loading or Error. This approach helps in handling exceptions caused from remote API calls in a better way. In repositories, before sending the data to use cases, we verify and return the API response status. If there is an error, corresponding error message is sent.


  
