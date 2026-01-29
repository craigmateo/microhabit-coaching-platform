# MicroHabit Tracker

A lightweight full-stack web application for tracking daily micro-habits.

Users can register, log in, mark habits as completed, and persist progress in a SQL database.  
The project is intentionally simple and end-to-end: schema → build → UI → database verification.

---

## Preview Video

https://github.com/user-attachments/assets/cddb1c5c-0977-4063-b403-91106557b67e

## Features

- User registration and authentication
- Habit completion tracking
- Persistent SQL database storage
- Minimal UI focused on core flows
- Clear, inspectable backend → database integration

---

## Tech Stack

- Backend: Java
- Database: MySQL / MariaDB
- Frontend: HTML / CSS / JavaScript
- Build Tool: (e.g. Maven, Gradle, npm)

---

## Project Structure

```text
microhabit/
├── schema.sql        # Database schema and table definitions
├── src/              # Application source code
├── public/           # Static assets (if applicable)
├── build/            # Compiled output (if applicable)
└── README.md
```

---

## Getting Started

### 1. Create the Database

Run the schema file to initialize the database and tables:

```sql
SOURCE schema.sql;
```

Or via command line:

```bash
mysql -u <user> -p < schema.sql
```

---

### 2. Build the Application

Build the project using your build tool:

```bash
# example
mvn clean package
```

(Use the equivalent command for your stack.)

---

### 3. Run and Open the App

Start the application and open it in your browser:

```text
http://localhost:<port>
```

---

## Usage Walkthrough

1. Register a new user account  
2. Log in with your credentials  
3. Mark a habit as completed  
4. Confirm the action is persisted in the database  

---

## Database Verification

After marking a habit as completed, inspect the database tables to verify new rows were inserted.

Example:

```sql
SELECT * FROM habit_completions;
```

This confirms:
- authentication works
- state changes persist
- backend ↔ database integration is correct

---

## Project Goals

- Practice full-stack fundamentals without heavy frameworks
- Reinforce database-first design
- Build a complete, verifiable workflow
- Serve as a foundation for future features

---

## Future Improvements

- Habit streaks and statistics
- Multiple habits per user
- UI polish and responsiveness
- REST API layer
- Automated tests

---

## License

MIT (or your preferred license)

