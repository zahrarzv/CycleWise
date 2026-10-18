# CycleWise App Architecture

## Overview

This project uses a layered architecture with three core tiers: Presentation, Logic, and Persistence. This structure is supported by shared domain Models, custom Exceptions, and Application composition class. 

Each layer is decoupled to ensure that user interface, business rules, and data storage mechanisms can be developed, tested, and maintained independently.


### 1. Presentation Layer: 

**Responsibility:** Manages Android framework and user interactions. Activities capture user input and display data returned from the business layer.

* FlowDisplayMapper: Converts internal Flow enum values used in the logic layer into user-friendly display text for the UI (e.g., LIGHT -> "Light").

* LoginActivity: Provides the login screen where users enter their username and password.

* PeriodLogActivity: Main interface for creating new period logs. Users can select start and end dates, choose flow intensity, record symptoms, and add notes.

* PeriodLogAdapter: A RecyclerView adapter responsible for transforming PeriodLog domain objects into UI elements. It formats log data for display and handles delete button interactions for each entry.

* PredictionActivity: Displays the predicted next cycle start date based on the user’s saved period logs.

* RegisterActivity: Handles the user registration screen by collecting input and passing registration requests to the appropriate logic layer.


* SavedLogsActivity: Displays all period logs belonging to the currently logged-in user. Allow user to sort logs by start date and delete individual logs.

* SymptomDisplayMapper

* NotificationHelper: Creates and sends period reminder notifications by creating a channel on app initialization.

* NotificationMapper: Manages scheduling of period reminder alarms via AlarmManager on Android.

* NotificationReceiver: BroadcastReceiver that responds to scheduled alarms and determines if reminder should fire.

* NotificationSettingsActivity: UI for configuring notification preferences. Provides toggle to enable/disable notifications, input for days before period threshold, and time picker for notificaation time. Saves settings through SchedulerService.

* PhaseCalendarActivity: Displays a monthly calendar showing menstrual cycle phases. Highlights current day, ongoing periods, and shows current phase label at the top.

* PhaseCalendarAdapter: RecyclerView adapter for the calendar grid.

* PhasePredictionWiring: Factory class that builds PhaseCalculator instances for the phase calendar.


### 2. Logic/Business Layer: 

**Responsibility:** Contains the business rules of the application and operates independently of Android UI components. This layer validates user input, processes period logs, and calculates cycle predictions.

* periodlog: Core business logic for creating and managing period logs.

    * PeriodLogManager: Contains the core business rules for period logs. It validates logs and coordinates add, retrieve, update, and delete operations through the PeriodLogPersistence interface.

    * PeriodLogMessages: Standardizes messages relating to periodlog creation, updating, and deletion.

    * PeriodLogService: Application-facing service used by the UI. It manages the logged-in user context and delegates period log operations to PeriodLogManager.

    * PeriodLogValidator: Validates PeriodLog data before it is passed to persistence. It checks required fields and enforces rules such as valid dates, non-null flow, and note length limits (e.g., end dates cannot be before start dates, start dates cannot be null).

    
* cycleprediction: Coordinates cycle prediction logic by analyzing previously logged cycles and calculating the expected next cycle start date.

    * CycleConstants: Stores constants used in cycle prediction calculations, such as default cycle lengths.

    * CycleConversionService: Converts PeriodLog objects into Cycle objects that can be used by prediction algorithms. It filters invalid logs and ensures only valid cycles are used for prediction calculations.

    * CycleHistory: List to ensure cycle data is valid, sorted, and ready for analysis for PeriodCalculator.

    * CyclePredictionMessages: Standardizes languages across errors relating to cycle prediction.

    * PeriodCalculator: Calculates the predicted next cycle start date based on historical cycle lengths.


* userlogin: Handles user login and registration logic.

    * AuthMessages: Standardizes user feedback messages to ensure messages relating to login are identical across app.

    * AuthResult: Encapsulates the result of authentication operations, including success or failure messages.

    * AuthService: Validates registration input, performs authentication checks for user account creation and login verification.

    * UserService: Provides user-related operations such as retrieving user accounts and accessing user data required by other services.


* notifications

    * NotificationScheduler: Bridges cycle prediction logic and the notification system to be able to determine specific calendar date for a notification based on predicted health data and user defined preferences.

    * NotificationValidator: Validates data for NotificationScheduler.

    * SchedulerConstants: Standardizes and centralizes static values for notifications.

    * SchedulerService: Coordinates between the persistence layer and the logic layer to schedule notifications.

