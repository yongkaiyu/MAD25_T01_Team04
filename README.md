##Disclaimer
- This is a student assignment project for the Kotlin App
Development module at Ngee Ann Polytechnic. Developed for
educational purposes.

Collaborators
- Yong Kai Yu : S10258484 (GitHub: yongkaiyu)
- Tabio : S10258652 (GitHub: ITAccadh)
- Sarrinah : S10241803 (GitHub: srrnh)
- Jian Hui : S10257708 (GitHub: yonaginn)

Introduction
- This project is a mobile application prototype which is a personalized micro-trailer movie discovery app.
- The app assists users in discovering movies through engagement via short movie clips, displayed through a swipe-based interface inspired by modern social media content consumption.
- The app aims to reduce decision fatigue and help users discover more movies efficiently

Motivation/Objective

- Many users using streaming services like Netflix experience from choice fatigue and spend most of their time scrolling through large catalogs. Trailers are often long, and recommendations could feel repetitive.

Our objective is to create a mobile application that
- Helps users discover movies quickly and visually
- Uses short movie clips to give them a taste of what to expect
- Reduces the time on deciding what to watch
- Provides targeted personal movie suggestions
- Offers an intuitive interface with swipe-based interactions

- The Phase 1 prototype is aimed on displaying the basic features required to support the concept.

App Category

- Entertainment
- Movie Recommendation
- Media Streaming Support Tool
- Video Discovery

Declaration of LLM

- This project utilised the LLM of OpenAI ChatGPT, to assist with
- Brainstorming ideas and refining core concept
- Drafting of user research summaries
- Structuring documentation
- Clarifying Android development concepts
- Assisting in explanations regarding code logic

Tasks and features allocation of each member in team for Stage 1
- Research on what app to do for Stage 1 (Kai Yu)
- Creating and making of wireframes (Kai Yu)
- Conducting of user testing (Kai Yu)
- Login Page (Kai Yu)
- Login Database Implementation using Firebase(Kai Yu)
- Navigation UI Implementation (Kai Yu)
- Movie Shorts Player Discovery Feature on Home Screen (Kai Yu)
- Movie Database Implementation (Kai Yu)
- Horizontal List Trending Implementation (Kai Yu)
- Details Page Implementation (Kai Yu)
- Profile Page (Nagu)

Planned Tasks and Feature Allocation for each member in Stage 2
- Review Page and Functions (Kai Yu)
- Delete comment
- Comment under the content page for each movie
- Comment top comments when there are no movie selected
- Daily Mystery Movie

Application Description
- CineXplorer is a discovery mobile application targeted towards movies. CineXplorer through its innovative features helps
users discover more targeted movies and reduce time on choosing what movie to watch. 
- One of CineXplorer's features helps quick discover movies through watching movie trailers in a Instagram/TikTok scrolling format.

Roles & Contributions of each member for Stage 1
- Research on what app to do for stage 1 (Kai Yu)
- Creating and making of wireframes (Kai Yu)
- Conducting of user testing (Kai Yu)
- Login Page (Kai Yu)
- Login Database Implementation using Firebase(Kai Yu)
- Navigation UI Implementation (Kai Yu)
- Movie Shorts Player Discovery Feature on Home Screen (Kai Yu)
- Movie Database Implementation (Kai Yu)
- Horizontal List Trending Implementation (Kai Yu)
- Details Page Implementation (Kai Yu)
- Profile Page (Nagu)

Research Questions

- Help to explore many domains before narrowing it down.

Daily Life & Pain Points

- What mobile applications do users spend the most time on?

- What are the most frustrating daily digital tasks?

- What causes users to feel overwhelmed, lost, or unproductive?

Usage

- What apps do users use daily or cannot live without?

- What apps did wish existed?

- What problems are not solved well by current mobile applications?

Research Method

- User Interviews (8 pax)

- Online Survey (20-30 responses)

- Competitor Scan (Instagram, CapCut, Netflix, Google, WhatsApp, Shopee )

![img.png](img.png)

Observed Behaviours

- Most users browse before watching

- Users are overwhelmed with options and abandon choices

- Short form video consumption are preferred

- Many prefer quick decisions over long browsing sessions

Interview Responses

- I think the YouTube and TikTok short clips help me decide faster. (P4)

- Trailers are long. I think quick previews of what I am shown would be nicer to have. (P2)

