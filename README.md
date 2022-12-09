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
* View Binding
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
   * In this project, viewModelScope coroutine builders are used so that coroutine is tied to view model lifecycle and gets canceled when view model is cleared which helps in avoiding memory leaks.

* Domain Layer
  * This layer contains use cases, repository interface and repository implementation class.
  * Use case classess do not contain any android components. These are simple Kotlin classess which invoke methods in repository.
  * Repository gets data from data sources and provides to use cases.

* Data Layer
   * This layer is the data provider. Data fetched from remote is inserted into local database. When internet connection is available, remote API calls are made. In the absence of internet connection, data will be fetched from local database.
   * It contains Remote Data Source and Local Data Source interfaces and corresponding implementation classess.

###### Points
* To understand the status of API response, a generic sealed class - Resource is used which will return if status is Success, Loading or Error. This approach helps in handling exceptions from remote API calls in a better way. In repositories, before sending the data to use cases, we verify the API response status and return.
* Article entity class has Source object as a member. Since room doesn't allow object references in entities, type converter is used to convert Source object to source name which is a primitive type. Likewise for retrieving data from table, we convert source name to Source object adding dummy values in other fields.
* All network calls and local database calls are made from background threads using viewModelScope or liveData coroutiune builders.
* In recycler view when list data changes, with notifyDataSetChanged() it will not know which particular element has been modified. Hence it will load/refresh all the visible items again which is an inefficient way when data is huge. To avoid this, DiffUtil utility class has been used which calculates the difference between old and new lists and returns the difference. With AsyncListDiffer class (helper class for computing the difference between two lists via DiffUtil), difference will be calculated in background thread instead of main thread.
* ItemTouchHelper callback has been used in Favorites to detect and delete the swiped article.

###### Dependency Injection
* Any class that requires an object of another class is a dependent class and the other class is dependency. With tightly coupled dependencies it is difficult for testing, maintaining and expanding the code. Hence dependency injection is used for making dependencies loosely coupled.
* Dagger2 and Hilt are mostly used dependency injection frameworks.
* There are three major components in Dagger2:
    * Module: Provider of dependency. It provides objects to consumer.
    * Component: Facilitator that connects provider to consumer. It gets instances through the module for any class. 
    * Any class/Activity: Consumer of dependency. It expresses consumption using @Inject annotation. For any field or constructor that has @Inject annotation, component will look for corresponding dependencies in the modules defined in @Component and provide that dependency.
* Hilt is built on top of Dagger which generates most of the complex codes for us. We need not create Component interfaces. Hilt library has predefined components with various scopes defined for us. We only need to install our modules into those components with @InstallIn annotation in module classes.
* We annotate the application class with @HiltAndroidApp and activity class with @AndroidEntryPoint. @AndroidEntryPoint generates an individual Hilt component for each class annotated with it and these components are used to receive dependencies.
