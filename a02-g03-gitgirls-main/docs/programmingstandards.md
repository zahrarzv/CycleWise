# FILE & PROJECT STRUCTURE

---

## NAMING CONVENTIONS
Packages
```
main/java
|- presentation
|- logic
|- persistence
|- models
|- application

```

Interfaces
`IInterfaceName` (Starts with `I`)

Main Application Files
- Placed under `src/main`

Testing Files
- Placed under `src/test`

Classes
- `PascalCase`

Variables
- `camelCase`

Methods
- `camelCase`

Constants
- `ALL_CAPS_WITH_UNDERSCORES`

Length
- Keep variable and method names within a reasonable word length

---

## FORMATTING & STYLE CONVENTIONS
Indentation, Line Length, Braces `{}`, Blank Lines
- Handled by auto-formatter

Comments
- Complex classes and methods use JavaDoc style
- Comments are placed on the line of code before the line they are for
- Use TODOs and FIXMEs appropriately

---

## ORGANIZATIONAL CONVENTIONS
Order of members of class, top to bottom:
```
constants
static variables
instance variables
constructor methods
private methods
public methods
```
- Instance variables are private


---

## HANDLING ERRORS
- Using custom exceptions, communicate unhappy paths with try/catch blocks

---


## VERSION CONTROL CONVENTIONS

### BRANCH RELATED CONVENTIONS

#### NAMING
Feature branches
`feat-featureNum/feature-name` or `feat-featreNum/feature-description`
- Feature branches use their own feature number as their number identifier.

Task branches
`task-issueNum/task-name` or `task-issueNum/task-description`
- Task branches use their GitLab Issue Number as an identifier, to link the specific devtask to the branch.

Miscellaneous branches
`misc-issueNum/name`
- Same reasoning as task branches.

####  STRATEGIES
Features
- One branch per feature

Dev Tasks
- Tasks with unassigned stories get branched directly off main

### COMMIT MESSAGE FORMAT
- Minimal description line, followed by a description on a newline, if any.

### FREQUENCIES

#### Merge requests
- Once merge request goes through main, all active feature branches must pull from main
 Should include description containing any important decisions made

#### Pushing local changes  
- Do often.

### APPROVALS
- MINIMUM one other developer must approve a PR before merging


---

## TESTING STANDARDS
Coverage    
- Minimum 80%
    - This means tests should cover 80% of the code written.

Databasing
- Use a test database for testing

### Naming
Classes 
- `NameOfClassTest` (Pascal case)

Methods
- `testDescription` (Camel case)

---

## MANAGING DEPENDENCIES
- Gradle

