# News-App
![Screenshot_20221208-230444_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529539-365d6046-e34e-4d7e-a667-0003b35b3950.jpg)
![Screenshot_20221208-230458_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529590-c0422f36-a414-4eae-a437-a1e2cbdeb2d8.jpg)
![Screenshot_20221208-231255_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529610-318bfacb-75d8-42c1-b8e0-d9e7f739ab1a.jpg)
![Screenshot_20221208-231346_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529637-b1e07b18-0bc3-404f-acd3-bdba01ea33a1.jpg)
![Screenshot_20221208-231405_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529686-4ba8514b-24ba-44cc-96c4-73eecb4f02cf.jpg)
![Screenshot_20221208-231413_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529722-105e715f-c17d-478d-9f85-adc6b9834dc5.jpg)
![Screenshot_20221208-231421_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529745-5abd2dcf-9736-4ae1-9bb0-c29319893d4b.jpg)
![Screenshot_20221208-231425_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206529779-38750b87-1abe-48c1-bf23-7645736979ad.jpg)

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


  
