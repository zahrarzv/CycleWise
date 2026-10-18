# RETROSPECTIVE: Iteration 3

## Group 3 - CycleWise app

### Iteration Reflection and Improvement

One meaningful weakness we identified in Iterations 1 and 2 was ineffective integration practices, particularly late merging and inconsistent branching. This resulted in frequent merge conflicts, overwritten features, and unstable builds on the dev branch.

- We often worked in isolation for extended periods and only merged close to deadlines. As a result, conflicts frequently occurred in shared layers such as logic and persistence, and even project configuration files (e.g., .idea). 

- In some cases, completed features, such as the delete functionality, were unintentionally overwritten during merges. This created avoidable rework and added pressure near submission deadlines.

- The lack of branching strategy introduced a lot of branches after releasing in two previous iterations.

More importantly, this issue reflected a deeper problem: our workflow was reactive rather than coordinated, making it difficult to maintain a stable and collaborative codebase.

### Corrective Actions Taken

In Iteration 3, we made deliberate changes to both our workflow and team coordination:

**1) Adopted a structured branching strategy:** (please refer to [Branching strategy](docs/branching-strategy.pdf))

- We formalized a clear hierarchy: main (stable), dev (integration), and i3/* (feature/task branches).

- We standardized naming conventions (e.g., i3/feature/..., i3/task/..., i3/misc/...) to improve clarity and traceability across the team.

**2) Encouraged earlier and more frequent integration into dev:**

- Commit often, merge often for every 1-2 days

- We shifted from large, late merges to smaller, more frequent integrations into dev, making them easier to review and integrate.

**3) Stronger Merge Discipline and Collaboration:**

- We required at least one approval before merging into dev or main.

- We paid closer attention to high-risk areas, especially in shared logic and persistence layers where multiple members are working on.

- When issues occurred, we used controlled recovery strategies such as cherry-picking and reverting specific merges, instead of introducing further instability with a lot of branches.

**4) Alignment with Feedback from previous iterations and Code Quality Standards**

- We ensured everyone worked on the latest dev version before starting tasks.

- We consistently applied feedback from previous iterations (e.g., removing hardcoded strings, centralizing constants, improving validation) to avoid reintroducing old issues from previous iteration feedback,

### Measurable Evidence of Improvement

These corrective actions resulted in clear, measurable improvements:

**1) Reduced merge conflicts and rework:**

- In earlier iterations, merge conflicts were frequent and sometimes caused loss of completed work near deadlines at 4.30 PM. In Iteration 3, conflicts still occurred but were smaller, more localized, and resolved more quickly thanks to incremental merging.

- More merge requests were created and merged in Iteration 3 compared to the previous iterations as shown in [milestone 3](https://code.cs.umanitoba.ca/comp3350-winter2026/a02-g03-gitgirls/-/milestones/3#tab-issues)

**2) Improved build stability:**

- Previously, merges often introduced breaking changes. In Iteration 3, the dev branch remained consistently stable, allowing all members to integrate and test without disruption.

**3) Higher test reliability and coverage:**

- During Iteration 2, integrating real persistence caused over 40 failing tests. By Iteration 3, after aligning tests with the architecture, test suites consistently passed, providing confidence in our changes.

**4) Faster feature completion and integration:**

- New features in iteration 3 such as phase calculation, notifications, and calendar updates were integrated smoothly without being overwritten. We were able to build on each other’s work instead of redoing it, which improved overall efficiency.

### Project Velocity Chart

![Velocity Chart](docs/Velocity_Chart.png)

**Trend:**
The velocity chart highlights a clear improvement in both estimation accuracy and team coordination:

- In Iteration 1, there was a large gap between estimated and actual time. This reflects that our tasks were not well scoped, and we were still learning the system and requirements.

- In Iteration 2, the gap decreased significantly. This shows improvement in task breakdown and understanding of dependencies, particularly after working through architectural changes and persistence integration.

- By Iteration 3, estimated and actual time closely aligned. This indicates that we developed realistic planning and a more stable workflow.

### Conclusion

Overall, Iteration 3 reflects a significant improvement in how we worked as a team. By addressing our earlier weaknesses in integration and coordination, we created a more stable and predictable development process.

We moved from working in isolation to working collaboratively, from large risky merges to incremental integration, and from uncertain planning to reliable execution.

These changes not only improved our technical outcomes, such as stability and test reliability, but also strengthened our ability to deliver features efficiently as a team.