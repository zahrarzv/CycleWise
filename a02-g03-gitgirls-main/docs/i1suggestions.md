# iteration 1 ideas

## iteration description 
>Android UI for at least one feature, model/business logic, unit tests, and a stubbed persistence interface

---

pLEASE WORK!!!!
## from sprint1 overview:

Process
In your GitLab project, do the following:

Create a milestone for Iteration 1, with the release date set to the due date of this iteration. - ALREADY DONE 

Assign the feature and user story issues you created for Iteration 0 to the Iteration 1 milestone. - DONE 

As a team, divide the work among yourselves. Once responsibilities are decided, assign each user story issue to the appropriate team member. - TO DO

If you did not complete developer tasks for your user stories in Iteration 0, add developer tasks to the user stories as a team. - TO DO 

As necessary, revise your plan from Iteration 0. Revising your plan during project execution is expected and encouraged. You will report on how and why your plan changed throughout the course. 

---

## FEATURE LIST
- F1 CYCLE CONDITIONS AND TRACKING
- F2 CYCLE PREDICTION
- F3 SEND REMINDERS AND NOTAFICATIONS
- F4 PRIVACY -> FOR NEXT ITERATION?

## GENERAL TASKS 
- CREATE ARCHITECTURE DIAGRAM + DOCUMENT - group works on together!
- CODING STANDARDS AGREEMENT - krissy started document
- ASSIGN FEATURES/USER STORIES TO GROUP MEMBERS - TO DO
- SET UP ANDROID STUDIO - TO DO (krissy will start importing skeleton of sample project)
- DECIDE WHICH FEATURE TO IMPLEMENT UI IN - TODO

## SUGGESTIONS FOR USER STORIES & THEIR DEVELOPER TASKS(?)
- F1U1 CYCLE AND SYMPTOMS TRACK
    - T1 USER INPUT PERIOD DATES INTO CALENDAR METHOD
    - T2 ASSIGN SYMPTOMS TO DATES IN CALENDAR
- F2U1 FERTILITY PREDICTION
    - T1 CALCULATE FERTILE DATES METHOD
- F2U2 PERIOD PREDICTION
    - T1 CALCULATE PERIOD CYCLE USING PREVIOUS DATES METHOD
    - T2 CALCULATE PERIOD CYCLE USING USER INPUT CYCLE LENGTH METHOD
    - T3 DISPLAY CALCULATIONS
- F3U1 INCOMING PERIOD REMINDER
    - CHECK FOR INCOMING PERIOD METHOD?
- F3U2 IRREGULAR PERIOD REMINDER
    - CHECK FOR MISSED PERIOD METHOD?
- F3U3 ADVANCE NOTAFICATION 
    - CHANGE FREQUENCY/EARLYNESS OF PERIOD REMINDER?IDK HWO TO IMPLEMENT THIS
- F4U1 APP LOCK SECURITY
    - .

---

FROM I1 PAGE:

# Documentation
Create an ARCHITECTURE.md file that describes the architecture of your application. This document should:

Identify the major packages and source code components in your repository

Describe how responsibilities are divided across layers (e.g., UI, logic, data)

Provide a high-level overview of how components interact

You may include architecture diagrams using tools such as asciiflow or Mermaid (which GitLab can render), or include an image file (.png, .jpg, .svg) if you prefer.

At the end of the iteration, move any incomplete feature or user story issues from the Iteration 1 milestone into a new milestone for Iteration 2. Any features or user stories remaining in the Iteration 1 milestone at the deadline will be assumed complete and will be assessed as such.

During development, record the time you actually spent on each user story in the corresponding GitLab issue. This data will help you assess the accuracy of your estimates later in the project

---

# programming standards suggestions

- Formatting and style
    - Spaces or Tabs???
    - On a previous project they talked about line length (100-120 characters)
    - How many blank lines before a new function (1 -3)
    - if else statements or switch case (I personally prefer if else :)

- Naming conventions
    - Folder and file name (What type of letter case are we using?) 
    - constants ALL_CAPS_WITH_UNDERSCORES or ALLCAPS
    - writing in full or abbreviations allowed
    - max number of words alloweds (1-5)

- Commenting practice
    - How are you commenting your functions/headers?
        - For classes are you making a brief summary and stating all the methods at the top and where your class can be used
        - For methods are using stating what each variable argument does like in COMP 1010/1020 as well as return value

- Code organization     
    (Just copy pasted from a previous project :\ )
    Order of class members
    - constants > static > instance > constructor > private methods > public methods
    - Instance variables will be private

<3

```java
//test
System.out.println("code");

void methodname(String argument){
    //inline brackets?
}
void methodname(String argument)
{
    //newline brackets?
}
//wow i love markdown! <3


```