- I don’t think the content given to me is for me. (P5)

- I always finish my food before I even chose a movie to watch. (P1)

- I always take a lot of time choosing what I watch. (P8)

- I would love to have a movie to watch immediately with my family. (P3)

Survey Findings (24 responses)

How long do you spend deciding what to watch?

- 62%: 10 minutes to 25 minutes

Do you feel overwhelmed by too many choices?

- 75%: Yes

Do current recommendations feel relevant?

- 58% : No

Would short previews help decide faster?

- 79% : Yes

Do you watch TikTok short style clips?

- 2% : Frequently

Do you finish full trailers?

- 37% : Rarely 

Findings

- Majority suffers decision fatigue

- High Preference for short style video

- Users want faster discovery, not more content

- Recommendation systems are repetitive 

Competitive Observations

- Netflix “Too much scrolling”, “repetitive suggestions”

- YouTube: “Not organized for movies”

- TikTok: “Great for fast reviews”, “not made to watch movies” 

Insights from data

- People send significant time relaxing using digital entertainment.

- Entertainment is consistently one of the most used categories

- Users frequently complain about having difficulties on choosing what to watch 

Consolidated Insights

Insight 1 – Decision Fatigue

- Users waste time choosing what to watch and feel overwhelmed by endless options

Insight 2 – Long Trailers Are Not Effective

- Users prefer like short highlight clips which are the “juicy” parts than trailers

Insight 3 – Irrelevant Recommendations

- Users dont feel suggestions are to their tastes; want personalization

Insight 4 – Short Attention Span

- Short-form content (10-30 sec clips) strongly influences their decision

Insight 5 – Users Want Quick, Confident Choices

- They prefer simple, fast browsing over complex menus 

![img_1.png](img_1.png)

![img_8.png](img_8.png)

![img_3.png](img_3.png)

![img_4.png](img_4.png)

![img_10.png](img_10.png)

![img_6.png](img_6.png)

Ideas:

- Movie Discovery through Instagram Reels style scrolling

- Mood-Based Movie Picker

- Smart Playlists for Movies

- Social Movie Sharing Cards

- Eliminating categories Mode

- Swipe-to-Discover Interface

- Weekly Movie Digest 

![img_11.png](img_11.png)

![img_12.png](img_12.png)

3 Iterations & Improvements

Iteration 1 – Issues Found

- Database movie data takes a while to load

Fixes

- Used Room as local caching combined with Firestore database

Passing Criteria:

- 80% of users should complete tasks without assistance

Iteration 2 – Issues Found

- Color Scheme for Login page not fitting of theme

Fixes

- Updated color scheme to better suit page

Passing Criteria:

- 90% of users should approve color scheme

Iteration 3 – Issues Found

- Users want clearer icons

Fixes

- Added clearer icons to better reflect options

Passing Criteria

- All users approve the new icons 

Wireframes

![img_13.png](img_13.png)

![img_14.png](img_14.png)

![img_15.png](img_15.png)

![img_16.png](img_16.png)

Stage 2:

Feature overview (Comment & Rating System) - Yong Kai Yu

- Movie Selector Dropdown -> Users are able to choose which movie to comment on
- Comment Input Box -> Users are able to input their comment
- Comment Edit and Delete Functionality -> Users are able to edit and delete their own comment
- Clickable Star Rating Selector -> Users are able to rate the movie using a clickable interface of 5 stars. Users can choose up to 5 stars to indicate their rating 
- Comment Display -> Users are able to view comments from respective movies either from the Review Screen or under the detail page of the movie
- Firestore datastore + Room as local cache -> comment and rating data are stored in firestore, data changes are automatically synchronized with Room and UI
- Delete Function Confirmation Dialog -> Pop-Up for verification will appear to avoid accidentally deletion