* phaseprediction

    * PhaseCalculator: Determines which stage of the menstrual cycle a user is on any given day by translating mathematical date differences into states.

    * PhaseCalculatorMessages: Standardizes messages related to phase calculation errors.

    * PhaseConstants: Stores important numbers that represent typical length of menstrual phases.

### 3. Persistence Layer: 

**Responsibility:** Manages data storage and retrieval. The persistence layer abstracts database operations through interfaces so that different implementations (stub or real database) can be used interchangeably.

* PeriodLogPersistence: The interface defining all required database operations (Add, Get, Update, Delete).

* UserPersistence: Defines the operations required for managing user accounts in storage.

* **stub/PeriodLogPersistenceStub:** This stub stores period logs in-memory while the application is running. Because it does not use persistent storage, all data resets to its default state when the app restarts.

* **real:** These classes implement the persistence interfaces and store data in an SQLite database using DatabaseHelper.

    * periodlogrepository

        - CycleMapper: Manages the unique identification and storage of menstrual cycles in the SQLite persistence layer

        - PeriodLogMapper: Maps PeriodLog data between the SQLite database and model objects. It converts Cursor data into PeriodLog objects and prepares ContentValues for insert and update operations, including related cycle and symptom data.

        - PeriodLogPersistenceReal: Handles database CRUD operations for period logs

        - PeriodLogQueries: Stores SQL query strings used for period log operations

        - SymptomMapper: Manages the relationship between period logs and symptoms in the SQLite database.

    * userrepository

        - UserPersistenceReal: Handles SQLite persistence for user accounts, including inserting new users, retrieving users by username, updating passwords, and deleting user records.

    * notificationrepository

        - NotificationSettingsPersistenceReal: Handles SQLite persistence for managing user notification preferences within persistence layer.

    * CycleWiseSchema: Defines database table structures and column names

    * DatabaseHelper: Manages the SQLite database connection and schema creation

    * SeedData: Provides optional initial data for development and testing

### Application Layer:

**Responsibility:** Acts as the composition root of the application. It initializes shared services and provides global access to them throughout the app.

* PeriodTracker: Acts as the composition root of the application. It initializes the database helper, configures persistence implementations, creates application services, and stores the currently logged-in user.

* ApplicationMessages: Handles global error messaging across the application.


### Domain Models: Core data objects shared across all layers.

* Cycle
* CycleDate
* Flow
* ModelMessages
* NotificationSettings
* PeriodLog
* Phases
* SymptomConstants
* Symptoms
* User

### Custom Exceptions: 

**Responsibility:** Application-specific error types used to signal validation or logic failures across layers.

* PeriodLogException
* InvalidPeriodLogException
* ValidationException
* InvalidCycleDateException
* NotEnabledException
* PhaseException


## Component Interactions

The application follows a request-response flow across layers.

Dependencies flow only downward between layers: the Presentation layer depends on the Logic layer, and the Logic layer depends on the Persistence layer. Persistence does not depend on higher layers.

1. **Request Phase (UI -> Logic)**

When a user interacts with the Presentation Layer, the Activity sends a request to the Logic layer through service classes such as PeriodLogService or AuthService.

2. **Processing Phase (Logic -> Persistence)**

The Logic layer validates user input using validator classes. If validation passes, the service layer calls persistence interfaces to retrieve or modify stored data.

3. **Data Retrieval Phase (Persistence)**

The Persistence layer retrieves or updates data in the SQLite database. Database records are converted into domain model objects.

4. **Response Phase (Persistence to UI)**

Data is packaged into the domain objects and are returned to the Presentation Layer. Adapters such as PeriodLogAdapter transform these objects into visual UI elements for display.

5. **Error Handling:** If validation or database errors occur, custom exceptions are thrown and the UI layer displays appropriate error messages to the user.

### Example Interaction: Deleting a Period Log

**Step 1** - UI/Presentation Layer
The user taps the Delete button on a period log entry. PeriodLogAdapter detects the click and calls the delete callback provided by SavedLogsActivity.

**Step 2** - Logic Layer
SavedLogsActivity calls PeriodLogService.deleteLog(logId) to remove the selected log.

**Step 3** - Persistence Layer
PeriodLogService delegates the request to PeriodLogManager, which applies business rules and calls PeriodLogPersistenceReal to delete the log from the database.

**Step 4** - UI Update
After deletion, the updated log list is retrieved and displayed again in the RecyclerView.


## Diagram

![CycleWise Architecture Diagram](ARCHITECTURE_DIAGRAM.png)

