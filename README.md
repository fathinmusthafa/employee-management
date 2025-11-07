# employee-management
Tugas Assesment membuat CRUD Employee

## How to Run

### Prerequisites
1. **Java 21**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **PostgreSQL 15+**
   ```bash
   psql --version
   ```

4. **Git**
   ```bash
   git --version
   ```

### Step-by-Step Setup

#### 1. Clone Repository
```bash
git clone 
cd employee-management
```

#### 2. Setup PostgreSQL Database
```bash
# Login ke PostgreSQL
psql -U postgres

# Buat database
CREATE DATABASE employees_db;

# Grant privileges (optional)
GRANT ALL PRIVILEGES ON DATABASE employees_db TO postgres;

# Exit
\q
```

#### 3. Configure Database Connection
Edit file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/employees_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### 4. Build Project
```bash
# Clean dan build
mvn clean install

# Atau skip tests jika ingin lebih cepat
mvn clean install -DskipTests
```

#### 5. Run Application
```bash
# Menggunakan Maven
mvn spring-boot:run

# Atau run JAR file
java -jar target/employee-management-1.0.0.jar
```

#### 6. Access Application
Tunggu hingga aplikasi start, kemudian akses:
- **Web UI**: http://localhost:8080/
- **Employees Page**: http://localhost:8080/employees
- **Departments Page**: http://localhost:8080/departments
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api/

### 7. DDL AUTO : Create-Drop
- Jika Aplikasi Berhenti DataBase di hapus
- Menggunakan Data Dumy di file data.sql, saat aplikasi dimulai akan terisi data dummy