User Research for addressing core user needs

    - Research Goal
        The goal of the user research is to evaluate the usability, clarity, and usefulness of the current comment feature within the app, and to identify any pain points. 
        It aims to validate whether the feature engages the user while being accessible.
    - Methodology
        Testing: Usability Testing (9 pax)
        Test Outline:
            1. Choose a movie and submit a comment with a rating
            2. View existing ratings and comments
            3. Edit and delete their own comment
            4. Interpret average movie ratings
            5. Attempt to find another way to submit a comment
        
    - Competitor Research 

        Competitor: YouTube
        Insights: Has comment system, but rating system is based on likes and dislikes,shows number of likes but not dislikes.

        Competitor: Netflix
        Insights: No dedicated comment system, rating system is based on 3 options (Love this!, I like this, Not for me)
        
        Competitor: Disney+
        Insights: No dedicated comment system, rating system is based on like and dislike system. Does not show the number of likes and dislikes
        
        Competitor: IMDb
        Insights: No dedicated comment system, rating system is a number based rating system of out of 10. Shows the bar chart of total users of each rating.

        Solution is completely unique with its dedicated comment system integrated with its own rating system.

    - Key Insights from Testing
        
        1. Ratings were more valuable than comments alone
            
            Observation: Most participants suggested that the comments without the rating system originally felt incomplete and lacked personality
            
            User Feedback:
                - I feel that the comment I entered is insignificant, people are not able to really tell how I rate this movie
                - There is something missing when I see my comment, it lacks sentiment

            Design Change:
                - Added a 5 star rating system
                - Displayed average rating for each movie, numerically and visually
                - Mandatory to have a rating when commenting
            
            User Need Addressed: Comments felt incomplete and lacked personality 

        2. Users expect functionality over their content
            
            Observation: Users feedback that they did not like that they were originally not able to edit or delete their own comment
            
            User Feedback:
                - Not being able to change my comment is something I disapprove, now the next time I feel like changing, I would have to put a new comment
                - I should be able to delete my own comment
            
            Design Change:
                - Implemented edit and delete functionality for their own posted comments

            User Need Addressed: Comments lacked customization

        3. Users do not want accidential actions
            
            Observation: Users felt that there was no verification when deleting their own comment and comments could be submitted with no rating or comment
            
            User Feedback:
                - I worry that I might be careless and delete my comment by accident since it only requires one click
                - Deleting comments should be double-checked
                - I should not be able to submit my comment with no rating

            Design Change:
                - Integrated confirmation dialog for deletion of comment
                - Comments require at least one star now and the comment input to not be empty

            User Need Addressed: Comments required a second layer for confirmation

Architecture Diagram

    ┌──────────────────────────────┐
    │          UI Layer            │        (View)
    │  Jetpack Compose Screens     │
    └──────────────▲───────────────┘
                   │  State (Flow / LiveData)
                   │
    ┌──────────────┴───────────────┐
    │        ViewModel Layer       │        (Controller)
    │       CommentViewModel       │                ┌──────────────────────────────┐
    │                              │                │      ViewModel Factory       │
    │     Handles UI state,        │<───────────────│   CommentViewModelFactory    │
    │       validation             │                │                              │
    │  and user ownership logic    │                │ Enables constructor argument │ 
    └──────────────▲───────────────┘                └──────────────────────────────┘
                   │
                   │   Repository abstraction
                   │
    ┌──────────────┴───────────────┐
    │       Repository Layer       │
    │      CommentRepository       │
    │                              │        (Model)
    │  Single source of truth      │
    │           logic              │
    └───────▲──────────────────▲───┘
            │                  │
            │                  │
    ┌───────┴────────┐   ┌─────┴───────────────┐
    │   Room (Local) │   │ Firebase Firestore  │
    │  CommentDao    │   │  Cloud Database     │
    │                │   │  comment collection │         (Database)
    │                │   │                     │
    │                │   │                     │
    │                │   │                     │
    └────────────────┘   └─────────────────────┘


Data Model Explanation

    @Entity(tableName = "comments")
    data class CommentEntity(
        @PrimaryKey val id: String,          // Firestore auto ID
        val userId: String,
        val userName: String,
        val movieId: String,
        val movieName: String,
        val comment: String,
        val rating: Int,
        val timestamp: Long
    )

- id: unique identifier
- userId: to indicate who the comment belongs to
- userName: much more accessible to retrieve for display
- movieId: to indicate which movie the comment belongs to
- comment: the comment message
- rating: the rating given by user
- timestamp: to save the time it was created or modified

Firebase + Room sync logic

1. Read Flow (Firebase -> Room -> UI)

        Firebase Snapshot Listener
                ↓
        Repository maps documents
                ↓
        Room insert/update
                ↓
        Room Flow emits changes
                ↓
        ViewModel collects
                ↓
        Compose recomposes UI

    It avoids UI flickering, supports configuration changes safely and UI remains responsive

