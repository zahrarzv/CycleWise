# Retrospective: Iteration 1

## What went well

- We established a strong foundation for the logic and persistence layers of the CycleWise app.

- A solid base for the UI was created, allowing core features to be demonstrated.

- Most features and user stories planned for Iteration 1 were completed, with remaining items moved to Iteration 2.

- The team worked together to resolve major blockers before submission.

## What did not go well

- Progress was not tracked consistently during the iteration.

- Work was integrated late, which caused merge conflicts and time pressure near the deadline.

- Version control practices were inconsistent, including unclear branch naming and messy commits.

## What changed from our iteration 0 plan

Our overall vision for the CycleWise app remained the same. However, implementation revealed several process and coordination issues that required adjustments.

After defining interfaces and method headers for the logic and persistence layers, we discovered that additional methods were necessary to support the actual functionality of features such as period logging and data storage. Besides that, we also realized some redundancies/ repeated code that need to be addressed going forward.

## Things to work on:

1. Version Control and Organization

    - Commits and merge requests were often messy and difficult to follow.

    - Branch naming was inconsistent, which caused confusion about where work belonged.

    - Some branches expanded beyond their original purpose, making it unclear which code was stable.

    - There were difficulties combining code due to merge conflicts and pulling issues.

    - In some cases, code had to be copied manually due to merge difficulties.

2. Workflow and Coordination

    - Coding standards were established but did not fully suit our design and workflow.

    - Changes were not always documented during development.

    - Lack of clear tracking made it harder to understand progress and responsibilities.

## How We Will Fix These Issues (Iteration 2 Plan)

1. Branching Strategy

    - Use a structured branching model:

        main -> stable version

        dev -> integration branch

        + Feature branches created from dev

    - All new work will branch from dev and merge back into dev.

    - Branch naming convention: layer/task (e.g., logic/period-log-validation, presentation/log-screen-ui)
        + where Layers include:
            1. Documentation
            2. Logic
            3. Persistence
            4. Objects
            5. Presentation

2. Commit Practices

    - Make smaller, more frequent commits to improve traceability.

    - Write clear commit messages describing what was changed and why.

    - Reference affected files, classes, or methods when possible.

    - Create new branches for newly discovered tasks instead of extending existing ones.

3. Integration Strategy

    - Integrate work earlier and more frequently to reduce last-minute conflicts.

    - Avoid delaying merges until the end of the iteration. Push stable changes sooner to reduce pressure near the deadline.

## What we learned

- Clear task ownership is essential for team efficiency.

- Regular check-ins help identify blockers early.

- Late integration increases risk, conflicts, and stress.

- Communication must occur throughout the iteration, not only near deadlines.

- Version control practices significantly affect team productivity.

## Action Items for Iteration 2

- Assign tasks clearly at the start of the iteration.

- Maintain a shared task board to track progress.

- Hold brief weekly meetings and async check-ins.

- Follow consistent branching and commit practices.

- Integrate code continuously and early in each meeting

- Ask for clarification sooner when unsure.