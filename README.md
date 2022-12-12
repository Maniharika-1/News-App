# News-App
![Screenshot_20221212-152803_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207020868-d3a0e2ad-0dc2-46b8-b0c6-41b356a7b72f.jpg)
![Screenshot_20221212-152927_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207021011-9093d22b-9eb3-4bb8-be58-8778ab6f0a1f.jpg)
![Screenshot_20221212-153036_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207021033-51e51415-18c2-4b81-9686-913ee0dede78.jpg)
![Screenshot_20221212-152853_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207021063-5431c52c-ae7b-4922-8a6e-6b3a13b3d0d6.jpg)
![Screenshot_20221212-152904_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207021090-2c14b582-846f-4cc9-b8d8-b1e3a64982ea.jpg)
![Screenshot_20221212-152912_NewsAPIClient](https://user-images.githubusercontent.com/39825424/207021117-a24e1992-a83b-4cd4-9485-211438487d93.jpg)

## Overview
* Helps users to discover latest trending stories/news from various news sources. 
* Fetches data from [NewsAPI](https://newsapi.org/). 
* Follows MVVM with clean architecture.
* Users can view news, search headlines, save news in local, view saved news and delete saved news.

## Libraries used
* Room
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
* Article entity class has Source object as a member. Since room doesn't allow object references in entities, type converter is used to convert Source object to source name which is a primitive type. Likewise for retrieving data from table, source name is converted to Source object adding dummy values in other fields.
* All network calls and local database calls are made from background threads using viewModelScope or liveData coroutiune builders.
* In recycler view when list data changes, with notifyDataSetChanged() it will not know which particular element has been modified. Hence it will load/refresh all the visible items again which is an inefficient way when data is huge. To avoid this, DiffUtil utility class has been used which calculates the difference between old and new lists and returns the difference. With AsyncListDiffer class (helper class for computing the difference between two lists via DiffUtil), difference will be calculated in background thread instead of main thread.
* ItemTouchHelper callback has been used in Favorites to detect and delete the swiped article.

###### Dependency Injection
* Any class that requires an object of another class is a dependent class and the other class is a dependency. With tightly coupled dependencies it is difficult for testing, maintaining and expanding the code. Hence dependency injection is used for making dependencies loosely coupled.
* Dagger2 and Hilt are most used dependency injection frameworks.
* There are three major components in Dagger2:
    * Module: Provider of dependency. It provides objects to consumer.
    * Component: Facilitator that connects provider to consumer. It gets instances through the module for any class. 
    * Any class/Activity: Consumer of dependency. It expresses consumption using @Inject annotation. For any field or constructor that has @Inject annotation, component will look for corresponding dependencies in the modules defined in @Component and provide that dependency.
* Hilt is built on top of Dagger which generates most of the complex codes for us. We need not create Component interfaces. Hilt library has predefined components with various scopes defined. We only need to install our modules into those components with @InstallIn annotation in module classes.
* We annotate the application class with @HiltAndroidApp and activity class with @AndroidEntryPoint. @AndroidEntryPoint generates an individual Hilt component for each class annotated with it and these components are used to receive dependencies.
* In this project- B
  * Below modules are created to provide dependencies:
    * NetModule: Provides Retrofit and API service instance.
    * DatabaseModule: Provides room database instance and DAO instance.
    * RemoteDataModule: Provides NewsRemoteDataSource dependency by returning RemoteDataModuleImpl instance.
    * LocalDataModule: Provides NewsLocalDataSource dependency by returning LocalDataModuleImpl instance.
    * RepositoryModule: Provides NewsRepository dependency by returning NewsRepositoryImpl instance.
    * UseCaseModule: Provides all use case instances.
    * AdapterModule: Provides all adapter instances.
    * FactoryModule: Provides NewsViewModelFactory instance.
  * All the modules are installed in SingletonComponent class provided by Hilt.
  * To avoid creating multiple instances of the object and to create a single instance of the object through the entire application lifecycle, all provider methods in      Modules are annotated with @Singleton.
   
