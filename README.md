# SafeChain 🔐

A secure file storage and sharing system built with Spring Boot that helps users securely upload, manage, and share files within an organization or among trusted users.

## 🌟 Features

### 🔐 User Management

- **User Registration & Authentication** - Secure account creation with JWT tokens
- **Role-Based Access Control** - USER and ADMIN roles with different permissions
- **Password Security** - BCrypt encryption for secure password storage

### 📁 File Management

- **File Upload** - Support for various file types (PDFs, images, documents)
- **File Storage** - Local file system storage with metadata tracking
- **File Download** - Secure file access with permission validation
- **File Sharing** - Share files with specific users in your organization

### 👥 Sharing & Collaboration

- **User-to-User Sharing** - Share files with specific users by email
- **Access Control** - Users can only access files they own or are shared with them
- **Shared Files View** - See all files shared with you

### 🛡️ Security Features

- **JWT Authentication** - Stateless, secure token-based authentication
- **Password Encryption** - BCrypt hashing for password security
- **File Permission System** - Granular access control for file sharing
- **Role-Based Authorization** - Different access levels for users and admins

### 👨‍💼 Admin Panel

- **User Management** - View all users in the system
- **User Deletion** - Remove users from the system
- **System Overview** - Admin-only access to system-wide data

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (Web UI)      │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   File Storage  │
                       │   (/uploads)    │
                       └─────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd safechain
   ```

2. **Set up PostgreSQL**

   ```sql
   CREATE DATABASE safechain;
   ```

3. **Configure database connection**
   Update `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/safechain
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Create uploads directory**

   ```bash
   mkdir uploads
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint             | Description       | Auth Required |
| ------ | -------------------- | ----------------- | ------------- |
| POST   | `/api/auth/register` | Register new user | No            |
| POST   | `/api/auth/login`    | Login user        | No            |

### File Management Endpoints

| Method | Endpoint                    | Description          | Auth Required |
| ------ | --------------------------- | -------------------- | ------------- |
| POST   | `/api/files/upload`         | Upload file          | Yes           |
| GET    | `/api/files/my`             | Get user's files     | Yes           |
| GET    | `/api/files/shared-with-me` | Get shared files     | Yes           |
| GET    | `/api/files/{id}/download`  | Download file        | Yes           |
| POST   | `/api/files/share`          | Share file with user | Yes           |

### Admin Endpoints

| Method | Endpoint                | Description   | Auth Required |
| ------ | ----------------------- | ------------- | ------------- |
| GET    | `/api/admin/users`      | Get all users | Admin only    |
| DELETE | `/api/admin/users/{id}` | Delete user   | Admin only    |

## 🔧 Configuration

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/safechain
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### JWT Configuration

```properties
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

### File Upload Configuration

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads
```

## 🧪 Testing the API

### 1. Register a new user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Upload a file (use the JWT token from login)

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf"
```

### 4. Get your files

```bash
curl -X GET http://localhost:8080/api/files/my \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏛️ Project Structure

```
src/main/java/com/safechain/safechain/
├── 📁 config/          # Security and application configuration
├── 📁 controller/       # REST API controllers
├── 📁 dto/             # Data Transfer Objects
├── 📁 entity/          # Database entities
├── 📁 repository/      # Database repositories
├── 📁 security/        # JWT security components
├── 📁 service/         # Business logic services
└── 📁 util/            # Utility classes
```

## 🔒 Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-Based Access Control**: Different permissions for users and admins
- **File Access Control**: Users can only access files they own or are shared with them
- **Input Validation**: All user inputs are validated and sanitized

## 🚀 Deployment

### Local Development

```bash
mvn spring-boot:run
```

### Production Build

```bash
mvn clean package
java -jar target/safechain-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/safechain-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
