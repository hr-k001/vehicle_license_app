# Vehicle License App

Lightweight training project that simulates an RTO (Regional Transport Office) portal and an applicant portal for learner and driving license workflows.

**Overview**
- Backend: Spring Boot application exposing REST APIs under `/api` (license, rto, applicants, reports).
- Frontend: React + Vite single-page app located in `frontend/` that talks to the backend at `http://localhost:8080`.

**Features**
- Apply for Learner License (LL) and Driving License (DL)
- RTO officer dashboard to view, approve/reject, search and edit application details
- Generate driving license numbers and view license details

**Prerequisites**
- Java (JDK) 17+ (project was developed on a later JDK; use a recent JDK)
- Maven 3.6+
- Node.js 16+ and npm

**Run - Backend**
1. From project root:

```
mvn spring-boot:run
```

The backend runs on `http://localhost:8080` by default.

**Run - Frontend**
1. In a separate terminal:

```
cd frontend
npm install
npm run dev
```

Open the URL printed by Vite (typically `http://localhost:5173`). The frontend expects the API at `http://localhost:8080`.

**Build**
- Backend: `mvn package`
- Frontend production build:

```
cd frontend
npm run build
```

**Tests**
- Run all backend tests: `mvn test`
- Run a single backend test class: `mvn -Dtest=RTOOfficerServiceTest test`
- Frontend tests (if added): `cd frontend && npm run test`

**Key files**
- Backend controllers: `src/main/java/com/online/controller`
- Services: `src/main/java/com/online/service` and `src/main/java/com/online/service/impl`
- Frontend app: `frontend/src`

**Notes**
- RTO officers can now edit application details from the Manage Applications screen (frontend change in `frontend/src/pages/rto/ManageApplications.jsx`).
- The generate-license path was hardened in `src/main/java/com/online/service/impl/LicenseServiceImpl.java` to avoid type conversion issues when persisting applicants.


