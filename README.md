# Music Artist Information API 

REST API which provide clients with information about a specific music artist from MusicBrainz, Cover Art Archive and Wikipedia.

## Description

The API takes a MBID (MusicBrainz Identifier) and return a result containing the following:

- List of all albums the artist has released and links to its corresponding album cover art.

- Description of the artist fetched from Wikipedia profile of the artist.

## Running

1. Java 8 or higher and Maven 3 should be installed.

2. Change directory to the root folder of the application.

3. Run the below Maven command.

```bash
mvn spring-boot:run
```
4. To run test cases, run the below Maven command

```bash
mvn test
```

## Endpoints

Get Artist Info (/musify/music-artist/details/{mbid}) - HTTP GET

- Sample Request

```
http://localhost:8081/musify/music-artist/details/f27ec8db-af05-4f36-916e-3d57f91ecf5e
```

- Sample Response

```
{
  "mbid": "f27ec8db-af05-4f36-916e-3d57f91ecf5e",
  "name": "Michael Jackson",
  "gender": "Male",
  "country": "US",
  "disambiguation": "“King of Pop”",
  "description": "Michael Joseph Jackson was an American singer, songwriter, and dancer. Dubbed the \"King of Pop\", he is regarded as one of the most significant cultural figures of the 20th century. Over a four-decade career, his contributions to music, dance, and fashion, along with his publicized personal life, made him a global figure in popular culture. Jackson influenced artists across many music genres; through stage and video performances, he popularized complicated dance moves such as the moonwalk, to which he gave the name, as well as the robot. He is the most awarded individual music artist in history.",
  "albums": [
    {
      "id": "500d9b05-68c3-3535-86e3-cf685869efc0",
      "title": "Farewell My Summer Love",
      "imageUrl": "http://coverartarchive.org/release/8172928a-a6c7-4d7c-83c8-5db2a4575094/13404444760.jpg"
    },
    {
      "id": "37906983-1005-36fb-b8e7-3a04687e6f4f",
      "title": "Anthology",
      "imageUrl": "http://coverartarchive.org/release/a7a74484-8c25-47e3-9afc-7de701ad3dde/1619836290.jpg"
    }
  ]
}
```

## Technologies and Tools

1. Java 8

2. Spring boot Reactive - To support tens and thousands of requests per second

3. Redis Cache - To cache responses in order to not access 3rd party APIs for same Mbid fo 5 minutes (yet to be implemented)

3. Maven 3 - Build, test and manage dependencies of the application.

4. Junit 4 - Unit testing.

5. SonarLint - Code quality and coverage.

6. Apache JMeter - Load test and measure performance.

## Different MBIDs for testing

1. b8a7c51f-362c-4dcb-a259-bc6e0095f0a6 (Ed Sheeran).

2. dc99e6fd-c710-4f79-b74b-127b4d0b7849 (Labrinth).

3. 134e6410-6954-45d1-bd4a-0f2d2ad5471d (Zara Larsson).

4. 2f548675-008d-4332-876c-108b0c7ab9c5 (Sia).

5. 122d63fc-8671-43e4-9752-34e846d62a9c (Katy Perry).

6. f27ec8db-af05-4f36-916e-3d57f91ecf5e (Michael Jackson)

## Solution And Assumptions

Planned to use Reactive Spring boot as it is efficient in handling tens and thousands of requests per second. It works on the principle of event loop similar to Java Script. In this exercise I fetch the API responses from all the required APIs as Mono and then zip them together and make the final response DTO.

I am assuming that all the requests to 3rd party APIs are successful. At the moment when loads of requests are sent to the server they actually return "Too Many Requests error"

## Code files
The main logic of the code is in ArtistInformationServiceImpl and ArtistController.

## Short comings in current state
1. Currently the list of cover art is not getting populated. Few experiments around it are in "test" branch.
2. JSON Objects from API responses have been manipulated in raw form. Response DTOs can be created with the interested fields and rest fields can be ignored via Jackson Configuration.
3. Custom exception handling has to be done.

## Proposed Improvements
1. Adding a Cache Manager and integrating Spring boot caching to cache API responses for given MBID. This will reduce the number of API calls to external system and will also be faster

2. Add a custom exception and have a exception advice sending specific error codes from the APIs. For example, For invalid MBID, NOT FOUND error can be sent
