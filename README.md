
> 🔐 Spring Security Privilege Management App
>
> Cette application est une API sécurisée construite avec **Spring Boot**, **Spring Security**, **JWT**, et **PostgreSQL**, permettant :
> - La gestion des utilisateurs
> - L’authentification basée sur JWT
> - La gestion fine des privilèges par module et sous-module
>
> Elle est conçue pour servir de base à tout projet nécessitant une architecture sécurisée avec des rôles personnalisés et des autorisations granulaires.

# 🔐 Spring Security Privilege Management

Cette application est une API sécurisée réalisée avec Spring Boot, qui propose :
- Authentification avec JWT
- Gestion des privilèges par `Module`, `SubModule` et `PrivilegeType`
- Intégration avec PostgreSQL
- Swagger UI pour la documentation des endpoints

## 🚀 Fonctionnalités

- Inscription / Connexion des utilisateurs
- Génération de tokens JWT avec expiration
- Sécurité basée sur les rôles et privilèges
- Architecture modulaire avec entités `User`, `Privilege`, `Role`

## 🧱 Technologies utilisées

- Java 17+
- Spring Boot 3+
- Spring Security
- JWT (JSON Web Tokens)
- PostgreSQL
- Lombok
- Swagger / SpringDoc

## ⚙️ Configuration

Le fichier `application.properties` contient tous les paramètres nécessaires :

```properties
# Application
spring.application.name=security
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/security
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update

# JWT
application.security.jwt.secret-key=
application.security.jwt.expiration=8640000

## 🔄 Exemple de token JWT

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI...
```

## 🧪 Tester l’API avec Swagger

Swagger est accessible via :
```
http://localhost:8080/swagger-ui/index.html
```

## 📦 Lancer l'application

```bash
./mvnw spring-boot:run
```



## 🙌 Auteur

Développé par ryhab elmoncer

