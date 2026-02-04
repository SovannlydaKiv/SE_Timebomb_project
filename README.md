# Time Tracker v1.0

A comprehensive Java-based desktop application for tracking time spent on projects and tasks, designed to help users improve productivity through detailed time analysis and reporting.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Architecture](#system-architecture)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Team Members](#team-members)
- [License](#license)

## 🎯 Overview

Time Tracker is a Java desktop application that provides a user-friendly interface for managing projects, tasks, and time entries. The application allows users to track their productivity, generate reports, and gain insights into how time is allocated across different activities.

**Main Objectives:**
- Help users record and monitor time spent on different tasks or activities
- Provide clear and simple insights into time allocation
- Improve time management and productivity
- Generate comprehensive reports for analysis

## ✨ Features

### User Management
- User registration and authentication
- Secure login system with "Remember Me" functionality
- User session management
- Profile management

### Project Management
- Create and manage multiple projects
- Assign projects to clients
- Set project budgets and hourly rates
- Track project status (Active, Completed, On Hold, Cancelled)
- Color-code projects for easy identification
- Set project deadlines

### Task Management
- Create tasks within projects
- Set task priorities (High, Medium, Low)
- Track task status (Not Started, In Progress, Completed, On Hold)
- Assign due dates to tasks
- Add task descriptions and notes

### Time Tracking
- Start/stop timers for active tasks
- Manually add time entries
- Track time spent on each task and project
- Record detailed time logs with timestamps
- Add notes to time entries

### Reporting & Analytics
- View daily, weekly, and monthly time summaries
- Generate project-based reports
- Track time spent by client
- Calculate earnings based on hourly rates
- View task progress and completion statistics
- Export reports for analysis

### User Interface
- Modern, intuitive GUI built with Java Swing
- Responsive design that adapts to different screen sizes
- Custom rounded buttons and styled components
- Color-coded visual indicators
- Dashboard with quick statistics

## 🛠 Technologies Used

- **Language:** Java
- **GUI Framework:** Java Swing
- **Database:** MySQL 8.2.0
- **JDBC Driver:** MySQL Connector/J 8.2.0
- **Build System:** Java Compiler (javac)
- **Version Control:** Git

## 🏗 System Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│     (GUI - Java Swing Views)        │
├─────────────────────────────────────┤
│         Service Layer               │
│  (Business Logic & Operations)      │
├─────────────────────────────────────┤
│       Data Access Layer             │
│         (DAO Pattern)               │
├─────────────────────────────────────┤
│         Model Layer                 │
│      (Domain Objects)               │
├─────────────────────────────────────┤
│         Database Layer              │
│        (MySQL Database)             │
└─────────────────────────────────────┘
```

### Design Patterns Used:
- **Singleton Pattern:** DatabaseManager for connection pooling
- **DAO Pattern:** Separation of data access logic
- **MVC Pattern:** Model-View-Controller architecture
- **Service Layer Pattern:** Business logic encapsulation

## 📦 Installation

### Prerequisites

- **Java Development Kit (JDK) 11 or higher**
  - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

- **MySQL Server 8.0 or higher**
  - Download from [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)
  - Ensure MySQL service is running

- **Git** (optional, for cloning the repository)

### Setup Steps

1. **Clone or Download the Repository**
   ```bash
   git clone https://github.com/SovannlydaKiv/SE_Timebomb_project.git
   cd timetracker-v3.0
   ```

2. **Set Up MySQL Database**
   
   The application will automatically create the database and tables on first run, but you need to ensure MySQL is running and accessible.

   Default database credentials:
   - Host: `localhost`
   - Port: `3306`
   - Database: `timetracker`
   - Username: `root`
   - Password: `password`

   To create the database manually (optional):
   ```sql
   CREATE DATABASE IF NOT EXISTS timetracker;
   ```

3. **Add MySQL Connector to Classpath**

   The MySQL JDBC driver is included in the `lib/` folder. Ensure it's in your classpath when compiling and running.

4. **Compile the Application**
   ```bash
   # On Windows
   javac -cp ".;lib/mysql-connector-j-8.2.0.jar" -d bin GUI/*.java src/model/*.java src/dao/*.java src/service/*.java

   # On Linux/Mac
   javac -cp ".:lib/mysql-connector-j-8.2.0.jar" -d bin GUI/*.java src/model/*.java src/dao/*.java src/service/*.java
   ```

5. **Run the Application**
   ```bash
   # On Windows
   java -cp "bin;lib/mysql-connector-j-8.2.0.jar" LoginPage

   # On Linux/Mac
   java -cp "bin:lib/mysql-connector-j-8.2.0.jar" LoginPage
   ```

## ⚙️ Configuration

### Database Configuration

You can configure database connection settings using environment variables:

```bash
# Set environment variables (Linux/Mac)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=timetracker
export DB_USER=root
export DB_PASSWORD=your_password

# Set environment variables (Windows)
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=timetracker
set DB_USER=root
set DB_PASSWORD=your_password
```

If environment variables are not set, the application uses the following defaults:
- Host: `localhost`
- Port: `3306`
- Database: `timetracker`
- User: `root`
- Password: `password`

## 🚀 Usage

### Getting Started

1. **Launch the Application**
   - Run the compiled application
   - The login screen will appear

2. **Create an Account**
   - Click "Sign Up" on the login screen
   - Fill in your details:
     - Username (unique)
     - Email (unique)
     - Full Name
     - Password
   - Click "Sign Up" to create your account

3. **Login**
   - Enter your username and password
   - Optionally check "Remember Me" to save credentials
   - Click "Login"

### Working with Projects

1. **Create a New Project**
   - Navigate to the Projects section
   - Click "New Project" or "+"
   - Fill in project details:
     - Project Name
     - Description
     - Client Name
     - Color Code
     - Hourly Rate (optional)
     - Budget (optional)
     - Deadline (optional)
   - Click "Save"

2. **Manage Projects**
   - View all your projects in the projects list
   - Edit project details by selecting a project
   - Change project status (Active, Completed, On Hold, Cancelled)
   - Delete projects (this will also delete associated tasks and time entries)

### Working with Tasks

1. **Create a New Task**
   - Select a project
   - Click "New Task" or "Add Task"
   - Fill in task details:
     - Task Name
     - Description
     - Priority (High, Medium, Low)
     - Due Date (optional)
   - Click "Save"

2. **Track Time on Tasks**
   - Select a task from the task list
   - Click "Start Timer" to begin tracking time
   - The timer will run in the background
   - Click "Stop Timer" when done
   - Time entry is automatically saved

3. **Manually Add Time Entries**
   - Navigate to the Tracker Page
   - Select a project and task
   - Click "Add Manual Entry"
   - Enter start time, end time, and notes
   - Click "Save"

### Viewing Reports

1. **Summary Page**
   - Navigate to the Summary Page
   - View statistics:
     - Total time tracked today
     - Total time this week
     - Total time this month
     - Earnings (based on hourly rates)
   - Filter by date range
   - View time breakdown by project
   - View task completion progress

2. **Export Reports**
   - Select date range
   - Choose report type (Daily, Weekly, Monthly)
   - Click "Export" to save report

## 🗄 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

### Projects Table
```sql
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    color_code VARCHAR(50),
    client VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    hourly_rate DECIMAL(10, 2),
    budget DECIMAL(10, 2),
    deadline DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_id BIGINT,
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    due_date DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id)
);
```

### Time Entries Table
```sql
CREATE TABLE time_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration_minutes INT,
    notes TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);
```

## 📁 Project Structure

```
timetracker-v3.0/
│
├── GUI/                        # Graphical User Interface
│   ├── LoginPage.java         # Login screen
│   ├── SignUpPage.java        # Registration screen
│   ├── ProjectGUI.java        # Project management interface
│   ├── TrackerPage.java       # Time tracking interface
│   ├── SummaryPage.java       # Reports and analytics
│   ├── ProjectForm.java       # Project creation/editing form
│   ├── IconHelper.java        # Icon utilities
│   └── UserSession.java       # Session management
│
├── src/
│   ├── model/                 # Domain Models
│   │   ├── User.java
│   │   ├── Project.java
│   │   ├── Task.java
│   │   ├── TimeEntry.java
│   │   ├── Priority.java
│   │   ├── ProjectStatus.java
│   │   └── TaskStatus.java
│   │
│   ├── dao/                   # Data Access Objects
│   │   ├── DatabaseManager.java
│   │   ├── UserDAO.java
│   │   ├── ProjectDAO.java
│   │   ├── TaskDAO.java
│   │   └── TimeEntryDAO.java
│   │
│   └── service/               # Business Logic
│       ├── UserService.java
│       ├── ProjectService.java
│       ├── TaskService.java
│       ├── TimeEntryService.java
│       └── ReportService.java
│
├── lib/                       # External Libraries
│   └── mysql-connector-j-8.2.0.jar
│
├── bin/                       # Compiled Classes
│
├── .gitignore
├── Planning.md                # Project planning document
├── projectPlanning.docx       # Detailed project plan
├── project_description        # Project overview
└── README.md                  # This file
```

### Development Branches
- `main` - Main branch
- `lyda` - Lyda's development branch
- `Inaco` - Inaco's development branch
- `Monika` - Monika's development branch
- `Vattey` - Vattey's development branch

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Team Members

This project was developed by a team of Software Engineering students:

- **Sovannlyda Kiv** - [@SovannlydaKiv](https://github.com/SovannlydaKiv)
- Additional team members who contributed to different branches

## 📄 License

This project is licensed for educational purposes as part of a Software Engineering course.

## 🐛 Known Issues

- Currently in development (v1.0)
- Some features may still be under testing
- Report generation features are being enhanced

## 🔮 Future Enhancements

- Export reports to PDF/Excel
- Team collaboration features
- Mobile application companion
- Integration with calendar applications
- Advanced analytics and charts
- Dark mode theme
- Multiple language support
- Cloud synchronization

---

**Version:** 1.0  
**Last Updated:** February 2026  
**Status:** Active Development
