# STEP-00 — Roadmap Analysis Prompt

You are beginning controlled development of PropertyHub.

Read:

```text
CLAUDE.md
docs/vibe-coding/00-project-requirements.md
docs/vibe-coding/02-validation-contract.md
docs/vibe-coding/03-frontend-standards.md
```

Then inspect the current project files.

Do not create, modify, delete, or rename any project files.

Do not implement anything.

Your job is to:

1. Understand the current project state.
2. Compare it with the PropertyHub requirements.
3. Identify what already exists.
4. Identify missing capabilities.
5. Produce the recommended implementation sequence.
6. Break the work into small, coherent STEPs.
7. Keep the design simple enough to explain and understand.
8. Preserve the mandatory architecture and AI milestones.
9. Include backend validation, frontend validation, and PostgreSQL integrity validation.
10. Include backend testing with JUnit 5/Mockito/Spring Boot Test/MockMvc/JaCoCo.
11. Include frontend testing with Vitest and @vitest/ui.
12. Include React + Vite + Axios.
13. Include Claymorphism UI design and timeout toast UX.
14. Respect the no-Docker and corporate-environment constraints.
15. Keep optional features after the mandatory MVP.

For every STEP provide:

- STEP ID
- Name
- Objective
- Dependencies
- Files expected to be created/modified
- Backend impact
- Frontend impact
- Database impact
- Validation impact
- Testing impact
- Expected outcome

Then stop.

Do not implement anything.

Ask:

Proceed with this development roadmap? [Y/N]
