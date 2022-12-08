# News-App
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


```mermaid
graph TD;
  UI (Activity/Fragment)-->View Model;
  View Model-->Use Case;
  Use Case-->Repository;
  Repository-->Remote Data Source;
  Repository--> Local Data Source;
```
  
