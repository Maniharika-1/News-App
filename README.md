# News-App
## Overview
* Helps users to discover latest trending stories/news from various news sources. 
* Fetches data from [NewsAPI](https://newsapi.org/). 
* Follows MVVM with clean architecture.
* Users can view news, search headlines, save news in local, view saved news and delete saved news.

![Screenshot_20221208-230444_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206525974-b30a56a9-f831-427e-a042-513d75ae6f69.jpg)
![Screenshot_20221208-230458_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526071-23de0cb5-d158-4a32-bf4b-2208ef548549.jpg)
![Screenshot_20221208-231255_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526136-72b9825c-cce8-4cba-a3fb-3bd286173ef5.jpg)
![Screenshot_20221208-231346_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526184-0d64a893-f7e2-4e09-8821-69ce7eacf733.jpg)
![Screenshot_20221208-231405_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526214-fec01175-0126-48b9-8c45-41a340c731aa.jpg)
![Screenshot_20221208-231413_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526246-2510983b-6435-4d92-b9fb-37a08182dcd8.jpg)
![Screenshot_20221208-231421_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526281-bdfad178-cec3-4096-94e4-5a8739b444ee.jpg)
![Screenshot_20221208-231425_NewsAPIClient](https://user-images.githubusercontent.com/39825424/206526333-e77b9391-ee81-418d-ad31-e31d8c1ecc84.jpg)


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


  