2. Write Flow (UI -> Firebase -> Room)

    Add / Update Comment

        User Action
            ↓
        ViewModel validation
            ↓
        Repository writes to Firebase
            ↓
        On success → write to Room
            ↓
        UI auto-updates from Room

    Delete Comment
        
        Delete Confirmation
            ↓
        Firebase delete
            ↓
        Room delete
            ↓
        UI updates instantly


User Guide (how to comment, edit, delete)

    1. How to add a comment on the Review Page
        1. Under Home Page, click Review under the navigation UI bar
        2. Click the Select a Movie clickable, a dropdown of the movies should be displayed
        3. Choose a movie you would like to comment on
        4. Choose your rating by clicking on the clickable stars
        5. Click on the comment input box and input your comment
        6. Once you have a rating and a comment, a button labelled "Submit Comment" appears
        7. Click on the "Submit Comment" button once you are done
        8. Your comment should automatically appear under the Comments section
    2. How to add a comment on the Detail Page for a selected movie
        1. Under Home Page, click Movies under the navigation UI bar
        2. Click on the thumbnail of the movie you would like to comment on
        3. Choose your rating by clicking on the clickable stars
        4. Click on the comment input box and input your comment
        5. Once you have a rating and a comment, a button labelled "Post" appears
        6. Click on the "Post" button once you are done
        7. Your comment should automatically appear under the Comments section
    3. How to edit a comment on the Review Page
        1. Under Home Page, click Review under the navigation UI bar
        2. Click the Select a Movie clickable, a dropdown of the movies should be displayed
        3. Navigate to the Comments Section
        4. Find your comment that you would want to edit (should have a red trash bin icon)
        5. Click on the area where the comment is displayed
        6. Change your rating by clicking on the clickable stars
        7. Change your comment by clicking on the comment input box
        8. Click "Cancel" if you do not want to change your comment
        9. Click "Save" if you want to save your changes
        10. Your comment should automatically update itself under the Comments section
    4. How to edit a comment on the Detail Page for a selected movie
        1. Under Home Page, click Movies under the navigation UI bar
        2. Click on the thumbnail of the movie you would like to comment on
        3. Navigate to the Comments Section
        4. Find your comment that you would want to edit (should have a red trash bin icon)
        5. Click on the area where the comment is displayed
        6. Change your rating by clicking on the clickable stars
        7. Change your comment by clicking on the comment input box
        8. Click "Cancel" if you do not want to change your comment
        9. Click "Save" if you want to save your changes
        10. Your comment should automatically update itself under the Comments section
    5. How to delete a comment on the Review Page
        1. Under Home Page, click Review under the navigation UI bar
        2. Click the Select a Movie clickable, a dropdown of the movies should be displayed
        3. Navigate to the Comments Section
        4. Find your comment that you would want to delete (should have a red trash bin icon)
        5. Click one the red trash bin icon
        6. A pop-up verifying your action should appear
        7. Click Cancel if you do not want to delete the comment
        8. Click Delete to confirm the deletion of your comment
        9. Your comment should be not be visible under the comments section if you had chosen to delete
    6. How to delete a comment on the Detail Page for a selected movie
        1. Under Home Page, click Movies under the navigation UI bar
        2. Click on the thumbnail of the movie you would like to comment on
        3. Navigate to the Comments Section
        4. Find your comment that you would want to delete (should have a red trash bin icon)
        5. Click one the red trash bin icon
        6. A pop-up verifying your action should appear
        7. Click Cancel if you do not want to delete the comment
        8. Click Delete to confirm the deletion of your comment
        9. Your comment should be not be visible under the comments section if you had chosen to delete

Accessibility considerations

    - User Insight: It would be convenient to be able to read comments once I have learnt more about the movie.
    - Resolution: User is now able to view, add, edit and delete comments under the movie detail page

    - User Insight: It would be great if there was a double-check before deleting a comment, I might accidentally misclick on the button
    - Resolution: Whenever a comment is deleted, a pop-up appears to verify their selection before deleting the comment.

    - Diverse User Need Addressed: Dark-themed UI with high contrast text and icons support users with low vision.
    - Diverse User Need Addressed: Critical actions (submit,delete,and edit) use color with iconography instead of just color, preventing reliance on color distinctions.