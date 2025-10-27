# Notes app with a minimal web UI

A Spring Boot notes application with JWT authentication, built with Java 21 and modern web technologies.

## 🚀 Features

- **User Authentication**: JWT-based signup, login, and secure endpoints
- **Notes Management**: Full CRUD operations with soft delete functionality
- **Advanced Search**: Search by title/content and filter by multiple tags
- **Pagination & Sorting**: Efficient data handling with customizable sorting
- **UI**: Clean, modern interface built with Bootstrap and Thymeleaf
- **RESTful API**: Well-structured endpoints with proper error handling

## 🛠️ Tech Stack

### Backend
- **Java 21** - Modern Java features
- **Spring Boot 3** - Framework foundation
- **Spring Security** - JWT authentication & authorization
- **Spring Data JPA** - Database operations
- **H2 Database** - Development database (file-based)
- **Flyway** - Database migrations

### Frontend
- **Thymeleaf** - Server-side templating
- **Bootstrap 5** - Responsive UI components
- **jQuery** - Client-side interactions
- **JavaScript** - Dynamic functionality

## 📋 Prerequisites

- Java 21 or later
- Maven 3.6+
- Git

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/Albymens/note-app.git
```

### 2. Set Environment Variables
```bash
export JWT_SECRET_KEY=your_generated_secret_key
export JWT_EXPIRY_LENGTH=86400000

# Optional: Verify variables are set
echo "JWT_SECRET_KEY: $JWT_SECRET_KEY"
echo "JWT_EXPIRY_LENGTH: $JWT_EXPIRY_LENGTH"
```

### 3. Build the Application
```bash
mvn clean compile
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Application
- **Main Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:file:./data/notesdb`
    - Username: `sa`
    - Password: `password`

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/com/albymens/note_app/
│   │   ├── controller/     # REST controllers & web controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── entity/         # JPA entities
│   │   ├── dto/           # Data transfer objects
│   │   ├── config/         # Configuration classes
│   │   └── exception/      # Custom exception handling
│   └── resources/
│       ├── templates/      # Thymeleaf templates
│       ├── static/         # CSS, JS, images
│       └── db/migration/   # Flyway migrations
└── test/                   # Comprehensive test suite
```

## 🔧 Configuration

### Application Properties
Key configuration in `application.properties`:

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/validate` - Token validation

### Notes Endpoints
- `GET /api/notes/search` - Search notes with filters
- `POST /api/notes` - Create new note
- `GET /api/notes/{id}` - Get specific note
- `PUT /api/notes/{id}` - Update note
- `DELETE /api/notes/{id}` - Soft delete note
- `PATCH /api/notes/{id}/restore` - Restore deleted note

### Search Parameters
- `searchTerm`: Search in title and content
- `tags`: Filter by tags (comma-separated)
- `page`: Page number (0-based)
- `size`: Page size (default: 10)
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

## 🎯 Usage Guide

### 1. User Registration
1. Navigate to `/signup`
2. Create account with username, email, and password
3. Automatic redirect to login

### 2. User Login
1. Navigate to `/login`
2. Enter credentials
3. Automatic redirect to notes dashboard

### 3. Managing Notes
- **Create**: Click "New Note" button
- **Edit**: Click edit icon on any note
- **Delete**: Click delete icon (soft delete)
- **Restore**: Click restore button on deleted notes
- **Search**: Use search box and tag filters

### 4. Advanced Features
- **Tag Management**: Add multiple comma-separated tags
- **Sorting**: Sort by date created, updated, or title
- **Pagination**: Navigate through large note collections

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Secure endpoint protection
- CSRF protection disabled for API (enabled for forms)
- Input validation and sanitization

## 🗃️ Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `email` (Unique)
- `password` (Encrypted)
- `created_at`, `updated_at`

### Notes Table
- `id` (Primary Key)
- `title`, `content`
- `tags` (JSON array)
- `user_id` (Foreign Key)
- `created_at`, `updated_at`, `deleted_at`

## 🐛 Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Kill process on port 8080
   npx kill-port 8080
   # Or change port in application.yml
   server.port: 8081
   ```

2. **H2 Database connection issues**
    - Check if `./data/` directory exists
    - Verify JDBC URL in H2 console matches application.yml

3. **JWT token issues**
    - Clear browser localStorage
    - Restart application to regenerate tokens

### Run locally
1. Build: `mvn -DskipTests package`
2. Run: `java -jar target/notes-app-0.0.1-SNAPSHOT.jar`
3. Open `http://localhost:8080/`

### Decisions
- H2 file used for simplicity; switching to Postgres is straightforward (change `spring.datasource`).
- Tags stored as comma-separated string in DB for simplicity; normalized and exposed as Set in API.
- JWT used; passwords stored with BCrypt.

### Docker
- 🔐 Generating a JWT Secret Key: You’ll need a secret key for signing JWTs (Generate online).
- Build: 
```bash 
docker build -t notes-app .
```
- Run:
```bash
    docker run -p 8080:8080 \
    -e JWT_SECRET_KEY=your_generated_secret_key \
    -e JWT_EXPIRY_LENGTH=3600000 \
    note-app
```

### Flyway
Migrations live under `src/main/resources/db/migration`.



