# Travella App
A social media–style web application for sharing travel experiences.

Built in collaboration with:
Katarzyna Kuzora,
Weronika Grzybowska,
Gabryel Jundziłł

## Tech stack

- **Backend:** Java, Spring Boot, Spring Security, JWT
- **Frontend:** React
- **Database:** PostgreSQL
- **Other:** Docker, REST API

## How it works

1. User creates an account on the platform.
2. User logs in to their account.
3. When adding a post, the user's location is retrieved (or manually selected), a date is entered, and the user fills out a prepared questionnaire about the visited place.
4. Photos can be attached to each post during creation.
5. Friends' profiles are visible within the app.
6. A search feature lets users find new friends by partial username match.
7. Each user's profile includes a map with pins marking locations of their posts and their friends' posts.
8. Profile personalization and editing is available at any time.

## My contributions

- Backend MVC architecture (Spring Boot) — config, controller, model, service, repository layers
- User management — `UserController`, `UserService`, `UserResponse` DTO
- JWT-based authentication and security configuration (`SecurityConfig`)
- Testing for user controller (`UserControllerTesting`), with coverage analysis to guide improvements
- Minor React frontend contributions
